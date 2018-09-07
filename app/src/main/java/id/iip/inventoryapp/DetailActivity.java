package id.iip.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import id.iip.inventoryapp.data.InventoryContract;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // default resource
    private EditText nameET, quantityET, priceET, supplierET, supplierNameET;
    private Spinner colorSP;
    private Button imageBT;
    private ImageView imageIV;

    // value of resource
    private String name;
    private int quantity;
    private double price;
    private int color;
    private String supplier;
    private String supplierName;
    private Uri imageUri;

    // data for spinner
    private ArrayAdapter<CharSequence> spinnerAdapter;

    // default uri
    private Uri mCurrentProductUri;

    // LOADER ID
    private static final int LOADER_ID = 2;

    // request code pick image
    private static final int PICK_IMAGE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        nameET = (EditText) findViewById(R.id.name_et);
        quantityET = (EditText) findViewById(R.id.quantity_et);
        priceET = (EditText) findViewById(R.id.price_et);
        supplierET = (EditText) findViewById(R.id.supplier_et);
        supplierNameET = (EditText) findViewById(R.id.supplier_name_et);

        colorSP = (Spinner) findViewById(R.id.color_sp);
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.colors_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSP.setAdapter(spinnerAdapter);

        imageBT = (Button)findViewById(R.id.image_btn);
        imageBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        imageIV = (ImageView)findViewById(R.id.image_iv);

        // get intent
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.add_product));
        } else {
            setTitle(getString(R.string.update_product));;
            imageBT.setVisibility(View.GONE);
            imageIV.setVisibility(View.VISIBLE);
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }

            imageUri = data.getData();
            imageBT.setVisibility(View.GONE);
            imageIV.setImageURI(imageUri);
            imageIV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                if (validateAndAssignValue())
                    showDialogAdd();
                else
                    Toast.makeText(this, getString(R.string.validate_your_input), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.edit:
                if (validateAndAssignValue())
                    showDialogEdit();
                else
                    Toast.makeText(this, getString(R.string.validate_your_input), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.update:
                showDialogModify();
                return true;
            case R.id.order:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(this, getString(R.string.phone_permission), Toast.LENGTH_SHORT).show();
                } else {
                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                    phoneIntent.setData(Uri.parse("tel:" + supplier));
                    startActivity(phoneIntent);
                }
                return true;
            case R.id.delete:
                showDialogDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validateAndAssignValue(){

        String sName = nameET.getText().toString();
        String sQuantity = quantityET.getText().toString();
        String sPrice = priceET.getText().toString();
        String sSupplier = supplierET.getText().toString();
        String sSupplierName = supplierNameET.getText().toString();

        if (TextUtils.isEmpty(sName) || TextUtils.isEmpty(sQuantity) || TextUtils.isEmpty(sPrice) || TextUtils.isEmpty(sSupplierName))
            return false;

        name = sName;
        quantity = Integer.parseInt(sQuantity);
        price = Double.parseDouble(sPrice);
        color = colorSP.getSelectedItemPosition();
        supplier = sSupplier;
        supplierName = sSupplierName;

        return true;
    }

    private void showDialogAdd(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_product, null);
        String msgDialog = String.format(getString(R.string.msg_dialog), name, String.valueOf(quantity), String.valueOf(price), supplier, supplierName);
        TextView tv = (TextView) view.findViewById(R.id.add_info_dialog);
        tv.setText(msgDialog);
        ImageView iv = (ImageView)view.findViewById(R.id.image_d_iv);
        iv.setImageURI(imageUri);
        builder.setView(view);
        builder.setMessage(R.string.add_product)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addProduct();
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    private void showDialogEdit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_product, null);
        String msgDialog = String.format(getString(R.string.msg_dialog), name, String.valueOf(quantity), String.valueOf(price), supplier, supplierName);
        TextView tv = (TextView) view.findViewById(R.id.add_info_dialog);
        tv.setText(msgDialog);
        ImageView iv = (ImageView)view.findViewById(R.id.image_d_iv);
        iv.setImageURI(imageUri);
        builder.setView(view);
        builder.setMessage(R.string.edit_product)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        editProduct();
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    private void addProduct(){
        ContentValues cv = new ContentValues();
        cv.put(InventoryContract.ProductEntry.COLUMN_NAME_NAME, name);
        cv.put(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY, quantity);
        cv.put(InventoryContract.ProductEntry.COLUMN_NAME_PRICE, price);
        cv.put(InventoryContract.ProductEntry.COLUMN_NAME_COLOR, color);
        cv.put(InventoryContract.ProductEntry.COLUMN_NAME_SUPPLIER_PHONE, supplier);
        cv.put(InventoryContract.ProductEntry.COLUMN_NAME_SUPPLIER_NAME, supplierName);
        if (imageUri != null) {
            byte[] data = null;
            try {
                InputStream is = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                ByteArrayOutputStream boas = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas);
                data = boas.toByteArray();
                cv.put(InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_IMAGE, data);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        getContentResolver().insert(InventoryContract.ProductEntry.CONTENT_URI, cv);
    }

    private void editProduct(){
        ContentValues cv = new ContentValues();
        cv.put(InventoryContract.ProductEntry.COLUMN_NAME_NAME, name);
        cv.put(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY, quantity);
        cv.put(InventoryContract.ProductEntry.COLUMN_NAME_PRICE, price);
        cv.put(InventoryContract.ProductEntry.COLUMN_NAME_COLOR, color);
        cv.put(InventoryContract.ProductEntry.COLUMN_NAME_SUPPLIER_PHONE, supplier);
        cv.put(InventoryContract.ProductEntry.COLUMN_NAME_SUPPLIER_NAME, supplierName);
        if (imageUri != null) {
            byte[] data = null;
            try {
                InputStream is = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                ByteArrayOutputStream boas = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas);
                data = boas.toByteArray();
                cv.put(InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_IMAGE, data);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        getContentResolver().update(mCurrentProductUri, cv, null, null);
    }

    private int tempQuantity = 0;

    private void showDialogModify(){
        String sQuantity = quantityET.getText().toString();

        if (TextUtils.isEmpty(sQuantity))
            tempQuantity = 0;
        else
            tempQuantity = Integer.parseInt(sQuantity);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.update_product));

        View view = LayoutInflater.from(this).inflate(R.layout.modify_quantity, null);
        final TextView tv = (TextView)view.findViewById(R.id.modify_tv);
        tv.setText(String.valueOf(quantityET.getText().toString()));

        final EditText et = (EditText)view.findViewById(R.id.modify_et);

        Button inc = (Button)view.findViewById(R.id.btn_increase);
        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et.getText().toString()))
                    return;
                tempQuantity = tempQuantity + Integer.parseInt(et.getText().toString());
                tv.setText(String.valueOf(tempQuantity));
            }
        });

        Button dec = (Button)view.findViewById(R.id.btn_decrease);
        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et.getText().toString()))
                    return;
                if (tempQuantity <= 0)
                    return;
                tempQuantity = tempQuantity - Integer.parseInt(et.getText().toString());
                tv.setText(String.valueOf(tempQuantity));
            }
        });
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (tempQuantity <= 0)
                    return;
                updateQuantity(tempQuantity);
            }
        });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    private void updateQuantity(int quantity){
        ContentValues cv = new ContentValues();
        cv.put(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY, quantity);
        getContentResolver().update(mCurrentProductUri, cv, null, null);
    }

    private void showDialogDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_product)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteProduct();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    private void deleteProduct(){
        getContentResolver().delete(mCurrentProductUri, null, null);
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, mCurrentProductUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()){

            name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_NAME));
            nameET.setText(name);

            quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY));
            quantityET.setText(String.valueOf(quantity));

            price = cursor.getDouble(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_PRICE));
            priceET.setText(String.valueOf(price));

            color = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_COLOR));
            switch (color){
                case 1: colorSP.setSelection(1); break;
                case 2: colorSP.setSelection(2); break;
                case 0:
                default: colorSP.setSelection(0); break;
            }

            supplier = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_SUPPLIER_PHONE));
            supplierET.setText(supplier);

            supplierName = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_SUPPLIER_NAME));
            supplierNameET.setText(supplierName);

            byte[] byteUri = cursor.getBlob(cursor.getColumnIndexOrThrow(InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_IMAGE));
            if (byteUri == null) {
                return;
            }

            Bitmap bitmap = BitmapFactory.decodeByteArray(byteUri, 0, byteUri.length);
            imageIV.setVisibility(View.VISIBLE);
            imageIV.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {;
    }

}

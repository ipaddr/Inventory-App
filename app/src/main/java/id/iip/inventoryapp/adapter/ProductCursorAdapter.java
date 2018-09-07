package id.iip.inventoryapp.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import id.iip.inventoryapp.R;
import id.iip.inventoryapp.data.InventoryContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_product_item, parent,false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        /**
         * get data from cursor
         */
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry._ID));
        final String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_NAME));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_QUANTITY));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_PRICE));

        /**
         * bind data to its view
         */
        TextView nameTV = (TextView)view.findViewById(R.id.product_name);
        nameTV.setText(name);

        TextView quanityTV = (TextView)view.findViewById(R.id.product_quantity);
        quanityTV.setText(String.valueOf(quantity));

        TextView priceTV = (TextView)view.findViewById(R.id.product_price);
        priceTV.setText(String.valueOf(price));

        /**
         * set id for each button as same as product id, later will use to update the quantity
         */
        Button button = (Button)view.findViewById(R.id.button_sale);
        button.setTag(id);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * get the id of the selected product and decrease the quantity
                 */
                int idInsideOnClickView = (Integer)v.getTag();
                int finalQuantity = quantity - 1;

                /**
                 * no need to decrease quanity since it value is zero
                 */
                if (finalQuantity < 0)
                    return;

                /**
                 * update quanity value decrese by 1
                 */
                ContentValues cv = new ContentValues();
                cv.put(ProductEntry.COLUMN_NAME_QUANTITY, finalQuantity);
                Uri uri = ProductEntry.buildProductUri(idInsideOnClickView);
                context.getContentResolver().update(uri, cv, null, null);
            }
        });
    }
}

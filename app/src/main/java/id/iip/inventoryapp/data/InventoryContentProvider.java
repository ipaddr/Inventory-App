package id.iip.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import id.iip.inventoryapp.data.InventoryContract.ProductEntry;

public class InventoryContentProvider extends ContentProvider {

    /** URI matcher code for the content URI for the product table */
    protected static final int PRODUCTS = 100;

    /** URI matcher code for the content URI for a single product in the pets table */
    protected static final int PRODUCT_ID = 101;

    /**
     * instance variable to hold the object of database
     */
    private InventoryDbHelper mDbHelper;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        /*
         * The calls to addURI() go here, for all of the content URI patterns that the provider
         * should recognize. For this snippet, only the calls for table 3 are shown.
         */

        /*
         * Sets the integer value for multiple rows
         */
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCT, PRODUCTS);

        /*
         * Sets the code for a single row
         */
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCT + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (sUriMatcher.match(uri)){
            case PRODUCTS: return InventoryContract.ProductEntry.CONTENT_TYPE;
            case PRODUCT_ID: return InventoryContract.ProductEntry.CONTENT_ITEM_TYPE;
            default: throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long _id;
        Uri returnUri;

        switch (sUriMatcher.match(uri)){
            case PRODUCTS:
                _id = db.insert(ProductEntry.TABLE_NAME, null, values);
                if(_id > 0){
                    returnUri =  ProductEntry.buildProductUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case PRODUCT_ID:
            default:throw new UnsupportedOperationException("Not yet implemented");
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)){
            case PRODUCTS:
                cursor = db.query(ProductEntry.TABLE_NAME, InventoryContract.ProductEntry.projection, null, null, null, null, null);
                break;
            case PRODUCT_ID:
                long id = ContentUris.parseId(uri);
                String arg = ProductEntry._ID + "=?";
                String args[] = {String.valueOf(id)};
                cursor = db.query(ProductEntry.TABLE_NAME, InventoryContract.ProductEntry.projection, arg, args, null, null, null);
                break;
            default:throw new UnsupportedOperationException("Not yet implemented");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Implement this to handle requests to delete one or more rows.
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int _id;

        switch (sUriMatcher.match(uri)){
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                _id = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCTS:
            default:throw new UnsupportedOperationException("Not yet implemented");
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return _id;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int _id;

        switch (sUriMatcher.match(uri)){
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                _id = db.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PRODUCTS:
            default:throw new UnsupportedOperationException("Not yet implemented");
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return _id;
    }
}

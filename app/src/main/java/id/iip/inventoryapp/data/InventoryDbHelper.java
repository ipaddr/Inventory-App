package id.iip.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventoryDbHelper extends SQLiteOpenHelper {

    protected static final String TEXT_TYPE = " TEXT";
    protected static final String INTEGER_TEXT = " INTEGER";
    protected static final String REAL_TEXT = " REAL";
    protected static final String BLOB_TYPE = " BLOB";
    protected static final String COMMA_SEP = ",";

    /** Create statement for table product */
    protected static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + InventoryContract.ProductEntry.TABLE_NAME + " (" +
                    InventoryContract.ProductEntry._ID + " INTEGER PRIMARY KEY," +
                    InventoryContract.ProductEntry.COLUMN_NAME_NAME + InventoryDbHelper.TEXT_TYPE + InventoryDbHelper.COMMA_SEP +
                    InventoryContract.ProductEntry.COLUMN_NAME_QUANTITY + InventoryDbHelper.INTEGER_TEXT + InventoryDbHelper.COMMA_SEP +
                    InventoryContract.ProductEntry.COLUMN_NAME_PRICE + InventoryDbHelper.REAL_TEXT + InventoryDbHelper.COMMA_SEP +
                    InventoryContract.ProductEntry.COLUMN_NAME_COLOR + InventoryDbHelper.INTEGER_TEXT + InventoryDbHelper.COMMA_SEP +
                    InventoryContract.ProductEntry.COLUMN_NAME_SUPPLIER_NAME + InventoryDbHelper.TEXT_TYPE + InventoryDbHelper.COMMA_SEP +
                    InventoryContract.ProductEntry.COLUMN_NAME_SUPPLIER_PHONE + InventoryDbHelper.TEXT_TYPE + InventoryDbHelper.COMMA_SEP +
                    InventoryContract.ProductEntry.COLUMN_NAME_PRODUCT_IMAGE + InventoryDbHelper.BLOB_TYPE +
                    " )";

    /** delete statement for table product */
    protected static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + InventoryContract.ProductEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Inventory.db";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

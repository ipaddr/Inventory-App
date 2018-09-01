package id.iip.inventoryapp.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {

    /** Content authority as same as on the android manifest */
    protected static final String CONTENT_AUTHORITY = "id.iip.inventory.authorrity";
    /** Base Uri which is content:// and + content authority */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /** path to the table */
    protected static final String PATH_PRODUCT = "path_product";

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private InventoryContract() {}

    /* Inner class that defines the table contents */
    public static class ProductEntry implements BaseColumns {
        /** Content uri for table product */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);

        /** Uri return should list or item */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_PRODUCT;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_PRODUCT;

        /** Uri to find spesific product */
        public static Uri buildProductUri(long id){return ContentUris.withAppendedId(CONTENT_URI, id);}

        /** Define the table scehma */
        public static final String TABLE_NAME = "product";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_COLOR = "color";
        public static final String COLUMN_NAME_SUPPLIER_PHONE = "supplierphone";
        public static final String COLUMN_NAME_PRODUCT_IMAGE = "product_image";

        /** projection of this table */
        public static final String [] projection = {_ID, COLUMN_NAME_NAME, COLUMN_NAME_QUANTITY
                , COLUMN_NAME_PRICE, COLUMN_NAME_COLOR, COLUMN_NAME_SUPPLIER_PHONE
                , COLUMN_NAME_PRODUCT_IMAGE};
    }
}

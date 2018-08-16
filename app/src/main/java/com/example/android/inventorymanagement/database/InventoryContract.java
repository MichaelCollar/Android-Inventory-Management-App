package com.example.android.inventorymanagement.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventorymanagement";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventorymanagement";

    private InventoryContract() {
    }

    public static final class ProductEntry implements BaseColumns {
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);
        public static final String TABLE_NAME = "product";
        public static final String _ID = BaseColumns._ID;
        public static final String PRODUCT_NAME = "name";
        public static final String QUANTITY = "quantity";
        public static final String PRICE = "price";
        public static final String SUPPLIER_NAME = "supplier_name";
        public static final String SUPPLIER_PHONE = "supplier_phone";
        public static final String IMAGE = "image";
    }
}
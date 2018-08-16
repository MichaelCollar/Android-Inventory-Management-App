package com.example.android.inventorymanagement.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventorymanagement.database.InventoryContract.ProductEntry;

public class InventoryProvider extends ContentProvider {

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    private static final int INVENTORY = 100;
    private static final int INVENTORY_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    private InventoryDbHelper inventoryDbHelper;

    @Override
    public boolean onCreate() {
        inventoryDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = inventoryDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, "name ASC");
                break;
            case INVENTORY_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, "name ASC");
                break;
            default:
                throw new IllegalArgumentException("Cannot Resolve URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return ProductEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + "with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        String productNameString = values.getAsString(ProductEntry.PRODUCT_NAME);
        if (productNameString == null) {
            throw new IllegalArgumentException("Product requires a name");
        }
        Integer quantityInteger = values.getAsInteger(ProductEntry.QUANTITY);
        if (quantityInteger != null && quantityInteger < 0) {
            throw new IllegalArgumentException("Product requires a valid quantity");
        }
        Integer priceInteger = values.getAsInteger(ProductEntry.PRICE);
        if (priceInteger != null && priceInteger < 0) {
            throw new IllegalArgumentException("Product requires a valid price");
        }
        String supplierNameString = values.getAsString(ProductEntry.SUPPLIER_NAME);
        if (supplierNameString == null) {
            throw new IllegalArgumentException("Product requires a supplier name");
        }
        String supplierPhoneString = values.getAsString(ProductEntry.SUPPLIER_PHONE);
        int phone_length = supplierPhoneString.length();
        if (supplierPhoneString != null && phone_length < 7) {
            throw new IllegalArgumentException("Product requires a valid phone number. Please enter at least 7 digit phone number.");
        }

        SQLiteDatabase db = inventoryDbHelper.getWritableDatabase();
        long id = db.insert(ProductEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = inventoryDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                rowsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri " + uri + "with match " + match);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateInventory(uri, values, selection, selectionArgs);
            case INVENTORY_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInventory(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String productNameString = values.getAsString(ProductEntry.PRODUCT_NAME);
        if (productNameString == null) {
            throw new IllegalArgumentException("Product requires a name");
        }
        Integer quantityInteger = values.getAsInteger(ProductEntry.QUANTITY);
        if (quantityInteger != null && quantityInteger < 0) {
            throw new IllegalArgumentException("Product requires a valid quantity");
        }
        Integer priceInteger = values.getAsInteger(ProductEntry.PRICE);
        if (priceInteger != null && priceInteger < 0) {
            throw new IllegalArgumentException("Product requires a valid price");
        }
        String supplierNameString = values.getAsString(ProductEntry.SUPPLIER_NAME);
        if (supplierNameString == null) {
            throw new IllegalArgumentException("Product requires a supplier name");
        }
        String supplierPhoneString = values.getAsString(ProductEntry.SUPPLIER_PHONE);
        if (supplierPhoneString == null) {
            throw new IllegalArgumentException("Product requires a valid phone number");
        }
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase db = inventoryDbHelper.getWritableDatabase();
        int rowsUpdated = db.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
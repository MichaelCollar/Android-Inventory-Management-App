package com.example.android.inventorymanagement.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventorymanagement.database.InventoryContract.ProductEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                        + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + ProductEntry.PRODUCT_NAME + " TEXT NOT NULL, "
                        + ProductEntry.QUANTITY + " INTEGER DEFAULT 0, "
                        + ProductEntry.PRICE + " INTEGER NOT NULL, "
                        + ProductEntry.IMAGE + " BLOB, "
                        + ProductEntry.SUPPLIER_NAME + " TEXT NOT NULL, "
                        + ProductEntry.SUPPLIER_PHONE + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME + ";");
        onCreate(db);
    }
}


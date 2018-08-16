package com.example.android.inventorymanagement;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventorymanagement.database.InventoryContract.ProductEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 0;
    ProductCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton addProductButton = (FloatingActionButton) findViewById(R.id.add_product_button);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        ListView productsListView = (ListView) findViewById(R.id.products_list);
        View emptyView = findViewById(R.id.empty_view);
        productsListView.setEmptyView(emptyView);

        cursorAdapter = new ProductCursorAdapter(this, null);
        productsListView.setAdapter(cursorAdapter);

        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                Uri uri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_all_products:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.remove_all_products));
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAllProducts();
                    }
                });
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeAllProducts() {
        getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
    }

    public void sellButton(long id, String name, int price, String supplier, int quantityBefore, int quantitySubstracted) {
        Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
        int current = (quantityBefore - quantitySubstracted);

        ContentValues values = new ContentValues();
        values.put(ProductEntry.PRODUCT_NAME, name);
        values.put(ProductEntry.PRICE, price);
        values.put(ProductEntry.SUPPLIER_PHONE, supplier);
        values.put(ProductEntry.QUANTITY, current);

        getContentResolver().update(currentProductUri, values, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection =
                {
                        ProductEntry._ID,
                        ProductEntry.PRODUCT_NAME,
                        ProductEntry.QUANTITY,
                        ProductEntry.PRICE,
                        ProductEntry.SUPPLIER_PHONE
                };
        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
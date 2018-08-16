package com.example.android.inventorymanagement;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventorymanagement.database.InventoryContract.ProductEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 0;


    @BindView(R.id.details_image)
    ImageView productImageView;
    @BindView(R.id.sell_button)
    ImageButton sellButton;
    @BindView(R.id.add_button)
    ImageButton addButton;
    @BindView(R.id.call_button)
    Button callSupplierButton;
    @BindView(R.id.details_product_name)
    TextView productNameTextView;
    @BindView(R.id.details_quantity)
    TextView quantityTextView;
    @BindView(R.id.details_price)
    TextView priceTextView;
    @BindView(R.id.details_supplier_name)
    TextView supplierNameTextView;
    @BindView(R.id.details_supplier_phone)
    TextView supplierPhoneTextView;

    String supplierNameString;
    String productNameString;
    String supplierPhoneString;

    Bitmap imageBitmap;
    private Uri productUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setTitle(getString(R.string.details));

        ButterKnife.bind(this);

        productUri = getIntent().getData();

        callSupplierButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String phoneNumber = supplierPhoneTextView.getText().toString().trim();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                supplierPhoneTextView.getText().toString().trim();
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                if (callIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(callIntent);
                }
            }
        });

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
                final EditText editText = new EditText(v.getContext());
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setMessage(getString(R.string.choose_quantity));
                builder.setView(editText);
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int quantitySubstracted;
                        if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                            quantitySubstracted = 0;
                        } else {
                            quantitySubstracted = Integer.parseInt(editText.getText().toString().trim());
                        }
                        String[] projection =
                                {
                                        ProductEntry._ID,
                                        ProductEntry.PRODUCT_NAME,
                                        ProductEntry.QUANTITY,
                                        ProductEntry.PRICE,
                                        ProductEntry.SUPPLIER_NAME,
                                        ProductEntry.SUPPLIER_PHONE,
                                        ProductEntry.IMAGE
                                };
                        Cursor cursor = getContentResolver().query(productUri, projection, null, null, null);
                        if (cursor.moveToFirst()) {
                            do {
                                int nameIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_NAME);
                                int quantityIndex = cursor.getColumnIndex(ProductEntry.QUANTITY);
                                int priceIndex = cursor.getColumnIndex(ProductEntry.PRICE);
                                int supplierNameIndex = cursor.getColumnIndex(ProductEntry.SUPPLIER_NAME);
                                int supplierPhoneIndex = cursor.getColumnIndex(ProductEntry.SUPPLIER_PHONE);
                                productNameString = cursor.getString(nameIndex);
                                int priceInteger = cursor.getInt(priceIndex);
                                supplierNameString = cursor.getString(supplierNameIndex);
                                supplierPhoneString = cursor.getString(supplierPhoneIndex);
                                int quantityInteger = cursor.getInt(quantityIndex);

                                if (quantityInteger - quantitySubstracted >= 0) {
                                    int currentQuantityInteger = (quantityInteger - quantitySubstracted);
                                    ContentValues values1 = new ContentValues();
                                    values1.put(ProductEntry.PRODUCT_NAME, productNameString);
                                    values1.put(ProductEntry.QUANTITY, currentQuantityInteger);
                                    values1.put(ProductEntry.PRICE, priceInteger);
                                    values1.put(ProductEntry.SUPPLIER_NAME, supplierNameString);
                                    values1.put(ProductEntry.SUPPLIER_PHONE, supplierPhoneString);
                                    getContentResolver().update(productUri, values1, null, null);
                                } else if (quantityInteger - quantitySubstracted < 0) {
                                    Toast.makeText(DetailsActivity.this, "Only " + quantityInteger + " products available !", Toast.LENGTH_SHORT).show();
                                }
                            }
                            while (cursor.moveToNext());
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
                final EditText editText = new EditText(v.getContext());
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setMessage(getString(R.string.choose_quantity));
                builder.setView(editText);
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int quantityAdded;
                        if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                            quantityAdded = 0;
                        } else {
                            quantityAdded = Integer.parseInt(editText.getText().toString().trim());
                        }
                        String[] projection =
                                {
                                        ProductEntry._ID,
                                        ProductEntry.PRODUCT_NAME,
                                        ProductEntry.QUANTITY,
                                        ProductEntry.PRICE,
                                        ProductEntry.SUPPLIER_NAME,
                                        ProductEntry.SUPPLIER_PHONE,
                                        ProductEntry.IMAGE
                                };
                        Cursor cursor = getContentResolver().query(productUri, projection, null, null, null);
                        if (cursor.moveToFirst()) {
                            do {
                                int nameIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_NAME);
                                int quantityIndex = cursor.getColumnIndex(ProductEntry.QUANTITY);
                                int priceIndex = cursor.getColumnIndex(ProductEntry.PRICE);
                                int supplierNameIndex = cursor.getColumnIndex(ProductEntry.SUPPLIER_NAME);
                                int supplierPhoneIndex = cursor.getColumnIndex(ProductEntry.SUPPLIER_PHONE);
                                productNameString = cursor.getString(nameIndex);
                                int quantityInteger = cursor.getInt(quantityIndex);
                                int priceInteger = cursor.getInt(priceIndex);
                                supplierNameString = cursor.getString(supplierNameIndex);
                                supplierPhoneString = cursor.getString(supplierPhoneIndex);
                                int currentQuantityInteger = (quantityInteger + quantityAdded);
                                ContentValues values2 = new ContentValues();
                                values2.put(ProductEntry.PRODUCT_NAME, productNameString);
                                values2.put(ProductEntry.QUANTITY, currentQuantityInteger);
                                values2.put(ProductEntry.PRICE, priceInteger);
                                values2.put(ProductEntry.SUPPLIER_NAME, supplierNameString);
                                values2.put(ProductEntry.SUPPLIER_PHONE, supplierPhoneString);
                                getContentResolver().update(productUri, values2, null, null);
                            }
                            while (cursor.moveToNext());
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        getSupportLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_product:
                showDeleteConfirmationDialog();
                return true;

            case R.id.edit_product:
                Intent intent = new Intent(DetailsActivity.this, EditActivity.class);
                intent.setData(productUri);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.remove_product));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {

        if (productUri != null) {
            int rowDeleted = getContentResolver().delete(productUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.remove_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.remove_success),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection =
                {
                        ProductEntry._ID,
                        ProductEntry.PRODUCT_NAME,
                        ProductEntry.QUANTITY,
                        ProductEntry.PRICE,
                        ProductEntry.SUPPLIER_NAME,
                        ProductEntry.SUPPLIER_PHONE,
                        ProductEntry.IMAGE
                };
        return new CursorLoader(this,
                productUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            do {
                int productNameIndex = data.getColumnIndex(ProductEntry.PRODUCT_NAME);
                int quantityIndex = data.getColumnIndex(ProductEntry.QUANTITY);
                int priceIndex = data.getColumnIndex(ProductEntry.PRICE);
                int supplierNameIndex = data.getColumnIndex(ProductEntry.SUPPLIER_NAME);
                int supplierPhoneIndex = data.getColumnIndex(ProductEntry.SUPPLIER_PHONE);
                int imageIndex = data.getColumnIndex(ProductEntry.IMAGE);
                productNameString = data.getString(productNameIndex);
                int quantityInteger = data.getInt(quantityIndex);
                int priceInteger = data.getInt(priceIndex);
                supplierNameString = data.getString(supplierNameIndex);
                supplierPhoneString = data.getString(supplierPhoneIndex);
                byte[] imageByte = data.getBlob(imageIndex);
                if (imageByte == null) {
                    productImageView.setImageResource(R.drawable.no_image);
                } else {
                    imageBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                    this.productImageView.setImageBitmap(imageBitmap);
                }
                productNameTextView.setText(productNameString);
                quantityTextView.setText(String.valueOf(quantityInteger));
                priceTextView.setText(String.valueOf(priceInteger));
                supplierNameTextView.setText(supplierNameString);
                supplierPhoneTextView.setText(supplierPhoneString);
            }
            while (data.moveToNext());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productImageView.setImageResource(R.drawable.no_image);
        productNameTextView.setText("");
        quantityTextView.setText("");
        priceTextView.setText("");
        supplierNameTextView.setText("");
        supplierPhoneTextView.setText("");
    }
}

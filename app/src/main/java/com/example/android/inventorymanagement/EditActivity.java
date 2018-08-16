package com.example.android.inventorymanagement;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventorymanagement.database.InventoryContract.ProductEntry;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PHOTO = 100;
    private static final int EXISTING_URI = 0;

    @BindView(R.id.no_image)
    ImageView productImageView;
    @BindView(R.id.new_photo_button)
    ImageButton newPhotoButton;
    @BindView(R.id.choose_from_gallery_button)
    ImageButton chooseFromGalleryButton;
    @BindView(R.id.edit_product_name)
    EditText productNameEditText;
    @BindView(R.id.edit_quantity)
    EditText quantityEditText;
    @BindView(R.id.edit_price)
    EditText priceEditText;
    @BindView(R.id.edit_supplier_name)
    EditText supplierNameEditText;
    @BindView(R.id.edit_supplier_phone)
    EditText supplierPhoneEditText;

    Bitmap imageBitmap;
    String productNameString;
    String supplierNameString;
    String supplierPhoneString;
    private Uri productUri;
    private boolean isProductEdited = false;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            isProductEdited = true;
            return false;
        }
    };

    public static byte[] getBytes(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            return stream.toByteArray();
        } else {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ButterKnife.bind(this);

        productUri = getIntent().getData();
        if (productUri == null) {
            setTitle(getString(R.string.add_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_product));
            getSupportLoaderManager().initLoader(EXISTING_URI, null, this);
        }

        productNameEditText.setOnTouchListener(onTouchListener);
        quantityEditText.setOnTouchListener(onTouchListener);
        priceEditText.setOnTouchListener(onTouchListener);
        supplierNameEditText.setOnTouchListener(onTouchListener);
        supplierPhoneEditText.setOnTouchListener(onTouchListener);

        newPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });
        chooseFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    imageBitmap = (Bitmap) data.getExtras().get("data");
                    productImageView.setImageBitmap(imageBitmap);
                }
                break;
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK && data != null) {
                    Uri imageUri = data.getData();
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                    productImageView.setImageBitmap(imageBitmap);
                }
                break;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (productUri == null) {
            MenuItem menuItem = menu.findItem(R.id.remove);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove:
                showDeleteConfirmationDialog();
                return true;
            case R.id.save:
                String productNameString = productNameEditText.getText().toString().trim();
                String quantityString = quantityEditText.getText().toString().trim();
                String priceString = priceEditText.getText().toString().trim();
                String supplierNameString = supplierNameEditText.getText().toString().trim();
                String supplierPhoneString = supplierPhoneEditText.getText().toString().trim();
                getBytes(imageBitmap);

                if (TextUtils.isEmpty(productNameString) || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(supplierNameString) || TextUtils.isEmpty(supplierPhoneString)) {
                    Toast.makeText(this, getString(R.string.empty_field_toast), Toast.LENGTH_LONG).show();
                } else if (TextUtils.getTrimmedLength(supplierPhoneString) < 8) {
                    Toast.makeText(this, getString(R.string.wrong_phone_length_toast), Toast.LENGTH_LONG).show();
                } else if (TextUtils.isDigitsOnly(supplierPhoneString) == false) {
                    Toast.makeText(this, getString(R.string.wrong_phone_toast), Toast.LENGTH_LONG).show();
                } else {
                    saveProduct();
                    finish();
                }
                return true;
            case android.R.id.home:
                if (!isProductEdited) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener buttonClick =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };
                unsavedDataDialog(buttonClick);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an Alert Dialog set the messages and click listeners
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.remove_product));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User confirmed removing the product, so remove the product.
                removeProduct();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // // User canceled removing the product, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the Alert Dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void removeProduct() {
        if (productUri != null) {
            int rowDeleted = getContentResolver().delete(productUri, null, null);
            // Show a toast message depending on whether or not the removal was successful
            if (rowDeleted == 0) {
                // If no data was removed, then show the error message
                Toast.makeText(this, getString(R.string.remove_failed), Toast.LENGTH_LONG).show();
            } else {
                // If data was successfully removed, display a toast.
                Toast.makeText(this, getString(R.string.remove_success),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isProductEdited) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener buttonClick =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };
        unsavedDataDialog(buttonClick);
    }

    private void unsavedDataDialog(DialogInterface.OnClickListener buttonClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.discard_changes));
        builder.setPositiveButton(getString(R.string.yes_discard), buttonClick);
        builder.setNegativeButton(getString(R.string.no_edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveProduct() {
        String stringProductName = productNameEditText.getText().toString().trim();
        String stringQuantity = quantityEditText.getText().toString().trim();
        String stringPrice = priceEditText.getText().toString().trim();
        String stringSupplierName = supplierNameEditText.getText().toString().trim();
        String stringSupplierPhone = supplierPhoneEditText.getText().toString().trim();
        byte[] imageByte = getBytes(imageBitmap);
        if (productUri == null &&
                TextUtils.isEmpty(stringProductName) && TextUtils.isEmpty(stringPrice)
                && TextUtils.isEmpty(stringQuantity) && TextUtils.isEmpty(stringSupplierName)
                && TextUtils.isEmpty(stringSupplierPhone)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ProductEntry.PRODUCT_NAME, stringProductName);
        values.put(ProductEntry.QUANTITY, stringQuantity);
        values.put(ProductEntry.PRICE, stringPrice);
        values.put(ProductEntry.SUPPLIER_NAME, stringSupplierName);
        values.put(ProductEntry.SUPPLIER_PHONE, stringSupplierPhone);
        if (imageByte != null) {
            values.put(ProductEntry.IMAGE, imageByte);
        }

        if (productUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_failed), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_success), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(productUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
            }
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            do {
                int productNameIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_NAME);
                int quantityIndex = cursor.getColumnIndex(ProductEntry.QUANTITY);
                int priceIndex = cursor.getColumnIndex(ProductEntry.PRICE);
                int supplierNameIndex = cursor.getColumnIndex(ProductEntry.SUPPLIER_NAME);
                int supplierPhoneIndex = cursor.getColumnIndex(ProductEntry.SUPPLIER_PHONE);
                int imageIndex = cursor.getColumnIndex(ProductEntry.IMAGE);
                productNameString = cursor.getString(productNameIndex);
                int quantityInteger = cursor.getInt(quantityIndex);
                int priceInteger = cursor.getInt(priceIndex);
                supplierNameString = cursor.getString(supplierNameIndex);
                supplierPhoneString = cursor.getString(supplierPhoneIndex);
                byte[] b = cursor.getBlob(imageIndex);

                if (b == null) {
                    productImageView.setImageResource(R.drawable.no_image);
                } else {
                    Bitmap image = BitmapFactory.decodeByteArray(b, 0, b.length);
                    productImageView.setImageBitmap(image);
                }
                productNameEditText.setText(productNameString);
                quantityEditText.setText(String.valueOf(quantityInteger));
                priceEditText.setText(String.valueOf(priceInteger));
                supplierNameEditText.setText(supplierNameString);
                supplierPhoneEditText.setText(supplierPhoneString);
            }
            while (cursor.moveToNext());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productImageView.setImageResource(R.drawable.no_image);
        productNameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplierNameEditText.setText("");
        supplierPhoneEditText.setText("");
    }
}
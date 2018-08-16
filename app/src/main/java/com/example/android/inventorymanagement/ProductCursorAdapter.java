package com.example.android.inventorymanagement;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventorymanagement.database.InventoryContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {

    private MainActivity mainActivity = new MainActivity();

    public ProductCursorAdapter(MainActivity context, Cursor cursor) {
        super(context, cursor, 0);
        this.mainActivity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final long id;
        final String name;
        final int quantity;
        final String supplier;
        final int price;

        id = cursor.getLong(cursor.getColumnIndex(ProductEntry._ID));
        name = cursor.getString(cursor.getColumnIndex(ProductEntry.PRODUCT_NAME));
        quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.QUANTITY));
        ;
        supplier = cursor.getString(cursor.getColumnIndex(ProductEntry.SUPPLIER_PHONE));
        price = cursor.getInt(cursor.getColumnIndex(ProductEntry.PRICE));

        TextView nameTextView = view.findViewById(R.id.list_name);
        TextView priceTextView = view.findViewById(R.id.list_price);
        TextView quantityTextView = view.findViewById(R.id.list_quantity);
        Button sellButton = view.findViewById(R.id.sell_button);

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                final EditText edittext = new EditText(v.getContext());
                edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setMessage(mainActivity.getString(R.string.choose_quantity));
                builder.setTitle(mainActivity.getString(R.string.product_quantity_hint));
                builder.setView(edittext);
                builder.setPositiveButton(mainActivity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int subtractedValue;
                        if (TextUtils.isEmpty(edittext.getText().toString().trim())) {
                            subtractedValue = 0;
                        } else {
                            subtractedValue = Integer.parseInt(edittext.getText().toString().trim());
                        }
                        if (quantity - subtractedValue >= 0) {
                            mainActivity.sellButton(id, name, price, supplier, quantity, subtractedValue);
                            Toast.makeText(mainActivity, "The sale was successful!", Toast.LENGTH_SHORT).show();
                        } else if (quantity - subtractedValue < 0) {
                            Toast.makeText(mainActivity, "You do not have so many products. Only " + quantity + " products are currently available.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton(mainActivity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
        });
        nameTextView.setText(name);
        priceTextView.setText(String.valueOf(price));
        quantityTextView.setText(String.valueOf(quantity));
    }
}
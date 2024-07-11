package com.example.todoviejovrg;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VentasActivity extends AppCompatActivity {

    private EditText productCodeEditText, quantityEditText;
    private Button sellButton;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);

        productCodeEditText = findViewById(R.id.productCodeEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        sellButton = findViewById(R.id.sellButton);

        mDatabase = FirebaseDatabase.getInstance().getReference("productos");

        sellButton.setOnClickListener(view -> sellProduct());
    }

    private void sellProduct() {
        String productCode = productCodeEditText.getText().toString().trim();
        String quantityStr = quantityEditText.getText().toString().trim();

        if (TextUtils.isEmpty(productCode) || TextUtils.isEmpty(quantityStr)) {
            Toast.makeText(VentasActivity.this, "Please enter product code and quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);

        mDatabase.child(productCode).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        if (product.getStock() >= quantity) {
                            int newStock = product.getStock() - quantity;
                            mDatabase.child(productCode).child("stock").setValue(newStock);
                            Toast.makeText(VentasActivity.this, "Product sold successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VentasActivity.this, "No hay stock suficiente", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(VentasActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(VentasActivity.this, "Failed to retrieve product", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class Product {
        private String name;
        private double price;
        private int stock;

        public Product() {
            // Default constructor required for calls to DataSnapshot.getValue(Product.class)
        }

        public Product(String name, double price, int stock) {
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getStock() {
            return stock;
        }
    }
}

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

public class ProductoActivity extends AppCompatActivity {

    private EditText productCodeEditText, productNameEditText, productPriceEditText, productStockEditText;
    private Button createButton, searchButton, updateButton, deleteButton;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        productCodeEditText = findViewById(R.id.productCodeEditText);
        productNameEditText = findViewById(R.id.productNameEditText);
        productPriceEditText = findViewById(R.id.productPriceEditText);
        productStockEditText = findViewById(R.id.productStockEditText);
        createButton = findViewById(R.id.createButton);
        searchButton = findViewById(R.id.searchButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);

        mDatabase = FirebaseDatabase.getInstance().getReference("productos");

        createButton.setOnClickListener(view -> createProduct());
        searchButton.setOnClickListener(view -> searchProduct());
        updateButton.setOnClickListener(view -> updateProduct());
        deleteButton.setOnClickListener(view -> deleteProduct());
    }

    private void createProduct() {
        String productCode = productCodeEditText.getText().toString().trim();
        String productName = productNameEditText.getText().toString().trim();
        String productPriceStr = productPriceEditText.getText().toString().trim();
        String productStockStr = productStockEditText.getText().toString().trim();

        if (TextUtils.isEmpty(productCode) || TextUtils.isEmpty(productName) || TextUtils.isEmpty(productPriceStr) || TextUtils.isEmpty(productStockStr)) {
            Toast.makeText(ProductoActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double productPrice = Double.parseDouble(productPriceStr);
        int productStock = Integer.parseInt(productStockStr);

        Product product = new Product(productName, productPrice, productStock);
        mDatabase.child(productCode).setValue(product)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProductoActivity.this, "Product created successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductoActivity.this, "Failed to create product", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void searchProduct() {
        String productCode = productCodeEditText.getText().toString().trim();

        if (TextUtils.isEmpty(productCode)) {
            Toast.makeText(ProductoActivity.this, "Please enter product code", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase.child(productCode).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        productNameEditText.setText(product.getName());
                        productPriceEditText.setText(String.valueOf(product.getPrice()));
                        productStockEditText.setText(String.valueOf(product.getStock()));
                    }
                } else {
                    Toast.makeText(ProductoActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProductoActivity.this, "Failed to retrieve product", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProduct() {
        String productCode = productCodeEditText.getText().toString().trim();
        String productName = productNameEditText.getText().toString().trim();
        String productPriceStr = productPriceEditText.getText().toString().trim();
        String productStockStr = productStockEditText.getText().toString().trim();

        if (TextUtils.isEmpty(productCode) || TextUtils.isEmpty(productName) || TextUtils.isEmpty(productPriceStr) || TextUtils.isEmpty(productStockStr)) {
            Toast.makeText(ProductoActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double productPrice = Double.parseDouble(productPriceStr);
        int productStock = Integer.parseInt(productStockStr);

        Product product = new Product(productName, productPrice, productStock);
        mDatabase.child(productCode).setValue(product)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProductoActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductoActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteProduct() {
        String productCode = productCodeEditText.getText().toString().trim();

        if (TextUtils.isEmpty(productCode)) {
            Toast.makeText(ProductoActivity.this, "Please enter product code", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase.child(productCode).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProductoActivity.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                        productNameEditText.setText("");
                        productPriceEditText.setText("");
                        productStockEditText.setText("");
                    } else {
                        Toast.makeText(ProductoActivity.this, "Failed to delete product", Toast.LENGTH_SHORT).show();
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

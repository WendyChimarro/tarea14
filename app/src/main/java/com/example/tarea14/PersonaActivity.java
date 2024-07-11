package com.example.todoviejovrg;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PersonaActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, addressEditText;
    private Button updateButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persona);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("personas");

        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        updateButton = findViewById(R.id.updateButton);

        updateButton.setOnClickListener(view -> updateUserInfo());
    }

    private void updateUserInfo() {
        String userId = mAuth.getCurrentUser().getUid();
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(PersonaActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        User updatedUser = new User(name, phone, address);
        mDatabase.child(userId).setValue(updatedUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PersonaActivity.this, "Info updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PersonaActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    static class User {
        public String name;
        public String phone;
        public String address;

        public User(String name, String phone, String address) {
            this.name = name;
            this.phone = phone;
            this.address = address;
        }
    }
}

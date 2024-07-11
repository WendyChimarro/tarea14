package com.example.todoviejovrg;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private Spinner optionsSpinner;
    private Button updateButton, productsButton, productListButton;
    private RecyclerView usersRecyclerView;
    private UsersAdapter usersAdapter;
    private List<User> userList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        optionsSpinner = findViewById(R.id.optionsSpinner);
        updateButton = findViewById(R.id.updateButton);
        productsButton = findViewById(R.id.productsButton);
        productListButton = findViewById(R.id.productListButton);
        usersRecyclerView = findViewById(R.id.usersRecyclerView);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("personas");  // Verifica la ruta correcta de tu base de datos

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.login_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        optionsSpinner.setAdapter(adapter);

        // Set welcome message
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String welcomeMessage = "Welcome, " + user.getEmail() + "!";
            welcomeTextView.setText(welcomeMessage);
        }

        updateButton.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, PersonaActivity.class);
            startActivity(intent);
        });

        productsButton.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, ProductoActivity.class);
            startActivity(intent);
        });

        productListButton.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, ListaProductosActivity.class);
            startActivity(intent);
        });

        // Configurar RecyclerView y adaptador
        userList = new ArrayList<>();
        usersAdapter = new UsersAdapter(userList);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(usersAdapter);

        // Obtener y mostrar la lista de usuarios
        fetchUsers();
    }

    private void fetchUsers() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(WelcomeActivity.this, "Failed to load users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

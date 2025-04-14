package com.example.muszakibolt.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.muszakibolt.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 42;
    private EditText userEmailEdit;
    private EditText userPasswordEdit;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        userEmailEdit = findViewById(R.id.editEmail);
        userPasswordEdit = findViewById(R.id.editPassword);
        mAuth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        Log.i(LOG_TAG, "onCreate");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void loginWithEmail(View view) {
        String email = userEmailEdit.getText().toString();
        String password = userPasswordEdit.getText().toString();

        if(email.isEmpty() || password.isEmpty()){
            Log.e(LOG_TAG, "A megadott mezők valamelyike üres!");
            Toast.makeText(MainActivity.this, "A megadott mezők valamelyike üres!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.d(LOG_TAG, "Felhasználó sikeresen bejelentkeztetve");
                forwardToShop();
            } else {
                Log.d(LOG_TAG, "Sikertelen bejelentkezés");
                String error = task.getException().getMessage() == null ? task.getException().getMessage() : "";
                Toast.makeText(MainActivity.this, "Sikertelen bejelentkezés: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void forwardToShop() {
        Log.d(LOG_TAG,"Vásárlás");
        Intent intent = new Intent(this, ShoppingActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }
    public void loginAsGuest(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.d(LOG_TAG, "Anoním felhasználó sikeresen bejelentkezve");
                forwardToShop();
            } else {
                Log.d(LOG_TAG, "Anoním felhasználó sikeresen bejelentkezve");
                Toast.makeText(MainActivity.this, "Sikertelen bejelentkezés: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }
    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userEmail", userEmailEdit.getText().toString());
        editor.putString("userPassword", userPasswordEdit.getText().toString());
        editor.apply();

        Log.i(LOG_TAG, "onPause");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return false;
    }

}
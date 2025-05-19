package com.example.muszakibolt.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.muszakibolt.R;
import com.example.muszakibolt.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private EditText userEmailEdit;
    private EditText userPasswordEdit;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        userEmailEdit = findViewById(R.id.editEmail);
        userPasswordEdit = findViewById(R.id.editPassword);
        mAuth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions();
        }

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
                users = firestore.collection("users");
                users.document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                User user = document.toObject(User.class);
                                Log.d(LOG_TAG,user.toString());
                                forwardToShop(user);
                            } else {
                                Toast.makeText(MainActivity.this, "Hiba történt a felhasználó lekérdezése közben!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Hiba történt a felhasználó lekérdezése közben!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Log.d(LOG_TAG, "Sikertelen bejelentkezés");
                String error = task.getException().getMessage() == null ? task.getException().getMessage() : "";
                Toast.makeText(MainActivity.this, "Sikertelen bejelentkezés: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void forwardToShop(User user) {
        Log.d(LOG_TAG,"Vásárlás");
        Intent intent = new Intent(this, ShoppingActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
    public void loginAsGuest(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.d(LOG_TAG, "Anoním felhasználó sikeresen bejelentkezve");
                User user = new User();
                user.setEmail("anomymus");
                forwardToShop(user);
            } else {
                Log.d(LOG_TAG, "Anoním felhasználó sikeresen bejelentkezve");
                Toast.makeText(MainActivity.this, "Sikertelen bejelentkezés: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
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
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestPermissions() {
        String[] permissions = { Manifest.permission.POST_NOTIFICATIONS,Manifest.permission.INTERNET};
        List<String> permissionsToRequest = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    123
            );
        }
    }
}
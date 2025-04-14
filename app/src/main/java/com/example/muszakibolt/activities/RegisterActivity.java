package com.example.muszakibolt.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.muszakibolt.R;
import com.example.muszakibolt.models.User;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 42;
    EditText userNameEdit;
    EditText userEmailEdit;
    EditText passwordEdit;
    EditText passwordConfirmEdit;
    EditText phoneEdit;
    EditText addressEdit;
    RadioGroup accountTypeGroup;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != SECRET_KEY) {
            finish();
        }

        userNameEdit = findViewById(R.id.userNameEdit);
        userEmailEdit = findViewById(R.id.userEmailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        passwordConfirmEdit = findViewById(R.id.passwordAgainEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        addressEdit = findViewById(R.id.addressEdit);
        accountTypeGroup = findViewById(R.id.accountTypeGroup);
        accountTypeGroup.check(R.id.buyer);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String username = preferences.getString("userName", "");
        String password = preferences.getString("userRegPassword", "");
        String email = preferences.getString("userRegEmail", "");
        String phone = preferences.getString("userPhone", "");
        String address = preferences.getString("userAddress", "");
        int account = preferences.getInt("userAccount", 0);

        userNameEdit.setText(username);
        passwordEdit.setText(password);
        passwordConfirmEdit.setText(password);
        userEmailEdit.setText(email);
        phoneEdit.setText(phone);
        addressEdit.setText(address);
        accountTypeGroup.check(account);
        mAuth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG, "onCreate");
    }
    public void register(View view) {
        String userName = userNameEdit.getText().toString();
        String email = userEmailEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String passwordConfirm = passwordConfirmEdit.getText().toString();
        String phone = phoneEdit.getText().toString();
        String address = addressEdit.getText().toString();

        int accountTypeId = accountTypeGroup.getCheckedRadioButtonId();
        View radioButton = accountTypeGroup.findViewById(accountTypeId);
        int id = accountTypeGroup.indexOfChild(radioButton);
        String accountType =  ((RadioButton)accountTypeGroup.getChildAt(id)).getText().toString();

        if(userName.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()
                || phone.isEmpty() || address.isEmpty() || accountType.isEmpty()){
            Log.e(LOG_TAG, "A megadott mezők valamelyike üres!");
            Toast.makeText(RegisterActivity.this, "A megadott mezők valamelyike üres!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            Log.e(LOG_TAG, "Nem egyenlő a jelszó és a megerősítése!");
            Toast.makeText(RegisterActivity.this, "Nem egyenlő a jelszó és a megerősítése!", Toast.LENGTH_LONG).show();
            return;
        }

        if(password.length() < 8){
            Log.e(LOG_TAG, "Túl rövid a jelszó! Legalább 8 karakter");
            Toast.makeText(RegisterActivity.this, "Túl rövid a jelszó! Legalább 8 karakter", Toast.LENGTH_LONG).show();
            return;
        }

        User user = new User();

        user.setUsername(userName);
        user.setEmail(email);
        user.setAddress(address);
        user.setPhone(phone);
        user.setAccountType(accountType);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.d(LOG_TAG, "Felhasználó sikeresen regisztrálva!");
                Log.i(LOG_TAG, "Regisztrált: " + user.toString());
                forwardToShop();
            } else {
                Log.d(LOG_TAG, "Sikertelen regisztráció!");
                Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    public void cancel(View view) {
        finish();
    }
    private void forwardToShop() {
        Intent intent = new Intent(this, ShoppingActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }
    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", userNameEdit.getText().toString());
        editor.putString("userRegPassword", passwordEdit.getText().toString());
        editor.putString("userRegEmail",userEmailEdit.getText().toString());
        editor.putString("userPhone",phoneEdit.getText().toString());
        editor.putString("userAddress",addressEdit.getText().toString());
        editor.putInt("userAccount",accountTypeGroup.getCheckedRadioButtonId());
        editor.apply();

        Log.i(LOG_TAG, "onPause");
    }
}

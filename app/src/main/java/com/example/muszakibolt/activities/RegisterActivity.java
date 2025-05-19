package com.example.muszakibolt.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;

import com.example.muszakibolt.R;
import com.example.muszakibolt.models.User;
import com.google.firebase.auth.FirebaseAuth;


import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    EditText userNameEdit;
    EditText userEmailEdit;
    EditText passwordEdit;
    EditText passwordConfirmEdit;
    EditText phoneEdit;
    EditText addressEdit;
    TextView accountTypeText;
    RadioGroup accountTypeGroup;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();;
    private CollectionReference users;
    private NotificationHelper notificationHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userNameEdit = findViewById(R.id.userNameEdit);
        userEmailEdit = findViewById(R.id.userEmailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        passwordConfirmEdit = findViewById(R.id.passwordAgainEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        addressEdit = findViewById(R.id.addressEdit);
        accountTypeText = findViewById(R.id.accountTypeText);
        accountTypeGroup = findViewById(R.id.accountTypeGroup);
        accountTypeGroup.check(R.id.buyer);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String username = preferences.getString("userName", "");
        String password = "";
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

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.row_slide);
        userNameEdit.startAnimation(animation);
        passwordEdit.startAnimation(animation);
        passwordConfirmEdit.startAnimation(animation);
        userEmailEdit.startAnimation(animation);
        phoneEdit.startAnimation(animation);
        addressEdit.startAnimation(animation);
        accountTypeText.startAnimation(animation);
        accountTypeGroup.startAnimation(animation);
        accountTypeGroup.check(R.id.buyer);

        notificationHelper = new NotificationHelper(this);

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

        if(!isValidEmail(email)){
            Log.e(LOG_TAG, "Nem email");
            Toast.makeText(RegisterActivity.this, "Nem email címet adtál meg!", Toast.LENGTH_LONG).show();
            return;
        }

        User user = new User();

        user.setUsername(userName);
        user.setEmail(email);
        user.setAddress(address);
        user.setPhone(phone);
        user.setAccountType(accountType);
        users = firestore.collection("users");
        users.where(Filter.or(
                Filter.equalTo("username",user.getUsername()),
                Filter.equalTo("email",user.getEmail())
                )).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean exists = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció: Ilyen felhasználó már létezik!", Toast.LENGTH_LONG).show();
                            exists = true;
                        }
                        if(!exists){
                            Map<String, Object> addUser = new HashMap<>();
                            addUser.put("username", user.getUsername());
                            addUser.put("email", user.getEmail());
                            addUser.put("phone", user.getPhone());
                            addUser.put("address", user.getAddress());
                            addUser.put("accountType", user.getAccountType());

                            users.document(email)
                                    .set(addUser)
                                    .addOnSuccessListener(documentReference -> {
                                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, mtask -> {
                                            if(task.isSuccessful()){
                                                Log.d(LOG_TAG, "Felhasználó sikeresen regisztrálva!");
                                                Log.i(LOG_TAG, "Regisztrált: " + user.toString());
                                                notificationHelper.send("Sikeres regisztráció: "+user.getEmail());
                                                forwardToShop(user);
                                            } else {
                                                Log.d(LOG_TAG, "Sikertelen regisztráció!");
                                                Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(LOG_TAG, "Error adding document", e);
                                        Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció!", Toast.LENGTH_LONG).show();
                                    });
                        }
                    } else {
                        Log.w(LOG_TAG, "Sikertelen regisztráció", task.getException());
                        Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció!", Toast.LENGTH_LONG).show();
                    }
                });
    }
    public void cancel(View view) {
        finish();
    }
    private void forwardToShop(User user) {
        Intent intent = new Intent(this, ShoppingActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", userNameEdit.getText().toString());
        editor.putString("userRegEmail",userEmailEdit.getText().toString());
        editor.putString("userPhone",phoneEdit.getText().toString());
        editor.putString("userAddress",addressEdit.getText().toString());
        editor.putInt("userAccount",accountTypeGroup.getCheckedRadioButtonId());
        editor.apply();

        Log.i(LOG_TAG, "onPause");
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}

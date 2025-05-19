package com.example.muszakibolt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.muszakibolt.R;
import com.example.muszakibolt.models.Article;
import com.example.muszakibolt.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class AddActivity extends AppCompatActivity {
    private static final String LOG_TAG = AddActivity.class.getName();
    private FirebaseUser user;
    private User userdata;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference articleRef;
    EditText infoEdit;
    EditText nameEdit;
    EditText priceEdit;
    RatingBar ratingEdit;
    Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        userdata =(User) getIntent().getSerializableExtra("user");
        Log.d(LOG_TAG,userdata.toString());

        if (user != null) {
            Log.d(LOG_TAG, "Hitelesített felhasználó!");
        } else {
            Log.d(LOG_TAG, "Hitelesítetlen felhasználó!");
            finish();
        }
        articleRef = firestore.collection("articles");
        nameEdit = findViewById(R.id.addName);
        infoEdit = findViewById(R.id.addInfo);
        priceEdit = findViewById(R.id.addPrice);
        ratingEdit = findViewById(R.id.addRating);

    }
    public void cancel(View view){
        Intent i = new Intent(this, ShoppingActivity.class);
        i.putExtra("user",userdata);
        startActivity(i);
        finish();
    }
    public void add(View view){
        String name = nameEdit.getText().toString();
        String info = infoEdit.getText().toString();
        String price = priceEdit.getText().toString();
        float rating = ratingEdit.getRating();

        if(name.isEmpty() || info.isEmpty() || price.isEmpty()){
            Log.e(LOG_TAG, "A megadott mezők valamelyike üres!");
            Toast.makeText(AddActivity.this, "A megadott mezők valamelyike üres!", Toast.LENGTH_LONG).show();
            return;
        }
        Article newArticle = new Article();
        newArticle.setName(name);
        newArticle.setInfo(info);
        newArticle.setPrice(price+" Ft");
        newArticle.setRating(rating);
        newArticle.setImage(0);
        articleRef.whereEqualTo("name",newArticle.getName()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean exists = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Toast.makeText(AddActivity.this, "Sikertelen hozzáadás: Ilyen áru már létezik!", Toast.LENGTH_LONG).show();
                            exists = true;
                        }
                        if (!exists) {
                            articleRef.document(name)
                                    .set(newArticle)
                                    .addOnSuccessListener(documentReference -> {
                                        Log.d(LOG_TAG, "Áru sikeresen hozzáadva!");
                                        Intent i = new Intent(this, ShoppingActivity.class);
                                        i.putExtra("user", userdata);
                                        startActivity(i);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(LOG_TAG, "Áru hozzáadása sikertelen", e);
                                        Toast.makeText(AddActivity.this, "Áru hozzáadása sikertelen!", Toast.LENGTH_LONG).show();
                                    });
                        }
                    }
        });
    }
}
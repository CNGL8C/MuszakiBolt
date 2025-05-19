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

public class UpdateActivity extends AppCompatActivity {
    private static final String LOG_TAG = UpdateActivity.class.getName();
    private FirebaseUser user;
    private User userdata;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference articleRef;
    EditText infoEdit;
    EditText priceEdit;
    RatingBar ratingEdit;

    Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);
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
        infoEdit = findViewById(R.id.updateInfo);
        priceEdit = findViewById(R.id.updatePrice);
        ratingEdit = findViewById(R.id.updateRating);
        article = (Article) getIntent().getSerializableExtra("article");
        infoEdit.setText(article.getInfo());
        String price = article.getPrice();
        StringBuilder newprice = new StringBuilder();
        for(int i=0;i<price.length();i++){
            if(price.charAt(i) >= '0' && price.charAt(i) <= '9'){
                newprice.append(price.charAt(i));
            }
        }
        priceEdit.setText(newprice.toString());
        ratingEdit.setRating(article.getRating());
    }

    public void cancel(View view){
        Intent i = new Intent(this, ShoppingActivity.class);
        i.putExtra("user",userdata);
        startActivity(i);
        finish();
    }

    public void update(View view){
        String info = infoEdit.getText().toString();
        String price = priceEdit.getText().toString();
        Float rating = ratingEdit.getRating();

        if(info.isEmpty() || price.isEmpty()){
            Log.e(LOG_TAG, "A megadott mezők valamelyike üres!");
            Toast.makeText(UpdateActivity.this, "A megadott mezők valamelyike üres!", Toast.LENGTH_LONG).show();
            return;
        }
        Article newArticle = new Article();
        newArticle.setName(article.getName());
        newArticle.setInfo(info);
        newArticle.setPrice(price+" Ft");
        newArticle.setRating(rating);
        newArticle.setImage(article.getImage());
        articleRef.document(article.getName())
                .set(newArticle)
                .addOnSuccessListener(aVoid -> {
                    Log.d(LOG_TAG, "Áru sikeresen frissítve!");
                    Intent i = new Intent(this, ShoppingActivity.class);
                    i.putExtra("user",userdata);
                    startActivity(i);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(LOG_TAG, "Áru frissítése sikertelen", e);
                    Toast.makeText(UpdateActivity.this,"Áru frissítése sikertelen!", Toast.LENGTH_LONG).show();
                });
    }
}
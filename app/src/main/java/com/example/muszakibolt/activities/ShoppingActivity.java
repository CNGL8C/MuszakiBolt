package com.example.muszakibolt.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muszakibolt.R;
import com.example.muszakibolt.adapters.ArticleAdapter;
import com.example.muszakibolt.models.Article;
import com.example.muszakibolt.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ShoppingActivity extends AppCompatActivity {
    private static final String LOG_TAG = ShoppingActivity.class.getName();
    private FirebaseUser user;
    private User userdata;
    private RecyclerView rView;
    private ArrayList<Article> articles;
    private ArticleAdapter articleAdapter;
    private FrameLayout redCircle;
    private TextView countTextView;
    private int cartItems = 0;
    private int gridNumber = 1;
    private boolean viewRow = false;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference articleRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        Log.i(LOG_TAG, "onCreate");

        user = FirebaseAuth.getInstance().getCurrentUser();
        userdata =(User) getIntent().getSerializableExtra("user");
        Log.d(LOG_TAG,userdata.toString());
        cartItems = userdata.getCart().size();

        if (user != null) {
            Log.d(LOG_TAG, "Hitelesített felhasználó!");
        } else {
            Log.d(LOG_TAG, "Hitelesítetlen felhasználó!");
            finish();
        }

        rView = findViewById(R.id.recyclerView);
        rView.setLayoutManager(new GridLayoutManager(
                this, gridNumber));

        articles = new ArrayList<>();
        articleAdapter = new ArticleAdapter(this, articles);
        rView.setAdapter(articleAdapter);
        articleRef = firestore.collection("articles");
        getData();
    }
    private void initialize() {
        Log.d(LOG_TAG,"initialize");
        String[] articlesList = getResources().getStringArray(R.array.article_names);
        String[] articlesInfo = getResources().getStringArray(R.array.article_infos);
        String[] articlesPrice = getResources().getStringArray(R.array.article_prices);
        TypedArray articlesImages = getResources().obtainTypedArray(R.array.article_images);
        TypedArray articlesRatings = getResources().obtainTypedArray(R.array.article_ratings);

        articles.clear();
        for (int i = 0; i < articlesList.length; i++) {
            Article addArticle = new Article(
                    articlesList[i],
                    articlesInfo[i],
                    articlesPrice[i],
                    articlesRatings.getFloat(i, 0),
                    articlesImages.getResourceId(i, 0));
            articles.add(addArticle);
        }
        for (int i=0;i<articles.size();i++){
            if(articleRef.document(articles.get(i).getName()).set(articles.get(i)).isSuccessful()){
                continue;
            }
        }

        articlesImages.recycle();
    }
    private void getData() {
        articles.clear();
        articleRef.orderBy("name").whereNotEqualTo("name","debug").limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Article article = document.toObject(Article.class);
                articles.add(article);
            }

            if (articles.isEmpty()) {
                initialize();
            }

            articleAdapter.notifyDataSetChanged();
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Log.d(LOG_TAG,"onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.shop_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView =(SearchView) menuItem.getActionView();
        assert searchView != null;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                articleAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG,"onOptionsItemSelected: "+item.getItemId());
        if(item.getItemId() == R.id.logout){
            Log.d(LOG_TAG, "Kijelentkezés!");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        } else if(item.getItemId() == R.id.cart){
            Log.d(LOG_TAG, "Kosár");
            forwardToCart(userdata);
            return true;
        } else if(item.getItemId() == R.id.view_selector){
            if (viewRow) {
                changeSpanCount(item, R.drawable.icon_view_grid, 1);
            } else {
                changeSpanCount(item, R.drawable.icon_view_row, 2);
            }
            return true;
        } else if(item.getItemId() == R.id.add_aritcle){
            if("Eladó".equals(userdata.getAccountType())) {
                Intent intent = new Intent(this, AddActivity.class);
                intent.putExtra("user", userdata);
                startActivity(intent);
            } else {
                Toast.makeText(ShoppingActivity.this, "Ez csak eladóknak megengedett", Toast.LENGTH_LONG).show();
            }
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }
    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        Log.d(LOG_TAG,"changeSpanCount");
        viewRow = !viewRow;
        Log.d(LOG_TAG,"viewRow: "+ viewRow);
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) rView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(LOG_TAG,"onPrepareOptionsMenu");
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        assert rootView != null;
        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);
        cartItems = userdata.getCart().size();
        if (0 < cartItems) {
            countTextView.setText(String.valueOf(cartItems));
        } else {
            countTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }
    public void deleteItem(Article article) {
        Log.d(LOG_TAG,"deleteItem");
        if("Eladó".equals(userdata.getAccountType())){
            articleRef.document(article.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(ShoppingActivity.this, "Tárgy sikeresen törölve!", Toast.LENGTH_LONG).show();
                    articles.remove(article);
                    articleAdapter.notifyDataSetChanged();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ShoppingActivity.this, "Tárgy törlése sikertelen!", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(ShoppingActivity.this, "Ez csak eladóknak megengedett", Toast.LENGTH_LONG).show();
        }
    }
    public void editItem(Article article) {
        if("Eladó".equals(userdata.getAccountType())){
            Intent intent = new Intent(this, UpdateActivity.class);
            intent.putExtra("user", userdata);
            intent.putExtra("article",article);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(ShoppingActivity.this, "Ez csak eladóknak megengedett", Toast.LENGTH_LONG).show();
        }
    }
    public void updateCartIcon(Article article) {
        Log.d(LOG_TAG,"updateAlertIcon");
        userdata.getCart().add(article);
        cartItems = userdata.getCart().size();
        if (0 < cartItems) {
            countTextView.setText(String.valueOf(cartItems));
        } else {
            countTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);
    }

    private void forwardToCart(User userdata){
        if("anomymus".equals(userdata.getEmail())){
            Log.e(LOG_TAG, "Ez csak emaillel bejelentkezett felhasználóknak lehetséges!");
            Toast.makeText(ShoppingActivity.this, "Anoním felhasználónak ez nem lehetséges!", Toast.LENGTH_LONG).show();
        } else{
            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra("user", userdata);
            startActivity(intent);
            finish();
        }
    }
}

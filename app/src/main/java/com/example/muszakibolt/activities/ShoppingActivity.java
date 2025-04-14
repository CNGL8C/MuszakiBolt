package com.example.muszakibolt.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muszakibolt.R;
import com.example.muszakibolt.adapters.ArticleAdapter;
import com.example.muszakibolt.models.Article;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ShoppingActivity extends AppCompatActivity {
    private static final String LOG_TAG = ShoppingActivity.class.getName();
    private static final int SECRET_KEY = 42;
    private static final String PREF_KEY = ShoppingActivity.class.getPackage().toString();
    private SharedPreferences preferences;
    private FirebaseUser user;
    private RecyclerView rView;
    private ArrayList<Article> articles;
    private ArticleAdapter articleAdapter;
    private FrameLayout redCircle;
    private TextView countTextView;
    private int cartItems = 0;
    private int gridNumber = 1;
    private Integer itemLimit = 5;
    private boolean viewRow = false;
    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        Log.i(LOG_TAG, "onCreate");

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if (secret_key != SECRET_KEY) {
            finish();
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Hitelesített felhasználó!");
        } else {
            Log.d(LOG_TAG, "Hitelesítetlen felhasználó!");
            finish();
        }
        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        rView = findViewById(R.id.recyclerView);
        rView.setLayoutManager(new GridLayoutManager(
                this, gridNumber));

        articles = new ArrayList<>();
        articleAdapter = new ArticleAdapter(this, articles);
        rView.setAdapter(articleAdapter);
        initialize();
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
            articles.add(new Article(
                    articlesList[i],
                    articlesInfo[i],
                    articlesPrice[i],
                    articlesRatings.getFloat(i, 0),
                    articlesImages.getResourceId(i, 0),
                    0));
        }

        articlesImages.recycle();
        articleAdapter.notifyDataSetChanged();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Log.d(LOG_TAG,"onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.shop_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView =(SearchView) menuItem.getActionView();
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
        switch (item.getItemId()) {
            case 2131296535:
                Log.d(LOG_TAG, "Kijelentkezés!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case 2131296695:
                Log.d(LOG_TAG, "Beállítások");
                return true;
            case 2131296378:
                Log.d(LOG_TAG, "Kosár");
                return true;
            case 2131296795:
                if (viewRow) {
                    changeSpanCount(item, R.drawable.icon_view_grid, 1);
                } else {
                    changeSpanCount(item, R.drawable.icon_view_row, 2);
                }
                return true;
            default:
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

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

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
        cartItems = Math.max(0,cartItems-1);
        if (0 < cartItems) {
            countTextView.setText(String.valueOf(cartItems));
        } else {
            countTextView.setText("");
        }
        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);
    }
    public void updateAlertIcon(Article article) {
        Log.d(LOG_TAG,"updateAlertIcon");
        cartItems++;
        if (0 < cartItems) {
            countTextView.setText(String.valueOf(cartItems));
        } else {
            countTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);
    }
}

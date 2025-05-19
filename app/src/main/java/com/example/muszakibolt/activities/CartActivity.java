package com.example.muszakibolt.activities;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muszakibolt.R;
import com.example.muszakibolt.adapters.CartItemAdapter;
import com.example.muszakibolt.models.Article;
import com.example.muszakibolt.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedList;

public class CartActivity extends AppCompatActivity {
    private static final String LOG_TAG = CartActivity.class.getName();
    private FirebaseUser user;
    private User userdata;
    private RecyclerView rView;
    private LinkedList<Article> articles;
    private CartItemAdapter cartItemAdapter;
    private JobScheduler jobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
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
        rView = findViewById(R.id.recyclerView);
        rView.setLayoutManager(new GridLayoutManager(
                this, 1));
        articles = userdata.getCart();
        cartItemAdapter = new CartItemAdapter(this, articles);
        rView.setAdapter(cartItemAdapter);
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
    }
    public void deleteItem(Article article) {
        Log.d(LOG_TAG,"deleteItem");
        userdata.getCart().remove(article);
        cartItemAdapter.notifyDataSetChanged();
    }
    public void buyItems(View view){
        if(!userdata.getCart().isEmpty()){
            articles.clear();
            userdata.getCart().clear();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                setNotificationScheduler();
            }
            Intent i = new Intent(this, ShoppingActivity.class);
            i.putExtra("user",userdata);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(CartActivity.this, "Üres a kosarad!", Toast.LENGTH_LONG).show();
        }
    }
    public void cancel(View view){
        Intent i = new Intent(this, ShoppingActivity.class);
        i.putExtra("user",userdata);
        startActivity(i);
        finish();
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    private void setNotificationScheduler(){
        int hardDeadline = 10000;
        int networkType = JobInfo.NETWORK_TYPE_CELLULAR;
        boolean notLowBattery = true;

        ComponentName serviceName = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            serviceName = new ComponentName(getPackageName(), NotificationJobService.class.getName());
        }
        assert serviceName != null;
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceName)
                .setOverrideDeadline(hardDeadline)
                .setRequiredNetworkType(networkType)
                .setRequiresBatteryNotLow(notLowBattery);
        JobInfo jobInfo = builder.build();
        jobScheduler.schedule(jobInfo);
    }
}
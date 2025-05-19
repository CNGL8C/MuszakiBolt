package com.example.muszakibolt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muszakibolt.R;
import com.example.muszakibolt.activities.CartActivity;
import com.example.muszakibolt.models.Article;

import java.util.LinkedList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>
        implements Filterable {
    private LinkedList<Article> articles;
    private Context context;
    private int lastPosition = -1;
    public CartItemAdapter(Context context, LinkedList<Article> itemsData) {
        this.articles = itemsData;
        this.context = context;
    }
    @Override
    public Filter getFilter() {
        return null;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CartItemViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        Article current = articles.get(position);
        holder.bindTo(current);

        if(holder.getBindingAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getBindingAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
    class CartItemViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView priceText;

        CartItemViewHolder(View articleView) {
            super(articleView);

            titleText = articleView.findViewById(R.id.cartTitle);
            priceText = articleView.findViewById(R.id.cartPrice);
        }

        public void bindTo(Article current){
            titleText.setText(current.getName());
            priceText.setText(current.getPrice());

            itemView.findViewById(R.id.cartDelete).setOnClickListener(view -> ((CartActivity)context).deleteItem(current));
        }
    }
}

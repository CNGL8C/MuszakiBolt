package com.example.muszakibolt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.muszakibolt.R;
import com.example.muszakibolt.activities.ShoppingActivity;
import com.example.muszakibolt.models.Article;

import java.util.ArrayList;

public class ArticleAdapter
        extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>
        implements Filterable {

    private ArrayList<Article> articles;
    private ArrayList<Article> allArticles;
    private Context context;
    private int lastPosition = -1;

    public ArticleAdapter(Context context, ArrayList<Article> itemsData) {
        this.articles = itemsData;
        this.allArticles = itemsData;
        this.context = context;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ArticleViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_article, parent, false));
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position) {
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

    @Override
    public Filter getFilter() {
        return shopFilter;
    }
    private Filter shopFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Article> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
                results.count = allArticles.size();
                results.values = allArticles;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(Article article : allArticles) {
                    if(article.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(article);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            articles = (ArrayList)filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ArticleViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView infoText;
        private TextView priceText;
        private ImageView itemImage;
        private RatingBar ratingBar;

        ArticleViewHolder(View articleView) {
            super(articleView);

            titleText = articleView.findViewById(R.id.articleTitle);
            infoText = articleView.findViewById(R.id.articleSubtitle);
            itemImage = articleView.findViewById(R.id.articleImage);
            ratingBar = articleView.findViewById(R.id.ratings);
            priceText = articleView.findViewById(R.id.articlePrice);
        }

        public void bindTo(Article current){
            titleText.setText(current.getName());
            infoText.setText(current.getInfo());
            priceText.setText(current.getPrice());
            ratingBar.setRating(current.getRating());

            Glide.with(context).load(current.getImage()).into(itemImage);

            itemView.findViewById(R.id.add_to_cart).setOnClickListener(view -> ((ShoppingActivity)context).updateCartIcon(current));
            itemView.findViewById(R.id.delete).setOnClickListener(view -> ((ShoppingActivity)context).deleteItem(current));
            itemView.findViewById(R.id.edit).setOnClickListener(view -> ((ShoppingActivity)context).editItem(current));
        }
    }
}

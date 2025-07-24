package com.YOURNAME.infomix;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.YOURNAME.infomix.models.NewsArticle;
import com.YOURNAME.infomix.models.NewsResponse;
import com.YOURNAME.infomix.utils.ApiClient;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private List<NewsArticle> newsList;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        
        initViews(view);
        setupRecyclerView();
        loadNews();
        
        return view;
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        progressBar = view.findViewById(R.id.progressBar);
        
        swipeRefresh.setOnRefreshListener(this::loadNews);
    }
    
    private void setupRecyclerView() {
        newsList = new ArrayList<>();
        adapter = new NewsAdapter(newsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    
    private void loadNews() {
        progressBar.setVisibility(View.VISIBLE);
        swipeRefresh.setRefreshing(false);
        
        new Thread(() -> {
            try {
                // IMPORTANT: You need to get your own API key from newsapi.org
                String apiKey = "YOUR_NEWS_API_KEY"; // Replace with your actual key
                String url = "https://newsapi.org/v2/top-headlines?country=ng&category=general&pageSize=20&apiKey=" + apiKey;
                
                String response = ApiClient.fetchData(url);
                NewsResponse newsResponse = new Gson().fromJson(response, NewsResponse.class);
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        newsList.clear();
                        if (newsResponse.articles != null) {
                            newsList.addAll(newsResponse.articles);
                        }
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    });
                }
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to load news", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        swipeRefresh.setRefreshing(false);
                    });
                }
            }
        }).start();
    }
    
    // News Adapter
    private class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
        private List<NewsArticle> articles;
        
        public NewsAdapter(List<NewsArticle> articles) {
            this.articles = articles;
        }
        
        @Override
        public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_news, parent, false);
            return new NewsViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(NewsViewHolder holder, int position) {
            NewsArticle article = articles.get(position);
            
            holder.titleText.setText(article.title != null ? article.title : "No title");
            holder.descriptionText.setText(article.description != null ? article.description : "No description");
            holder.sourceText.setText(article.source != null ? article.source : "Unknown Source");
            
            // Format date
            if (article.publishedAt != null) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    Date date = inputFormat.parse(article.publishedAt);
                    if (date != null) {
                        holder.dateText.setText(outputFormat.format(date));
                    } else {
                        holder.dateText.setText(article.publishedAt);
                    }
                } catch (Exception e) {
                    holder.dateText.setText(article.publishedAt);
                }
            }
            
            // Load image with Glide
            if (article.urlToImage != null && !article.urlToImage.isEmpty()) {
                Glide.with(holder.imageView.getContext())
                        .load(article.urlToImage)
                        .placeholder(android.R.color.darker_gray)
                        .into(holder.imageView);
            }
            
            // Open article in browser
            holder.cardView.setOnClickListener(v -> {
                if (article.url != null && !article.url.isEmpty()) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.url));
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Cannot open link", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        
        @Override
        public int getItemCount() {
            return articles.size();
        }
        
        class NewsViewHolder extends RecyclerView.ViewHolder {
            MaterialCardView cardView;
            android.widget.ImageView imageView;
            android.widget.TextView titleText, descriptionText, sourceText, dateText;
            
            public NewsViewHolder(View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.cardView); // This might need to be adjusted
                imageView = itemView.findViewById(R.id.imageView);
                titleText = itemView.findViewById(R.id.titleText);
                descriptionText = itemView.findViewById(R.id.descriptionText);
                sourceText = itemView.findViewById(R.id.sourceText);
                dateText = itemView.findViewById(R.id.dateText);
            }
        }
    }
    }

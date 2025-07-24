package com.YOURNAME.infomix;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.YOURNAME.infomix.models.Quote;
import com.YOURNAME.infomix.utils.ApiClient;
import java.io.IOException;

public class QuotesFragment extends Fragment {
    
    private TextView quoteText, authorText;
    private Button refreshBtn, premiumBtn;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quotes, container, false);
        
        quoteText = view.findViewById(R.id.quoteText);
        authorText = view.findViewById(R.id.authorText);
        refreshBtn = view.findViewById(R.id.refreshBtn);
        premiumBtn = view.findViewById(R.id.premiumBtn);
        
        refreshBtn.setOnClickListener(v -> fetchQuote());
        premiumBtn.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showRewardedAd("premium_content");
            }
        });
        
        fetchQuote();
        return view;
    }
    
    private void fetchQuote() {
        refreshBtn.setEnabled(false);
        refreshBtn.setText("Loading...");
        
        new Thread(() -> {
            try {
                String response = ApiClient.fetchData("https://api.quotable.io/random");
                Quote quote = new Gson().fromJson(response, Quote.class);
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        quoteText.setText("\"" + quote.content + "\"");
                        authorText.setText("- " + quote.author);
                        refreshBtn.setEnabled(true);
                        refreshBtn.setText("New Quote");
                    });
                }
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to load quote", Toast.LENGTH_SHORT).show();
                        refreshBtn.setEnabled(true);
                        refreshBtn.setText("New Quote");
                    });
                }
            }
        }).start();
    }
}

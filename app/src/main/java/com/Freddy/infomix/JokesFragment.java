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
import com.YOURNAME.infomix.models.Joke;
import com.YOURNAME.infomix.utils.ApiClient;
import java.io.IOException;

public class JokesFragment extends Fragment {
    
    private TextView setupText, punchlineText;
    private Button revealBtn, refreshBtn;
    private boolean punchlineRevealed = false;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jokes, container, false);
        
        setupText = view.findViewById(R.id.setupText);
        punchlineText = view.findViewById(R.id.punchlineText);
        revealBtn = view.findViewById(R.id.revealBtn);
        refreshBtn = view.findViewById(R.id.refreshBtn);
        
        revealBtn.setOnClickListener(v -> togglePunchline());
        refreshBtn.setOnClickListener(v -> fetchJoke());
        
        fetchJoke();
        return view;
    }
    
    private void togglePunchline() {
        if (punchlineRevealed) {
            punchlineText.setVisibility(View.GONE);
            revealBtn.setText("Reveal Punchline");
            punchlineRevealed = false;
        } else {
            punchlineText.setVisibility(View.VISIBLE);
            revealBtn.setText("Hide Punchline");
            punchlineRevealed = true;
        }
    }
    
    private void fetchJoke() {
        refreshBtn.setEnabled(false);
        refreshBtn.setText("Loading...");
        punchlineText.setVisibility(View.GONE);
        punchlineRevealed = false;
        revealBtn.setText("Reveal Punchline");
        
        new Thread(() -> {
            try {
                String response = ApiClient.fetchData("https://official-joke-api.appspot.com/jokes/random");
                Joke joke = new Gson().fromJson(response, Joke.class);
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        setupText.setText(joke.setup);
                        punchlineText.setText(joke.punchline);
                        refreshBtn.setEnabled(true);
                        refreshBtn.setText("New Joke");
                    });
                }
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to load joke", Toast.LENGTH_SHORT).show();
                        refreshBtn.setEnabled(true);
                        refreshBtn.setText("New Joke");
                    });
                }
            }
        }).start();
    }
  }

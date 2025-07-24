package com.YOURNAME.infomix;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.YOURNAME.infomix.utils.ApiClient;
import java.util.Random;

public class FactsFragment extends Fragment {
    
    private TextView factText;
    private Button refreshBtn, doubleFactsBtn;
    private Random random = new Random();
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facts, container, false);
        
        factText = view.findViewById(R.id.factText);
        refreshBtn = view.findViewById(R.id.refreshBtn);
        doubleFactsBtn = view.findViewById(R.id.doubleFactsBtn);
        
        refreshBtn.setOnClickListener(v -> fetchFact());
        doubleFactsBtn.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showRewardedAd("double_facts");
            }
        });
        
        fetchFact();
        return view;
    }
    
    private void fetchFact() {
        refreshBtn.setEnabled(false);
        refreshBtn.setText("Loading...");
        
        new Thread(() -> {
            try {
                int number = random.nextInt(100) + 1;
                String response = ApiClient.fetchData("http://numbersapi.com/" + number + "/trivia");
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        factText.setText(response);
                        refreshBtn.setEnabled(true);
                        refreshBtn.setText("New Fact");
                    });
                }
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to load fact", Toast.LENGTH_SHORT).show();
                        refreshBtn.setEnabled(true);
                        refreshBtn.setText("New Fact");
                    });
                }
            }
        }).start();
    }
          }

package com.YOURNAME.infomix;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;
    private boolean isAdFreeMode = false;
    private long adFreeEndTime = 0;
    private long appStartTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        appStartTime = System.currentTimeMillis();
        
        // Initialize AdMob
        MobileAds.initialize(this, initializationStatus -> {});
        
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        
        loadInterstitialAd();
        loadRewardedAd();
        
        // Setup Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            
            int itemId = item.getItemId();
            if (itemId == R.id.nav_quotes) {
                selectedFragment = new QuotesFragment();
            } else if (itemId == R.id.nav_jokes) {
                selectedFragment = new JokesFragment();
            } else if (itemId == R.id.nav_facts) {
                selectedFragment = new FactsFragment();
            } else if (itemId == R.id.nav_news) {
                selectedFragment = new NewsFragment();
            }
            
            if (selectedFragment != null) {
                if (!isAdFreeMode() && shouldShowInterstitial()) {
                    showInterstitialAd();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        .commit();
            }
            return true;
        });
        
        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new QuotesFragment())
                    .commit();
            bottomNav.setSelectedItemId(R.id.nav_quotes);
        }
    }
    
    // Interstitial Ad Methods
    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }
                    
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }
    
    private void showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
            loadInterstitialAd();
        }
    }
    
    private boolean shouldShowInterstitial() {
        return Math.random() < 0.3 && 
               System.currentTimeMillis() - appStartTime > 30000;
    }
    
    // Rewarded Ad Methods
    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", adRequest,
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        mRewardedAd = null;
                    }
                    
                    @Override
                    public void onAdLoaded(RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                    }
                });
    }
    
    public void showRewardedAd(String rewardType) {
        if (mRewardedAd != null) {
            mRewardedAd.show(this, rewardItem -> {
                handleReward(rewardType);
                loadRewardedAd();
            });
        } else {
            Toast.makeText(this, "Ad not ready yet, try again", Toast.LENGTH_SHORT).show();
            loadRewardedAd();
        }
    }
    
    private void handleReward(String rewardType) {
        switch (rewardType) {
            case "premium_content":
                Toast.makeText(this, "Premium content unlocked!", Toast.LENGTH_LONG).show();
                break;
            case "double_facts":
                Toast.makeText(this, "Double facts unlocked for next load!", Toast.LENGTH_LONG).show();
                break;
            case "ad_free":
                enableAdFreeMode();
                break;
        }
    }
    
    private void enableAdFreeMode() {
        isAdFreeMode = true;
        adFreeEndTime = System.currentTimeMillis() + (5 * 60 * 1000);
        Toast.makeText(this, "Ad-free mode activated for 5 minutes!", Toast.LENGTH_LONG).show();
        
        new android.os.Handler().postDelayed(() -> {
            isAdFreeMode = false;
            Toast.makeText(this, "Ad-free mode ended", Toast.LENGTH_SHORT).show();
        }, 5 * 60 * 1000);
    }
    
    private boolean isAdFreeMode() {
        if (isAdFreeMode && System.currentTimeMillis() > adFreeEndTime) {
            isAdFreeMode = false;
        }
        return isAdFreeMode;
    }
    
    public boolean getAdFreeStatus() {
        return isAdFreeMode();
    }
}

package com.demo.iap;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements IAPHelper.IAPHelperListener {

    private String TAG = MainActivity.class.getSimpleName();


    IAPHelper iapHelper;
    HashMap<String, SkuDetails> skuDetailsHashMap = new HashMap<>();
    //For non_consumable tag "nc" is used at start
    final String RING = "nc_ring";
    final String NECKLACE = "nc_necklace";
    final String CROWN = "nc_crown";
    final String COIN = "coin";
    final String TEST = "android.test.purchased"; //This id can be used for testing purpose
    private List<String> skuList = Arrays.asList(COIN, RING, NECKLACE, CROWN, TEST);

    private SharedPreferences pref;
    private String SETTINGS = "saved_settings";
    private String SETTINGS_COINS = "saved_coins";

    @BindView(R.id.tvTotalCoinsNum)
    TextView tvtotalCoin;

    private Integer totalCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        iapHelper = new IAPHelper(this, this, skuList);

        //Get previous coins from shared pref data
        pref = getSharedPreferences(SETTINGS, 0);
        totalCoins = pref.getInt(SETTINGS_COINS, 0);
        tvtotalCoin.setText(String.format(Locale.getDefault(), "%d", totalCoins));
    }

    @OnClick(R.id.llBuyCoin)
    public void buyCoin(){
        launch(TEST);
    }

    @OnClick(R.id.llUnlockRing)
    public void unlockRing(){
        launch(RING);
    }

    @OnClick(R.id.llUnlockNecklace)
    public void unlockNecklace(){
        launch(NECKLACE);
    }

    @OnClick(R.id.llUnlockCrown)
    public void unlockCrown(){
        launch(CROWN);
    }

    private void launch(String sku){
        if(!skuDetailsHashMap.isEmpty())
            iapHelper.launchBillingFLow(skuDetailsHashMap.get(sku));
    }


    @Override
    public void onSkuListResponse(HashMap<String, SkuDetails> skuDetails) {
        skuDetailsHashMap = skuDetails;
    }

    @Override
    public void onPurchasehistoryResponse(List<Purchase> purchasedItems) {
        if (purchasedItems != null) {
            for (Purchase purchase : purchasedItems) {
                //Update UI and backend according to purchased items if required
                // Like in this project I am updating UI for purchased items
                String sku = purchase.getSku();
                switch (sku) {
                    case RING:
                        findViewById(R.id.tvRingBought).setVisibility(View.VISIBLE);
                        break;
                    case NECKLACE:
                        findViewById(R.id.tvNecklaceBought).setVisibility(View.VISIBLE);
                        break;
                    case CROWN:
                        findViewById(R.id.tvCrownBought).setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
    }

    @Override
    public void onPurchaseCompleted(Purchase purchase) {
        Toast.makeText(getApplicationContext(), "Purchase Successful", Toast.LENGTH_SHORT).show();
        updatePurchase(purchase);
    }

    private void updatePurchase(Purchase purchase){
        String sku = purchase.getSku();
        switch (sku) {
            case RING:
                findViewById(R.id.tvRingBought).setVisibility(View.VISIBLE);
                break;
            case NECKLACE:
                findViewById(R.id.tvNecklaceBought).setVisibility(View.VISIBLE);
                break;
            case CROWN:
                findViewById(R.id.tvCrownBought).setVisibility(View.VISIBLE);
                break;
            case TEST:
                totalCoins += 25;
                pref.edit().putInt(SETTINGS_COINS, totalCoins).apply();
                tvtotalCoin.setText(String.format(Locale.getDefault(), "%d", totalCoins));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (iapHelper != null)
            iapHelper.endConnection();
    }
}

package com.example.courseworkhabittracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class UserGuide extends AppCompatActivity {

    private WebView webViewUserGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);

        webViewUserGuide = (WebView) findViewById(R.id.WebViewUserGuide);
        webViewUserGuide.setWebViewClient(new WebViewClient());
        webViewUserGuide.getSettings().setJavaScriptEnabled(true);
        webViewUserGuide.loadUrl("file:///android_asset/userguide.html");



    }

    @Override
    public void onBackPressed(){
        if(webViewUserGuide.canGoBack()){
            webViewUserGuide.goBack();
        }else{
            super.onBackPressed();
        }
    }

}
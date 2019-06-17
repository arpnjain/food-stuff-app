package com.foodstuff;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class PostWebView extends AppCompatActivity {

    ProgressBar progressBar;
    WebView webView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_web_view);
        progressBar=(ProgressBar) findViewById(R.id.processbar_post);
        toolbar=(Toolbar) findViewById(R.id.toolbar_post);
        webView=(WebView) findViewById(R.id.webView_post);
        setSupportActionBar(toolbar);
        webView.setVisibility(View.INVISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Toast.makeText(PostWebView.this, "Page Started Loading", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                Toast.makeText(PostWebView.this, "Page Loaded", Toast.LENGTH_SHORT).show();
                String javaScript = "javascript:(function(){var a= document.getElementsByTagName('header');a[0].hidden = 'true';a= document.getElementsByClassName('page_body');a[0].style.padding='0px';})()";
                webView.loadUrl(javaScript);
            }

        });
        webView.loadUrl(getIntent().getStringExtra("url"));
    }
}

package com.sjp.backendtracker;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.CookieManager;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

private WebView webView;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    CookieManager.getInstance().setAcceptCookie(true);
    CookieManager.getInstance().setAcceptThirdPartyCookies(
        null, true
    );

    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);
    layout.setBackgroundColor(Color.WHITE);

    TextView header = new TextView(this);
    header.setText("SJP Backend Tracker");
    header.setTextSize(21);
    header.setTextColor(Color.WHITE);
    header.setBackgroundColor(Color.rgb(20, 110, 75));
    header.setPadding(20, 18, 12, 18);
    layout.addView(header);

    webView = new WebView(this);
    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setDomStorageEnabled(true);
    settings.setSupportMultipleWindows(false);

    webView.setWebViewClient(new WebViewClient());
    layout.addView(webView,
        new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0, 1
        ));

    setContentView(layout);
    webView.loadUrl("https://sjp.gos.pk/");
}

@Override
public void onBackPressed() {
    if (webView != null && webView.canGoBack()) {
        webView.goBack();
    } else {
        super.onBackPressed();
    }
}

}
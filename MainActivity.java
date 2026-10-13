package pk.sjpbackendtracker;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.view.Gravity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;

public class MainActivity extends Activity {
    private WebView webView;
    private TextView hint;
    private String trackerScript = "";
    private static final String HOME = "https://sjp.gos.pk/";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(20, 83, 45));
        getWindow().setNavigationBarColor(Color.rgb(20, 83, 45));
        FrameLayout frame = new FrameLayout(this);
        frame.setBackgroundColor(Color.WHITE);
        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(false);
        CookieManager cookies = CookieManager.getInstance();
        cookies.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= 21) cookies.setAcceptThirdPartyCookies(webView, true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase();
                if ("https".equals(uri.getScheme()) && (host.equals("sjp.gos.pk") || host.endsWith(".sjp.gos.pk") || host.equals("gos.pk") || host.endsWith(".gos.pk"))) return false;
                if ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme())) {
                    try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); } catch (Exception ignored) {}
                    return true;
                }
                return true;
            }
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hint.setText("Sign in to SJP, then open Applied Posts. The tracker starts automatically there.");
                if (isSjpUrl(url)) installPageWatcher();
            }
        });
        hint = new TextView(this);
        hint.setText("Opening Sindh Job Portal…");
        hint.setTextColor(Color.rgb(20, 83, 45));
        hint.setTextSize(12);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(12, 5, 12, 5);
        hint.setBackgroundColor(Color.rgb(236, 253, 245));
        FrameLayout.LayoutParams webParams = new FrameLayout.LayoutParams(-1, -1);
        frame.addView(webView, webParams);
        FrameLayout.LayoutParams hintParams = new FrameLayout.LayoutParams(-1, dp(34), Gravity.BOTTOM);
        frame.addView(hint, hintParams);
        setContentView(frame);
        trackerScript = readAsset("tracker.js");
        if (state == null) webView.loadUrl(HOME);
        else webView.restoreState(state);
    }

    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    private boolean isSjpUrl(String value) {
        try { Uri u = Uri.parse(value); String h = u.getHost(); return "https".equals(u.getScheme()) && h != null && (h.equals("sjp.gos.pk") || h.endsWith(".sjp.gos.pk")); } catch (Exception e) { return false; }
    }
    private String readAsset(String name) {
        StringBuilder out = new StringBuilder();
        try (InputStream in = getAssets().open(name); BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
            String line; while ((line = br.readLine()) != null) out.append(line).append('\n');
        } catch (IOException ignored) {}
        return out.toString();
    }
    private void installPageWatcher() {
        if (trackerScript == null || trackerScript.trim().isEmpty()) return;
        String quoted;
        try { quoted = JSONObject.quote(trackerScript); } catch (Exception e) { return; }
        String js = "(function(){if(window.__sjpBackendWatcherInstalled)return;window.__sjpBackendWatcherInstalled=true;var code=" + quoted + ";var wasApplied=false;function check(){try{if(location.hostname!=='sjp.gos.pk'&&!location.hostname.endsWith('.sjp.gos.pk'))return;var txt=(document.body&&document.body.innerText||'').toLowerCase();var url=location.href.toLowerCase();var hasAppliedLabel=(txt.indexOf('applied post')>=0||txt.indexOf('applied jobs')>=0||txt.indexOf('my applications')>=0);var hasApplicationData=(txt.indexOf('application id')>=0||txt.indexOf('application status')>=0||txt.indexOf('job title')>=0||txt.indexOf('no applied')>=0);var applied=((hasAppliedLabel&&hasApplicationData)||(url.indexOf('applied')>=0&&txt.indexOf('application')>=0));if(applied&&!document.getElementById('__sjpviewer')&&!window.__sjpBackendTrackerRunning){window.__sjpBackendTrackerRunning=true;try{(0,eval)(code)}catch(e){window.__sjpBackendTrackerRunning=false;console.error('SJP tracker injection failed',e)}}if(!applied){wasApplied=false;window.__sjpBackendTrackerRunning=false}}catch(e){}}check();setInterval(check,1200);})();";
        webView.evaluateJavascript(js, null);
    }
    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }
    @Override protected void onSaveInstanceState(Bundle out) { if (webView != null) webView.saveState(out); super.onSaveInstanceState(out); }
    @Override protected void onDestroy() { if (webView != null) { webView.stopLoading(); webView.destroy(); } super.onDestroy(); }
}

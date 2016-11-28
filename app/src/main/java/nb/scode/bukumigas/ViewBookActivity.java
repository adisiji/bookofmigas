package nb.scode.bukumigas;

import android.annotation.TargetApi;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import nb.scode.bukumigas.app.App;
import nb.scode.bukumigas.dialogs.ErrorStartDialog;

import static android.view.View.GONE;
import static nb.scode.bukumigas.constants.Constants.ADMOB_ENABLED;


public class ViewBookActivity extends AppCompatActivity implements  ErrorStartDialog.MyAlertListener{

    private String pathurl;
    private WebView myWebView;
    private static final String UA_Chrome = "Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 Mobile Safari/537.36 [WebView Android/v2.0]";
    private TextView loading;
    private ProgressBar progressBar;


    @Override
    public void onClickAlert(){
        loadPage();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);
        myWebView = (WebView) findViewById(R.id.webview);
        loading = (TextView)findViewById(R.id.pdf_view_loading);
        progressBar = (ProgressBar)findViewById(R.id.prg_load_book);

        setAds();
        if(savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            pathurl = bundle.getString("pathurl");
        }
        else
        {
            pathurl = savedInstanceState.getString("pathurl");
        }

        myWebView.getSettings().setAppCacheMaxSize(1024*1024*10); //cache 10MB
        myWebView.getSettings().setAllowContentAccess(true);
        myWebView.getSettings().setAppCacheEnabled(true);
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        myWebView.getSettings().setUserAgentString(UA_Chrome);
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setSupportZoom(true);
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 ) {
                    progressBar.setProgress(progress);
                    loading.setVisibility(View.VISIBLE);
                }
            }
        });

        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                view.setVisibility(GONE);
                loading.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }

            public void onPageFinished(WebView view, String url) {
                view.setVisibility(View.VISIBLE);
                loading.setVisibility(GONE);
                progressBar.setVisibility(GONE);
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("Error received","BAD BOY");
                loadErrorPage(view);
                showerror();
                progressBar.setProgress(0);
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view,errorCode,description,failingUrl);
                Log.e("Error received","BAD BOY");
                loadErrorPage(view);
                showerror();
                progressBar.setProgress(0);
            }
        });
        loadPage();

    }

    private void setAds(){
        if (App.getInstance().getAdmob() == ADMOB_ENABLED) {
            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }

    private void loadPage(){
        if(pathurl!=null && App.getInstance().isConnected()) {
            myWebView.loadUrl(pathurl);
        }
        else {
            loadErrorPage(myWebView);
            showerror();
        }
    }

    private void showerror(){
        FragmentManager fm = getSupportFragmentManager();
        Fragment prev = fm.findFragmentByTag("dialog");
        if(prev!=null){
            fm.beginTransaction().remove(prev).commit();
        }
        fm.beginTransaction().addToBackStack(null).commit();
        // Create and show the dialog.
        DialogFragment newFragment = ErrorStartDialog.newInstance("Cannot load page!");
        newFragment.show(fm, "dialog");
    }

    private void loadErrorPage(WebView webview){
        if(webview!=null){
            String htmlData ="<html><body><div align=\"center\" >Error Load Book</div></body>";
            webview.loadUrl("about:blank");
            webview.loadDataWithBaseURL(null,htmlData, "text/html", "UTF-8",null);
            webview.invalidate();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.d("onSaveInstance","Viewbook");
        savedInstanceState.putString("pathurl",pathurl);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        pathurl = savedInstanceState.getString("pathurl");
    }

}
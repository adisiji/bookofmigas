package nb.scode.bukumigas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jaredrummler.android.device.DeviceName;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import nb.scode.bukumigas.app.App;
import nb.scode.bukumigas.common.ActivityBase;
import nb.scode.bukumigas.dialogs.ErrorStartDialog;
import nb.scode.bukumigas.util.CustomRequest;

public class AppActivity extends ActivityBase implements ErrorStartDialog.MyAlertListener{

    RelativeLayout loadingScreen;
    private String username, password, devicename, seed, refreshedToken;
    private Boolean loading = false, fresh=false;
    private static ErrorStartDialog errorDialog;
    private Handler handler;
    private Runnable runnableCode;
    private static final String errormsg = "Error Connecting to Server!";

    @Override
    public void onClickAlert(){
        startProcess();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        refreshedToken = FirebaseInstanceId.getInstance().getToken();

        SharedPreferences sharedPreferences = this.getSharedPreferences("jaguar", Context.MODE_PRIVATE);
        String uuid = sharedPreferences.getString("blackbox",null);
        handler = new Handler();
        runnableCode = new Runnable() {
            @Override
            public void run() {
                // Do something here on the main thread
                Log.d("Handlers", "Called on main thread");
                refreshedToken = FirebaseInstanceId.getInstance().getToken();
                if(refreshedToken!=null && !fresh){
                    fresh = true;
                    login();
                } else if(sp%3==0) {
                    showAlert(errormsg);
                } else {
                    // Repeat this the same runnable code block again another 2.5 seconds
                    sp++;
                    handler.postDelayed(runnableCode, 2500);
                }

            }
        };
        if(uuid == null){
            uuid = UUID.randomUUID().toString(); // harus disimpan
            sharedPreferences.edit().putString("blackbox",uuid).apply();
        }

        seed = "neyar"+ (uuid.replace("-","")).substring(4,15);
        password = seed;
        Log.d("password",password);
        loadingScreen = (RelativeLayout) findViewById(R.id.loadingScreen);
        showLoadingScreen();
        startProcess();
    }

    private void startProcess(){
        if(App.getInstance().isConnected()){
            if(!fresh){
            DeviceName.with(getApplicationContext()).request(new DeviceName.Callback() {
                @Override
                public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                        devicename = info.getName();
                        username = seed + (devicename.replaceAll(" ","")).toLowerCase();
                        if(username.length() > 24){
                            username = username.substring(0,24);
                        }
                    }
                });
                Log.e("let's find toke","GOOD");
                caritoken();
            }
        }
        else
        {

            showAlert(getString(R.string.error_internet_connection));
        }
    }

    private int sp = 1;
    private void caritoken(){
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if(refreshedToken==null) {
            handler.post(runnableCode);
        }
        else {
            login();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("restore", true);
        outState.putBoolean("loading", loading);
    }

    private void login() {
        Log.e("Start Login","GOOD");
        if(fresh){
            handler.removeCallbacks(runnableCode);
        }
        if (!loading) {

            loading = true;

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_LOGIN, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            if (App.getInstance().authorize(response)) {
                                if (App.getInstance().getState() == ACCOUNT_STATE_ENABLED) {
                                    App.getInstance().updateGeoLocation();
                                    success();
                                } else {
                                    if (App.getInstance().getState() == ACCOUNT_STATE_BLOCKED ) {
                                        //App.getInstance().logout();
                                        Toast.makeText(getApplicationContext(), getText(R.string.msg_account_blocked), Toast.LENGTH_SHORT).show();

                                    } else {
                                        App.getInstance().updateGeoLocation();
                                        success();
                                    }
                                }

                            } else {

                                registerPhone();
                                //Toast.makeText(getApplicationContext(), getString(R.string.error_signin), Toast.LENGTH_SHORT).show();
                            }

                            loading = false;

                            hidepDialog();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error login","CKKCK");
                    showAlert(errormsg);

                    loading = false;

                    hidepDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    if(username!=null && password!=null){
                        params.put("username", username);
                        params.put("password", password);
                    }

                    params.put("clientId", CLIENT_ID);

                    return params;
                }
            };

            App.getInstance().addToRequestQueue(jsonReq);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        //AppInitializer.getInstance().trackScreenView("Welcome Screen");
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void success() {
        Intent intent = new Intent(AppActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    public void showLoadingScreen() {
        loadingScreen.setVisibility(View.VISIBLE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        final RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.loadingScreen);
        if(Build.VERSION.SDK_INT > 15 ){
            Glide.with(this).load(R.drawable.wallpaper_cover).asBitmap().into(new SimpleTarget<Bitmap>(width,height) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable drawable = new BitmapDrawable(resource);
                    relativeLayout.setBackground(drawable);
                }
            });
        }

        ImageView splashImageView = (ImageView)findViewById(R.id.appLogoView2);
        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/Panton-LightCaps.otf");
        TextView header = (TextView)findViewById(R.id.header_splash);
        if(face!=null){
            header.setTypeface(face);
        }
        Glide.with(this)
                .load(R.drawable.app_logo)
                .asBitmap().fitCenter().into(splashImageView);

    }

    private void registerPhone(){
        Log.d("New Phone","Lets register it");
        //we need this code to register new phone

        if (App.getInstance().isConnected()) {
                    CustomRequest jsonReq = new CustomRequest(Request.Method.POST, METHOD_ACCOUNT_SIGNUP, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    Log.e("Profile", "Malformed JSON: \"" + response.toString() + "\"");

                                    if (App.getInstance().authorize(response)) {

                                        Log.e("Profile", "Malformed JSON: \"" + response.toString() + "\"");
                                            success();
                                    } else {
                                        showAlert(errormsg);
                                        Log.e("Profile", "Could not parse malformed JSON: \"" + response.toString() + "\"");
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e("Error register phone", "Malformed JSON: \"" + error.getMessage() + "\"");
                            showAlert(errormsg);
                        }

                    })
                    {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            String fullname = seed + " "+ devicename;
                            String email = username+"@dummy.com";
                            String lang = Locale.getDefault().getLanguage();
                            params.put("username", username);
                            params.put("fullname", fullname);
                            params.put("password", password);
                            params.put("email", email);
                            params.put("language", lang );
                            params.put("facebookId", "");
                            params.put("clientId", CLIENT_ID);
                            params.put("gcm_regId", App.getInstance().getGcmToken());

                            return params;
                        }
                    };

                    App.getInstance().addToRequestQueue(jsonReq);
        }
            else {
            showAlert(getString(R.string.error_internet_connection));
        }
    }

    private void showAlert(String message){
        FragmentManager fm = getSupportFragmentManager();
        errorDialog = ErrorStartDialog.newInstance(message);
        errorDialog.show(fm,"fragment");
    }

}
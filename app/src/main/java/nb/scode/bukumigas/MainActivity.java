package nb.scode.bukumigas;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import nb.scode.bukumigas.app.App;
import nb.scode.bukumigas.common.ActivityBase;
import nb.scode.bukumigas.dialogs.PopularSettingsDialog;

public class MainActivity extends ActivityBase implements FragmentDrawer.FragmentDrawerListener,
        PopularSettingsDialog.AlertPositiveListener {

    private Toolbar mToolbar;

    private FragmentDrawer drawerFragment;

    // used to store app title
    private CharSequence mTitle;

    private LinearLayout mContainerAdmob;

    private InterstitialAd interstitialAd;
    private Fragment fragment;
    private Boolean action = false;
    private boolean isDoubleBackToExit = false;
    private int page = 0;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Boolean restore = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {

            //Restore the fragment's instance
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

            restore = savedInstanceState.getBoolean("restore");
            mTitle = savedInstanceState.getString("mTitle");

        } else {

            fragment = new StreamFragment();
            restore = false;
            mTitle = getString(R.string.app_name);
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        requestInterstitial();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(mTitle);

        drawerFragment = (FragmentDrawer) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        mContainerAdmob = (LinearLayout) findViewById(R.id.container_admob);

        if (App.getInstance().getAdmob() == ADMOB_ENABLED) {

            mContainerAdmob.setVisibility(View.VISIBLE);

            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        if (!restore) {

            // Show default section "Stream"

            displayView(1);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putString("mTitle", getSupportActionBar().getTitle().toString());
        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
    }

    @Override
    public void onChangePopularCategory(int position) {

        PopularFragment p = (PopularFragment) fragment;
        p.onChangeCategory(position);
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {

        displayView(position);
    }

    private void displayView(int position) {

        action = false;

        switch (position) {

            case 0: {

                break;
            }

            case 1: {

                page = 1;

                fragment = new StreamFragment();
                getSupportActionBar().setTitle(R.string.page_1);

                action = true;

                break;
            }

            case 2: {

                page = 2;

                fragment = new CategoriesFragment();
                getSupportActionBar().setTitle(R.string.page_2);

                action = true;

                break;
            }


            case 3: {

                page = 3;

                fragment = new SearchFragment();
                getSupportActionBar().setTitle("");

                action = true;

                break;
            }

            case 4: {

                page = 4;

                fragment = new PopularFragment();
                getSupportActionBar().setTitle(R.string.page_4);

                action = true;

                break;
            }

            case 5: {

                if (App.getInstance().getId() != 0){

                    page = 5;

                    fragment = new FavoritesFragment();
                    getSupportActionBar().setTitle(R.string.page_5);

                    action = true;

                } else {

                    Log.e("Error "+TAG,"Error get favorites");
                }

                break;
            }

            default: {

                if (App.getInstance().getId() != 0) {

                    Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(i);

                } else {

                    Log.e("Error "+TAG,"Error get settings");
                }

                break;
            }
        }

        if (action) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container_body, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home: {

                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void requestInterstitial(){
        AdRequest.Builder adRequest = new AdRequest.Builder();
        adRequest.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        adRequest.addTestDevice("AB65D4D610C6856E00BE2BB6B4D907D2");
        AdRequest request = adRequest.build();
        interstitialAd.loadAd(request);
    }

    @Override
    public void onBackPressed() {

        if (drawerFragment.isDrawerOpen()) {

            drawerFragment.closeDrawer();

        } else {

            if (isDoubleBackToExit) {
                super.onBackPressed();
                if(interstitialAd.isLoaded()){
                    interstitialAd.show();
                }
            }
            if (!isDoubleBackToExit) {
                Toast.makeText(this, getString(R.string.tap_exit), Toast.LENGTH_SHORT).show();
            }
            this.isDoubleBackToExit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isDoubleBackToExit = false;
                }
            }, 2000); //delay 2 detik
        }
    }

    @Override
    public void setTitle(CharSequence title) {

        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }
}

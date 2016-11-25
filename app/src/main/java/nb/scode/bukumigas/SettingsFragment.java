package nb.scode.bukumigas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import nb.scode.bukumigas.app.App;
import nb.scode.bukumigas.constants.Constants;
import nb.scode.bukumigas.util.CustomRequest;

public class SettingsFragment extends PreferenceFragment implements Constants {

    private Preference itemContactUs, aboutPreference, itemTerms, itemThanks, itemNotifications;

    private ProgressDialog pDialog;

    LinearLayout aboutDialogContent;
    TextView aboutDialogAppName, aboutDialogAppVersion, aboutDialogAppCopyright;

    private Boolean loading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        initpDialog();

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        Preference pref = findPreference("settings_version");

        pref.setTitle(getString(R.string.app_name) + " v" + getString(R.string.app_version));

        aboutPreference = findPreference("settings_version");
        itemTerms = findPreference("settings_terms");
        itemThanks = findPreference("settings_thanks");
        itemNotifications = findPreference("settings_push_notifications");
        itemContactUs = findPreference("settings_contact_us");

        itemContactUs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), SupportActivity.class);
                startActivity(i);

                return true;
            }
        });

        itemThanks.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), WebViewActivity.class);
                i.putExtra("url", METHOD_APP_THANKS);
                i.putExtra("title", getText(R.string.settings_thanks));
                startActivity(i);

                return true;
            }
        });

        itemTerms.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), WebViewActivity.class);
                i.putExtra("url", METHOD_APP_TERMS);
                i.putExtra("title", getText(R.string.settings_terms));
                startActivity(i);

                return true;
            }
        });

        aboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(getText(R.string.action_about));

                aboutDialogContent = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.about_dialog, null);

                alertDialog.setView(aboutDialogContent);

                aboutDialogAppName = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppName);
                aboutDialogAppVersion = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppVersion);
                aboutDialogAppCopyright = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppCopyright);

                aboutDialogAppName.setText(getString(R.string.app_name));
                aboutDialogAppVersion.setText("Version " + getString(R.string.app_version));
                aboutDialogAppCopyright.setText("Copyright © " + getString(R.string.app_year) + " " + getString(R.string.app_copyright));

//                    alertDialog.setMessage("Version " + APP_VERSION + "/r/n" + APP_COPYRIGHT);
                alertDialog.setCancelable(true);
                alertDialog.setNeutralButton(getText(R.string.action_ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

                alertDialog.show();

                return false;
            }
        });


    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            loading = savedInstanceState.getBoolean("loading");

        } else {

            loading = false;
        }

        if (loading) {

            showpDialog();
        }
    }

    public void onDestroyView() {

        super.onDestroyView();

        hidepDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("loading", loading);
    }

    protected void initpDialog() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing())
            pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
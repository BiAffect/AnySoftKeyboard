package com.anysoftkeyboard.ui.settings.setup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.anysoftkeyboard.ui.settings.MainSettingsActivity;
import com.menny.android.anysoftkeyboard.LauncherSettingsActivity;
import com.menny.android.anysoftkeyboard.R;
import com.menny.android.anysoftkeyboard.BiAffect.bridge.BiAffectBridge;

public class ConsentPage extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // use the Bridge sdk to check if the user already logged in, if so skip the login page
        if (BiAffectBridge.getInstance().isUserLoggedIn()) {
            String loggedInMsg = "You've already logged in";
            // already logged in, use intent to pass the message and jump to the next page
            Intent toLogging = new Intent(ConsentPage.this, MainSettingsActivity.class);
            toLogging.putExtra("LoggedInMsg", loggedInMsg);
            startActivity(toLogging);
//            Log.d("test", "logined in");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_page);
        // init webView
        webView = (WebView) findViewById(R.id.consentWebView);
        // displaying content in WebView from html file that stored in assets folder
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/" + "consent_full.html");

        // Agree button
        final Button agree_button = (Button) findViewById(R.id.btn_agree);
        agree_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConsentPage.this, LoggingPage.class);
                startActivity(intent);
            }
        });

        // Cancel button
        final Button exit_button = (Button) findViewById(R.id.btn_exit);
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });

        // Disagree button
        final Button disagree_button = (Button) findViewById(R.id.btn_disagree);
        disagree_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });
    }
}

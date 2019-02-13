package com.cireson.scanner.splash;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;

import com.cireson.scanner.view.LoginActivity;
import com.cireson.scanner.R;

/**
 * Created by seattleapplab on 1/31/14.
 */
public class SplashScreen extends Activity {

    private int SPLASH_TIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
               // Intent i = new Intent(SplashScreen.this, MainNavActivity.class);
                startActivity(i);

                //
                finish();
            }
        }, SPLASH_TIMEOUT);
    }

}


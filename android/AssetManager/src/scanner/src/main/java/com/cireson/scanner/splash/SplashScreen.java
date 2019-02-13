
//////////////////////////////////////////////////////////////////////////////
//This file is part of Cireson Barcode Scanner. 
//
//Cireson Barcode Scanner is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//Cireson Barcode Scanner is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with Cireson Barcode Scanner.  If not, see<https://www.gnu.org/licenses/>.
/////////////////////////////////////////////////////////////////////////////


package com.cireson.scanner.splash;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.cireson.scanner.R;
import com.cireson.scanner.view.LoginActivity;

public class SplashScreen extends Activity {

    private static String TAG = "Cireson Scanner SplashScreen";
    private int SPLASH_TIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);

            View decorView = getWindow().getDecorView();
            //Hide the status bar.
            int uiOptions = //View.SYSTEM_UI_FLAG_FULLSCREEN;
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE ;

            decorView.setSystemUiVisibility(uiOptions);

            //Hide the action bar.
            ActionBar actionBar = getActionBar();
            actionBar.hide();

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
        catch(Exception ex){
            Log.d(TAG, Log.getStackTraceString(ex));
        }
    }

}


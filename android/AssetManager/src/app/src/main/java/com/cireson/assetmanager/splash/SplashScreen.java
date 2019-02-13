
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


package com.cireson.assetmanager.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.controller.LoginActivity;
import com.cireson.assetmanager.controller.MainNavigationActivity;

/**
 * Created by seattleapplab on 1/31/14.
 */
public class SplashScreen extends Activity {

    private static String TAG = "Cireson Scanner SplashScreen";
    private int SPLASH_TIMEOUT = 3000;
    private GlobalData globalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);
            View decorView = getWindow().getDecorView();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i;
                    globalData = GlobalData.getInstance();
                    globalData.setContext(SplashScreen.this);
                    if(globalData.getCurrentUser()!=null){
                        i = new Intent(SplashScreen.this, MainNavigationActivity.class);
                    }
                    else {
                        i = new Intent(SplashScreen.this, LoginActivity.class);
                    }
                    startActivity(i);
                    finish();
                }
            }, SPLASH_TIMEOUT);
        }
        catch(Exception ex){
            Log.d(TAG, Log.getStackTraceString(ex));
        }
    }

}


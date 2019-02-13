
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


package com.cireson.scanner.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cireson.scanner.R;

public class LoginActivity extends Activity {

    private static String TAG = "Cireson Scanner LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new PlaceholderFragment())
                        .commit();
            }

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
        }
        catch(Exception ex){
            Log.d(TAG, Log.getStackTraceString(ex));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        try{
            getMenuInflater().inflate(R.menu.main, menu);

            findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View view) {
                    tryLogin();
                }
            });
        }
        catch(Exception ex){
            Log.d(TAG, Log.getStackTraceString(ex));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        try{
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                return true;
            }
        }
        catch(Exception ex){
            Log.d(TAG, Log.getStackTraceString(ex));
        }
        return super.onOptionsItemSelected(item);
    }

    public void tryLogin(){
        try{
            Intent i = new Intent(LoginActivity.this, MainNavExActivity.class);
            startActivity(i);
        }
        catch(Exception ex){
            Log.d(TAG, Log.getStackTraceString(ex));
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_app_login, container, false);
            return rootView;
        }
    }
}

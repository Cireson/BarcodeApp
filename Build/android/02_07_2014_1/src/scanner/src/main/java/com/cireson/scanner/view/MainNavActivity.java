package com.cireson.scanner.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cireson.scanner.ConfigureSettings;
import com.cireson.scanner.InventoryAudit;
import com.cireson.scanner.R;
import com.cireson.scanner.ScanActivity;

public class MainNavActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_nav, menu);
        registerListeners();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void registerListeners(){

        findViewById(R.id.imgBtnReviewAssets).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(MainNavActivity.this, SearchAssetsActivity.class);
                startActivity(i);
            }
        });
        findViewById(R.id.imgBtnSwapAssets).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(MainNavActivity.this, ScanAssetsActivity.class);
                startActivity(i);
            }
        });
        findViewById(R.id.imgBtnInventoryAudit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(MainNavActivity.this, InventoryAudit.class);
                startActivity(i);
            }
        });
        findViewById(R.id.imgBtnConfigureSettings).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(MainNavActivity.this, ConfigureSettings.class);
                startActivity(i);
            }
        });
        findViewById(R.id.imgBtnEditAssets).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(MainNavActivity.this, ScanAssetsActivity.class);
                startActivity(i);
            }
        });
        findViewById(R.id.imgBtnDisposeAssets).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(MainNavActivity.this, ScanAssetsActivity.class);
                startActivity(i);
            }
        });
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
            View rootView = inflater.inflate(R.layout.fragment_main_nav, container, false);
            return rootView;
        }
    }
}

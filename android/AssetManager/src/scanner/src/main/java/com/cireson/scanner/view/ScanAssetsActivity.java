
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cireson.scanner.R;
import com.cireson.scanner.controller.ScanItemController;
import com.cireson.scanner.model.ScanItemModel;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Iterator;

public class ScanAssetsActivity extends Activity {

    private static String TAG = "Cireson Scanner ScanAssetActivity";

    //ToDo: temporary - move to storage
    // private static  ArrayList<String> assetList = new ArrayList<String>();

    private ScanItemController scanItemController = new ScanItemController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_scan_assets);

            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new PlaceholderFragment())
                        .commit();
            }
        }
        catch(Exception ex){
            Log.d(TAG, Log.getStackTraceString(ex));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try{
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan_assets, menu);

        findViewById(R.id.scan_anything).setOnClickListener(scanAll);
        findViewById(R.id.next_button_scan_assets).setOnClickListener(scanAssetsNextButton);

        recreateUi();

        }catch(Exception ex){

            Log.d(TAG, Log.getStackTraceString(ex));
        }

        return true;
    }

    private void recreateUi() {


        if(!scanItemController.getItems().isEmpty()){
            findViewById(R.id.banner_scanned_items).setVisibility(View.VISIBLE);
            findViewById(R.id.next_button_scan_assets).setVisibility(View.VISIBLE);

                addItemsToAssetContainer();
        }
        else{
            findViewById(R.id.banner_scanned_items).setVisibility(View.INVISIBLE);
            findViewById(R.id.next_button_scan_assets).setVisibility(View.INVISIBLE);
        }
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        try{

            recreateUi();
        }
        catch(Exception ex){
            Log.d(TAG, Log.getStackTraceString(ex));
        }

    }

    private final Button.OnClickListener scanAll = new Button.OnClickListener() {
        @Override
        public void onClick(View v){
            IntentIntegrator integrator = new IntentIntegrator(ScanAssetsActivity.this);
            integrator.initiateScan();
        }
    };

    private final Button.OnClickListener scanAssetsNextButton = new Button.OnClickListener() {

        @Override
        public void onClick(View view) {
            startActivity(new Intent(ScanAssetsActivity.this, MatchConfirmationActivity.class));
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try{
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String contents = result.getContents();
            if (contents != null) {

                String assetName = getContentsInfo(result.toString());
                if(assetName != null || !assetName.trim().equals("") ){
                    if(!scanItemController.containsItem(assetName)){

                            ScanItemModel sim = new ScanItemModel();
                            sim.setScanItemText(assetName);
                            sim.setScanItemCheckbox(false);
                            sim.setShowScanItemButton(true);
                            scanItemController.add(sim);

                    }
//                    else {
//                        showDialog(R.string.barcode_exists, "#"+ assetName);
//                        return;
//                    }
                    recreateUi();
                }
            } else {
                showDialog(R.string.result_failed, getString(R.string.result_failed_why));
            }
        }
        }catch (Exception ex){
            Log.d(TAG, Log.getStackTraceString(ex));
        }
    }
    private void showDialog(int title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok_button, null);
        builder.show();
    }

    private String getContentsInfo(String barCodeInput){
        String[] tokens = barCodeInput.split("\n");
        for (int i=0; i < tokens.length; i++){

            if (tokens[i].toLowerCase().contains("contents")){
                String str = tokens[i];
                return str.substring(str.indexOf(':'));
            }
        }
        return "";
    }

    private void addItemsToAssetContainer(){

        if(!scanItemController.getItems().isEmpty()){

            ScrollView sv = (ScrollView) findViewById(R.id.items_container_scan_assets);
            sv.removeAllViews();

            LinearLayout ll = new LinearLayout(this);
            ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            ll.setOrientation(1);


            Iterator<ScanItemModel> iterator = scanItemController.getItems().iterator();

            LayoutInflater inflater =  (LayoutInflater)this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

            while(iterator.hasNext()){

                LinearLayout ll2 = (LinearLayout) inflater.inflate( R.layout.scan_assets_row, null );
                ll2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT));

                TextView tv = (TextView) ll2.findViewById(R.id.assetItemNumber);
                tv.setText("#"+ iterator.next().getScanItemText());

                ll.addView(ll2);
            }

            sv.addView(ll);
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
            View rootView = inflater.inflate(R.layout.fragment_scan_assets, container, false);
            return rootView;
        }
    }

}

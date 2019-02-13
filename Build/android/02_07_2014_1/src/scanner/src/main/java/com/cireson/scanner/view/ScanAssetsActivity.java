package com.cireson.scanner.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.cireson.scanner.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Iterator;

public class ScanAssetsActivity extends Activity {

    private static String logtag = "barcode";

    //ToDo: temporary - move to storage
    private static  ArrayList<String> assetList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_assets);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan_assets, menu);

        findViewById(R.id.scan_anything).setOnClickListener(scanAll);

        recreateUi();

        return true;
    }

    private void recreateUi() {

        if(!assetList.isEmpty()){
            findViewById(R.id.banner_scanned_items).setVisibility(View.VISIBLE);
            findViewById(R.id.nextButton).setVisibility(View.VISIBLE);

            Iterator<String> iterator = assetList.iterator();
            while(iterator.hasNext()){
                addItemsToAssetContainer(iterator.next());
            }
        }
        else{
            findViewById(R.id.banner_scanned_items).setVisibility(View.INVISIBLE);
            findViewById(R.id.nextButton).setVisibility(View.INVISIBLE);
        }
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        recreateUi();

    }

    private final Button.OnClickListener scanAll = new Button.OnClickListener() {
        @Override
        public void onClick(View v){
            IntentIntegrator integrator = new IntentIntegrator(ScanAssetsActivity.this);
            integrator.initiateScan();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String contents = result.getContents();
            if (contents != null) {

                //showDialog(R.string.result_succeeded, result.toString());
                String assetName = getContentsInfo(result.toString());
                if(assetName != null || !assetName.trim().equals("") ){
                    assetList.add(assetName);
                    addItemsToAssetContainer(assetName);
                }
            } else {
                showDialog(R.string.result_failed, getString(R.string.result_failed_why));
            }
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

    private void addItemsToAssetContainer(String assetItem){

        TableLayout tableLayout = (TableLayout) findViewById(R.id.assetItemsContainer);

        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT));

        TextView tv1 = new TextView(this);
        tv1.setText("Asset Item");
        tv1.setTypeface(null, Typeface.BOLD);

        TextView tv2 = new TextView(this);
        tv2.setText("#"+ assetItem);

        tr.addView(tv1);
        tr.addView(tv2);

        CheckBox cb = new CheckBox(this);
        tr.addView(cb);

        tableLayout.addView(tr);

        TableRow tr2 = new TableRow(this);
        tr2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,2));
        tr2.setBackgroundColor(0x33333333);

        tableLayout.addView(tr2);
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

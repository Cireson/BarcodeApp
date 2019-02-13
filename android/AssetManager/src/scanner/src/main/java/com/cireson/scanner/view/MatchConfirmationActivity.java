
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
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.cireson.scanner.R;
import com.cireson.scanner.controller.ScanItemController;
import com.cireson.scanner.model.ScanItemModel;

import java.util.ArrayList;

public class MatchConfirmationActivity extends Activity implements AbsListView.OnItemClickListener{

    private static String TAG = "Cireson Scanner MatchConfirmationActivity";

    private ScanItemController matchNavItems = new ScanItemController();

    private MatchAssetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_match_confirmation);

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
        
        // Inflate the menu; this adds items to the action bar if it is present.
        try{
            getMenuInflater().inflate(R.menu.match_confirmation, menu);
            createNavigationItems();
        }
        catch(Exception ex){
            Log.d(TAG, Log.getStackTraceString(ex));
        }
        return true;
    }

    private void createNavigationItems() {

        adapter = new MatchAssetsAdapter(this, R.id.matched_asset_item);

        for (ScanItemModel data : matchNavItems.getItems()) {
            adapter.add(data);
        }

        ListView listView = (ListView) findViewById(R.id.asset_items_lv_match_container);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
            View rootView = inflater.inflate(R.layout.fragment_match_confirmation, container, false);
            return rootView;
        }
    }

}


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
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.cireson.scanner.R;
import com.cireson.scanner.model.NavUIModel;
import com.cireson.scanner.util.StaggeredGridView;

import java.util.ArrayList;

public class MainNavExActivity extends Activity implements AbsListView.OnItemClickListener {

    private static String TAG = "Cireson Scanner MainNavExActivity";

    private ArrayList<NavUIModel> navigationItems = new ArrayList();

    private MainNavAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_nav_ex);

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
            getMenuInflater().inflate(R.menu.main_nav_ex, menu);
            createNavigationItems();
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


    private void createNavigationItems() {

        Resources resources = getResources();

        NavUIModel navUIModel = new NavUIModel();
        navUIModel.setNavText(resources.getString(R.string.title_review_assets));
        navUIModel.setNavImageDrawable(getResources().getDrawable(R.drawable.review_assets));
        navigationItems.add(navUIModel);

        NavUIModel navUIModel1 = new NavUIModel();
        navUIModel1.setNavText(resources.getString(R.string.title_swap_assets));
        navUIModel1.setNavImageDrawable(getResources().getDrawable(R.drawable.swap_assets));
        navigationItems.add(navUIModel1);

        NavUIModel navUIModel2 = new NavUIModel();
        navUIModel2.setNavText(resources.getString(R.string.title_inventory_audit));
        navUIModel2.setNavImageDrawable(getResources().getDrawable(R.drawable.inventory_audit));
        navigationItems.add(navUIModel2);

        NavUIModel navUIModel3 = new NavUIModel();
        navUIModel3.setNavText(resources.getString(R.string.title_configure_settings));
        navUIModel3.setNavImageDrawable(getResources().getDrawable(R.drawable.configure_settings));
        navigationItems.add(navUIModel3);

        NavUIModel navUIModel4 = new NavUIModel();
        navUIModel4.setNavText(resources.getString(R.string.title_edit_assets));
        navUIModel4.setNavImageDrawable(getResources().getDrawable(R.drawable.edit_assets));
        navigationItems.add(navUIModel4);

        NavUIModel navUIModel5 = new NavUIModel();
        navUIModel5.setNavText(resources.getString(R.string.title_dispose_assets));
        navUIModel5.setNavImageDrawable(getResources().getDrawable(R.drawable.dispose_assets));
        navigationItems.add(navUIModel5);

        adapter = new MainNavAdapter(this, R.id.nav_line_main);

        for (NavUIModel data : navigationItems) {
            adapter.add(data);
        }

        StaggeredGridView gridView = (StaggeredGridView) findViewById(R.id.main_nav_grid_view);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        switch (i){
            case 0: startActivity(new Intent(MainNavExActivity.this, ScanAssetsActivity.class));
                    break;
            case 1: startActivity(new Intent(MainNavExActivity.this, ScanAssetsActivity.class));
                    break;
            case 2: startActivity(new Intent(MainNavExActivity.this, InventorySearchActivity.class));
                    break;
            case 3: startActivity(new Intent(MainNavExActivity.this, SettingsActivity.class));
                    break;
            case 4: startActivity(new Intent(MainNavExActivity.this, ScanAssetsActivity.class));
                    break;
            case 5: startActivity(new Intent(MainNavExActivity.this, ScanAssetsActivity.class));
                    break;
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
            View rootView = inflater.inflate(R.layout.fragment_main_nav_ex, container, false);
            return rootView;
        }
    }
}

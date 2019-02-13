
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


package com.cireson.assetmanager.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.util.CiresonConstants;

public class MainNavigationActivity extends CiresonBaseActivity implements AdapterView.OnItemClickListener {

    /*Instances..*/
    MenuItemsAdapter menuItemsAdapter;

    /*Get the menu items..*/
    int[] mainMenuItemsIds = {
            R.drawable.received_assets,
            R.drawable.swap_assets,
            R.drawable.edit_assets,
            R.drawable.inventory_audit,
            R.drawable.dispose_assets,
            R.drawable.configure_settings
    };

    String[] mainMenuItemTitles = null;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        /*Assign menu names..*/
        mainMenuItemTitles = new String[]{
                getResources().getString(R.string.menu_receive_assets),
                getResources().getString(R.string.menu_swap_assets),
                getResources().getString(R.string.menu_edit_assets),
                getResources().getString(R.string.menu_inventory_audit),
                getResources().getString(R.string.menu_dispose_assets),
                getResources().getString(R.string.menu_settings),
        };
        GridView gridView = (GridView)findViewById(R.id.main_menu_grid_view);
        gridView.setOnItemClickListener(this);
        menuItemsAdapter = new MenuItemsAdapter(this);
        gridView.setAdapter(menuItemsAdapter);

        /*Indicating application has started a flow..*/
        GlobalData.APPLICATION_FLOW_HAS_STARTED = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*Indicating that application flow is reversed with back navigation..*/
        GlobalData.APPLICATION_FLOW_HAS_STARTED = false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = null;
        Bundle extras = new Bundle();
        GlobalData globalData = GlobalData.getInstance();
        globalData.gone=false;
        globalData.clearSearchAssets();
        globalData.clearEditorsSelectedStringItems();
        globalData.clearEditorInstances();
        switch (mainMenuItemsIds[position]){
            case R.drawable.received_assets:
                GlobalData.ACTIVE_MAIN_MENU = CiresonConstants.RECEIVED_ASSETS;
                i = new Intent(this, AssetsSearchActivity.class);
                break;
            case R.drawable.swap_assets:
                GlobalData.ACTIVE_MAIN_MENU = CiresonConstants.SWAP_ASSETS;
                GlobalData.getInstance().getSwaper().setSwap(true);
                extras.putString(AssetsScannerActivity.ASSETS_SCANNER_EXTRA,getResources().getString(R.string.scan_assets_title_for_swap));
                AssetsScannerActivity.TYPE_OF_NEXT_ACTIVITY_AFTER_SCAN = InventorySearchActivity.class;
                i = new Intent(this, AssetsScannerActivity.class);
                AssetsEditorActivity.count = 0;
                break;
            case R.drawable.edit_assets:
                GlobalData.ACTIVE_MAIN_MENU = CiresonConstants.EDIT_ASSETS;
                extras.putString(AssetsScannerActivity.ASSETS_SCANNER_EXTRA,getResources().getString(R.string.scan_assets_title_for_edit));
                globalData.gone = true;
                AssetsScannerActivity.TYPE_OF_NEXT_ACTIVITY_AFTER_SCAN = InventorySearchActivity.class;
                i = new Intent(this, AssetsScannerActivity.class);
                break;
            case R.drawable.inventory_audit:
                GlobalData.ACTIVE_MAIN_MENU = CiresonConstants.INVENTORY_AUDIT;
                i = new Intent(this, AssetsEditorActivity.class);
                break;
            case R.drawable.dispose_assets:
                GlobalData.ACTIVE_MAIN_MENU = CiresonConstants.DISPOSE_ASSETS;
                AssetsScannerActivity.TYPE_OF_NEXT_ACTIVITY_AFTER_SCAN = InventorySearchActivity.class;
                extras.putString(AssetsScannerActivity.ASSETS_SCANNER_EXTRA,getResources().getString(R.string.scan_assets_title_for_dispose));
                i = new Intent(this, AssetsScannerActivity.class);
                break;
            case R.drawable.configure_settings:
                GlobalData.ACTIVE_MAIN_MENU = CiresonConstants.SETTINGS;
                i=new Intent(this,TheSettingsActivity.class);
                break;
        }
        if(i!=null) {
            i.putExtras(extras);
            this.startActivity(i);
        }
    }

    /*An adapter class to be provided to the grid view..*/
    private class MenuItemsAdapter extends BaseAdapter{
        /*Create a private context instance that refers to the context of parent..*/
        private Context context=null ;
        private LayoutInflater inflater;

        /*Default Constructor..*/
        public MenuItemsAdapter(Context c){
            this.context = c;
        }

        /*Implementation of necessary methods to be provided to the grid view..*/
        public int getCount(){
            return mainMenuItemsIds.length;
        }

        public long getItemId(int pos){
            return mainMenuItemsIds[pos];
        }

        public Object getItem(int pos){
            return pos;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            View cellView = convertView;
            if (cellView == null) {
                inflater = getLayoutInflater();
                cellView = inflater.inflate(R.layout.main_menu_grid_items,parent, false);
            }
            ImageView imageView = (ImageView)cellView.findViewById(R.id.main_menu_item_image_view);
            TextView titleTextView = (TextView)cellView.findViewById(R.id.main_menu_item_title);
            imageView.setImageResource(mainMenuItemsIds[position]);
            titleTextView.setText(mainMenuItemTitles[position]);
            if(position==this.getCount()-1){
                cellView.setBackgroundResource(R.drawable.border_left);
            }
            else if(position==this.getCount()-2){
                cellView.setBackgroundResource(R.drawable.border_right);
            }
            else if(position%2==0){
                cellView.setBackgroundResource(R.drawable.border_right_bottom);
            }
            else{
                cellView.setBackgroundResource(R.drawable.border_left_bottom);
            }
            return cellView;
        }
    }
    /*End of MenuItemsAdapter class.*/
}
/*End of MainNavigationActivity.*/


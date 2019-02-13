
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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.util.CiresonConstants;
import com.cireson.assetmanager.util.CiresonUtilites;

public class AssetsSearchActivity extends CiresonBaseActivity {

    /*Instances..*/
    private static String TAG = "Cireson Scanner AssetsSearchActivity";

    public static final int REQUEST_SEARCH_ASSETS = 0;
    public static final int REQUEST_BROWSE_ASSETS = 1;

    public static final String MESSAGE = "MESSAGE";
    public static final String PURCHASE_ORDER_NUMBER = "PURCHASE_ORDER_NUMBER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_assets);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*When this activity is active,
        there are three possible options to proceed..
        1. Search assets by PO
        2. Browse by PO
        3. Add assets.
        So, initially it is necessary to set this flag to false and when Add Assets button is clicked, this flag is again set to true.*/
        GlobalData.SCAN_TO_ADD_ASSETS = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        try{
            getMenuInflater().inflate(R.menu.search_assets, menu);
        }
        catch(Exception ex){
            Log.d(TAG, Log.getStackTraceString(ex));
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_SEARCH_ASSETS:
            case REQUEST_BROWSE_ASSETS:
                if(resultCode == RESULT_OK){
                    String error = data.getStringExtra(MESSAGE);
                    String title = "";
                    if(error.trim().isEmpty()){
                        error = getResources().getString(R.string.search_assets_result_purchase_order_not_found);
                    }else{
                        title = getResources().getString(R.string.error);
                    }
                    CiresonUtilites.getLoadingDialog().dismiss();
                    if(error!=null&&
                       (error.equalsIgnoreCase(getResources().getString(R.string.search_assets_result_purchase_order_not_found))||
                        !error.isEmpty())){
                        CiresonUtilites.displayMessage(title,error,this).show(this.getFragmentManager(), "");
                    }
                }
                break;
            default:
                break;
        }
    }

    /*Search for the assets with given purchase order number..*/
    public void searchAssets(String searchText) {
        Intent i = new Intent(this, AssetsSearchResultsActivity.class);
        i.putExtra(CiresonConstants.NAV_FROM_SEARCH_ASSETS,CiresonConstants.SEARCH_ASSETS);
        i.putExtra(AssetsSearchActivity.PURCHASE_ORDER_NUMBER, searchText);
        startActivityForResult(i,REQUEST_SEARCH_ASSETS);
    }

    /*Browse all the purchase orders..*/
    public void browseOrders() {
        Intent i = new Intent(this,AssetsSearchResultsActivity.class);
        i.putExtra(CiresonConstants.NAV_FROM_SEARCH_ASSETS,CiresonConstants.BROWSE_ORDERS);
        startActivityForResult(i,REQUEST_BROWSE_ASSETS);
    }

    /*When any of the buttons is clicked: Search, Browse or Add Assets..*/
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchAssets:
                EditText editSearchByKeyword = (EditText) findViewById(R.id.editSearchByKeyword);
                String searchedText = editSearchByKeyword.getText().toString().trim();
                if(searchedText!=null&&!searchedText.isEmpty()){
                    searchAssets(editSearchByKeyword.getText().toString().trim());
                }else{
                    CiresonUtilites.displayMessage(getString(R.string.error),getString(R.string.search_assets_search_text_field_empty),this)
                            .show(this.getFragmentManager(),"");
                }
                break;
            case R.id.browseOrders:
                browseOrders();
                break;
            case R.id.addAssets:
                AssetsScannerActivity.TYPE_OF_NEXT_ACTIVITY_AFTER_SCAN = AssetsEditorActivity.class;
                GlobalData.SCAN_TO_ADD_ASSETS = true;
                Intent i = new Intent(this, AssetsScannerActivity.class);
                i.putExtra(AssetsScannerActivity.ASSETS_SCANNER_EXTRA,getString(R.string.scan_assets_title_for_add));
                startActivity(i);
                break;
            default:
                break;
        }
        CiresonUtilites.hideSoftKeyboard(this);
    }

    public void goToHome(View v){
        Intent i = new Intent(this, MainNavigationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        AssetsSearchActivity.this.startActivity(i);
    }

    public void invalidateToken(View view){
        GlobalData.getInstance().getCurrentUser().setToken("123");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        CiresonUtilites.hideSoftKeyboard(this);
        return true;
    }

}

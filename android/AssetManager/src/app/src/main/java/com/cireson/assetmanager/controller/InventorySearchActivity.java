
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.assistance.Swaper;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.model.Assets;
import com.cireson.assetmanager.service.APICaller;
import com.cireson.assetmanager.service.SearchAssetsWithBarCode;
import com.cireson.assetmanager.service.SearchAssetsWithPropertiesRequest;
import com.cireson.assetmanager.util.CiresonConstants;
import com.cireson.assetmanager.util.CiresonListViewAdapter;
import com.cireson.assetmanager.util.CiresonUtilites;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class InventorySearchActivity extends CiresonBaseActivity implements AbsListView.OnItemClickListener{

    /*Instances..*/
    private ListView searchList;
    private TextView titleTextView,subTitleTextView ;
    private Button nextButton;

    private GlobalData globalData;
    private ArrayList<String> notfound = new ArrayList<String>();
    private static int numberOfItemsFound = 0;

    public final static String INVENTORY_SEARCH_EXTRA = "INVENTORY_SEARCH_EXTRA";
    public final static String INVENTORY_TO_SCANNER_FOR_EDIT_AND_DISPOSE = "INVENTORY_TO_SCANNER_FOR_EDIT_AND_DISPOSE";

    public void onCreate(Bundle objectState){
        super.onCreate(objectState);
        setContentView(R.layout.activity_inventory_search_assets);
        /*Prepare loading view..*/
        globalData = GlobalData.getInstance();
        searchList = (ListView)findViewById(R.id.inventory_search_assets_list);
        searchList.setChoiceMode(ListView.CHOICE_MODE_NONE);
        titleTextView = (TextView)findViewById(R.id.inventory_search_assets_title);
        subTitleTextView = (TextView)findViewById(R.id.inventory_search_assets_sub_title);
        nextButton = (Button)findViewById(R.id.activity_new_product_next_btn);
        nextButton.setEnabled(false);
        /*Perform API call and other operations based on navigation from the main menu..*/
        switch(GlobalData.ACTIVE_MAIN_MENU){
            case CiresonConstants.SWAP_ASSETS:
                subTitleTextView.setText("Choose one to swap");
                searchList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                CiresonUtilites.initLoadingDialog(R.string.progress_dialog_message, R.style.cireson_progress_dialog_theme, this);
                CiresonUtilites.getLoadingDialog().show();
                callAPIForSwapAssets();
                break;
            case CiresonConstants.EDIT_ASSETS:
                nextButton.setEnabled(true);
                CiresonUtilites.initLoadingDialog(R.string.progress_dialog_message, R.style.cireson_progress_dialog_theme, this);
                CiresonUtilites.getLoadingDialog().show();
                callAPIForEditAssets();
                break;
            case CiresonConstants.INVENTORY_AUDIT:
                CiresonUtilites.initLoadingDialog(R.string.progress_dialog_message, R.style.cireson_progress_dialog_theme, this);
                CiresonUtilites.getLoadingDialog().show();
                callAPIForInventoryAudit();
                break;
            case CiresonConstants.DISPOSE_ASSETS:
                CiresonUtilites.initLoadingDialog(R.string.progress_dialog_message, R.style.cireson_progress_dialog_theme, this);
                CiresonUtilites.getLoadingDialog().show();
                nextButton.setEnabled(true);
                callAPIForDisposeAssets();
                break;
        }
}

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: this part caused exception when going back and clicking next
    }

    /*When navigated from Edit Assets menu*/
    private void callAPIForDisposeAssets(){
        callCommonApi();
    }

    /*When navigated from Swap Assets menu*/
    private void callAPIForSwapAssets(){
        callCommonApi();
    }

    /*When navigated from Dispose Assets menu..*/
    private void callAPIForEditAssets(){
       callCommonApi();
    }

    /*When navigated from Inventory Audit..*/
    private void callAPIForInventoryAudit(){
        /*Request string to be passed to the api for Inventory Assets search...*/
        String date = (globalData.currentDate==null?"":globalData.currentDate);
        SearchAssetsWithPropertiesRequest searchAssetsRequest = new SearchAssetsWithPropertiesRequest(
                AssetsEditorActivity.statusLists,
                AssetsEditorActivity.locationLists,
                AssetsEditorActivity.organizationLists,
                AssetsEditorActivity.costCenterLists,
                AssetsEditorActivity.custodianLists,
                date
        );
        /*Make an API call..*/
        final Type type = new TypeToken<ArrayList<Assets>>(){}.getType();
        APICaller<Assets> apiCaller = new APICaller<Assets>(Assets.class, type, this);
        apiCaller.setMethodForBaseUrl("GetProjectionByCriteria");
        apiCaller.callArrayApi(searchAssetsRequest, new APICaller.Callback<ArrayList<Assets>>() {
            @Override
            public void onApiCallComplete(ArrayList<Assets> obj, String error) {
                CiresonUtilites.getLoadingDialog().dismiss();
                if (obj != null && obj.size() > 0) {
                    globalData.searchedAssets = obj;
                    AssetsScannerActivity.TYPE_OF_NEXT_ACTIVITY_AFTER_SCAN = InventoryAuditsEmailSettingsActivity.class;
                    Intent i = new Intent(InventorySearchActivity.this, AssetsScannerActivity.class);
                    i.putExtra(AssetsScannerActivity.ASSETS_SCANNER_EXTRA,"Match Assets");
                    startActivity(i);
                    finish();
                } else {
                    boolean objectIsNull = false;
                    if(obj==null){
                        objectIsNull = true;
                    }
                    Intent i = new Intent();
                    i.putExtra(CiresonConstants.MESSAGE_FROM_INVENTORY_SEARCH_TO_ASSETS_EDITOR_ERROR,error);
                    i.putExtra(CiresonConstants.MESSAGE_FROM_INVENTORY_SEARCH_TO_ASSETS_EDITOR_OBJECT_STATE,objectIsNull);
                    setResult(RESULT_OK,i);
                    finish();
                }
            }
        });
    }

    /*Common API for both Edit and Dispose assets..*/
    private void callCommonApi(){
        final Type type = new TypeToken<ArrayList<Assets>>(){}.getType();
        APICaller<Assets> apiCaller = new APICaller<Assets>(Assets.class, type, this);
        apiCaller.setMethodForBaseUrl("GetProjectionByCriteria");
        apiCaller.callArrayApi(new SearchAssetsWithBarCode(globalData.searchedAssets),new APICaller.Callback<ArrayList<Assets>>(){
            @Override
            public void onApiCallComplete(ArrayList<Assets> obj, String error) {
                CiresonUtilites.getLoadingDialog().cancel();
                switch(GlobalData.ACTIVE_MAIN_MENU){
                    case CiresonConstants.SWAP_ASSETS:
                        if(obj==null){
                            setResult(RESULT_OK,getIntent().putExtra(INVENTORY_SEARCH_EXTRA,error));
                            Swaper.setShowMessageDialog(true);
                            finish();
                        }else{
                            numberOfItemsFound = obj.size();
                            if(numberOfItemsFound==0){
                                String message = getString(R.string.inventory_search_no_assets_found);
                                setResult(RESULT_OK,getIntent().putExtra(INVENTORY_SEARCH_EXTRA,message));
                                Swaper.setShowMessageDialog(true);
                                finish();
                            }else if((numberOfItemsFound>0&&!isAssetMatched(obj.get(0)))){
                                String assetTagOrSerial = "";
                                Assets asset = obj.get(0);
                                if(asset.assetTag!=null){
                                    assetTagOrSerial = asset.assetTag;
                                }else if(asset.serialNumber!=null){
                                    assetTagOrSerial = asset.serialNumber;
                                }
                                setResult(RESULT_OK,getIntent().putExtra(INVENTORY_SEARCH_EXTRA,assetTagOrSerial));
                                Swaper.setShowMessageDialog(true);
                                finish();
                            }else{
                                if (globalData.getSwaper().getSwap()) {
                                    globalData.getSwaper().setFirstAsset(obj.get(0));
                                    globalData.getSwaper().setProperitesMapForFirstAsset(obj.get(0));
                                } else {
                                    globalData.getSwaper().setSecondAsset(obj.get(0));
                                    globalData.getSwaper().setProperitesMapForSecondAsset(obj.get(0));
                                }
                                nextButton.performClick();
                                finish();
                            }
                        }
                        break;
                    case CiresonConstants.DISPOSE_ASSETS:
                    case CiresonConstants.EDIT_ASSETS:
                        if(obj==null){
                            Intent intent = new Intent();
                            intent.putExtra(INVENTORY_TO_SCANNER_FOR_EDIT_AND_DISPOSE,error);
                            setResult(RESULT_OK,intent);
                            finish();
                        }else{
                            numberOfItemsFound = obj.size();
                            if(numberOfItemsFound ==0){
                                Intent intent = new Intent();
                                intent.putExtra(INVENTORY_TO_SCANNER_FOR_EDIT_AND_DISPOSE,getString(R.string.inventory_search_no_assets_found));
                                setResult(RESULT_OK,intent);
                                finish();
                            }else{
                                globalData.searchedAssets.clear();
                                globalData.searchedAssets.addAll(obj);
                                searchList.setAdapter(new SearchAssetsListAdapter(InventorySearchActivity.this,obj));
                                searchList.setOnItemClickListener(InventorySearchActivity.this);
                                adjustTitleTexts();
                            }
                        }
                        break;
                    default:
                        adjustTitleTexts();
                        break;
                }
            }
        });
    }

    private boolean isAssetMatched(Assets responsedAsset){
        boolean assetMatched = false;
        for(Assets scannedAsset:globalData.searchedAssets){
            if(scannedAsset.assetTag!=null){
                if((scannedAsset.assetTag.equals(responsedAsset.assetTag))||(scannedAsset.assetTag.equals(responsedAsset.serialNumber))){
                    assetMatched = true;
                    break;
                }
            }else if(scannedAsset.serialNumber!=null){
                if((scannedAsset.serialNumber.equals(responsedAsset.serialNumber))||(scannedAsset.serialNumber.equals(responsedAsset.assetTag))){
                    assetMatched = true;
                    break;
                }
            }
        }
        return assetMatched;
    }

    private void adjustTitleTexts(){
        StringBuilder titleText;
        switch (GlobalData.ACTIVE_MAIN_MENU){
            case CiresonConstants.SWAP_ASSETS:
                titleText = new StringBuilder("Found "+numberOfItemsFound+" assets");
                titleTextView.setText(titleText.toString());
                break;
            case CiresonConstants.EDIT_ASSETS:
            case CiresonConstants.DISPOSE_ASSETS:
                titleTextView.setText("Search Results");
                titleText = new StringBuilder("Found "+numberOfItemsFound+" of "+globalData.copyOfsearchedAssets.size()+" scanned items");
                subTitleTextView.setText(titleText.toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(GlobalData.ACTIVE_MAIN_MENU==CiresonConstants.EDIT_ASSETS||
                GlobalData.ACTIVE_MAIN_MENU==CiresonConstants.DISPOSE_ASSETS){
            globalData.searchedAssets.clear();
        }
    }

    /*Handle next..*/
    public void next(View v){
        Intent i = null;
        int count=0;
        if(SearchAssetsListAdapter.assetsList!=null){
            count = SearchAssetsListAdapter.assetsList.size();
        }
        switch (GlobalData.ACTIVE_MAIN_MENU){
            case CiresonConstants.SWAP_ASSETS:
                i = new Intent(this,AssetsEditorActivity.class);
                i.putExtra(AssetsEditorActivity.EXTRA_ASSETS_COUNT, count);
                break;
            case CiresonConstants.EDIT_ASSETS:
                i = new Intent(this, AssetsEditorActivity.class);
                i.putExtra(AssetsEditorActivity.EXTRA_ASSETS_COUNT, count);
                break;
            case CiresonConstants.DISPOSE_ASSETS:
                i = new Intent(this,AssetsDisposalActivity.class);
                i.putExtra(AssetsDisposalActivity.EXTRA_ASSETS_COUNT, count);
                break;
            default:
                break;
        }
        if(i!=null){
            startActivity(i);
        }
    }

    /*When an item is clicked..*/
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(GlobalData.ACTIVE_MAIN_MENU==CiresonConstants.SWAP_ASSETS){
            if(globalData.getSwaper().getSwap()){
                globalData.getSwaper().setFirstAsset(SearchAssetsListAdapter.assetsList.get(i));
                globalData.getSwaper().setProperitesMapForFirstAsset(SearchAssetsListAdapter.assetsList.get(i));
            }else{
                globalData.getSwaper().setSecondAsset(SearchAssetsListAdapter.assetsList.get(i));
            }
        }
        nextButton.setEnabled(true);
    }

    private static class SearchAssetsListAdapter extends CiresonListViewAdapter<Assets>{

        LayoutInflater inflater;
        static ArrayList<Assets> assetsList = new ArrayList<Assets>();

        public SearchAssetsListAdapter(Context context,ArrayList<Assets> assets){
            super(assets);
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            assetsList = (ArrayList)getDisplayList();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if(rowView==null){
                rowView = inflater.inflate(R.layout.inventory_search_assets_row, parent, false);
            }
            TextView assetName = (TextView)rowView.findViewById(R.id.inventorySearchAssetsName);
            TextView name = (TextView)rowView.findViewById(R.id.inventorySearchAssetsRowName);
            TextView model = (TextView)rowView.findViewById(R.id.inventorySearchAssetsRowModel);
            TextView serialNumber = (TextView)rowView.findViewById(R.id.inventorySearchAssetsRowSerialNumber);
            TextView tag = (TextView)rowView.findViewById(R.id.inventorySearchAssetsRowTag);
            TextView disposedText = (TextView)rowView.findViewById(R.id.inventory_search_assets_list_view_right_text);

            assetName.setText(assetsList.get(position).name);
            name.setText(getDisplayText(assetsList.get(position).manufacturer));
            model.setText(getDisplayText(assetsList.get(position).model));
            serialNumber.setText(getDisplayText(assetsList.get(position).serialNumber));
            tag.setText( getDisplayText(assetsList.get(position).assetTag));

            /*In the case of disposal assets, if hardware status is disposed, this icon is displayed..*/
            if(GlobalData.ACTIVE_MAIN_MENU==CiresonConstants.DISPOSE_ASSETS){
                if(assetsList.get(position).hardwareAssetStatus.name.equalsIgnoreCase(CiresonConstants.HARDWARE_STATUS_NAME_DISPOSED)){
                    disposedText.setVisibility(View.VISIBLE);
                }
            }
            return rowView;
        }

        private String getDisplayText(String s){
            if(s==null || "".equals(s)){
                return "N/A";
            }
            else{
                return s;
            }
        }

    }
}

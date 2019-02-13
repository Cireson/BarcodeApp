
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.assistance.ScanItemController;
import com.cireson.assetmanager.assistance.Swaper;
import com.cireson.assetmanager.model.ScanItemModel;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.model.Assets;
import com.cireson.assetmanager.service.APICaller;
import com.cireson.assetmanager.service.SearchAssetsWithBarCode;
import com.cireson.assetmanager.util.BluetoothClient;
import com.cireson.assetmanager.util.CiresonConstants;
import com.cireson.assetmanager.util.CiresonDialogs;
import com.cireson.assetmanager.util.CiresonUtilites;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

import zxing.library.DecodeCallback;
import zxing.library.ZXingFragment;

public class AssetsScannerActivity extends CiresonBaseActivity implements  AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,BluetoothClient.BluetoothClientCallbackForResult {

    private static String TAG = "Cireson Scanner ScanAssetActivity";
    /*Instances..*/
    /*Views to be updated dynamically..*/
    private TextView listHeadingText;
    private ListView scannedItemsList;
    private Button nextButton;
    private LinearLayout zxingFragmentContainer,buttonControllersContainer;

    /*Other instances..*/
    private GlobalData globalData;
    private CiresonDialogs dialog;
    private ScanItemController scanItemController = new ScanItemController();
    private int numberOfAddedAssets = 0;
    private boolean useCameraScanner = false;

    /*These instances work with bluetooth service..*/
    private BluetoothClient bluetoothClient;
    private View scannerHelp;

    /*Required static fields..*/
    private static ZXingFragment zxingFragment;
    /*If any single asset is matched with scanned barcodes, next button has to be enabled. field ENABLE_NEXT_BUTTON contains a
    value to determine wheter to enable next button or not..*/
    public static boolean ENABLE_NEXT_BUTTON = false;
    /*These fields are required to work with Add assets functionality of Receive Assets menu.*/
    /*This field determines state of API call each time new asset is sent to the service..*/
    public static boolean ADD_ASSETS_API_CALL_STATE = false;
    /*This field determines state of API call if any of the request could not be forwarded..*/
    public static boolean ADD_ASSETS_API_CALL_STATE_FAILED = false;
    /*This field is required to determine if assets to be added are filtered before proceeding to next activity. This is
    * because, searched assets list can contain already available assets with new assets and here we are required to add
    * only new assets..*/
    public static boolean NEW_ASSETS_FILTERED = false;
    /*This field indicates if any of the unknown or new asset is assigned with an asset tag or a serial number.
    If any of these property is assigned, its value is set to be true..*/
    public static boolean UNKNOWN_RECEIVED_ASSETS_FIXED = false;
    /*This field is required to check if this activity is currently displayed or active. This helps to avoid exception that
     * might occur during filling up a list view after API Calls which are asynchronous calls to populate data. So, only when
     * activity is visible, list view is updated.*/
    public static boolean SCANNER_ACTIVITY_IS_ACTIVE = false;
    /*This field is used to identify the control that instantiated this activity from the main menu based upon which next activity from
    * this activity can be instantiated..*/
    public static Class TYPE_OF_NEXT_ACTIVITY_AFTER_SCAN = AssetsScannerActivity.class;

    /*Extra field as a key to a intent value to be received from this activity.*/
    public static final String ASSETS_SCANNER_EXTRA = "ASSETS_SCANNER_EXTRA";
    /*This field is a intent request code while calling PossibleMatchedAssets intent..*/
    public final static int REQUEST_CODE_TO_POSSIBLE_MATCHED_ASSETS = 100;
    /*This field is a intent request code to InventorySearchActivity while working for Swap Assets..*/
    public final static int REQUEST_CODE_TO_INVENTORY_SEARCH_FOR_SWAP = 101;

    public final static int REQUEST_CODE_TO_INVENTORY_SEARCH_FOR_EDIT = 102;

    public final static int REQUEST_CODE_TO_INVENTORY_SEARCH_FOR_DISPOSE = 103;

    /*This field is used to control whether to use AsyncTask or not to provide random barcodes.
    AsyncTask is used only to provide custom serial numbers during development.................*/
    private static boolean ASYN_SCAN_TASK = false;

    /*This instance indicates if forward navigation is allowed.*/
    private static boolean GO_TO_NEXT_PAGE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_assets);
        scannerHelp = findViewById(R.id.scannerHelp);
        /*Initially disable Next button..*/
        nextButton = (Button)findViewById(R.id.btnNext);
        GO_TO_NEXT_PAGE = false;
        /*Get zxing fragment that holds a barcode scanner..*/
        zxingFragment = (ZXingFragment)getFragmentManager().findFragmentById(R.id.scanner);
        zxingFragmentContainer = (LinearLayout)findViewById(R.id.id_scan_assets_zxing_fragment_container);
        /*Get Intent value..*/
        String valueFromIntent = "";
        if(getIntent().getStringExtra(AssetsScannerActivity.ASSETS_SCANNER_EXTRA)!=null){
            valueFromIntent = getIntent().getStringExtra(AssetsScannerActivity.ASSETS_SCANNER_EXTRA);
        }
        /*A title text above scan list view that is dynamically changed base on calling activity..*/
        listHeadingText = (TextView)findViewById(R.id.scanAssetsListHeading);
        listHeadingText.setText(valueFromIntent);

        /*List view that holds scanned items as well as items received from services..*/
        scannedItemsList = (ListView) findViewById(R.id.lstScannedItems);

        /*Initialize a singleton global data..*/
        globalData = GlobalData.getInstance();

        /*Clear all assets holder lists..*/
        try{
            globalData.searchedAssetsToAddAssets.clear();
            globalData.copyOfsearchedAssets.clear();
            globalData.copyOfMatchedAssets.clear();
            globalData.searchedAssetsToAddAssetsAfterSelection.clear();
        }catch(NullPointerException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        /*If scanner is activated for adding or receiving assets, initialize following fields ..*/
        if(GlobalData.SCAN_TO_ADD_ASSETS){
            GlobalData.SCAN_TO_ADD_ASSETS = true;
            ADD_ASSETS_API_CALL_STATE = false;
            ADD_ASSETS_API_CALL_STATE_FAILED = false;
            NEW_ASSETS_FILTERED = false;
            UNKNOWN_RECEIVED_ASSETS_FIXED = false;
            numberOfAddedAssets = 0;
        }
        ASYN_SCAN_TASK = false;

    }

    @Override
    protected void onResume() {
        super.onResume();

        SCANNER_ACTIVITY_IS_ACTIVE = true;
        ScannedAssetListAdapter adpt = null;

        if(globalData.copyOfsearchedAssets ==null){
            globalData.copyOfsearchedAssets = new ArrayList<Assets>();
        }

        switch (GlobalData.ACTIVE_MAIN_MENU){
            case CiresonConstants.RECEIVED_ASSETS:
                if(GlobalData.SCAN_TO_ADD_ASSETS){
                    /*If this scan is for receive assets (i.e. Add Assets)..*/
                    adpt = new ScannedAssetListAdapter(this, globalData.searchedAssetsToAddAssets);
                }else{
                    adpt = new ScannedAssetListAdapter(this, globalData.searchedAssets);
                }
                break;
            case CiresonConstants.SWAP_ASSETS:
                adpt = new ScannedAssetListAdapter(this, globalData.searchedAssets);
                if(Swaper.getShowMessageDialog()){
                    StringBuilder messageToDisplay = new StringBuilder();
                    messageToDisplay.append(Swaper.getMessage());
                    CiresonUtilites.displayMessage(getString(R.string.message),
                            messageToDisplay.toString(),
                            this).show(getFragmentManager(),"");
                    Swaper.setShowMessageDialog(false);
                }
                break;
            case CiresonConstants.EDIT_ASSETS:
                adpt = new ScannedAssetListAdapter(this, globalData.copyOfsearchedAssets);
                break;
            case CiresonConstants.INVENTORY_AUDIT:
                adpt = new ScannedAssetListAdapter(this, globalData.searchedAssets);
                break;
            case CiresonConstants.DISPOSE_ASSETS:
                adpt = new ScannedAssetListAdapter(this, globalData.copyOfsearchedAssets);
                break;
            default:
                break;
        }
        if(adpt!=null){
            scannedItemsList.setAdapter(adpt);
            scannedItemsList.setOnItemClickListener(this);
            scannedItemsList.setOnItemLongClickListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(GlobalData.ACTIVE_MAIN_MENU==CiresonConstants.SWAP_ASSETS){
            globalData.getSwaper().setSwap(!globalData.getSwaper().getSwap());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SCANNER_ACTIVITY_IS_ACTIVE = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bluetoothClient!=null){
            bluetoothClient.release();
        }
    }

    /**Receive scanned barcode value and take corresponding actions based on Menu navigated from.
     * If navigation is from Receive Assets, either pass value to scanAssetsToAddAssets or checkMatchedAssets.
     * If navigation is from Inventory Audit, pass barcode value to scanAsstes().
     * If navigation is from Swap Assets, Edit Assets and Dispose Assets, enable next button and pass value to scan Assets..*/
    public void passBarcodeToMatch(String barcodeValue){
        switch(GlobalData.ACTIVE_MAIN_MENU){
            case CiresonConstants.RECEIVED_ASSETS:
                if(GlobalData.SCAN_TO_ADD_ASSETS){
                    boolean isScanned = false;
                    for(Assets a:globalData.searchedAssetsToAddAssets){
                        if(a.assetTag!=null){
                            if(a.assetTag.equals(barcodeValue)){
                                isScanned = true;
                                Toast.makeText(this, "Item already scanned", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }else if(a.serialNumber!=null){
                            if(a.serialNumber.equals(barcodeValue)){
                                isScanned = true;
                                Toast.makeText(this, "Item already scanned", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                    if(!isScanned){
                        scanAssetsToAddAssets(barcodeValue);
                    }
                }else{
                    checkMatchedAssets(barcodeValue);
                }
                break;
            case CiresonConstants.INVENTORY_AUDIT:
                checkMatchedAssets(barcodeValue);
                break;
            case CiresonConstants.SWAP_ASSETS:
            case CiresonConstants.EDIT_ASSETS:
            case CiresonConstants.DISPOSE_ASSETS:
                checkMatchedAssets(barcodeValue);
                break;
            default:
                break;
        }
        ((BaseAdapter) scannedItemsList.getAdapter()).notifyDataSetChanged();
    }

    /**Check each barcode against assets serial number or assetTag, if matched, asset is marked as matched (isMatched = true).
     * If matched, enable next button and if not, create a new asset for this barcode and set this assets as a new asset
     * (isNew = true).*/
    private void checkMatchedAssets(String barcodeValue){
        boolean matched = false;
        Assets matchedAsset = null;
        for (Assets asset : globalData.searchedAssets) {
            if (barcodeValue.equals(asset.assetTag) || barcodeValue.equals(asset.serialNumber)) {
                matched = true;
                matchedAsset = asset;
                if (!asset.isMatched) {
                    asset.isMatched = true;
                    switch (GlobalData.ACTIVE_MAIN_MENU){
                        case CiresonConstants.RECEIVED_ASSETS:
                            GO_TO_NEXT_PAGE = true;
                            break;
                        default:
                            break;
                    }
                } else {
                    Toast.makeText(this, "Item already scanned", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        if (!matched) {
            Assets newAssets = new Assets();
            newAssets.isNew = true;
            newAssets.assetTag = barcodeValue;
            newAssets.serialNumber = barcodeValue;
            globalData.searchedAssets.add(0, newAssets);
            globalData.copyOfsearchedAssets.add(0,newAssets);
        }else{
            if(matchedAsset!=null){
                globalData.searchedAssets.remove(matchedAsset);
                globalData.searchedAssets.add(0,matchedAsset);
            }
        }
        /**/
        switch (GlobalData.ACTIVE_MAIN_MENU){
            case CiresonConstants.EDIT_ASSETS:
                GO_TO_NEXT_PAGE = true;
                break;
            default:
                break;
        }
    }

    /*Only for receive assets.*/
    private void checkMatchedAssetsForReceivedAssets(String barcodeValue){
        boolean matched = false;
        for (Assets asset : globalData.searchedAssets) {
            if (barcodeValue.equals(asset.assetTag) || barcodeValue.equals(asset.serialNumber)) {
                matched = true;
                if (!asset.isMatched) {
                    asset.isMatched = true;
                    GO_TO_NEXT_PAGE = true;
                } else {
                    Toast.makeText(this, "Item already scanned", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        if (!matched) {
            Assets newAssets = new Assets();
            newAssets.isNew = true;
            newAssets.assetTag = barcodeValue;
            newAssets.serialNumber = barcodeValue;
        }
    }

    /*To add new assets, this method is called passing scanned bar codes..*/
    public void scanAssetsToAddAssets(String barcodeValue) {
        /*Create a new Assets object whose asset tag is set as this bar code value..*/
        final Assets newAsset = new Assets();
        newAsset.isNew = true;
        newAsset.assetTag = barcodeValue;
        newAsset.serialNumber = barcodeValue;
        newAsset.isForwarded = true;
        newAsset.originalJson = new JsonObject();
        globalData.searchedAssetsToAddAssets.add(newAsset);
        final ArrayList<Assets> temporaryArrayList = new ArrayList<Assets>();
        temporaryArrayList.add(newAsset);

        /*This field indicates if add assets api call is in the process.*/
        ADD_ASSETS_API_CALL_STATE = true;

        /*Prepare a API Caller for Receive Assets call..*/
        final Type type = new TypeToken<ArrayList<Assets>>() {}.getType();
        APICaller<Assets> apiCallerToAddNewAssets = new APICaller<Assets>(Assets.class, type, this);
        apiCallerToAddNewAssets.setMethodForBaseUrl("GetProjectionByCriteria");
        apiCallerToAddNewAssets.callArrayApi(new SearchAssetsWithBarCode(temporaryArrayList), new APICaller.Callback<ArrayList<Assets>>() {
            @Override
            public void onApiCallComplete(ArrayList<Assets> obj, String error) {
                newAsset.isForwarded = false;
                if (obj != null && obj.size() == 0) {
                    /*If responsed object contains no known asset, requested asset has been processed but not available,
                    i.e. this is a new asset to be added, so enable next button to proceed to add new asset if next button is disabled.*/
                    newAsset.isProcessed = true;
                }/*If responsed object is nut null and if object has no null element then proceed further..*/
                else if (obj != null && (obj.size() > 0 && obj.get(0) != null)) {
                    Assets responsedAsset = obj.get(0);
                    boolean matched = false;
                    /*If assetTag or a serialNumber of both assets (new and responsed) match, replace new asset with the responsed one.*/
                    if (responsedAsset.assetTag != null) {
                        if (responsedAsset.assetTag.equals(newAsset.assetTag) || responsedAsset.assetTag.equals(newAsset.serialNumber)) {
                            matched = true;
                        }
                    } else if (responsedAsset.serialNumber != null) {
                        if (responsedAsset.serialNumber.equals(newAsset.assetTag) || responsedAsset.serialNumber.equals(newAsset.serialNumber)) {
                            matched = true;
                        }
                    }
                    if (matched) {
                        int index = globalData.searchedAssetsToAddAssets.indexOf(newAsset);
                        if (index > -1) {
                            responsedAsset.isNew = false;
                            responsedAsset.isProcessed = true;
                            responsedAsset.isAvailable = true;
                            responsedAsset.isMatched = true;
                            globalData.searchedAssetsToAddAssets.remove(newAsset);
                            globalData.searchedAssetsToAddAssets.add(index, responsedAsset);
                        }
                    } else {
                        newAsset.isProcessed = true;
                    }
                }/*Handling the error message..*/
                else if(error!=null&&!error.trim().isEmpty()){
                    CiresonUtilites.displayMessage(getResources().getString(R.string.error),error,AssetsScannerActivity.this).show(AssetsScannerActivity.this.getFragmentManager(),"");
                }
                /*Update the list view if this activity is currently active..*/
                if (SCANNER_ACTIVITY_IS_ACTIVE) {
                    ((BaseAdapter) scannedItemsList.getAdapter()).notifyDataSetChanged();
                }

                /*Indicates that an API Call process is completed..*/
                ADD_ASSETS_API_CALL_STATE = false;
            }
        });
    }

    /*Ensure if there are any new assets as unknkown to fix them as known or discard..*/
    private void ensureIfUnknowAssetsAvailble(){
        /*Initially assuming Unknown Received Assets fixed, which will not be forwarded to add..*/
        UNKNOWN_RECEIVED_ASSETS_FIXED = true;
        if(globalData.searchedAssetsToAddAssets == null || globalData.searchedAssetsToAddAssets.size() == 0){
            UNKNOWN_RECEIVED_ASSETS_FIXED = false;
        }else{
            for(Assets a:globalData.searchedAssetsToAddAssets){
                if(a.isNew){
                    globalData.searchedAssetsToAddAssetsAfterSelection.add(a);
                    UNKNOWN_RECEIVED_ASSETS_FIXED = false;
                    numberOfAddedAssets++;
                }
            }
        }
        NEW_ASSETS_FILTERED = true;
    }

    private void recreateUi() {
        if(!scanItemController.getItems().isEmpty()){
            findViewById(R.id.banner_scanned_items).setVisibility(View.VISIBLE);
            findViewById(R.id.next_button_scan_assets).setVisibility(View.VISIBLE);
            addItemsToAssetContainer();
        }
        else{
            findViewById(R.id.banner_scanned_items).setVisibility(View.GONE);
            findViewById(R.id.next_button_scan_assets).setVisibility(View.GONE);
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode==RESULT_OK){
            switch(requestCode){
                case REQUEST_CODE_TO_INVENTORY_SEARCH_FOR_EDIT:
                    CiresonUtilites.displayMessage(getString(R.string.message),intent.getStringExtra(InventorySearchActivity.INVENTORY_TO_SCANNER_FOR_EDIT_AND_DISPOSE),this).show(this.getFragmentManager(),"");
                    break;
                case REQUEST_CODE_TO_INVENTORY_SEARCH_FOR_DISPOSE:
                    CiresonUtilites.displayMessage(getString(R.string.message),intent.getStringExtra(InventorySearchActivity.INVENTORY_TO_SCANNER_FOR_EDIT_AND_DISPOSE),this).show(this.getFragmentManager(),"");
                    break;
                case REQUEST_CODE_TO_POSSIBLE_MATCHED_ASSETS:
                    GO_TO_NEXT_PAGE = true;
                    break;
                case BluetoothClient.REQ_DISCOVER_DEVICE:
                    globalData.currentAssetsScannerActivity = this;
                    bluetoothClient.setCallbackForResult(this);
                    break;
                case BluetoothClient.REQ_ENABLE_BLUETOOTH:
                    GlobalData.ACTIVE_BLUETOOTH_CALLER = BluetoothClient.SCAN_ASSETS_ACTIVITY;
                    AsyncTask delayBlueTooth = new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] params) {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            bluetoothClient.proceedToBluetoothScan();
                            super.onPostExecute(o);
                        }
                    };
                    delayBlueTooth.execute();
                    break;
                case BluetoothClient.REQ_DEVICESELECTED:
                    globalData.currentAssetsScannerActivity = this;
                    bluetoothClient.setCallbackForResult(this);
                    break;
                case REQUEST_CODE_TO_INVENTORY_SEARCH_FOR_SWAP:
                    Swaper.setMessage(intent.getStringExtra(InventorySearchActivity.INVENTORY_SEARCH_EXTRA));
                    break;
                default:
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
                                    recreateUi();
                                }
                            } else {
                                showDialogForLocalMessage(getResources().getString(R.string.result_failed),
                                        getString(R.string.result_failed_why));
                            }
                        }
                    }catch (Exception ex){
                        Log.d(TAG, Log.getStackTraceString(ex));
                    }
                    break;
            }
        }else if(resultCode==BluetoothClient.REQ_DISCOVER_TIME){
            Intent i = new Intent(this, BluetoothPairedDeviceListActivity.class);
            GlobalData.getInstance().bluetoothClient = bluetoothClient;
            startActivityForResult(i, BluetoothClient.REQ_DISCOVER_DEVICE);
        }
    }

    /*This dialog is used to display message locally with in this activity..*/
    private void showDialogForLocalMessage(String title, CharSequence message) {
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

    /**For Receive assets (Not Add Assets) and Inventory Audit and others if required, count for matched assets and return the count..*/
    private int countMatchedAssets(){
        int count = 0;
        for(Assets asset : globalData.searchedAssets){
            if(asset.isMatched){
                count++;
            }
        }
        return count;
    }

    /**Proceed to the next activity as a result of positive response from the confirmation dialog..*/
    public void proceedToNext(){
        Intent i = new Intent(this, TYPE_OF_NEXT_ACTIVITY_AFTER_SCAN);
        int count = 0;
        switch(GlobalData.ACTIVE_MAIN_MENU){
            case CiresonConstants.RECEIVED_ASSETS:
                if(GlobalData.SCAN_TO_ADD_ASSETS){
                    i.putExtra(AssetsEditorActivity.EXTRA_ASSETS_COUNT,numberOfAddedAssets);
                }else{
                    count = countMatchedAssets();
                    i.putExtra(AssetsEditorActivity.EXTRA_ASSETS_COUNT, count);
                }
                startActivity(i);
                break;
            case CiresonConstants.SWAP_ASSETS:
                if(globalData.searchedAssets!=null&&globalData.searchedAssets.size()>0){
                    startActivityForResult(i,REQUEST_CODE_TO_INVENTORY_SEARCH_FOR_SWAP);
                }else{
                    CiresonUtilites.displayMessage(getString(R.string.error),getString(R.string.scan_assets_no_asset_to_go_to_next_page),this)
                            .show(this.getFragmentManager(),"");
                }
                break;
            case CiresonConstants.EDIT_ASSETS:
                if(globalData.searchedAssets!=null){
                    count = globalData.searchedAssets.size();
                }
                i.putExtra(AssetsEditorActivity.EXTRA_ASSETS_COUNT, count);
                startActivityForResult(i,REQUEST_CODE_TO_INVENTORY_SEARCH_FOR_EDIT);
                break;
            case CiresonConstants.INVENTORY_AUDIT:
                count = countMatchedAssets();
                i.putExtra(AssetsEditorActivity.EXTRA_ASSETS_COUNT, count);
                startActivity(i);
                break;
            case CiresonConstants.DISPOSE_ASSETS:
                if(globalData.copyOfsearchedAssets!=null&&globalData.copyOfsearchedAssets.size()>0){
                    globalData.searchedAssets.addAll(globalData.copyOfsearchedAssets);
                }
                if((globalData.searchedAssets!=null&&globalData.searchedAssets.size()>0)&&
                   (globalData.copyOfsearchedAssets!=null&&globalData.copyOfsearchedAssets.size()>0)){
                    startActivityForResult(i,REQUEST_CODE_TO_INVENTORY_SEARCH_FOR_DISPOSE);
                }else{
                    CiresonUtilites.displayMessage(getString(R.string.error),getString(R.string.scan_assets_no_asset_to_go_to_next_page),this)
                            .show(this.getFragmentManager(),"");
                }
                break;
            default:
                break;
        }
    }

    /*Confirm activity navigation in the case of Add Assets..*/
    private void confirmNavigationForAddAssets(){
        /*If there is no any new data to be added..*/
        if(globalData.searchedAssetsToAddAssets==null||globalData.searchedAssetsToAddAssets.size()==0) {
            CiresonUtilites.displayMessage(
                    getString(R.string.error),
                    getResources().getString(R.string.scan_assets_no_asset_to_add),this)
                    .show(getFragmentManager(),"");
        }else{
            String messageToDisplay = getResources().getString(R.string.scan_assets_title_for_dialog_when_scan_for_receive_Assets_changed);
            dialog = new CiresonDialogs();
            dialog.setContext(this);
            dialog.setTitle(getResources().getString(R.string.dialog_confirmation))
                    .setMessage(messageToDisplay)
                    .setNumberOfButtons(2)
                    .setPositiveButtonMessage(getResources().getString(R.string.scan_assets_choice_serial_number))
                    .setNegativeButtonMessage(getResources().getString(R.string.scan_assets_choice_asset_tag));
            class ScanAssetsAlertDialog implements CiresonDialogs.CiresonDialogActions<ScanAssetsAlertDialog> {
                @Override
                public void handleDialogsClickEvent(DialogInterface dialogInterface, int i) {
                    switch (i) {
                            /*If serial tag is to be assigned..*/
                        case DialogInterface.BUTTON_POSITIVE:
                            for(Assets a : globalData.searchedAssetsToAddAssets){
                                a.assetTag = null;
                                a.isNew = false;
                            }
                            UNKNOWN_RECEIVED_ASSETS_FIXED = true;
                            proceedToNext();
                            dialog.dismiss();
                            break;
                        /**If asset tag is to be assigned..*/
                        case DialogInterface.BUTTON_NEGATIVE:
                            for (Assets a : globalData.searchedAssetsToAddAssets) {
                                a.serialNumber = null;
                                a.isNew = false;
                            }
                            UNKNOWN_RECEIVED_ASSETS_FIXED = true;
                            proceedToNext();
                            dialog.dismiss();
                            break;
                        default:
                            break;
                    }
                }
            }
            dialog.setCiresonDialogActions(new ScanAssetsAlertDialog());
            dialog.show(this.getFragmentManager(), "");
        }
    }

    /*Confirm navigation to other activities.
    * 1. Receive Assets : if any new asset is identified during scanning, next button is enabled which
    * if clicked this confirmation has to be made.*/
    private void confirmNavigation(){
        /*Set a message to be displayed to a user based upon active current action
        (i.e. whether action is scan for receive assets or others)..*/
        String messageToDisplay = getResources().getString(R.string.scan_assets_dialog_message);
        /*Assign respective arraylist to a temporary arraylist before an action defined here..*/
        final ArrayList<Assets> TEMP_ARRAY_LIST = getActiveGlobalSearchedAssetsList();
        if(TEMP_ARRAY_LIST!=null) {
            boolean allMatched = true;
            for (Assets asset : TEMP_ARRAY_LIST) {
                if (!asset.isMatched) {
                    allMatched = false;
                    break;
                }
            }
            if (!allMatched && GlobalData.ACTIVE_MAIN_MENU!=CiresonConstants.EDIT_ASSETS && GlobalData.ACTIVE_MAIN_MENU!=CiresonConstants.SWAP_ASSETS){
                dialog = new CiresonDialogs();
                dialog.setContext(this);
                dialog.setTitle(getResources().getString(R.string.dialog_confirmation))
                        .setMessage(messageToDisplay)
                        .setNumberOfButtons(2)
                        .setPositiveButtonMessage(getResources().getString(R.string.dialog_yes))
                        .setNegativeButtonMessage(getResources().getString(R.string.dialog_no));
                class ScanAssetsAlertDialog implements CiresonDialogs.CiresonDialogActions<ScanAssetsAlertDialog> {
                    public void handleDialogsClickEvent(DialogInterface di, int i) {
                        switch (i) {
                            case DialogInterface.BUTTON_POSITIVE:
                                    /*Proceed to next activity..*/
                                    proceedToNext();
                                    dialog.dismiss();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                 /*Dispose dialog..*/
                                dialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                };
                dialog.setCiresonDialogActions(new ScanAssetsAlertDialog());
                dialog.show(this.getFragmentManager(), "");
            }else{
                proceedToNext();
            }
        }else{
            Toast.makeText(this,getResources().getText(R.string.toast_message_for_scan_assets_activity),Toast.LENGTH_SHORT).show();
        }
    }

    /*Proceeding to next activity..*/
    public void goNext(View v){
        startScanner(false);
        switch(GlobalData.ACTIVE_MAIN_MENU){
            case CiresonConstants.RECEIVED_ASSETS:
                /*In the case of receive assets, if all unknown receive assets are already known or converted to isNew = false,
                * no need to confirm with any message. If there are unknown items display the message for further action.*/
                if(GlobalData.SCAN_TO_ADD_ASSETS){
                    if(ADD_ASSETS_API_CALL_STATE){
                        CiresonUtilites.displayMessage(
                                getResources().getString(R.string.error),
                                getResources().getString(R.string.scan_assets_add_assets_api_call_in_process),this)
                                .show(getFragmentManager(),"");
                    }else{
                        ensureIfUnknowAssetsAvailble();
                        if(numberOfAddedAssets>0) {
                            if (!UNKNOWN_RECEIVED_ASSETS_FIXED) {
                                confirmNavigationForAddAssets();
                            } else {
                                proceedToNext();
                            }
                        }
                        else{
                            CiresonUtilites.displayMessage(getString(R.string.error),getString(R.string.scan_assets_no_new_barcode),this)
                                    .show(this.getFragmentManager(),"");
                        }
                    }
                }else{
                    if(GO_TO_NEXT_PAGE){
                        confirmNavigation();
                    }else{
                        /*Display a relevant message..*/
                        CiresonUtilites.displayMessage(getString(R.string.error),getString(R.string.scan_assets_no_matched_assets_to_go_to_next_page),this)
                                .show(getFragmentManager(),"");
                    }
                }
                break;
            case CiresonConstants.EDIT_ASSETS:
                if(globalData.copyOfsearchedAssets!=null&&globalData.copyOfsearchedAssets.size()>0){
                    globalData.searchedAssets.addAll(globalData.copyOfsearchedAssets);
                }
                if((globalData.searchedAssets!=null&&globalData.searchedAssets.size()>0)&&
                        (globalData.copyOfsearchedAssets!=null&&globalData.copyOfsearchedAssets.size()>0)&&
                        GO_TO_NEXT_PAGE){
                    confirmNavigation();
                }else{
                    CiresonUtilites.displayMessage(getString(R.string.error),getString(R.string.scan_assets_no_asset_to_go_to_next_page),this)
                            .show(this.getFragmentManager(),"");
                }
                break;
            case CiresonConstants.SWAP_ASSETS:
            case CiresonConstants.INVENTORY_AUDIT:
            case CiresonConstants.DISPOSE_ASSETS:
                proceedToNext();
                break;
            default:
                break;
        }
    }

    private void addItemsToAssetContainer(){
        if(!scanItemController.getItems().isEmpty()){
            ScrollView sv = (ScrollView) findViewById(R.id.items_container_scan_assets);
            sv.removeAllViews();

            LinearLayout ll = new LinearLayout(this);
            ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            // edited by subash
            //ll.setOrientation(1);
            ll.setOrientation(LinearLayout.VERTICAL);

            Iterator<ScanItemModel> iterator = scanItemController.getItems().iterator();
            LayoutInflater inflater =  (LayoutInflater)this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

            while(iterator.hasNext()){
                LinearLayout ll2 = (LinearLayout) inflater.inflate( R.layout.scan_assets_row, null );
                ll2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT));
                TextView tv = (TextView) ll2.findViewById(R.id.subTitleTextView);
                tv.setText("#"+ iterator.next().getScanItemText());
                ll.addView(ll2);
            }
            sv.addView(ll);
        }
    }

    /**For received assets, (Either searched assets by PO Number or browsed by purchase order),
     after a list in a list view is clicked, an asset item of a clicked item is verified if its a
     new asset item. If this asset is new, a navigation is made to next activity which shows all the unmatched
     assets received or scanned. Then, in the new activity, if any unmatched asset item is clicked, its asset tag
     or a serial number is assigned to that of new asset.*/
    private void navToPossibleMatchedAssets(View view,int position){
        /*Assign respective arraylist to a temporary arraylist before an action defined here..*/
        final ArrayList<Assets> TEMP_ARRAY_LIST = globalData.searchedAssets;
        view.setSelected(true);
        Assets a = (Assets) scannedItemsList.getAdapter().getItem(position);
        if(a.isNew){
            globalData.setImmediateObject((Assets) a);
            Intent i = new Intent(this,PossibleMatchedAssetsActivity.class);
            i.putExtra(PossibleMatchedAssetsActivity.NEW_BARCODE,TEMP_ARRAY_LIST.get(position).assetTag);
            startActivityForResult(i,REQUEST_CODE_TO_POSSIBLE_MATCHED_ASSETS);
        }
    }

    /*Return an active arraylist */
    private ArrayList<Assets> getActiveGlobalSearchedAssetsList(){
        if(GlobalData.SCAN_TO_ADD_ASSETS){
            return  globalData.searchedAssetsToAddAssets;
        }else{
            return  globalData.searchedAssets;
        }
    }

    /** Based on the parameter value passed, if scannerState is true first check for the bluetooth state,
    if bluetooth is active, first release the bluetooth resource and activate zxing fragment camera scanner.
    If scannerState is false, deactivate zxing fragments camera scanner.*/
    private void startScanner(boolean scannerState){
        if(scannerState){
            if(bluetoothClient!=null){
                bluetoothClient.release();
            }
            try{
                if(!useCameraScanner){
                    useCameraScanner = true;
                    zxingFragmentContainer.setVisibility(View.VISIBLE);
                    scannerHelp.setVisibility(View.INVISIBLE);
                }
            }catch(Exception e){
                /**/
                e.printStackTrace();
            }
        }else{
            if(useCameraScanner){
                useCameraScanner = false;
                zxingFragmentContainer.setVisibility(View.INVISIBLE);
                scannerHelp.setVisibility(View.VISIBLE);
            }
        }
    }

    /**start zxing camera scanner to scan items..*/
    public void useScanner(View view){
        /*Make the scanner visible..*/
        startScanner(true);
        if(!ASYN_SCAN_TASK){
            if(bluetoothClient!=null){
                bluetoothClient.release();
            }
            zxingFragment.setDecodeCallback(new DecodeCallback() {
                @Override
                public void handleBarcode(Result result, Bitmap barcode, float scaleFactor) {
                    passBarcodeToMatch(result.getText());
                    /*After a barcode is scanned, delay the scan for 1.5 seconds..*/
                    zxingFragment.restartScanningIn(1500);
                }
            });
            ASYN_SCAN_TASK = true;
        }
    }

    /**start bluetooth communication..*/
    public void useBluetooth(View view){
        /*Ensure that scanner fragment is invisible..*/
        startScanner(false);
        if(bluetoothClient==null){
            GlobalData.ACTIVE_BLUETOOTH_CALLER = BluetoothClient.SCAN_ASSETS_ACTIVITY;
            bluetoothClient = new BluetoothClient(this);
        }
        if(bluetoothClient.enableBlueTooth(BluetoothClient.REQ_ENABLE_BLUETOOTH)){
            GlobalData.ACTIVE_BLUETOOTH_CALLER = BluetoothClient.SCAN_ASSETS_ACTIVITY;
            bluetoothClient.proceedToBluetoothScan();
        }
    }

    @Override
    public void bluetoothResult(String result) {
        if(result!=null && !result.isEmpty()){
            passBarcodeToMatch(result);
        }else{
            Toast.makeText(this,"Null or empty scanned value",Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (GlobalData.ACTIVE_MAIN_MENU){
            case CiresonConstants.RECEIVED_ASSETS:
                if(!GlobalData.SCAN_TO_ADD_ASSETS){
                    /*First of all check, if there are searched assets available in globalData.searchedAssets array.
                    * This array holds associated assets for given purchase order. If no assets are available, display
                    * an error message..*/
                     if(!globalData.isAssociatedAssetsAvailable){
                        showDialogForLocalMessage(getResources().getString(R.string.error),
                                getResources().getString(R.string.scan_assets_no_matched_assets_error_message));
                    }else{
                        navToPossibleMatchedAssets(view, position);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        /*First get assets list.
        If scanner is active for adding new assets, take assets from globalData.searchedAssetsToAddAssets
        else get assets from globalData.searchedAssets.*/
        ArrayList<Assets> assetsList = null;
        if(GlobalData.SCAN_TO_ADD_ASSETS){
            assetsList = globalData.searchedAssetsToAddAssets;
        }else{
            if(GlobalData.ACTIVE_MAIN_MENU==CiresonConstants.EDIT_ASSETS||
               GlobalData.ACTIVE_MAIN_MENU==CiresonConstants.DISPOSE_ASSETS){
                assetsList = globalData.copyOfsearchedAssets;
            }else{
                assetsList = globalData.searchedAssets;
            }
        }
        final ArrayList<Assets> TEMP_ARRAY_LIST = assetsList;
        if(TEMP_ARRAY_LIST!=null){
            /*If only the item selected (long clicked) is new or unknown, proceed to remove from the list.*/
            if(TEMP_ARRAY_LIST.get(position).isNew||
                    GlobalData.ACTIVE_MAIN_MENU == CiresonConstants.SWAP_ASSETS||
                    GlobalData.ACTIVE_MAIN_MENU == CiresonConstants.EDIT_ASSETS||
                    GlobalData.ACTIVE_MAIN_MENU == CiresonConstants.DISPOSE_ASSETS
                    ){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.warning));
                alertDialogBuilder
                        .setMessage(getResources().getString(R.string.scan_assets_title_for_warning_dialog))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                /*With positive button clicked, remove the item from the list..*/
                                TEMP_ARRAY_LIST.remove(position);
                                NEW_ASSETS_FILTERED = false;
                                UNKNOWN_RECEIVED_ASSETS_FIXED = false;
                                ((BaseAdapter) scannedItemsList.getAdapter()).notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                /*Do no thing..*/
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
        return true;
    }

    public static class ScannedAssetListAdapter extends BaseAdapter{
        private LayoutInflater layoutInflater;
        private ArrayList<Assets> assets;
        private Context context;

        public ScannedAssetListAdapter(Context ctx, ArrayList<Assets> assets){
            this.assets = assets;
            this.context = ctx;
            this.layoutInflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return assets.size();
        }

        @Override
        public Assets getItem(int position) {
            return assets.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View cellRow = convertView;
            if(cellRow==null){
                cellRow = layoutInflater.inflate(R.layout.scan_assets_row, parent, false);
            }
            Assets curItem = getItem(position);
            TextView titleTextView = (TextView) cellRow.findViewById(R.id.titleTextView);
            TextView searchingTextView = (TextView) cellRow.findViewById(R.id.idScanActivityRowSearchingText);
            TextView subTitleTextView = (TextView) cellRow.findViewById(R.id.subTitleTextView);
            ImageView rightImageView = (ImageView) cellRow.findViewById(R.id.rightImageView);
            GlobalData data = GlobalData.getInstance();
            if(curItem.isNew) {
                if(data.gone)
                {
                    titleTextView.setText(curItem.assetTag);
                    subTitleTextView.setText("");
                    rightImageView.setImageBitmap(null);
                    rightImageView.setVisibility(View.GONE);
                    subTitleTextView.setVisibility(View.GONE);
                }else {
                    titleTextView.setText(curItem.assetTag);
                    subTitleTextView.setText("");
                    rightImageView.setImageBitmap(null);
                    subTitleTextView.setVisibility(View.GONE);
                    /*If this list view is being updated while adding new assets..*/
                    if(GlobalData.SCAN_TO_ADD_ASSETS){
                        /*Initially show "searching..." view and hide right image view.*/
                        searchingTextView.setVisibility(View.VISIBLE);
                        rightImageView.setVisibility(View.GONE);
                        if(curItem.isForwarded){
                            searchingTextView.setText(R.string.scan_assets_list_row_searching_text);
                        }else{
                            if (!curItem.isProcessed && !curItem.isAvailable) {
                                searchingTextView.setText(R.string.scan_assets_list_row_search_error_text);
                            }
                            else if(curItem.isProcessed&&!curItem.isAvailable) {
                                if(searchingTextView.getVisibility()==View.VISIBLE){
                                    searchingTextView.setVisibility(View.GONE);
                                }
                                titleTextView.setText(curItem.assetTag + " (unknown)");
                                rightImageView.setImageResource(R.drawable.question_mark);
                                rightImageView.setVisibility(View.VISIBLE);
                                }
                        }
                    }
                    else if(GlobalData.ACTIVE_MAIN_MENU==CiresonConstants.RECEIVED_ASSETS){
                        rightImageView.setImageResource(R.drawable.list_arrow_new);
                        rightImageView.setVisibility(View.VISIBLE);
                    }
                    else{
                        rightImageView.setVisibility(View.GONE);
                    }
                }
            }
            else {
                if (data.gone){
                    titleTextView.setText(curItem.serialNumber);
                    subTitleTextView.setText(curItem.assetTag);
                    subTitleTextView.setVisibility(View.VISIBLE);
                    rightImageView.setVisibility(View.GONE);
                }else{
                    subTitleTextView.setVisibility(View.VISIBLE);
                    if(rightImageView.getVisibility()!=View.VISIBLE){
                        rightImageView.setVisibility(View.VISIBLE);
                    }
                    if (curItem.isMatched) {
                        rightImageView.setImageResource(R.drawable.ok);
                    }else{
                        rightImageView.setImageResource(R.drawable.question_mark);
                    }
                    /*If this list view is being updated while adding new assets, replace the title text view with asset tag or a serial
                    * number and sub title text view with a display name..*/
                    if(GlobalData.SCAN_TO_ADD_ASSETS){
                        if(curItem.assetTag==null){
                            titleTextView.setText(curItem.serialNumber);
                        }else{
                            titleTextView.setText(curItem.assetTag);
                        }
                        subTitleTextView.setText(curItem.displayName);
                        if(searchingTextView.getVisibility()==View.VISIBLE){
                            searchingTextView.setVisibility(View.GONE);
                        }
                    }else{
                        titleTextView.setText(curItem.name);
                        subTitleTextView.setText(curItem.getAssetTypeDisplayName());
                    }
                }
            }
            return cellRow;
        }
    }

    private AsyncTask<Void,Integer, Void> fakeScanner = new AsyncTask<Void, Integer, Void>() {

        String[] fakeBarcodeValues = new String[]{"32151","3211","3210","3209"};

        @Override
        protected Void doInBackground(Void... params) {
            for(int i=0;i<fakeBarcodeValues.length;i++){
                try {
                    Thread.sleep(500);
                    publishProgress(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int i = values[0];
            passBarcodeToMatch(fakeBarcodeValues[i]);
            super.onProgressUpdate(values);
        }

    };
}

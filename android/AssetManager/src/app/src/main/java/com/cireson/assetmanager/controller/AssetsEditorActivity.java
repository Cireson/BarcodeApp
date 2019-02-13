
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.assistance.Swaper;
import com.cireson.assetmanager.model.Assets;
import com.cireson.assetmanager.model.CiresonEnumeration;
import com.cireson.assetmanager.model.CommonSaveModel;
import com.cireson.assetmanager.model.CostCenter;
import com.cireson.assetmanager.model.CustodianUser;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.model.Location;
import com.cireson.assetmanager.model.Organization;
import com.cireson.assetmanager.model.ServiceResponse;
import com.cireson.assetmanager.service.APICaller;
import com.cireson.assetmanager.service.BatchAPICaller;
import com.cireson.assetmanager.util.CiresonConstants;
import com.cireson.assetmanager.util.CiresonUtilites;

import java.util.ArrayList;

/**
 * Created by devkhadka on 3/29/14.
 */
public class AssetsEditorActivity extends CiresonBaseActivity implements  AbsListView.OnItemClickListener{

    /*Instances..*/
    /*Views required to update..*/
    private TextView textViewToUpdate;
    private Button nextButton;

    /*Other required instances ..*/
    private GlobalData globalData;

    /*Array lists to hold  multiple selection items from each categories (status, locations, organizations, costcenters and custodian users)..*/
    static ArrayList<CiresonEnumeration> statusLists = new ArrayList<CiresonEnumeration>();
    static ArrayList<Location> locationLists = new ArrayList<Location>();
    static ArrayList<Organization> organizationLists = new ArrayList<Organization>();
    static ArrayList<CostCenter> costCenterLists = new ArrayList<CostCenter>();
    static ArrayList<CustodianUser> custodianLists = new ArrayList<CustodianUser>();

    /*Array of string that holds asset name, serial number and tag name of currently selected asset to swap..*/
    private static String[] assetDetailsForSwap = new String[8];

    /*Count navigation only for swap assets..*/
    public static int count = 0;

    /*Temporary static list of assets to hold assets while saving..*/
    private static ArrayList<Assets> TEMP_ASSETS_LIST = new ArrayList<Assets>();

    /*Static fields for request codes used for handling result when intent call returns..*/
    private final static int REQUEST_CODE_STATUS = 0;
    private final static int REQUEST_CODE_LOCATION = 1;
    private final static int REQUEST_CODE_ORGANIZATION = 2;
    private final static int REQUEST_CODE_CUSTODIAN = 3;
    private final static int REQUEST_CODE_COST_CENTER = 4;
    private final static int REQUEST_CODE_RECEIVED_DATE = 5;
    private final static int REQUEST_CODE_NONE = 6;

    /*Request codes for manufacturer and model fields for the case of add assets..*/
    private final static int REQUEST_CODE_MANUFACTURER = 9;
    private final static int REQUEST_CODE_MODEL = 10;
    private final static int REQUEST_CODE_SWAP_ASSETS_NOTE_EDITOR = 11;
    private final static int REQUEST_CODE_PRIMARY_USER = 12;

    public static final String EXTRA_ASSETS_COUNT = "AssetsEditorActivity.EXTRA_ASSETS_COUNT";
    public static final String EDIT_ASSETS_RESULT = "AssetsEditorActivity.EDIT_ASSETS_RESULT";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assets);

        /*Get views to change their display as required..*/
        ListView listView = (ListView)findViewById(R.id.editAssetsListView);
        TextView txtCount = (TextView)findViewById(R.id.editAssetsResultCount);
        TextView txtTitle = (TextView)findViewById(R.id.editAssetsTitle);
        nextButton = (Button)findViewById(R.id.btnNext);

        globalData = GlobalData.getInstance();
        globalData.statusEnumerationList = null;

        /*If this navigation is not from swap assets of main menu, set count text in a head to 0 inititally... */
        if(GlobalData.ACTIVE_MAIN_MENU!=CiresonConstants.SWAP_ASSETS){
            txtCount.setText(String.valueOf(getIntent().getIntExtra(EXTRA_ASSETS_COUNT, 0)));
        }

        /*Based on the navigation, initialize instances and configure required settings. Edit assets activity is called
        for different menus..*/
        switch(GlobalData.ACTIVE_MAIN_MENU){
            case CiresonConstants.RECEIVED_ASSETS:
                if(GlobalData.SCAN_TO_ADD_ASSETS){
                    txtTitle.setText(getResources().getText(R.string.add_assets_title));
                }
                else {
                    txtTitle.setText(getResources().getText(R.string.edit_assets_title));
                }
                break;
            case CiresonConstants.SWAP_ASSETS:
                /*During swap, in the first round, no asset property is selected. So, assetDetailForSwap[] array contains no selected values
                 * to display in the list initially. But, in the second round, selected items from the first round are to be shown in the lists....*/
                txtCount.setText(getResources().getText(R.string.edit_assets_title_for_swap_count));
                txtTitle.setText(getResources().getText(R.string.edit_assets_title));
                if(globalData.getSwaper().getSwap()){
                    setAssetsDetailForSwap(1);
                    nextButton.setText(getResources().getText(R.string.next));
                }else{
                    setAssetsDetailForSwap(2);
                    nextButton.setText(getResources().getText(R.string.button_save));
                }
                Swaper.SKIP_ON_BACK_PRESS = false;
                break;
            case CiresonConstants.EDIT_ASSETS:
                txtCount.setVisibility(View.GONE);
                txtTitle.setText(getResources().getText(R.string.edit_assets_title));
                break;
            case CiresonConstants.INVENTORY_AUDIT:
                /*If call to this activity from Inventory Audit..*/
                txtCount.setVisibility(View.GONE);
                txtTitle.setText(getResources().getText(R.string.edit_assets_title_for_inventory_audit));
                nextButton.setText(getResources().getText((R.string.next)));
                break;
            default:
                break;
        }
        /*Set list view adapter now..*/
        listView.setAdapter(new EditAssetsListAdapter(this));
        listView.setOnItemClickListener(this);
    }

    public void setAssetsDetailForSwap(int when){
        switch (when){
            case 1:
                /*In first round of swap..*/
                String statusName = globalData.getSwaper().getProperitesMapForFirstAsset().get("statusName");
                String locationDisplayName = globalData.getSwaper().getProperitesMapForFirstAsset().get("locationDisplayName");
                String organizationDisplayName = globalData.getSwaper().getProperitesMapForFirstAsset().get("organizationDisplayName");
                String costCenterDisplayName = globalData.getSwaper().getProperitesMapForFirstAsset().get("costCenterDisplayName");
                String loanReturnedDate = globalData.getSwaper().getProperitesMapForFirstAsset().get("loanReturnedDate");
                String notes = globalData.getSwaper().getProperitesMapForFirstAsset().get("notes");
                assetDetailsForSwap[0] = (statusName = (statusName==null?"":statusName));
                assetDetailsForSwap[1] = (locationDisplayName = (locationDisplayName==null?"":locationDisplayName));
                assetDetailsForSwap[2] = (organizationDisplayName = (organizationDisplayName==null?"":organizationDisplayName));
                assetDetailsForSwap[3] = (costCenterDisplayName = (costCenterDisplayName==null?"":costCenterDisplayName));
                assetDetailsForSwap[4] = (loanReturnedDate = (loanReturnedDate==null?"":loanReturnedDate));
                assetDetailsForSwap[5] = "";
                break;
            case 2:
                /*In second round of swap..*/
                String statusName2 = globalData.getSwaper().getProperitesMapForSecondAsset().get("statusName");
                String locationDisplayName2 = globalData.getSwaper().getProperitesMapForSecondAsset().get("locationDisplayName");
                String organizationDisplayName2 = globalData.getSwaper().getProperitesMapForSecondAsset().get("organizationDisplayName");
                String custodianUserDisplayName = globalData.getSwaper().getProperitesMapForSecondAsset().get("custodianUserDisplayName");
                String costCenterDisplayName2 = globalData.getSwaper().getProperitesMapForSecondAsset().get("costCenterDisplayName");
                String loanedDate = globalData.getSwaper().getProperitesMapForSecondAsset().get("loanedDate");
                String notes2 = globalData.getSwaper().getProperitesMapForSecondAsset().get("notes");
                assetDetailsForSwap[0] = (statusName2 = (statusName2==null?"":statusName2));
                assetDetailsForSwap[1] = (locationDisplayName2 = (locationDisplayName2==null?"":locationDisplayName2));
                assetDetailsForSwap[2] = (organizationDisplayName2 = (organizationDisplayName2==null?"":organizationDisplayName2));
                assetDetailsForSwap[3] = (custodianUserDisplayName = (custodianUserDisplayName==null?"":custodianUserDisplayName));
                assetDetailsForSwap[4] = (costCenterDisplayName2 = (costCenterDisplayName2==null?"":costCenterDisplayName2));
                assetDetailsForSwap[5] = (loanedDate = (loanedDate==null?"":loanedDate));
                assetDetailsForSwap[6] = "";
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(GlobalData.ACTIVE_MAIN_MENU==CiresonConstants.SWAP_ASSETS){
            Swaper.SKIP_ON_BACK_PRESS = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocOrgCcSelectionActivity.clearListsOfItems();
    }

    /**Save received assets after setting necessary properties..*/
    private void saveReceivedAssets(){
        /*Initialize Progress Dialog..*/
        CiresonUtilites.initLoadingDialog(R.string.progress_dialog_message, R.style.cireson_progress_dialog_theme, this);
        CiresonUtilites.getLoadingDialog().show();
        ArrayList<Assets> receivedAssets = new ArrayList<Assets>();
        for(Assets a:globalData.searchedAssets){
            if(!a.isNew){
                a.setAssetTag(a.assetTag);
                a.setSerialNumber(a.serialNumber);
                receivedAssets.add(a);
            }
        }

        globalData.getEditor().setAssetPropertiesForReceiveAssets(receivedAssets);
        globalData.getSavedReceiveAssets().projectionObject.associatedAssets = receivedAssets;

        APICaller<ServiceResponse> apiCaller = new APICaller<ServiceResponse>(ServiceResponse.class,this);
        apiCaller.setMethodForBaseUrl("UpdateProjection");
        apiCaller.callApi(globalData.getSavedReceiveAssets().toJson(),new APICaller.Callback<ServiceResponse>(){
            @Override
            public void onApiCallComplete(ServiceResponse obj, String error) {
                CiresonUtilites.getLoadingDialog().dismiss();
                if(obj!=null){
                    AssetsScannerActivity.ENABLE_NEXT_BUTTON = false;
                    globalData.searchedAssets.clear();
                    globalData.resetTemporaryAsset();
                    navigateToMain();
                }else{
                    /*Show an error message..*/
                    if(error!=null&&!error.trim().isEmpty()){
                        CiresonUtilites.displayMessage(getResources().getString(R.string.error),error,AssetsEditorActivity.this).show(AssetsEditorActivity.this.getFragmentManager(),"");
                    }
                }
            }
        });
    }

    /**Save new assets to add them after setting required properties..*/
    private void saveNewAssetsToAdd(){
        /*Initialize Progress Dialog..*/
        CiresonUtilites.initLoadingDialog(R.string.progress_dialog_message, R.style.cireson_progress_dialog_theme, this);
        CiresonUtilites.getLoadingDialog().show();

        /*Set all necessary properties for new assets..*/
        globalData.getEditor().setAssetPropertiesForAddAssets(globalData.searchedAssetsToAddAssetsAfterSelection);

        /*Set name and displayName for the new assets..*/
        Assets.setNamesForAddedAssets(globalData.searchedAssetsToAddAssetsAfterSelection);

        /*Generate array list that holds objects of type CommonSaveModel to be forwarded to a BatchAPICaller..*/
        ArrayList<CommonSaveModel> addedAssets = new ArrayList<CommonSaveModel>();
        for(Assets a:globalData.searchedAssetsToAddAssetsAfterSelection){
            CommonSaveModel model = new CommonSaveModel();
            model.projectionObject = a;
            addedAssets.add(model);
        }
        /*Assining this field to true. This field will be used by a BaseJSON to serialize null values along with others..*/
        GlobalData.SAVE_NEW_ADDED_ASSETS = true;
        /*Call service to save changes after full swap..*/
        BatchAPICaller batchAPICaller = new BatchAPICaller(addedAssets,this,new BatchAPICaller.BatchAPICallback(){
            @Override
            public void onBatchRequestComplete(String[] successBatchResponses, String[] failureBatchResponses, String exceptionMessage) {
                /*Assing this field to back to false so that BaseJSON component works with default serializer..*/
                GlobalData.SAVE_NEW_ADDED_ASSETS = false;
                CiresonUtilites.getLoadingDialog().dismiss();
                if(exceptionMessage!=null){
                    /*Display exceptional message..*/
                    CiresonUtilites.displayMessage(getResources().getString(R.string.error),exceptionMessage,AssetsEditorActivity.this).show(AssetsEditorActivity.this.getFragmentManager(),"");
                }else{
                    if(failureBatchResponses.length>0){
                        /*Display failure responses..*/
                        CiresonUtilites.displayMessage(getResources().getString(R.string.error),failureBatchResponses[0],AssetsEditorActivity.this).show(AssetsEditorActivity.this.getFragmentManager(),"");
                    }else{
                        /*New assets are added and the list holding those assets is now cleared..*/
                        globalData.searchedAssetsToAddAssets.clear();
                        globalData.searchedAssetsToAddAssetsAfterSelection.clear();
                        globalData.resetTemporaryAsset();
                        GlobalData.SCAN_TO_ADD_ASSETS = false;
                        navigateToMain();
                    }
                }
            }
        });
        batchAPICaller.sendBatchRequests("UpdateProjection");
    }

    /**Save swapped assets after editing properties..*/
    private void saveSwappedAssets(){
        /*If no need to swap further..*/
        if(!globalData.getSwaper().getSwap()){
            /*Initialize Progress Dialog..*/
            CiresonUtilites.initLoadingDialog(R.string.progress_dialog_message, R.style.cireson_progress_dialog_theme, this);
            CiresonUtilites.getLoadingDialog().show();
            Assets secondAsset = globalData.getSwaper().getSecondAsset();
            globalData.getEditor().setAssetsProperties(secondAsset);
            if(TEMP_ASSETS_LIST.size()>1){
                try{
                    TEMP_ASSETS_LIST.remove(1);
                }catch(IndexOutOfBoundsException e){
                }catch(Exception e){
                }
            }
            TEMP_ASSETS_LIST.add(1,secondAsset);
            /*Set notes for both assets now:*/
            globalData.getEditor().setNotes(TEMP_ASSETS_LIST);
            /*Generate array list that holds objects of type CommonSaveModel to be forwarded to a BatchAPICaller..*/
            ArrayList<CommonSaveModel> swappableAssets = new ArrayList<CommonSaveModel>();
            for(Assets a:TEMP_ASSETS_LIST){
                CommonSaveModel modelToSwap = new CommonSaveModel();
                modelToSwap.projectionObject = a;
                swappableAssets.add(modelToSwap);
            }
            /*Call service to save changes after full swap..*/
            BatchAPICaller batchAPICaller = new BatchAPICaller(swappableAssets,this,new BatchAPICaller.BatchAPICallback(){
                @Override
                public void onBatchRequestComplete(String[] successBatchResponses, String[] failureBatchResponses, String exceptionMessage) {
                    CiresonUtilites.getLoadingDialog().dismiss();
                    if(exceptionMessage!=null){
                        /*Display exceptional message..*/
                        CiresonUtilites.displayMessage(getResources().getString(R.string.error),exceptionMessage,AssetsEditorActivity.this).show(AssetsEditorActivity.this.getFragmentManager(),"");
                    }else{
                        if(failureBatchResponses.length>0){
                             /*Handle the failure message from batch response..*/
                             /*Display failure responses..*/
                            CiresonUtilites.displayMessage(getResources().getString(R.string.error),failureBatchResponses[0],AssetsEditorActivity.this).show(AssetsEditorActivity.this.getFragmentManager(),"");
                        }else{
                            /*Clear the list containing editable assets now..*/
                            globalData.searchedAssets.clear();
                            globalData.resetTemporaryAsset();
                            navigateToMain();
                        }
                    }
                }
            });
            batchAPICaller.sendBatchRequests("UpdateProjection");
        }else{
            Assets firstAssetToSave = globalData.getSwaper().getFirstAsset();
            globalData.getEditor().setAssetsProperties(firstAssetToSave);
            TEMP_ASSETS_LIST = new ArrayList<Assets>();
            TEMP_ASSETS_LIST.add(0,firstAssetToSave);
            /*Clear all searched assets now for the next round..*/
            globalData.clearSearchAssets();
            globalData.resetTemporaryAsset();
            globalData.getSwaper().setSwap(!globalData.getSwaper().getSwap());
            Intent i = new Intent(this,AssetsScannerActivity.class);
            i.putExtra(AssetsScannerActivity.ASSETS_SCANNER_EXTRA,"Scan Asset to Replace With");
            startActivity(i);
        }
    }

    /**Save Edited assets after editing properties..*/
    private void saveEditedAssets(){
        /*Initialize Progress Dialog..*/
        CiresonUtilites.initLoadingDialog(R.string.progress_dialog_message, R.style.cireson_progress_dialog_theme, this);
        CiresonUtilites.getLoadingDialog().show();
        globalData.getEditor().setAssetPropertiesForEditAssets(globalData.searchedAssets);
        /*Generate array list that holds objects of type CommonSaveModel to be forwarded to a BatchAPICaller..*/
        ArrayList<CommonSaveModel> editableAssets = new ArrayList<CommonSaveModel>();
        for(Assets a:globalData.searchedAssets){
            CommonSaveModel model = new CommonSaveModel();
            model.projectionObject = a;
            editableAssets.add(model);
        }
        /*Call service to save changes after full swap..*/
        BatchAPICaller batchAPICaller = new BatchAPICaller(editableAssets,this,new BatchAPICaller.BatchAPICallback(){
            @Override
            public void onBatchRequestComplete(String[] successBatchResponses, String[] failureBatchResponses, String exceptionMessage) {
                CiresonUtilites.getLoadingDialog().dismiss();
                if(exceptionMessage!=null){
                    /*Display exceptional message..*/
                    CiresonUtilites.displayMessage(getResources().getString(R.string.error),exceptionMessage,AssetsEditorActivity.this).show(AssetsEditorActivity.this.getFragmentManager(),"");
                }else{
                    if(failureBatchResponses.length>0){
                        /*Display failure responses..*/
                        CiresonUtilites.displayMessage(getResources().getString(R.string.error),failureBatchResponses[0],AssetsEditorActivity.this).show(AssetsEditorActivity.this.getFragmentManager(),"");
                    }else{
                        /*Clear the list containing editable assets now..*/
                        globalData.searchedAssets.clear();
                        globalData.resetTemporaryAsset();
                        navigateToMain();
                    }
                }
            }
        });
        batchAPICaller.sendBatchRequests("UpdateProjection");
    }

    /*This is a view button click handler. This button click action is distinct for different functions:
    * Save for Receive and Add assets.
    * Next for Swap assets.
    * Save for edit assets.
    * Next for Inventory Audits.*/
    public void proceed(View v){
        switch(GlobalData.ACTIVE_MAIN_MENU){
            case CiresonConstants.RECEIVED_ASSETS:
                /*Proceed to add new assets within Receive Assets functionality..*/
                if(GlobalData.SCAN_TO_ADD_ASSETS){  saveNewAssetsToAdd();   }
                /*To save received assets after edits..*/
                else{   saveReceivedAssets();   }
                break;
            case CiresonConstants.SWAP_ASSETS:
                saveSwappedAssets();
                break;
            case CiresonConstants.EDIT_ASSETS:
                saveEditedAssets();
                break;
            case CiresonConstants.INVENTORY_AUDIT:
                if((AssetsEditorActivity.statusLists!=null&&AssetsEditorActivity.statusLists.size()==0)&&
                   (AssetsEditorActivity.locationLists!=null&&AssetsEditorActivity.locationLists.size()==0)&&
                   (AssetsEditorActivity.organizationLists!=null&&AssetsEditorActivity.organizationLists.size()==0)&&
                   (AssetsEditorActivity.costCenterLists!=null&&AssetsEditorActivity.costCenterLists.size()==0)&&
                   (AssetsEditorActivity.custodianLists!=null&&AssetsEditorActivity.custodianLists.size()==0)&&
                   (globalData.currentDate == null||(globalData.currentDate!=null&&globalData.currentDate.isEmpty()))){
                    /*Do nothing. Display an error message.*/
                    CiresonUtilites.displayMessage(getString(R.string.error),getString(R.string.edit_assets_display_message_for_search_invalid_criteria),this)
                            .show(this.getFragmentManager(),"");
            }else{
                /*If call to this activity from Inventory Audit..*/
                /*Make an API call as per the Inventory search criteria..*/
                Intent i = new Intent(AssetsEditorActivity.this,InventorySearchActivity.class);
                startActivityForResult(i,REQUEST_CODE_NONE);
            }
                break;
            default:
                break;
        }
    }

    private void navigateToMain(){
        clearAllSearchedData();
        GlobalData.SCAN_TO_ADD_ASSETS = false;
        Intent i = new Intent(this, MainNavigationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void clearAllSearchedData(){
        /*clear all the selected items from global data..*/
        globalData.selectedLocations.clear();
        globalData.selectedOrganizations.clear();
        globalData.selectedCustodians.clear();
        globalData.selectedCostCenters.clear();
    }

    /*When result is received from a called activity..*/
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==RESULT_OK) {
            String message = "";
            switch (GlobalData.ACTIVE_MAIN_MENU){
                case CiresonConstants.INVENTORY_AUDIT:
                    EditAssetsListAdapter.LIST_OF_VALUES[EditAssetsListAdapter.INDEX_OF_LIST_OF_VALUES] = data.getStringExtra(AssetsEditorActivity.EDIT_ASSETS_RESULT);
                    switch (requestCode){
                        case REQUEST_CODE_NONE:
                            if(resultCode == RESULT_OK){
                                String errorMessage = data.getStringExtra(CiresonConstants.MESSAGE_FROM_INVENTORY_SEARCH_TO_ASSETS_EDITOR_ERROR);
                                boolean objectIsNull = data.getBooleanExtra(CiresonConstants.MESSAGE_FROM_INVENTORY_SEARCH_TO_ASSETS_EDITOR_OBJECT_STATE,false);
                                message = getResources().getString(R.string.inventory_search_criteria_required);
                                if (!objectIsNull) {
                                    message = getResources().getString(R.string.inventory_search_no_assets_found);
                                }else if(errorMessage!=null&&!errorMessage.trim().isEmpty()){
                                    message = errorMessage;
                                }else{
                                    message = "";
                                }
                                /*If message is not null or empty..*/
                                if(message!=null&&!message.trim().isEmpty()){
                                    CiresonUtilites.displayMessage("",message,this).show(this.getFragmentManager(),"");
                                }
                            }
                            break;
                        case REQUEST_CODE_STATUS:
                            message = data.getStringExtra(StatusTreeViewActivity.STATUS_TREE_VIEW_EXTRA);
                            if(message!=null&&!message.isEmpty()){
                                CiresonUtilites.displayMessage(getString(R.string.error),message,this).show(getFragmentManager(),"");
                            }else{
                                AssetsEditorActivity.statusLists = new ArrayList<CiresonEnumeration>(globalData.selectedStatuses);
                                EditAssetsListAdapter.LIST_OF_VALUES[EditAssetsListAdapter.INDEX_OF_LIST_OF_VALUES] = data.getStringExtra(AssetsEditorActivity.EDIT_ASSETS_RESULT);
                            }
                            break;
                        case REQUEST_CODE_LOCATION:
                            message = data.getStringExtra(LocOrgCcSelectionActivity.LOC_ORG_CC_ERROR_MESSAGE_EXTRA);
                            if(message!=null&&!message.isEmpty()){
                                CiresonUtilites.displayMessage(getString(R.string.error),message,this).show(getFragmentManager(),"");
                            }else{
                                AssetsEditorActivity.locationLists = new ArrayList<Location>(globalData.selectedLocations);
                                EditAssetsListAdapter.LIST_OF_VALUES[EditAssetsListAdapter.INDEX_OF_LIST_OF_VALUES] = data.getStringExtra(AssetsEditorActivity.EDIT_ASSETS_RESULT);
                            }
                            break;
                        case REQUEST_CODE_ORGANIZATION:
                            message = data.getStringExtra(LocOrgCcSelectionActivity.LOC_ORG_CC_ERROR_MESSAGE_EXTRA);
                            if(message!=null&&!message.isEmpty()){
                                CiresonUtilites.displayMessage(getString(R.string.error),message,this).show(getFragmentManager(),"");
                            }else{
                                AssetsEditorActivity.organizationLists = new ArrayList<Organization>(globalData.selectedOrganizations);
                                EditAssetsListAdapter.LIST_OF_VALUES[EditAssetsListAdapter.INDEX_OF_LIST_OF_VALUES] = data.getStringExtra(AssetsEditorActivity.EDIT_ASSETS_RESULT);
                            }
                            break;
                        case REQUEST_CODE_CUSTODIAN:
                            message = data.getStringExtra(CustodianSearchActivity.CUSTODIAN_ERROR_MESSAGE_EXTRA);
                            if(message!=null&&!message.isEmpty()){
                                CiresonUtilites.displayMessage(getString(R.string.error),message,this).show(getFragmentManager(),"");
                            }else{
                                AssetsEditorActivity.custodianLists = new ArrayList<CustodianUser>(globalData.selectedCustodians);
                                EditAssetsListAdapter.LIST_OF_VALUES[EditAssetsListAdapter.INDEX_OF_LIST_OF_VALUES] = data.getStringExtra(AssetsEditorActivity.EDIT_ASSETS_RESULT);
                            }
                            break;
                        case REQUEST_CODE_COST_CENTER:
                            message = data.getStringExtra(LocOrgCcSelectionActivity.LOC_ORG_CC_ERROR_MESSAGE_EXTRA);
                            if(message!=null&&!message.isEmpty()){
                                CiresonUtilites.displayMessage(getString(R.string.error),message,this).show(getFragmentManager(),"");
                            }else{
                                AssetsEditorActivity.costCenterLists = new ArrayList<CostCenter>(globalData.selectedCostCenters);
                                EditAssetsListAdapter.LIST_OF_VALUES[EditAssetsListAdapter.INDEX_OF_LIST_OF_VALUES] = data.getStringExtra(AssetsEditorActivity.EDIT_ASSETS_RESULT);
                            }
                            break;
                        case REQUEST_CODE_RECEIVED_DATE:
                            String date = data.getStringExtra(DatePickerActivity.KEY_DATE_RESULT);
                            EditAssetsListAdapter.LIST_OF_VALUES[EditAssetsListAdapter.INDEX_OF_LIST_OF_VALUES] = date;
                            break;
                        default:
                            EditAssetsListAdapter.LIST_OF_VALUES[EditAssetsListAdapter.INDEX_OF_LIST_OF_VALUES] = "";
                            break;
                    }
                    String listValue = EditAssetsListAdapter.LIST_OF_VALUES[EditAssetsListAdapter.INDEX_OF_LIST_OF_VALUES];
                    if(listValue!=null){
                        textViewToUpdate.setText(listValue);
                    }
                    break;
                default:
                    switch(requestCode){
                        case REQUEST_CODE_RECEIVED_DATE:
                            String date = data.getStringExtra(DatePickerActivity.KEY_DATE_RESULT);
                            EditAssetsListAdapter.LIST_OF_VALUES[EditAssetsListAdapter.INDEX_OF_LIST_OF_VALUES] = date;
                            break;
                        default:
                            if(data.getStringExtra(StatusTreeViewActivity.STATUS_TREE_VIEW_EXTRA)!=null){
                                message = data.getStringExtra(StatusTreeViewActivity.STATUS_TREE_VIEW_EXTRA);
                            }else if(data.getStringExtra(LocOrgCcSelectionActivity.LOC_ORG_CC_ERROR_MESSAGE_EXTRA)!=null){
                                message = data.getStringExtra(LocOrgCcSelectionActivity.LOC_ORG_CC_ERROR_MESSAGE_EXTRA);
                            }else if(data.getStringExtra(CustodianSearchActivity.CUSTODIAN_ERROR_MESSAGE_EXTRA)!=null){
                                message = data.getStringExtra(CustodianSearchActivity.CUSTODIAN_ERROR_MESSAGE_EXTRA);
                            }

                            if(message!=null&&!message.isEmpty()){
                                CiresonUtilites.displayMessage(getString(R.string.error),message,this).show(getFragmentManager(),"");
                            }else{
                                EditAssetsListAdapter.LIST_OF_VALUES[EditAssetsListAdapter.INDEX_OF_LIST_OF_VALUES] = data.getStringExtra(EDIT_ASSETS_RESULT);
                                EditAssetsListAdapter.LIST_OF_VALUES[EditAssetsListAdapter.INDEX_OF_LIST_OF_VALUES] =
                                                EditAssetsListAdapter.LIST_OF_VALUES[EditAssetsListAdapter.INDEX_OF_LIST_OF_VALUES] ==null?
                                                "":EditAssetsListAdapter.LIST_OF_VALUES[EditAssetsListAdapter.INDEX_OF_LIST_OF_VALUES];
                            }
                            break;
                    }
                    textViewToUpdate.setText(EditAssetsListAdapter.LIST_OF_VALUES[EditAssetsListAdapter.INDEX_OF_LIST_OF_VALUES]);
                    break;
            }
        }
    }

    /** Navigate to EditDisposal Activity.
     *  text - to be displayed in LocOrgCc activity title.
     * constant - determines an active list wheter its manufacturer or model for Add Assets functionality.
     * requestCode - request code for activities back result.*/
    private void navigateToEditDisposalActivity(String text,int constant,int requestCode){
        Intent intent = new Intent(this, DisposalReferenceEditorActivity.class);
        intent.putExtra(DisposalReferenceEditorActivity.EDIT_DISPOSE_TEXT_FIELD_EXTRA,String.valueOf(textViewToUpdate.getText()));
        intent.putExtra(DisposalReferenceEditorActivity.EDIT_DISPOSE_EXTRA,text);
        if(GlobalData.SCAN_TO_ADD_ASSETS){
            GlobalData.ACTIVE_MANUFACTURER_OR_MODEL_LIST_FOR_ADD_ASSETS = constant;
        }
        startActivityForResult(intent, requestCode);
    }

    /**Navigate to SelectStatus Activity.
     * text - to be displayed in Select activity title.
     * requestCode - request code for activities back result. */
    private void navigateToSelectStatusActivity(String text,int constant,int requestCode){
        Intent intent = new Intent(this,StatusTreeViewActivity.class);
        startActivityForResult(intent,requestCode);
    }

    /**Navigate to LocOrgCc Activity.
     * text - to be displayed in LocOrgCc activity title.
     * forWhich - determines if this navigation is to select Location, Organization or CostCenter.
     * requestCode - request code for activities back result.*/
    private void navigateToLocOrgCCActivity(String text,int forWhich,int requestCode){
        Intent intent = new Intent(this, LocOrgCcSelectionActivity.class);
        intent.putExtra(LocOrgCcSelectionActivity.TYPE_OF_SELECTION,forWhich);
        intent.putExtra(LocOrgCcSelectionActivity.SELECT_TITLE, text);
        startActivityForResult(intent, requestCode);
    }

    /**Navigate to CustodianSearchActivity Activity.
     * text - to be displayed in CustodianSearchActivity activity title.
     * constant -
     * requestCode - request code for activities back result.*/
    private void navigateToCustodianSearchActivity(String text,int constant,int requestCode){
        startActivityForResult(new Intent(this, CustodianSearchActivity.class), requestCode);
    }

    /*Navigate to Date Picker Activity
     * text - to be displayed in CustodianSearchActivity activity title.
     * constant -
     * requestCode - request code for activities back result.*/
    private void navigateToDatePickerActivity(String text,int constant,int requestCode){
        startActivityForResult(new Intent(this, DatePickerActivity.class), requestCode);
    }

    private void navigateToNotesEditorActivityForSwap(String text, int constant,int requestCode){
        Intent i = new Intent(this,SwapNoteEditorActivity.class);
        i.putExtra(SwapNoteEditorActivity.SWAP_NOTE_EDIT_TEXT_EXTRA,text);
        startActivityForResult(i, requestCode);
    }

    /**When an item is clicked..*/
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView textView = (TextView) view.findViewById(R.id.editAssetsListLabel);
        String titleText = String.valueOf(textView.getText());
        /*switch to a next activity based on menu item clicked..*/
        textViewToUpdate = (TextView) view.findViewById(R.id.editAssetsSelectionLabel);
        EditAssetsListAdapter.INDEX_OF_LIST_OF_VALUES = i;
        /*Whenever a list item is clicked under one of the following conditions.. */
        switch (GlobalData.ACTIVE_MAIN_MENU) {
            case CiresonConstants.SWAP_ASSETS:
                if(globalData.getSwaper().getSwap()){
                    switch(i){
                        case 0:
                            navigateToSelectStatusActivity("", -1, REQUEST_CODE_STATUS);
                            break;
                        case 1:
                            navigateToLocOrgCCActivity(titleText, 0, REQUEST_CODE_LOCATION);
                            break;
                        case 2:
                            navigateToLocOrgCCActivity(titleText, 1, REQUEST_CODE_ORGANIZATION);
                            break;
                        case 3:
                            navigateToLocOrgCCActivity(titleText, 2, REQUEST_CODE_COST_CENTER);
                            break;
                        case 4:
                            navigateToDatePickerActivity(titleText, -1, REQUEST_CODE_RECEIVED_DATE);
                            break;
                        case 5:
                            navigateToNotesEditorActivityForSwap(EditAssetsListAdapter.LIST_OF_VALUES[i], -1, REQUEST_CODE_SWAP_ASSETS_NOTE_EDITOR);
                            break;
                        default:
                            break;
                    }
                }else{
                    switch(i){
                        case 0:
                            navigateToSelectStatusActivity("", -1, REQUEST_CODE_STATUS);
                            break;
                        case 1:
                            navigateToLocOrgCCActivity(titleText, 0, REQUEST_CODE_LOCATION);
                            break;
                        case 2:
                            navigateToLocOrgCCActivity(titleText, 1, REQUEST_CODE_ORGANIZATION);
                            break;
                        case 3:
                            navigateToCustodianSearchActivity(titleText, 0, REQUEST_CODE_CUSTODIAN);
                            break;
                        case 4:
                            navigateToLocOrgCCActivity(titleText, 2, REQUEST_CODE_COST_CENTER);
                            break;
                        case 5:
                            navigateToDatePickerActivity(titleText, -1, REQUEST_CODE_RECEIVED_DATE);
                            break;
                        case 6:
                            navigateToNotesEditorActivityForSwap(EditAssetsListAdapter.LIST_OF_VALUES[i], -1, REQUEST_CODE_SWAP_ASSETS_NOTE_EDITOR);
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                if (GlobalData.SCAN_TO_ADD_ASSETS) {
                    switch (i) {
                        case 0:
                            navigateToEditDisposalActivity(titleText, 0, REQUEST_CODE_MANUFACTURER);
                            break;
                        case 1:
                            navigateToEditDisposalActivity(titleText, 1, REQUEST_CODE_MODEL);
                            break;
                        case 2:
                            navigateToSelectStatusActivity("", -1, REQUEST_CODE_STATUS);
                            break;
                        case 3:
                            navigateToLocOrgCCActivity(titleText, 0, REQUEST_CODE_LOCATION);
                            break;
                        case 4:
                            navigateToLocOrgCCActivity(titleText, 1, REQUEST_CODE_ORGANIZATION);
                            break;
                        case 5:
                            navigateToCustodianSearchActivity(titleText, 0, REQUEST_CODE_CUSTODIAN);
                            break;
                        case 6:
                            navigateToLocOrgCCActivity(titleText, 2, REQUEST_CODE_COST_CENTER);
                            break;
                        case 7:
                            navigateToDatePickerActivity(titleText, -1, REQUEST_CODE_RECEIVED_DATE);
                            break;
                    }
                } else {
                    switch (i) {
                        case 0:
                            navigateToSelectStatusActivity("", -1, REQUEST_CODE_STATUS);
                            break;
                        case 1:
                            navigateToLocOrgCCActivity(titleText, 0, REQUEST_CODE_LOCATION);
                            break;
                        case 2:
                            navigateToLocOrgCCActivity(titleText, 1, REQUEST_CODE_ORGANIZATION);
                            break;
                        case 3:
                            navigateToCustodianSearchActivity(titleText, 0, REQUEST_CODE_CUSTODIAN);
                            break;
                        case 4:
                            navigateToLocOrgCCActivity(titleText, 2, REQUEST_CODE_COST_CENTER);
                            break;
                        case 5:
                            navigateToDatePickerActivity(titleText, -1, REQUEST_CODE_RECEIVED_DATE);
                            break;
                        default:
                            break;
                    }
                }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        CiresonUtilites.hideSoftKeyboard(this);
        return true;
    }

    /*List Adapter Class..*/
    private static class EditAssetsListAdapter extends BaseAdapter {
        /*Instances..*/

        /*These static fields hold image ids of image view sources and titles [Status, Location and so on] which are assigned to a dynamic static list during list update
        * depending on for which function (Receive, Swap or Edit) this list view is being updated..*/

        private final static int[] DEFAULT_IMAGE_IDS = {
                R.drawable.status,
                R.drawable.location,
                R.drawable.organization,
                R.drawable.custodian,
                R.drawable.cost_center,
                R.drawable.calender
        };
        private final static int[] ADD_ASSETS_IMAGE_IDS = {
                R.drawable.manufacturer,
                R.drawable.model,
                R.drawable.status,
                R.drawable.location,
                R.drawable.organization,
                R.drawable.custodian,
                R.drawable.cost_center,
                R.drawable.calender
        };
        private final static int[] SWAP_ASSETS_IMAGE_IDS_FIRST = {
                R.drawable.status,
                R.drawable.location,
                R.drawable.organization,
                R.drawable.cost_center,
                R.drawable.calender,
                R.drawable.notes
        };
        private final static int[] SWAP_ASSETS_IMAGE_IDS_SECOND = {
                R.drawable.status,
                R.drawable.location,
                R.drawable.organization,
                R.drawable.custodian,
                R.drawable.cost_center,
                R.drawable.calender,
                R.drawable.notes
        };

        private final static String[] DEFAULT_LIST_ITEMS = {"Status","Location","Organization","Custodian","Cost Center","Received Date"};
        private final static String[] ADD_ASSETS_LIST_ITEMS = {"Manufacturer","Model","Status","Location","Organization","Custodian","Cost Center","Received Date"};
        private final static String[] SWAP_LIST_ITEMS_FIRST = {"Status","Location","Organization","Cost Center","Loan Returned Date","Append Notes"};
        private final static String[] SWAP_LIST_ITEMS_SECOND = {"Status","Location","Organization","Custodian User","Cost Center","Loaned Date","Append Notes"};

        /*These static lists hold image ids, list title items (above final static fields) and values to be shown in a right text view
         dynamically based on for which function list is being updated..*/
        private static int[] LIST_OF_IMAGE_IDS;
        private static String[] LIST_OF_ITEMS;
        public static String[] LIST_OF_VALUES;
        public static int INDEX_OF_LIST_OF_VALUES = 0;

        /*View elements within lists to update..*/
        private TextView leftTextView, rightTextView;
        private ImageView imageView;
        private ImageView iv;
        private int counter = 0;
        LayoutInflater inflater;

        public EditAssetsListAdapter(Context ctx){
            inflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
            switch(GlobalData.ACTIVE_MAIN_MENU){
                case CiresonConstants.SWAP_ASSETS:
                    if(GlobalData.getInstance().getSwaper().getSwap()){
                        LIST_OF_IMAGE_IDS = SWAP_ASSETS_IMAGE_IDS_FIRST;
                        LIST_OF_ITEMS = SWAP_LIST_ITEMS_FIRST;
                        LIST_OF_VALUES = assetDetailsForSwap;
                    }else{
                        LIST_OF_IMAGE_IDS = SWAP_ASSETS_IMAGE_IDS_SECOND;
                        LIST_OF_ITEMS = SWAP_LIST_ITEMS_SECOND;
                        LIST_OF_VALUES = assetDetailsForSwap;
                    }
                    break;
                default:
                    if(GlobalData.SCAN_TO_ADD_ASSETS){
                        LIST_OF_IMAGE_IDS = ADD_ASSETS_IMAGE_IDS;
                        LIST_OF_ITEMS = ADD_ASSETS_LIST_ITEMS;
                        LIST_OF_VALUES = new String[ADD_ASSETS_LIST_ITEMS.length];
                    }else{
                        LIST_OF_IMAGE_IDS = DEFAULT_IMAGE_IDS;
                        LIST_OF_ITEMS = DEFAULT_LIST_ITEMS;
                        LIST_OF_VALUES = new String[DEFAULT_LIST_ITEMS.length];
                    }
                    break;
            }
        }

        @Override
        public int getCount() {
            return LIST_OF_ITEMS.length;
        }

        @Override
        public String getItem(int position) {
            return LIST_OF_ITEMS[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View cellView = convertView;
            if(convertView==null){
                cellView = inflater.inflate(R.layout.edit_assets_list_row, null);
            }
            imageView = (ImageView)cellView.findViewById(R.id.editAssetsListIcon);
            leftTextView = (TextView)cellView.findViewById(R.id.editAssetsListLabel);
            rightTextView = (TextView) cellView.findViewById(R.id.editAssetsSelectionLabel);
            /*Set values for all the views in a list..*/
            imageView.setImageResource(LIST_OF_IMAGE_IDS[position]);
            leftTextView.setText(LIST_OF_ITEMS[position]);
            rightTextView.setText(LIST_OF_VALUES[position]);
            return cellView;
        }
    }
}

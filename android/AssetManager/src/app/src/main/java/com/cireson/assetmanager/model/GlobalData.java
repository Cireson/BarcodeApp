
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


package com.cireson.assetmanager.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.cireson.assetmanager.assistance.Editor;
import com.cireson.assetmanager.assistance.Swaper;
import com.cireson.assetmanager.service.BaseJsonObject;
import com.cireson.assetmanager.util.BluetoothClient;
import com.cireson.assetmanager.controller.AssetsScannerActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by welcome on 4/8/14.
 */
/*This is a singleton class and helps to access data globally..*/
public class GlobalData {
    /*Instances..*/
    /*Global data specifics..*/
    private static final String PREFS_FILENAME = "GlobalData_Cireson";

    private static GlobalData _singleInstance = null;
    private Context appContext = null;
    private SharedPreferences sharedPreference = null;

    /*Instances for specific Cireson components..*/
    private User curUser = null;
    private Object anyObjectInstance = null;
    private LocationResponse locationResponse = null;
    private Organization organization = null;
    private CostCenter costCenter = null;
    private SaveReceiveAssets saveReceiveAssets = null;
    private CommonSaveModel disposeAssets = null;

    public boolean isAssociatedAssetsAvailable = false;
    public boolean gone = false;

    /*Instance for all..*/
    /*ArrayList searchedAssets holds assets accumulated from the API call..*/
    public ArrayList<Assets> searchedAssets = new ArrayList<Assets>();
    /*create copy of searchedAssets*/
    public ArrayList<Assets> copyOfsearchedAssets = new ArrayList<Assets>();
    public ArrayList<Assets> copyOfMatchedAssets = new ArrayList<Assets>();

    public ArrayList<Assets> searchedAssetsToAddAssets = new ArrayList<Assets>();
    public ArrayList<Assets> searchedAssetsToAddAssetsAfterSelection = new ArrayList<Assets>();

    /*These array lists hold currently selected objects of correcponding types..*/
    public ArrayList<CiresonEnumeration> selectedStatuses = new ArrayList<CiresonEnumeration>();
    public ArrayList<Location> selectedLocations = new ArrayList<Location>();
    public ArrayList<Organization> selectedOrganizations = new ArrayList<Organization>();
    public ArrayList<CustodianUser> selectedCustodians = new ArrayList<CustodianUser>();
    public ArrayList<CostCenter> selectedCostCenters = new ArrayList<CostCenter>();

    /*Instance for Receive Assets..*/
    /*These string builders are used to hold string during selection and display in edit activity when navigated from Received assets..*/
    public StringBuilder selectedStatus = new StringBuilder();
    public StringBuilder selectedLocation = new StringBuilder();
    public StringBuilder selectedOrganization = new StringBuilder();
    public StringBuilder selectedCustodian = new StringBuilder();
    public StringBuilder selectedCostCenter = new StringBuilder();
    public StringBuilder selectedPrimaryUser = new StringBuilder();

    /*These fields hold currently selected objects for each list in edit list activity.*/
    public Location currentLocation = new Location();
    public Organization currentOrganization = new Organization();
    public CostCenter currentCostCenter = new CostCenter();
    public CustodianUser currentCustodianUser = new CustodianUser(), currentPrimaryUser = new CustodianUser();
    public String currentDate = "";

    /*Instances to store selected date credentials....*/
    public int selectedMonth = 0;
    public int selectedDayOfMonth = 1;
    public int selectedYear = -1;

    /*These are the additional fields required for additional list items in AssetsEditorActivity for swap function..*/
    public StringBuilder currentNotesForSwapAssets = new StringBuilder();

    /*Instance for Edit Assets..*/
    public String selectedManufacturer = "";
    public String selectedModel = "";

    /*Public instance that holds name of a class to be used by Edit Assets..*/

    /*Public instance that specifies current active main menu..*/
    public static int ACTIVE_MAIN_MENU = 0;
    public static int ACTIVE_BLUETOOTH_CALLER = 0;
    public static int ACTIVE_MANUFACTURER_OR_MODEL_LIST_FOR_ADD_ASSETS = 0;

    /*This instance is used to pass intent extra from any other activities to AssetsEditor and used with onActivityResult..*/
    public final static String STRING_EXTRA_TO_EDITOR = "STRING_EXTRA_TO_EDITOR";

    /*public instance as an array list to hold any objects..*/
    public static ArrayList tempList = null;

    public ArrayList<CiresonEnumeration> statusEnumerationList;

    /*Instance for bluetooth client*/
    public BluetoothClient bluetoothClient;

    /*Instance of Scan Assets to be used in bluetooth BluetoothPairedDeviceListActivity*/
    public AssetsScannerActivity currentAssetsScannerActivity;

    /*Static instance of global data to determine if receive assets function is required in scan assets activity.
    * If it is true, scanning process has to perform extra functions.*/
    public static boolean SCAN_TO_ADD_ASSETS = false;
    /*This field is required by BaseJson component to serialize null values along with others to save newly added assets..*/
    public static boolean SAVE_NEW_ADDED_ASSETS = false;

    /*Request JSON string for update or save..*/
    public StringBuilder receiveAssetsSaveRequestString = new StringBuilder();

    /*An instance for a model that holds service call messages when call is failed..*/
    public APICallFailureHandler apiCallFailureHandler = new APICallFailureHandler();

    /*These instances are JSONObject and JSONArray instances to hold JSON Responses from API call..*/
    public JSONObject jsonObject = new JSONObject();
    public JSONArray jsonArray = new JSONArray();

    /**/
    public static boolean APPLICATION_FLOW_HAS_STARTED = false;

    /*Some more public constants..*/
    public final static String USER_TOKEN_INVALID = "User Token was invalid or expired.";

    /*Private constructor that makes this class to generate a single object throughout the app..*/
    private GlobalData(){
        /*Default..*/
    }

    /*A static method that returns single instance of GlobalData..*/
    public static GlobalData getInstance(){
        if(_singleInstance==null){
            _singleInstance = new GlobalData();
        }
        return _singleInstance;
    }

    /*Save and retrieve given data via shared preferences..*/
    public void saveToStorage(String key, String value) {
        SharedPreferences.Editor edt = sharedPreference.edit();
        edt.putString(key, value);
        edt.commit();
    }

    public void saveToStorage(String key, BaseJsonObject value) {
        saveToStorage(key, value.toJson());
    }

    /*Return the value associated with the given key. If preference does not exist, return default value which is passed as a second argument..*/
    public String getFromStorage(String key) {
        return sharedPreference.getString(key, "");
    }

    public void deleteFromStorage(){
        curUser=null;
        SharedPreferences.Editor edt=sharedPreference.edit();
        edt.clear();
        edt.commit();
    }

    public <T extends BaseJsonObject> T getFromStorage(String key, Class<T> klass) {
        T ret = null;
        try {
            T ins = klass.newInstance();
            String value = getFromStorage(key);
            if (value != "") {
                ret = BaseJsonObject.fromJson(value, klass);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /*Clear searched assets list..*/
    public void clearSearchAssets(){
        if(searchedAssets!=null){
            searchedAssets.clear();
        }
        if(copyOfsearchedAssets !=null){
            copyOfsearchedAssets.clear();
        }
    }

    /*getters.. and setters..*/
    public void setContext(Context context){
        this.appContext = context;
        /*default private constructor that instantiates a singleton object..*/
        this.sharedPreference =  this.appContext.getSharedPreferences(PREFS_FILENAME, 0);
    }

    public Context getAppContext(){
        return appContext;
    }

    /*Set any object instance required immediately during execution..*/
    public void setImmediateObject(Object o){
        anyObjectInstance = o;
    }

    /*Return the immediate object set..*/
    public Object getImmediateObject(){
        return anyObjectInstance;
    }

    /*getters for model classes..*/
    public User getCurrentUser(){
        if(curUser==null){
            curUser = User.loadUserFromStorage();
        }
        return curUser;
    }

    public LocationResponse getLocation(){
        if(locationResponse==null){
            locationResponse = new LocationResponse();
        }
        return locationResponse;
    }

    public Organization getOrganization() {
        if(organization==null){
            organization = new Organization();
        }
        return organization;
    }

    public CostCenter getCostCenter() {
        if (costCenter == null) {
            costCenter = new CostCenter();
        }
        return costCenter;
    }

    /**Returns a single instance of a receive assets saver. Its instance provides a json request to be forward via api call.*/
    public SaveReceiveAssets getSavedReceiveAssets() {
        if(saveReceiveAssets==null){
            saveReceiveAssets = new SaveReceiveAssets();
        }
        return saveReceiveAssets;
    }

    /**Returns a single instance of a assets disposer. Its instance provides a json request to be forward via api call.*/
    public CommonSaveModel getDisposeAssets(){
        if(disposeAssets==null){
            disposeAssets = new CommonSaveModel();
        }
        return disposeAssets;
    }

    /*Clears all the string builders created for model, manufacturer, status, location, organization, cost center, note and date.*/
    public void clearEditorsSelectedStringItems(){
        selectedManufacturer = "";
        selectedModel = "";
        selectedStatus.replace(0,selectedStatus.length(),"");
        selectedLocation.replace(0,selectedLocation.length(),"");
        selectedOrganization.replace(0,selectedOrganization.length(),"");
        selectedCostCenter.replace(0,selectedCostCenter.length(),"");
        selectedCustodian.replace(0,selectedCustodian.length(),"");
        selectedPrimaryUser.replace(0,selectedPrimaryUser.length(),"");
        currentNotesForSwapAssets.replace(0,selectedStatus.length(),"");
        currentDate = "";
    }

    /*Clear instances of Location, Organization, Cost center and Custodian Users..*/
    public void clearEditorInstances(){
        if(currentLocation==null){
            currentLocation = new Location();
        }
        if(currentOrganization==null){
            currentOrganization = new Organization();
        }
        if(currentCostCenter==null){
            currentCostCenter = new CostCenter();
        }
        if(currentCustodianUser==null){
            currentCustodianUser = new CustodianUser();
        }
        currentLocation.classTypeId = "";
        currentOrganization.classTypeId = "";
        currentCostCenter.classTypeId = "";
        currentCustodianUser.classTypeId = "";
    }

    public static boolean isNullOrEmpty(String string){
        return (string==null || "".equals(string));
    }

    /** Test Methods..*/
    public void testSearchedAssets(){
        for(Assets a:searchedAssets){
            Log.d("searched assets ", a.toString());
        }
    }

    /*This Assets instance holds edited properties temporarily to be assigned to real asset during saving..*/
    private Assets temporaryAsset;
    public Assets getTemporaryAsset(){
        if(temporaryAsset==null){
            temporaryAsset = new Assets();
        }
        return temporaryAsset;
    }

    /*Set temporary asset to null and it holds a new reference of asset when called getTemporaryAsset()*/
    public void resetTemporaryAsset(){
        temporaryAsset = null;
    }

    /*Instance and method to assist in swapping of assets..*/
    private Swaper swaper;
    public Swaper getSwaper(){
        if(swaper ==null){
            swaper = new Swaper();
        }
        return swaper;
    }

    /*Instance and method to assist in editing assets..*/
    private Editor editor;
    public Editor getEditor() {
        if(editor==null){
            editor = new Editor();
        }
        return editor;
    }
}

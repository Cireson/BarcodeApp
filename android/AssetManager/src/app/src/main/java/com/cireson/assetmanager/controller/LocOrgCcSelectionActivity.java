
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
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.model.CostCenter;
import com.cireson.assetmanager.model.Location;
import com.cireson.assetmanager.model.Organization;
import com.cireson.assetmanager.service.APICaller;
import com.cireson.assetmanager.service.CostCenterRequest;
import com.cireson.assetmanager.service.LocationRequest;
import com.cireson.assetmanager.service.OrganizationRequest;
import com.cireson.assetmanager.util.CiresonConstants;
import com.cireson.assetmanager.util.CiresonListViewAdapter;
import com.cireson.assetmanager.util.CiresonUtilites;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by welcome on 4/14/2014.
 */
public class LocOrgCcSelectionActivity extends CiresonBaseActivitySecond implements AbsListView.OnItemClickListener, View.OnClickListener{

    /*Instances..*/
    private static TextView selectTitleTextView;
    private static ListView listView;

    private GlobalData globalData;
    private SearchListAdapter searchListAdapter;

    /*These array lists hold objects of corresponding classes while navigating from Edit to Select activity.*/
    private static ArrayList<Location> locations = new ArrayList<Location>();
    private static ArrayList<Organization> organizations = new ArrayList<Organization>();
    private static ArrayList<CostCenter> costCenters = new ArrayList<CostCenter>();

    /*These array lists hold group title alphabets for corresponding types to display in list view..*/
    private static ArrayList<String> groupOfTitleAlphabetsForLocation = new ArrayList<String>();
    private static ArrayList<String> groupOfTitleAlphabetsForOrganization = new ArrayList<String>();
    private static ArrayList<String> groupOfTitleAlphabetsForCostCenters = new ArrayList<String>();

    /*These lists hold sorted names for each category..*/
    private static List<String> sortedLocationNames = new ArrayList<String>();
    private static List<String> sortedOrganizationNames = new ArrayList<String>();
    private static List<String> sortedCostCenterNames = new ArrayList<String>();

    /*This array list holds indexes of lists that are previously selected..*/
    private static ArrayList<Integer> indexesOfSelectedLocations = new ArrayList<Integer>();
    private static ArrayList<Integer> indexesOfSelectedOrganizations = new ArrayList<Integer>();
    private static ArrayList<Integer> indexesOfSelectedCostCenters = new ArrayList<Integer>();

    /*These links hold maps hold alphabetic indexes for fast scroll in list views..*/
    private static LinkedHashMap<String,Integer> alphaIndexesForLocations = new LinkedHashMap<String, Integer>();
    private static LinkedHashMap<String,Integer> alphaIndexesForOrganizations = new LinkedHashMap<String, Integer>();
    private static LinkedHashMap<String,Integer> alphaIndexesForCostCenters = new LinkedHashMap<String, Integer>();

    /*Static instances..*/
    private static int RECEIVED_INTENT_VALUE_TO_FIND_LIST_SELECTED = -1;
    private static String RESULT_INTENT_STRING = "";

    public final static String SELECT_TITLE = "SELECT_TITLE";
    public final static String TYPE_OF_SELECTION = "TYPE_OF_SELECTION";
    public final static String LOC_ORG_CC_ERROR_MESSAGE_EXTRA = "LOC_ORG_CC_ERROR_MESSAGE_EXTRA";
    public final static int SELECT_LOCATION = 0;
    public final static int SELECT_ORGANIZATION = 1;
    public final static int SELECT_COSTCENTER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_loc_org_cc);
        listView = (ListView)findViewById(R.id.selectListView);
        selectTitleTextView = (TextView)findViewById(R.id.selectTitleTextView);
        RECEIVED_INTENT_VALUE_TO_FIND_LIST_SELECTED = getIntent().getIntExtra(LocOrgCcSelectionActivity.TYPE_OF_SELECTION,0);
        CiresonUtilites.initLoadingDialog(R.string.progress_dialog_message, R.style.cireson_progress_dialog_theme, this);
        globalData = GlobalData.getInstance();
        /*Configure list attribute as per main menu navigation..*/
        switch (GlobalData.ACTIVE_MAIN_MENU){
            case CiresonConstants.INVENTORY_AUDIT:
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                break;
            default:
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                break;
        }
        /*Based on received intent string, receive the list of */
        switch(RECEIVED_INTENT_VALUE_TO_FIND_LIST_SELECTED){
            case SELECT_LOCATION:
                /*Call api for location array list..*/
                callAPIForLocation();
                break;
            case SELECT_ORGANIZATION:
                /*Call api for organization array list..*/
                callAPIForOrganization();
                break;
            case SELECT_COSTCENTER:
                /*Call api for cost center array list..*/
                callAPIForCostCenter();
                break;
            default:
                break;
        }
    }

    public void callAPIForLocation(){
        if(locations.size()==0){
            CiresonUtilites.getLoadingDialog().show();
            final Type type = new TypeToken<ArrayList<Location>>(){}.getType();
            APICaller<Location> apiCaller = new APICaller<Location>(Location.class, type, this);
            apiCaller.setMethodForBaseUrl("GetProjectionByCriteria");
            apiCaller.callArrayApi(new LocationRequest(), new APICaller.Callback<ArrayList<Location>>() {
                @Override
                public void onApiCallComplete(ArrayList<Location> obj, String error) {
                    if(obj!=null && obj.size()>0){
                        locations = obj;
                        String[] names = new String[obj.size()];
                        int counter = 0;
                        for(Location location:obj){
                            names[counter] = location.displayName;
                            counter++;
                        }
                        sortedLocationNames = CiresonUtilites.getAlphaSortedListsOfString(names);
                        groupOfTitleAlphabetsForLocation = new ArrayList<String>(CiresonUtilites.groupOfTitleAlphabets);
                        alphaIndexesForLocations = new LinkedHashMap<String, Integer>(CiresonUtilites.alphaIndexes);
                        setUpListAdapter("Select Location",groupOfTitleAlphabetsForLocation,sortedLocationNames,indexesOfSelectedLocations);
                        displayIndex(alphaIndexesForLocations);
                    }else{
                        closeLoadingAndDisplayErrorMessage(error);
                    }
                }
            });
        }else{
            setUpListAdapter("Select Location",groupOfTitleAlphabetsForLocation, sortedLocationNames,indexesOfSelectedLocations);
            displayIndex(alphaIndexesForLocations);
        }
    }

    public void callAPIForOrganization(){
        if(organizations.size()==0){
            CiresonUtilites.getLoadingDialog().show();
            final Type type = new TypeToken<ArrayList<Organization>>(){}.getType();
            APICaller<Organization> apiCaller = new APICaller<Organization>(Organization.class, type, this);
            apiCaller.setMethodForBaseUrl("GetProjectionByCriteria");
            apiCaller.callArrayApi(new OrganizationRequest(), new APICaller.Callback<ArrayList<Organization>>() {
                @Override
                public void onApiCallComplete(ArrayList<Organization> obj, String error) {
                    if(obj!=null && obj!=null && obj.size()>0){
                        organizations = obj;
                        String[] names = new String[obj.size()];
                        int counter = 0;
                        for(Organization organization:obj){
                            names[counter] = organization.displayName;
                            counter++;
                        }
                        sortedOrganizationNames = CiresonUtilites.getAlphaSortedListsOfString(names);
                        groupOfTitleAlphabetsForOrganization = new ArrayList<String>(CiresonUtilites.groupOfTitleAlphabets);
                        alphaIndexesForOrganizations = new LinkedHashMap<String, Integer>(CiresonUtilites.alphaIndexes);
                        setUpListAdapter("Select Organization",groupOfTitleAlphabetsForOrganization,sortedOrganizationNames,indexesOfSelectedOrganizations);
                        displayIndex(alphaIndexesForOrganizations);
                    }else{
                        closeLoadingAndDisplayErrorMessage(error);
                    }
                }
            });
        }else{
            setUpListAdapter("Select Organization",groupOfTitleAlphabetsForOrganization, sortedOrganizationNames,indexesOfSelectedOrganizations);
            displayIndex(alphaIndexesForOrganizations);
        }
    }

    public void callAPIForCostCenter(){
        if(costCenters.size()==0){
            CiresonUtilites.getLoadingDialog().show();
            final Type type = new TypeToken<ArrayList<CostCenter>>(){}.getType();
            APICaller<CostCenter> apiCaller = new APICaller<CostCenter>(CostCenter.class, type, this);
            apiCaller.setMethodForBaseUrl("GetProjectionByCriteria");
            apiCaller.callArrayApi(new CostCenterRequest(), new APICaller.Callback<ArrayList<CostCenter>>() {
                @Override
                public void onApiCallComplete(ArrayList<CostCenter> obj, String error) {
                    if(obj!=null && obj!=null && obj.size()>0){
                        costCenters = obj;
                        String[] names = new String[obj.size()];
                        int counter = 0;
                        for(CostCenter costCenter:obj){
                            names[counter] = costCenter.displayName;
                            counter++;
                        }
                        sortedCostCenterNames = CiresonUtilites.getAlphaSortedListsOfString(names);
                        groupOfTitleAlphabetsForCostCenters = new ArrayList<String>(CiresonUtilites.groupOfTitleAlphabets);
                        alphaIndexesForCostCenters = new LinkedHashMap<String, Integer>(CiresonUtilites.alphaIndexes);
                        setUpListAdapter("Select Cost Center",groupOfTitleAlphabetsForCostCenters,sortedCostCenterNames,indexesOfSelectedCostCenters);
                        displayIndex(alphaIndexesForCostCenters);
                    }else{
                        closeLoadingAndDisplayErrorMessage(error);
                    }
                }
            });
        }else{
            setUpListAdapter("Select Cost Center", groupOfTitleAlphabetsForCostCenters, sortedCostCenterNames, indexesOfSelectedCostCenters);
            displayIndex(alphaIndexesForCostCenters);
        }
    }

    public void closeLoadingAndDisplayErrorMessage(String error){
        CiresonUtilites.getLoadingDialog().dismiss();
        Intent i = new Intent();
        if(error!=null&&error.trim().isEmpty()){
            setResult(RESULT_CANCELED);
            finish();
        }
        else {
            i.putExtra(LOC_ORG_CC_ERROR_MESSAGE_EXTRA,error);
            setResult(RESULT_OK,i);
            finish();
        }
    }

    private void displayIndex(LinkedHashMap<String,Integer> linkedHashMapOfAlphaIndexes){
        LinearLayout indexLinearLayout = (LinearLayout)findViewById(R.id.selectListIndexView);
        indexLinearLayout.removeAllViews();
        TextView indexTextView;
        for(String indexText:linkedHashMapOfAlphaIndexes.keySet()){
            indexTextView = (TextView)getLayoutInflater().inflate(R.layout.list_view_alphabetic_index,null);
            indexTextView.setText(indexText);
            indexTextView.setTextColor(Color.WHITE);
            indexTextView.setOnClickListener(this);
            indexLinearLayout.addView(indexTextView);
        }
    }

    /*Set up an adapter for a list view and close the loading view..*/
    private void setUpListAdapter(String titleText,ArrayList<String> stringListTitles, List<String> stringLists,ArrayList<Integer> selectedLists){
        searchListAdapter = new SearchListAdapter(this,stringListTitles,stringLists,selectedLists);
        selectTitleTextView.setText(titleText);
        CiresonUtilites.getLoadingDialog().dismiss();
        listView.setAdapter(searchListAdapter);
        listView.setOnItemClickListener(LocOrgCcSelectionActivity.this);
    }

    /*This event handler currently works only for side list index items..*/
    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;
        switch (RECEIVED_INTENT_VALUE_TO_FIND_LIST_SELECTED){
            case SELECT_LOCATION:
                listView.smoothScrollToPosition(alphaIndexesForLocations.get(selectedIndex.getText()));
                break;
            case SELECT_ORGANIZATION:
                listView.smoothScrollToPosition(alphaIndexesForOrganizations.get(selectedIndex.getText()));
                break;
            case SELECT_COSTCENTER:
                listView.smoothScrollToPosition(alphaIndexesForCostCenters.get(selectedIndex.getText()));
                break;
            default:
                break;
        }
    }

    public void done(View view){
        Intent data = new Intent();
        switch(GlobalData.ACTIVE_MAIN_MENU){
            case CiresonConstants.RECEIVED_ASSETS:
            case CiresonConstants.SWAP_ASSETS:
                switch (LocOrgCcSelectionActivity.RECEIVED_INTENT_VALUE_TO_FIND_LIST_SELECTED){
                    case SELECT_LOCATION:
                        RESULT_INTENT_STRING = globalData.selectedLocation.toString();
                        break;
                    case SELECT_ORGANIZATION:
                        RESULT_INTENT_STRING = globalData.selectedOrganization.toString();
                        break;
                    case SELECT_COSTCENTER:
                        RESULT_INTENT_STRING = globalData.selectedCostCenter.toString();
                        break;
                    default:
                        break;
                }
                break;
            case CiresonConstants.EDIT_ASSETS:
                switch (LocOrgCcSelectionActivity.RECEIVED_INTENT_VALUE_TO_FIND_LIST_SELECTED){
                    case SELECT_LOCATION:
                        RESULT_INTENT_STRING = globalData.selectedLocation.toString();
                        break;
                    case SELECT_ORGANIZATION:
                        RESULT_INTENT_STRING = globalData.selectedOrganization.toString();
                        break;
                    case SELECT_COSTCENTER:
                        RESULT_INTENT_STRING = globalData.selectedCostCenter.toString();
                        break;
                    default:
                        break;
                }
                break;
            case CiresonConstants.INVENTORY_AUDIT:
                switch (LocOrgCcSelectionActivity.RECEIVED_INTENT_VALUE_TO_FIND_LIST_SELECTED){
                    case SELECT_LOCATION:
                        globalData.selectedLocation.replace(0,globalData.selectedLocation.length(),"");
                        for(Location location:globalData.selectedLocations){
                            if(globalData.selectedLocations.indexOf(location)!=globalData.selectedLocations.size()-1){
                                globalData.selectedLocation.append(location.displayName+", ");
                            }else{
                                globalData.selectedLocation.append(location.displayName);
                            }
                        }
                        RESULT_INTENT_STRING = globalData.selectedLocation.toString();
                        break;
                    case SELECT_ORGANIZATION:
                        globalData.selectedOrganization.replace(0,globalData.selectedOrganization.length(),"");
                        for(Organization organization:globalData.selectedOrganizations){
                            if(globalData.selectedOrganizations.indexOf(organization)!=globalData.selectedOrganizations.size()-1){
                                globalData.selectedOrganization.append(organization.displayName+", ");
                            }else{
                                globalData.selectedOrganization.append(organization.displayName);
                            }
                        }
                        RESULT_INTENT_STRING = globalData.selectedOrganization.toString();
                        break;
                    case SELECT_COSTCENTER:
                        globalData.selectedCostCenter.replace(0,globalData.selectedCostCenter.length(),"");
                        for(CostCenter costCenter:globalData.selectedCostCenters){
                            if(globalData.selectedCostCenters.indexOf(costCenter)!=globalData.selectedCostCenters.size()-1){
                                globalData.selectedCostCenter.append(costCenter.displayName+", ");
                            }else{
                                globalData.selectedCostCenter.append(costCenter.displayName);
                            }
                        }
                        RESULT_INTENT_STRING = globalData.selectedCostCenter.toString();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        data.putExtra(AssetsEditorActivity.EDIT_ASSETS_RESULT, RESULT_INTENT_STRING);
        setResult(RESULT_OK, data);
        finish();
    }

    public void clear(View view){
        switch(LocOrgCcSelectionActivity.RECEIVED_INTENT_VALUE_TO_FIND_LIST_SELECTED){
            case LocOrgCcSelectionActivity.SELECT_LOCATION:
                indexesOfSelectedLocations.clear();
                globalData.selectedLocations.clear();
                globalData.selectedLocation.replace(0, globalData.selectedLocation.length(), "");
                globalData.getTemporaryAsset().targetHardwareAssetHasLocation.classTypeId = null;
                globalData.getTemporaryAsset().targetHardwareAssetHasLocation.displayName = null;
                globalData.currentLocation = new Location();
                setUpListAdapter("Select Location",groupOfTitleAlphabetsForLocation, sortedLocationNames,indexesOfSelectedLocations);
                break;
            case LocOrgCcSelectionActivity.SELECT_ORGANIZATION:
                indexesOfSelectedOrganizations.clear();
                globalData.selectedOrganizations.clear();
                globalData.selectedOrganization.replace(0, globalData.selectedOrganization.length(), "");
                globalData.getTemporaryAsset().targetHardwareAssetHasOrganization.classTypeId = null;
                globalData.getTemporaryAsset().targetHardwareAssetHasOrganization.displayName = null;
                globalData.currentOrganization = new Organization();
                setUpListAdapter("Select Organization",groupOfTitleAlphabetsForOrganization, sortedOrganizationNames,indexesOfSelectedOrganizations);
                break;
            case LocOrgCcSelectionActivity.SELECT_COSTCENTER:
                indexesOfSelectedCostCenters.clear();
                globalData.selectedCostCenters.clear();
                globalData.selectedCostCenter.replace(0, globalData.selectedCostCenter.length(), "");
                globalData.getTemporaryAsset().targetHardwareAssetHasCostCenter.classTypeId = null;
                globalData.getTemporaryAsset().targetHardwareAssetHasCostCenter.displayName = null;
                globalData.currentCostCenter = new CostCenter();
                setUpListAdapter("Select Cost Center",groupOfTitleAlphabetsForCostCenters,sortedCostCenterNames,indexesOfSelectedCostCenters);
                break;
            default:
                break;
        }
    }

    /*Clear all the array lists filled via api calls..*/
    public static void clearListsOfItems(){
        locations.clear();
        organizations.clear();
        costCenters.clear();
        indexesOfSelectedLocations.clear();
        indexesOfSelectedOrganizations.clear();
        indexesOfSelectedCostCenters.clear();
    }

    /*A generic method to work with list items after list is clicked based on any navigations..*/
    private <T> void handleListViewAfterItemClicked
            (
                    int index,
                    List<String> sortedNames,
                    ArrayList<String>groupTitleAlphabets,
                    ArrayList<T> actualObjectList,
                    ArrayList<Integer> indexesOfSelectedObjects
            ) {
        if (!sortedNames.contains(groupTitleAlphabets)){
            String selectedName = sortedNames.get(index);
            switch (GlobalData.ACTIVE_MAIN_MENU) {
                case CiresonConstants.RECEIVED_ASSETS:
                case CiresonConstants.SWAP_ASSETS:
                case CiresonConstants.EDIT_ASSETS:
                    indexesOfSelectedObjects.clear();
                    indexesOfSelectedObjects.add(0, index);
                    for (T object : actualObjectList) {
                        switch (RECEIVED_INTENT_VALUE_TO_FIND_LIST_SELECTED) {
                            case SELECT_LOCATION:
                                if (((Location) object).displayName.equals(selectedName)) {
                                    globalData.selectedLocation.replace(0, globalData.selectedLocation.length(), selectedName);
                                    globalData.currentLocation = (Location)object;
                                    globalData.getTemporaryAsset().targetHardwareAssetHasLocation.classTypeId = ((Location) object).classTypeId;
                                    globalData.getTemporaryAsset().targetHardwareAssetHasLocation.displayName = ((Location) object).displayName;
                                    globalData.getTemporaryAsset().targetHardwareAssetHasLocation.baseId = ((Location)object).baseId;
                                }
                                break;
                            case SELECT_ORGANIZATION:
                                if (((Organization) object).displayName.equals(selectedName)) {
                                    globalData.selectedOrganization.replace(0, globalData.selectedOrganization.length(), selectedName);
                                    globalData.currentOrganization = (Organization)object;
                                    globalData.getTemporaryAsset().targetHardwareAssetHasOrganization.classTypeId = ((Organization) object).classTypeId;
                                    globalData.getTemporaryAsset().targetHardwareAssetHasOrganization.displayName = ((Organization) object).displayName;
                                    globalData.getTemporaryAsset().targetHardwareAssetHasOrganization.baseId = ((Organization) object).baseId;
                                }
                                break;
                            case SELECT_COSTCENTER:
                                if (((CostCenter) object).displayName.equals(selectedName)) {
                                    globalData.selectedCostCenter.replace(0, globalData.selectedCostCenter.length(), selectedName);
                                    globalData.currentCostCenter = (CostCenter )object;
                                    globalData.getTemporaryAsset().targetHardwareAssetHasCostCenter.classTypeId = ((CostCenter) object).classTypeId;
                                    globalData.getTemporaryAsset().targetHardwareAssetHasCostCenter.displayName = ((CostCenter) object).displayName;
                                    globalData.getTemporaryAsset().targetHardwareAssetHasCostCenter.baseId = ((CostCenter) object).baseId;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                case CiresonConstants.INVENTORY_AUDIT:
                    for (T object : actualObjectList) {
                        switch (RECEIVED_INTENT_VALUE_TO_FIND_LIST_SELECTED) {
                            case SELECT_LOCATION:
                                if (((Location) object).displayName.equals(selectedName)) {
                                    if (globalData.selectedLocations.contains(((Location) object))) {
                                        globalData.selectedLocations.remove(((Location) object));
                                        indexesOfSelectedObjects.remove((Integer)index);
                                    }else{
                                        globalData.selectedLocations.add(((Location) object));
                                        indexesOfSelectedObjects.add((Integer)index);
                                    }
                                }
                                break;
                            case SELECT_ORGANIZATION:
                                if (((Organization) object).displayName.equals(selectedName)) {
                                    if (globalData.selectedOrganizations.contains(((Organization) object))) {
                                        globalData.selectedOrganizations.remove(((Organization) object));
                                        indexesOfSelectedObjects.remove((Integer)index);
                                    }else{
                                        globalData.selectedOrganizations.add(((Organization) object));
                                        indexesOfSelectedObjects.add((Integer)index);
                                    }
                                }
                                break;
                            case SELECT_COSTCENTER:
                                if (((CostCenter) object).displayName.equals(selectedName)) {
                                    if (globalData.selectedCostCenters.contains(((CostCenter) object))) {
                                        globalData.selectedCostCenters.remove(((CostCenter) object));
                                        indexesOfSelectedObjects.remove((Integer)index);
                                    }else{
                                        globalData.selectedCostCenters.add(((CostCenter) object));
                                        indexesOfSelectedObjects.add((Integer)index);
                                    }
                                }
                                break;
                        }
                    }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (LocOrgCcSelectionActivity.RECEIVED_INTENT_VALUE_TO_FIND_LIST_SELECTED){
            case SELECT_LOCATION:
                handleListViewAfterItemClicked(i,sortedLocationNames,groupOfTitleAlphabetsForLocation,locations,indexesOfSelectedLocations);
                break;
            case SELECT_ORGANIZATION:
                handleListViewAfterItemClicked(i,sortedOrganizationNames,groupOfTitleAlphabetsForOrganization,organizations,indexesOfSelectedOrganizations);
                break;
            case SELECT_COSTCENTER:
                handleListViewAfterItemClicked(i,sortedCostCenterNames,groupOfTitleAlphabetsForCostCenters,costCenters,indexesOfSelectedCostCenters);
                break;
        }
    }

    /*Select List Adapter..*/
    private class SearchListAdapter extends CiresonListViewAdapter<String>{
        private List<String> key;
        private List<String> displayList;
        private LayoutInflater inflater;
        private ArrayList<Integer> selectedLists;

        public SearchListAdapter(Context context,List<String> key,List<String> lists,ArrayList<Integer> selectedLists){
            super(lists);
            displayList = getDisplayList();
            this.key = key;
            this.selectedLists = selectedLists;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View cellView = convertView;
            if(convertView==null){
                cellView = inflater.inflate(R.layout.select_list_row, parent, false);
            }

            /**/
            TextView searchListTextView = (TextView)cellView.findViewById(R.id.selectListRowTextView);
            TextView headerTextView = (TextView) cellView.findViewById(R.id.sectionHeader);
            if(key.contains(displayList.get(position))){
                /*Set view groups background color to gray..*/
                cellView.setBackgroundColor(Color.GRAY);
                headerTextView.setText(displayList.get(position));
                headerTextView.setVisibility(View.VISIBLE);
                searchListTextView.setVisibility(View.GONE);
            }else{
                cellView.setBackgroundResource(R.drawable.list_row_background);
                searchListTextView.setText(displayList.get(position));
                headerTextView.setVisibility(View.GONE);
                searchListTextView.setVisibility(View.VISIBLE);
            }

            /*Highlight selected lists..*/
            if(selectedLists.indexOf(position)>=0){
                listView.setItemChecked(position, true);
            }
            else{
            }
            return cellView;
        }
    }
}

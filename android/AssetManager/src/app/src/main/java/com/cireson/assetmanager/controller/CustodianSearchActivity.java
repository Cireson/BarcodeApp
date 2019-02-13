
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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.model.CustodianUser;
import com.cireson.assetmanager.service.APICaller;
import com.cireson.assetmanager.service.CustodianRequest;
import com.cireson.assetmanager.util.CiresonConstants;
import com.cireson.assetmanager.util.CiresonListViewAdapter;
import com.cireson.assetmanager.util.CiresonUtilites;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class CustodianSearchActivity extends CiresonBaseActivitySecond implements AbsListView.OnItemClickListener, View.OnTouchListener {

    /*Instances..*/
    private ListView listView = null;
    private TextView searchTextView;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private ImageButton btnSearch;

    private CustodianSearchListAdapter custodianSearchListAdapter;
    private GlobalData globalData;

    /*This array lists holds objects of Custodian type while navigating from Edit to Select activity.*/
    private static ArrayList<CustodianUser> custodians = new ArrayList<CustodianUser>();
    public static String RESULT_INTENT_STRING = "";

    private final static String CUSTODIAN_LIST_STATE = "CUSTODIAN_LIST_STATE";
    public final static String CUSTODIAN_ERROR_MESSAGE_EXTRA = "CUSTODIAN_ERROR_MESSAGE_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custodian_search);
        listView = (ListView)findViewById(R.id.custodianSearchList);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        listView.setOnItemClickListener(this);
        linearLayout=(LinearLayout)findViewById(R.id.listViewLinearLayout);
        linearLayout.setOnTouchListener(this);
        relativeLayout=(RelativeLayout)findViewById(R.id.custodianSearchRelativeLayout);
        relativeLayout.setOnTouchListener(this);
        searchTextView = (TextView)findViewById(R.id.searchText);
        globalData = GlobalData.getInstance();
        if(GlobalData.ACTIVE_MAIN_MENU==CiresonConstants.INVENTORY_AUDIT){
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }
        CiresonUtilites.initLoadingDialog(R.string.progress_dialog_message,R.style.cireson_progress_dialog_theme,this);
    }

    public void callCustodianApi(String searchString){
        CiresonUtilites.getLoadingDialog().show();
        String searchQuery = String.format("%s%s%s", "%", searchString.trim(), "%");
        String newString = CiresonUtilites.getRequestJsonString(
                "GetData",
                CiresonConstants.Token,
                CiresonConstants.CUSTODIAN_USER_TYPE,
                CiresonConstants.Custodian_User_Property_Display_Name,
                "Like",
                searchQuery,
                CiresonConstants.User_ProjectionType);
        final Type type = new TypeToken<ArrayList<CustodianUser>>(){}.getType();
        APICaller<CustodianUser> apiCaller = new APICaller<CustodianUser>(CustodianUser.class, type, this);
        apiCaller.setMethodForBaseUrl("GetProjectionByCriteria");
        CustodianRequest cr = new CustodianRequest(searchQuery);
        apiCaller.callArrayApi(new CustodianRequest(searchQuery), new APICaller.Callback<ArrayList<CustodianUser>>() {
            @Override
            public void onApiCallComplete(ArrayList<CustodianUser> obj, String error) {
                CiresonUtilites.getLoadingDialog().dismiss();
                if (obj != null && obj.size()>0) {
                    globalData.selectedCustodians.clear();
                    globalData.selectedCustodian.replace(0, globalData.selectedCustodian.length(),"");
                    custodians = obj;
                    custodianSearchListAdapter = new CustodianSearchListAdapter(custodians);
                    listView.setAdapter(custodianSearchListAdapter);
                }else {
                    if(error!=null&&error.isEmpty()){
                        setResult(RESULT_CANCELED);
                        finish();
                    }else if(error!=null){
                        Intent i = new Intent();
                        i.putExtra(CUSTODIAN_ERROR_MESSAGE_EXTRA,error);
                        setResult(RESULT_OK,i);
                        finish();
                    }
                }
            }
        });
    }

    public void cancel(View v){
        setResult(RESULT_CANCELED);
        finish();
    }

    public void done(View v){
        Intent i = new Intent();
        switch (GlobalData.ACTIVE_MAIN_MENU){
            case CiresonConstants.INVENTORY_AUDIT:
                globalData.selectedCustodian.replace(0,globalData.selectedCustodian.length(),"");
                for(CustodianUser custodianUser:globalData.selectedCustodians){
                    if(globalData.selectedCustodians.indexOf(custodianUser)!=globalData.selectedCustodians.size()-1){
                        globalData.selectedCustodian.append(custodianUser.displayName+", ");
                    }else{
                        globalData.selectedCustodian.append(custodianUser.displayName);
                    }
                }
                RESULT_INTENT_STRING = globalData.selectedCustodian.toString();
                break;
            default:
                RESULT_INTENT_STRING = globalData.selectedCustodian.toString();
                break;
        }
        i.putExtra(AssetsEditorActivity.EDIT_ASSETS_RESULT,RESULT_INTENT_STRING);
        setResult(RESULT_OK, i);
        finish();
    }

    /*Clear the list selection..*/
    public void clear(View v) {
        /*Clear custodian data and search text..*/
        //custodians.clear();
        searchTextView.setText("");
        globalData.selectedCustodians.clear();
        globalData.selectedCustodian.replace(0,globalData.selectedCustodian.length(),"");
        globalData.currentCustodianUser = new CustodianUser();
        listView.clearChoices();
        if(custodianSearchListAdapter!=null){
            custodianSearchListAdapter.setItems(new ArrayList<CustodianUser>());
        }

        switch(GlobalData.ACTIVE_MAIN_MENU) {
            case CiresonConstants.RECEIVED_ASSETS:
            case CiresonConstants.EDIT_ASSETS:
                globalData.getTemporaryAsset().owndedBy.baseId = null;
                globalData.getTemporaryAsset().owndedBy.classTypeId = null;
                globalData.getTemporaryAsset().owndedBy.userName = null;
                break;
            case CiresonConstants.SWAP_ASSETS:
                if(globalData.getSwaper().getUsePrimaryUser()){
                    globalData.getTemporaryAsset().targetHardwareAssetHasPrimaryUser.classTypeId = null;
                    globalData.getTemporaryAsset().targetHardwareAssetHasPrimaryUser.displayName = null;
                }else{
                    globalData.getTemporaryAsset().owndedBy.baseId = null;
                    globalData.getTemporaryAsset().owndedBy.classTypeId = null;
                    globalData.getTemporaryAsset().owndedBy.userName = null;
                }
                break;
            default:
                break;
        }
    }

    public void searchClicked(View v){
        btnSearch.requestFocus();
        CiresonUtilites.hideSoftKeyboard(this);
        String searchText = String.valueOf(searchTextView.getText());
        if(searchText==null||(searchText!=null&&searchText.length()<3)){
            CiresonUtilites.displayMessage(getString(R.string.error),
                    getString(R.string.custodian_search_edit_text_unsufficient_characters),this)
                    .show(getFragmentManager(),"");
        }else{
            callCustodianApi(searchTextView.getText().toString());
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER){
            callCustodianApi(searchTextView.getText().toString());
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        CiresonUtilites.hideSoftKeyboard(this);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch(GlobalData.ACTIVE_MAIN_MENU){
            case CiresonConstants.RECEIVED_ASSETS:
            case CiresonConstants.SWAP_ASSETS:
            case CiresonConstants.EDIT_ASSETS:
                globalData.selectedCustodian.replace(0, globalData.selectedCustodian.length(),custodians.get(i).displayName);
                globalData.currentCustodianUser = custodians.get(i);
                globalData.getTemporaryAsset().owndedBy.baseId = custodians.get(i).baseId;
                globalData.getTemporaryAsset().owndedBy.classTypeId = custodians.get(i).classTypeId;
                globalData.getTemporaryAsset().owndedBy.userName = custodians.get(i).displayName;
                break;
            case CiresonConstants.INVENTORY_AUDIT:
                if(globalData.selectedCustodians.contains(custodians.get(i))){
                    globalData.selectedCustodians.remove(custodians.get(i));
                }else{
                    globalData.selectedCustodians.add(custodians.get(i));
                }
                break;
        }
    }

    /*Select List Adapter..*/
    private class CustodianSearchListAdapter extends CiresonListViewAdapter<CustodianUser>{

        public CustodianSearchListAdapter(ArrayList<CustodianUser> items) {
            super(items);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View cellView = convertView;
            if(convertView==null){
                cellView = getLayoutInflater().inflate(R.layout.title_subtitle_list_row, null);
            }
            TextView title = (TextView)cellView.findViewById(R.id.title);
            TextView subTitle = (TextView)cellView.findViewById(R.id.subtitle);
            CustodianUser user = getItem(position);
            title.setText(user.displayName);
            subTitle.setText(user.domain);
            return cellView;
        }
    }

}

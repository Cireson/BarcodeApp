
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
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.Assets;
import com.cireson.assetmanager.model.CommonSaveModel;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.service.BatchAPICaller;
import com.cireson.assetmanager.util.CiresonConstants;
import com.cireson.assetmanager.util.CiresonUtilites;

import java.util.ArrayList;

/**
 * Created by welcome on 4/1/14.
 */
public class AssetsDisposalActivity extends CiresonBaseActivity implements AbsListView.OnItemClickListener, BatchAPICaller.BatchAPICallback{

    /*Instances..*/
    private ListView listView = null;
    private TextView textViewToUpdate = null;
    private boolean clearPrimaryUser = false, clearCustodians = false;

    private final static int DISPOSE_ASSETS_RECEIVED_DATE = 0;
    private final static int DISPOSE_ASSETS_REFERENCE_NUMBER = 1;
    public  final static String EXTRA_TEXT = "EXTRA_TEXT";
    public static final String EXTRA_ASSETS_COUNT = "EXTRA_ASSETS_COUNT";

    public void onCreate(Bundle objectState){
        super.onCreate(objectState);
        setContentView(R.layout.activity_dispose_purchase_order);

        TextView txtTitleCount = (TextView) findViewById(R.id.titleCount);

        int count = getIntent().getIntExtra(EXTRA_ASSETS_COUNT, 0);
        txtTitleCount.setText(String.valueOf(count));

        listView = (ListView)findViewById(R.id.dispose_assets_list_view);
        listView.setAdapter(new DisposeAssetsListAdapter(this));
        listView.setOnItemClickListener(this);

        /*Initially set states to clear users and custodians to false..*/
        clearPrimaryUser = false;
        clearCustodians = false;
    }

    /*Click handler for the save button*/
    public void save(View v){
        /*For all the assets currently available with specified property, collect request objects to be sent via api call to dispose assets..*/
        /*Initialize Progress Dialog..*/
        CiresonUtilites.initLoadingDialog(R.string.progress_dialog_message, R.style.cireson_progress_dialog_theme, this);
        CiresonUtilites.getLoadingDialog().show();
        ArrayList<CommonSaveModel> disposableAssets = new ArrayList<CommonSaveModel>();
        for(Assets a: GlobalData.getInstance().searchedAssets){
            /*If toggle button is toggled on to clear primary users..*/
            if(clearPrimaryUser){
                a.setTargetHardwareAssetHasPrimaryUser(null);
            }
            /*If toggle button is toggled on to clear custodians..*/
            if(clearCustodians){
                a.setOwndedBy(null);
            }

            /*Set asset's staus to disposed.*/
            a.setHardwareStatusId(CiresonConstants.HARDWARE_STATUS_ID_DISPOSED);
            a.setHardwareStatusName(CiresonConstants.HARDWARE_STATUS_NAME_DISPOSED);

            CommonSaveModel disposeAsset = new CommonSaveModel();
            disposeAsset.projectionObject = a;
            disposableAssets.add(disposeAsset);
        }
        BatchAPICaller<CommonSaveModel> batchAPICaller = new BatchAPICaller<CommonSaveModel>(disposableAssets,this,this);
        batchAPICaller.sendBatchRequests("UpdateProjection");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch(requestCode){
                case DISPOSE_ASSETS_RECEIVED_DATE:
                    String date = data.getStringExtra(DatePickerActivity.KEY_DATE_RESULT);
                    textViewToUpdate.setText(date);
                    for(Assets a:GlobalData.getInstance().searchedAssets){
                        a.setDisposalDate(date);
                    }
                    break;
                case DISPOSE_ASSETS_REFERENCE_NUMBER:
                    String disposeReferenceNumber = data.getStringExtra(DisposalReferenceEditorActivity.EDIT_DISPOSE_RESULT);
                    textViewToUpdate.setText(disposeReferenceNumber);
                    for(Assets a:GlobalData.getInstance().searchedAssets){
                        a.setDisposalReference(disposeReferenceNumber);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void navigateToMain(){
        Intent i = new Intent(AssetsDisposalActivity.this, MainNavigationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        AssetsDisposalActivity.this.startActivity(i);
    }

    /** This is a callback handler after batch api call is requested for final disposal of assets..*/
    @Override
    public void onBatchRequestComplete(String[] successBatchResponses, String[] failureBatchResponses, String exceptionMessage) {
        /*Initialize Progress Dialog..*/
        CiresonUtilites.getLoadingDialog().dismiss();
        /*If there is no exceptional message..*/
        if(exceptionMessage==null){
            if(failureBatchResponses.length>0){
                /*Handle the failure message from batch response..*/
                CiresonUtilites.displayMessage(getResources().getString(R.string.error),failureBatchResponses[0],this).show(this.getFragmentManager(),"");
            }else{
                GlobalData.getInstance().clearEditorsSelectedStringItems();
                GlobalData.getInstance().clearEditorInstances();
                GlobalData.getInstance().resetTemporaryAsset();
                GlobalData.getInstance().searchedAssets.clear();
                navigateToMain();
            }
        }else{
            /*If token is expired, redirect to the logout page..*/
            /*Display exception message..*/
            CiresonUtilites.displayMessage(getResources().getString(R.string.error),exceptionMessage,this).show(this.getFragmentManager(),"");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = null;
        switch (i){
            case 0:
                textViewToUpdate = (TextView) view.findViewById(R.id.dispose_assets_list_row_selected_text);
                intent = new Intent(this,DatePickerActivity.class);
                intent.putExtra(AssetsDisposalActivity.EXTRA_TEXT, AssetsDisposalActivity.EXTRA_TEXT);
                startActivityForResult(intent, DISPOSE_ASSETS_RECEIVED_DATE);
                break;
            case 1:
                textViewToUpdate = (TextView) view.findViewById(R.id.dispose_assets_list_row_selected_text);
                intent = new Intent(this,DisposalReferenceEditorActivity.class);
                intent.putExtra(DisposalReferenceEditorActivity.EDIT_DISPOSE_EXTRA,String.valueOf(getResources().getText(R.string.edit_dispose_ref_title_for_dispose)));
                startActivityForResult(intent,DISPOSE_ASSETS_REFERENCE_NUMBER);
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }
    }

    /*List adapter..*/
    /*Select List Adapter..*/
    private class DisposeAssetsListAdapter extends BaseAdapter{

        /*Instances..*/
        private LayoutInflater inflater;
        private String[] displayTexts = {
            String.valueOf(getResources().getText(R.string.dispose_purchase_date)),
            String.valueOf(getResources().getText(R.string.dispose_purchase_reference_number)),
            String.valueOf(getResources().getText(R.string.dispose_purchase_clear_primary_user)),
            String.valueOf(getResources().getText(R.string.dispose_purchase_clear_custodian))
        };

        private int[] icons = {
            R.drawable.calender,
            R.drawable.disposal_reference_number,
            R.drawable.custodian,
            R.drawable.custodian
        };

        private ImageView imageView;
        private TextView textView,selectedTextView;
        private ToggleButton toggleButton;

        public DisposeAssetsListAdapter(Context context){
            super();
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return displayTexts.length;
        }

        @Override
        public String getItem(int position) {
            return displayTexts[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View cellView = convertView;

            if(convertView==null){
                if(position==0||position==1){
                    cellView = inflater.inflate(R.layout.dispose_assets_list_row1, parent, false);
                    selectedTextView = (TextView)cellView.findViewById(R.id.dispose_assets_list_row_selected_text);
                }else{
                    cellView = inflater.inflate(R.layout.dispose_assets_list_row2, parent, false);
                    toggleButton = (ToggleButton)cellView.findViewById(R.id.dispose_assets_list_row_toggle);
                    if(position==2){
                        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                clearPrimaryUser = isChecked;
                            }
                        });
                    }else{
                        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                clearCustodians = isChecked;
                            }
                        });
                    }
                }
            }
            ImageView imageView = (ImageView)cellView.findViewById(R.id.dispose_assets_list_row_image);
            textView = (TextView)cellView.findViewById(R.id.dispose_assets_list_row_text);
            if(icons.length>position){
                imageView.setImageResource(icons[position]);
            }
            textView.setText(getItem(position));
            return cellView;
        }

    }

}

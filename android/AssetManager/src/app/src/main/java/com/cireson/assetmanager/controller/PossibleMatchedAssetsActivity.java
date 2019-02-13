
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.model.Assets;
import com.cireson.assetmanager.util.CiresonDialogs;
import com.cireson.assetmanager.util.CustomDialog;

import java.util.ArrayList;


public class PossibleMatchedAssetsActivity extends CiresonBaseActivity implements AdapterView.OnItemClickListener {

    /*Instances..*/
    /*Views*/
    private ListView listView;

    private GlobalData globalData;
    private CiresonDialogs dialog;
    private ArrayList<Assets> unMatchedAssets;
    private Assets selectedAsset;
    public static final String NEW_BARCODE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_possible_matched_assets);

        /*Get the text view of sub title to set the barcode number..*/
        TextView barcodeTextView = (TextView)findViewById(R.id.possible_matched_assets_barcode_number);
        if(getIntent()!=null){
            barcodeTextView.setText(getIntent().getStringExtra(NEW_BARCODE));
        }
        listView = (ListView)findViewById(R.id.possible_matched_assets_lists);
        /*Get all the unmatched assets from the searchedAssets list.*/
        unMatchedAssets = new ArrayList<Assets>();
        globalData = GlobalData.getInstance();
        for(Assets a:globalData.searchedAssets){
            if(!a.isMatched&&!a.isNew){
                unMatchedAssets.add(a);
            }
        }
        final PossibleMatchedAssetsAdapter adpter = new PossibleMatchedAssetsAdapter(this, unMatchedAssets);
        listView.setAdapter(adpter);
        listView.setOnItemClickListener(this);
    }

    /*Handle click events for cancel and done...*/
    public void cancel(View v){
    }

    public void done(View v){
        if(selectedAsset!=null){/*If an item is selected to assign a serial number of asset tag to the new asset..*/
            Intent intent = new Intent(this, AssetsScannerActivity.class);
            dialog = new CiresonDialogs();
            dialog.setContext(this);
            String[] lists = {
                    getResources().getString(R.string.possible_matched_assets_dialog_assetTag),
                    getResources().getString(R.string.possible_matched_assets_dialog_serialNumber),
                    getResources().getString(R.string.dialog_cancel),
            };
            CustomDialog customDialog=new CustomDialog(this);
            customDialog.setTitle(getResources().getString(R.string.dialog_confirmation));
            customDialog .setNumberOfButtons(0);
            customDialog.setMessage(getResources().getString(R.string.possible_matched_assets_dialog_message));
            customDialog .setListItems(lists);
            class PossibleMatchedAssetsDialog implements CustomDialog.CustomDialogActions<PossibleMatchedAssetsDialog>{
                public void handleDialogsClickEvent(int di,int i){
                    Assets a = (Assets)globalData.getImmediateObject();
                    switch(i){
                        case 0:
                            selectedAsset.isMatched = true;
                            selectedAsset.setAssetTag(a.assetTag);
                            globalData.searchedAssets.remove(a);
                            setResult(RESULT_OK);
                            finish();
                            break;
                        case 1:
                            selectedAsset.isMatched = true;
                            selectedAsset.setSerialNumber(a.serialNumber);
                            globalData.searchedAssets.remove(a);
                            setResult(RESULT_OK);
                            finish();
                            break;
                        default:
                            break;
                    }
                }
            };
            customDialog.setCustomDialogActions(new PossibleMatchedAssetsDialog());
            customDialog.show();
        }else{
            /*If no item is selected..*/
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(selectedAsset==unMatchedAssets.get(i)){
            listView.setItemChecked(i,false);
            selectedAsset = null;
        }else{
            selectedAsset = unMatchedAssets.get(i);
        }
    }

    private static class PossibleMatchedAssetsAdapter extends BaseAdapter{
        private LayoutInflater layoutInflater;
        private ArrayList<Assets> assets;

        public PossibleMatchedAssetsAdapter(Context ctx, ArrayList<Assets> assets){
            this.assets = assets;
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
            if (cellRow == null) {
                cellRow = layoutInflater.inflate(R.layout.possible_matched_assets_row, parent, false);
            }
            TextView url = (TextView) cellRow.findViewById(R.id.possible_matched_assets_url);
            TextView name = (TextView) cellRow.findViewById(R.id.possible_matched_assets_name);
            TextView serialNumber = (TextView) cellRow.findViewById(R.id.possible_matched_assets_serialNo);
            TextView tag = (TextView) cellRow.findViewById(R.id.possible_matched_assets_tag);
            url.setText(assets.get(position).name);
            name.setText(assets.get(position).getAssetTypeDisplayName());
            tag.setText(String.format("Tag: %s", assets.get(position).assetTag));
            serialNumber.setText( String.format("Serial NO: %s", assets.get(position).serialNumber));
            ImageView checkedImage = (ImageView) cellRow.findViewById(R.id.possibleMatchedAssetsRightImageView);
            checkedImage.setVisibility(View.INVISIBLE);
            return cellRow;
        }
    }

}

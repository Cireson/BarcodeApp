
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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.CiresonEnumeration;
import com.cireson.assetmanager.model.CostCenter;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.model.Assets;
import com.cireson.assetmanager.model.Location;
import com.cireson.assetmanager.model.Organization;
import com.cireson.assetmanager.util.CiresonListViewAdapter;
import com.cireson.assetmanager.util.CiresonUtilites;
import com.cireson.assetmanager.util.SDCardUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class InventoryAuditsEmailSettingsActivity extends CiresonBaseActivity {
    /*Instances..*/
    /*View instances for dynamic updates..*/
    private ListView reviewListView;
    private TextView titleNumberTextView;

    private ArrayList<Assets> nonInventoredAssets,inventoriedAssets,notMatchedAssets; /*unknown,matched and unmatched*/
    private GlobalData globalData;
    private static int currentTab = 0;
    private Button inventoriedButton=null, nonInventoriedButton=null, notMatchedButton=null;

    public void onCreate(Bundle objectState){
        super.onCreate(objectState);
        setContentView(R.layout.activity_review_email_settings);

        titleNumberTextView = (TextView)findViewById(R.id.titleNumberOfReviewEmailSettings);
        /*Get the buttons so as to maintain their highlights when active..*/
        nonInventoriedButton = (Button)findViewById(R.id.reviewEmailNotInventoriedButton);
        inventoriedButton = (Button)findViewById(R.id.reviewEmailInventoriedButton);
        notMatchedButton = (Button)findViewById(R.id.reviewEmailNoMatchButton);

        nonInventoriedButton.setActivated(true);
        /*Get the list view..*/
        reviewListView = (ListView)findViewById(R.id.reviewEmailListView);

        nonInventoredAssets = new ArrayList<Assets>();
        inventoriedAssets = new ArrayList<Assets>();
        notMatchedAssets = new ArrayList<Assets>();
        globalData = GlobalData.getInstance();

    }

    /*handler for save button*/
    /*Click handler for the save button*/
    public void email(View v){
        /*Create a file that is to be stored in the internal storage and attach to the email intent..*/
        /*File Names..*/
        String nonInventoriedFileName = "NonInventoried.csv";
        String inventoriedFileName = "Inventoried.csv";
        String unMatchedFileName = "Unmatched.csv";

        /*Files..*/
        File nonInventoriedFile = null;
        File inventoriedFile = null;
        File unMatchedFile = null;

        /*Check if these files exist. If these files exist, clear the files..*/
        clearFilesIfEXist(nonInventoriedFileName,inventoriedFileName,unMatchedFileName);
        /*Write to CSVs to corresponding files..*/
        nonInventoriedFile = writeCSVFilesToStorage(getCSVString(nonInventoredAssets),nonInventoriedFileName);
        inventoriedFile = writeCSVFilesToStorage(getCSVString(inventoriedAssets),inventoriedFileName);
        unMatchedFile = writeCSVFilesToStorage(getCSVWithBarcodesForUnmatched(),unMatchedFileName);

        /*Prepare an array list of URI to hold URI of these files..*/
        ArrayList<Uri> uriArrayList = new ArrayList<Uri>();
        /* Create the Intent to open up an email application via lists of available applications.. */
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("message/rfc822");

        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, String.format("Inventory Report - (%s)",dft.format(Calendar.getInstance().getTime())));
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Inventory audit reports are attached.");
        /*Attach files to email if files are not null..*/
        if(nonInventoriedFile!=null){
            uriArrayList.add(Uri.parse("file://" + nonInventoriedFile.getAbsolutePath()));
        }
        if(inventoriedFile!=null){
            uriArrayList.add(Uri.parse("file://" + inventoriedFile.getAbsolutePath()));
        }
        if(unMatchedFile!=null){
            uriArrayList.add(Uri.parse("file://" + unMatchedFile.getAbsolutePath()));
        }

        /*If single file is to be attached..*/
        /*emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(unMatchedFile));*/
        if(uriArrayList.size()>0){
            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriArrayList);
        }
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        StringBuilder sendMailMessage = new StringBuilder();
        sendMailMessage.append("Send mail via...");
        CharSequence sendMailMessageCharacters = sendMailMessage.toString();
        try{
            startActivityForResult(Intent.createChooser(emailIntent, sendMailMessageCharacters),1);
        }catch (ClassCastException e){
        }catch (Exception e){
        }
    }

    /*Clear files if they already exist in the storage..*/
    private void clearFilesIfEXist(String... fileNames){
        for(String fileName:fileNames){
            File file = SDCardUtils.getFile(this, "/Cireson",fileName);
            if(file.exists()){
                file.delete();
            }
        }
    }

    /*Write CSV files to the storage..*/
    private File writeCSVFilesToStorage(String csvString, String fileName){
        File file = null;
        if(SDCardUtils.appendToSdcard(getApplicationContext(),csvString,"/Cireson", fileName)) {
            file = SDCardUtils.getFile(getApplicationContext(), "/Cireson", fileName);
        }else{
            Toast.makeText(getApplicationContext(),"Sorry, CSV file could not be saved to a storage.",Toast.LENGTH_SHORT);
        }
        return file;
    }

    /*Return CSV strings..*/
    /*Return CSV strings for assets..*/
    private <T extends Assets> String getCSVString(ArrayList<T> arrayList){
        StringBuilder csvString = new StringBuilder();
        csvString.append(getSearchValues());
        csvString.append("Search Details\n");
        csvString.append("Name,FullName,HardwareAssetId,SerialNumber,AssetTag\n");
        for(T asset:arrayList){
            csvString.append(String.format("%s,%s,%s,%s,%s\n",asset.displayName,asset.fullName,asset.hardwareAssetID,asset.serialNumber,asset.assetTag));
        }
        return csvString.toString();
    }

    /*With BarCodes*/
    private String getCSVWithBarcodesForUnmatched(){
        /*CSV string for new or unknown assets..*/
        StringBuilder csvString = new StringBuilder();
        csvString.append(getSearchValues());
        csvString.append("Scanned Barcode\n");
        for(Assets asset:notMatchedAssets){
            csvString.append(String.format("%s\n",asset.assetTag));
        }
        return csvString.toString();
    }

    private String getSearchValues(){
        StringBuilder searchString = new StringBuilder();

        searchString.append("Search Values\n");
        searchString.append("Property Name,Operator,Value\n");

        if(AssetsEditorActivity.statusLists!=null&&AssetsEditorActivity.statusLists.size()>0){
            StringBuilder theList = new StringBuilder();
            for(int i=0;i<AssetsEditorActivity.statusLists.size();i++){
                if(i==AssetsEditorActivity.statusLists.size()-1){
                    theList.append(AssetsEditorActivity.statusLists.get(i).text);
                }else{
                    theList.append(AssetsEditorActivity.statusLists.get(i).text+", ");
                }
            }
            searchString.append("Status, Equals, " + CiresonUtilites.escape(theList.toString())+"\n");
        }

        if(AssetsEditorActivity.locationLists!=null&&AssetsEditorActivity.locationLists.size()>0){
            StringBuilder theList = new StringBuilder();
            for(int i=0;i<AssetsEditorActivity.locationLists.size();i++){
                if(i==AssetsEditorActivity.locationLists.size()-1){
                    theList.append(AssetsEditorActivity.locationLists.get(i).displayName);
                }else{
                    theList.append(AssetsEditorActivity.locationLists.get(i).displayName+", ");
                }
            }
            searchString.append("Location, Equals, " + CiresonUtilites.escape(theList.toString())+"\n");
        }

        if(AssetsEditorActivity.organizationLists!=null&&AssetsEditorActivity.organizationLists.size()>0){
            StringBuilder theList = new StringBuilder();
            for(int i=0;i<AssetsEditorActivity.organizationLists.size();i++){
                if(i==AssetsEditorActivity.organizationLists.size()-1){
                    theList.append(AssetsEditorActivity.organizationLists.get(i).displayName);
                }else{
                    theList.append(AssetsEditorActivity.organizationLists.get(i).displayName+", ");
                }
            }
            searchString.append("Organization, Equals, " + CiresonUtilites.escape(theList.toString())+"\n");
        }

        if(AssetsEditorActivity.costCenterLists!=null&&AssetsEditorActivity.costCenterLists.size()>0){
            StringBuilder theList = new StringBuilder();
            for(int i=0;i<AssetsEditorActivity.costCenterLists.size();i++){
                if(i==AssetsEditorActivity.costCenterLists.size()-1){
                    theList.append(AssetsEditorActivity.costCenterLists.get(i).displayName);
                }else{
                    theList.append(AssetsEditorActivity.costCenterLists.get(i).displayName+", ");
                }
            }
            searchString.append("Cost Center, Equals, " + CiresonUtilites.escape(theList.toString())+"\n");
        }

        if(AssetsEditorActivity.custodianLists!=null&&AssetsEditorActivity.custodianLists.size()>0){
            StringBuilder theList = new StringBuilder();
            for(int i=0;i<AssetsEditorActivity.custodianLists.size();i++){
                if(i==AssetsEditorActivity.custodianLists.size()-1){
                    theList.append(AssetsEditorActivity.custodianLists.get(i).displayName);
                }else{
                    theList.append(AssetsEditorActivity.custodianLists.get(i).displayName+", ");
                }
            }
            searchString.append("Custodian, Equals, " + CiresonUtilites.escape(theList.toString())+"\n");
        }

        if(globalData.currentDate!=null&&!globalData.currentDate.trim().isEmpty()){
            searchString.append("Received Date, Equals, " + CiresonUtilites.escape(globalData.currentDate)+"\n");
        }

        searchString.append("\n");
        return searchString.toString();
    }

    /*When filter buttons are clicked..*/
    public void filterNonInventoried(View v){
        currentTab = 0;
        reviewListView.setAdapter(new ReviewEmailListAdapter(this,nonInventoredAssets));
        titleNumberTextView.setText(String.valueOf(nonInventoredAssets.size()));
        //adjustButtonColors(nonInventoriedButton);
        nonInventoriedButton.setActivated(true);
        inventoriedButton.setActivated(false);
        notMatchedButton.setActivated(false);
    }

    public void filterInventoried(View v){
        currentTab = 1;
        reviewListView.setAdapter(new ReviewEmailListAdapter(this,inventoriedAssets));
        titleNumberTextView.setText(String.valueOf(inventoriedAssets.size()));
        nonInventoriedButton.setActivated(false);
        inventoriedButton.setActivated(true);
        notMatchedButton.setActivated(false);
    }

    public void filterNoMatch(View v){
        currentTab = 2;
        reviewListView.setAdapter(new ReviewEmailListAdapter(this,notMatchedAssets));
        titleNumberTextView.setText(String.valueOf(notMatchedAssets.size()));
        nonInventoriedButton.setActivated(false);
        inventoriedButton.setActivated(false);
        notMatchedButton.setActivated(true);
    }

    public void toHome(View view){
        Intent i = new Intent(this, MainNavigationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    /*Adjust button background color..*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void adjustButtonColors(Button activeButton){
        /*Reset colors for buttons and texts..*/
        nonInventoriedButton.setBackground(getResources().getDrawable(R.drawable.rounded_button));
        nonInventoriedButton.setTextColor(getResources().getColor(R.color.white_color));
        inventoriedButton.setBackground(getResources().getDrawable(R.drawable.rounded_button));
        inventoriedButton.setTextColor(getResources().getColor(R.color.white_color));
        notMatchedButton.setBackground(getResources().getDrawable(R.drawable.rounded_button));
        notMatchedButton.setTextColor(getResources().getColor(R.color.white_color));
        /*Change color states of active button and the text.*/
        activeButton.setBackgroundColor(getResources().getColor(R.color.white_color));
        activeButton.setTextColor(getResources().getColor(R.color.orange_text_color));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*Default calls..*/
    public void onPause(){
        super.onPause();
    }

    public void onResume(){
        super.onResume();
        if(globalData.searchedAssets!=null&&
                ((notMatchedAssets!=null&&notMatchedAssets.size()==0)&&
                 (inventoriedAssets!=null&&inventoriedAssets.size()==0)&&
                 (nonInventoredAssets!=null&&nonInventoredAssets.size()==0))){
            for(Assets a: globalData.searchedAssets){
                if(a.isNew){
                    notMatchedAssets.add(a);
                }
                else if(a.isMatched){
                    inventoriedAssets.add(a);
                }
                else{
                    nonInventoredAssets.add(a);
                }
            }
        }
            if(currentTab==0){
                reviewListView.setAdapter(new ReviewEmailListAdapter(this,nonInventoredAssets));
                titleNumberTextView.setText(String.valueOf(nonInventoredAssets.size()));
            }else if(currentTab == 1){
                reviewListView.setAdapter(new ReviewEmailListAdapter(this,inventoriedAssets));
                titleNumberTextView.setText(String.valueOf(inventoriedAssets.size()));
            }else if(currentTab==2){
                reviewListView.setAdapter(new ReviewEmailListAdapter(this,notMatchedAssets));
                titleNumberTextView.setText(String.valueOf(notMatchedAssets.size()));
            }
    }

    public void onDestroy(){
        super.onDestroy();
    }

    /*An adapter inner static class*/
    private static class ReviewEmailListAdapter extends CiresonListViewAdapter<Assets> {
        LayoutInflater inflater;
        private ArrayList<Assets> filteredAssets;

        public ReviewEmailListAdapter(Context ctx,ArrayList<Assets> filteredAssets){
            super(filteredAssets);
            inflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
            this.filteredAssets = filteredAssets;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if(rowView==null){
                rowView = inflater.inflate(R.layout.review_email_settings_row, parent, false);
            }
            TextView displayNameTextView = (TextView)rowView.findViewById(R.id.reviewEmailSettingsListRowDisplayName);
            TextView barcodeTextView = (TextView)rowView.findViewById(R.id.reviewEmailSettingsListRowBarCode);
            String displayName = getItem(position).displayName;
            if(displayName!=null && !"".equals(displayName)){
                displayNameTextView.setText(displayName);
                displayNameTextView.setVisibility(View.VISIBLE);
                barcodeTextView.setText(getItem(position).getAssetTypeDisplayName());
            }
            else{
                displayNameTextView.setVisibility(View.GONE);
                barcodeTextView.setText(getItem(position).assetTag);
            }
            return rowView;
        }
    }

}

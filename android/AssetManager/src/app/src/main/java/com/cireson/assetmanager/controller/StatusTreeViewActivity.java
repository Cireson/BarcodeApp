
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
import android.view.View;
import android.widget.Toast;
import com.cireson.assetmanager.R;
import com.cireson.assetmanager.component.TreeView;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.model.CiresonEnumeration;
import com.cireson.assetmanager.service.APICaller;
import com.cireson.assetmanager.util.CiresonConstants;
import com.cireson.assetmanager.util.CiresonUtilites;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class StatusTreeViewActivity extends CiresonBaseActivitySecond {
    private TreeView treeView;
    private GlobalData globalData;

    public final static String STATUS_TREE_VIEW_EXTRA = "STATUS_TREE_VIEW_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_status_tree_veiw);
        treeView = (TreeView) findViewById(R.id.treeView);
        if(GlobalData.ACTIVE_MAIN_MENU== CiresonConstants.INVENTORY_AUDIT){
            treeView.isMultiSelect = true;
        }

        globalData = GlobalData.getInstance();
        if(globalData.statusEnumerationList==null) {
            CiresonUtilites.initLoadingDialog(R.string.progress_dialog_message, R.style.cireson_progress_dialog_theme, this);
            CiresonUtilites.getLoadingDialog().show();
            callStatusAPI();
        }
        else{
            treeView.setDataSource(globalData.statusEnumerationList);
        }
    }

    public void callStatusAPI(){
        String requestString = CiresonUtilites.getRequestJsonStringForStatus();
        final Type type = new TypeToken<ArrayList<CiresonEnumeration>>(){}.getType();
        APICaller<CiresonEnumeration> apiCaller = new APICaller<CiresonEnumeration>(CiresonEnumeration.class, type, this);
        apiCaller.setMethodForBaseUrl("GetEnumeration");
        apiCaller.callArrayApi(requestString, new APICaller.Callback<ArrayList<CiresonEnumeration>>() {
            @Override
            public void onApiCallComplete(ArrayList<CiresonEnumeration> obj, String error) {
                CiresonUtilites.getLoadingDialog().dismiss();
                Intent i = new Intent();
                if(obj==null){
                    if(error!=null&&error.isEmpty()){
                        setResult(RESULT_OK,i);
                        i.putExtra(STATUS_TREE_VIEW_EXTRA,"Try again");
                        finish();
                    }else if(error!=null){
                        i.putExtra(STATUS_TREE_VIEW_EXTRA, error);
                        setResult(RESULT_OK,i);
                        finish();
                    }
                }else{
                    globalData.statusEnumerationList = obj;
                    treeView.setDataSource(obj);
                }
            }
        });
    }

    public void done(View v){
        ArrayList<CiresonEnumeration> item = treeView.getCheckedItems();
        Intent i = new Intent();
        if(item!=null){
            globalData.selectedStatuses = item;
            if(GlobalData.ACTIVE_MAIN_MENU == CiresonConstants.INVENTORY_AUDIT){
                String result = "";
                for(int l=0;l<item.size();l++){
                    result += (l==0?"":", ") + item.get(l).text;
                }
                i.putExtra(AssetsEditorActivity.EDIT_ASSETS_RESULT, result);
            }else{
                if(item.size()>0){
                    i.putExtra(AssetsEditorActivity.EDIT_ASSETS_RESULT, item.get(0).text );
                    globalData.selectedStatus.replace(0,GlobalData.getInstance().selectedStatus.length(),item.get(0).text);
                    globalData.getTemporaryAsset().hardwareAssetStatus.id = item.get(0).id;
                    globalData.getTemporaryAsset().hardwareAssetStatus.name = item.get(0).text;
                }else{
                    i.putExtra(AssetsEditorActivity.EDIT_ASSETS_RESULT, "");
                }
            }
            setResult(RESULT_OK, i);
        }
        else{
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    public void clear(View v){
        if(globalData.selectedStatuses!=null) {
            globalData.selectedStatuses.clear();
        }
        globalData.getTemporaryAsset().hardwareAssetStatus.id = null;
        globalData.getTemporaryAsset().hardwareAssetStatus.name = null;
        treeView.clearSelection();
    }

}

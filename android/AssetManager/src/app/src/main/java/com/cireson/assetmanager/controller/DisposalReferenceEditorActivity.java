
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.util.CiresonConstants;
import com.cireson.assetmanager.util.CiresonUtilites;

public class DisposalReferenceEditorActivity extends CiresonBaseActivitySecond {

    /*Instances..*/
    private TextView titleTextView;
    private EditText editText;

    private GlobalData globalData;

    /*Static fields..*/
    public final static String EDIT_DISPOSE_EXTRA = "EDIT_DISPOSE_EXTRA";
    public final static String EDIT_DISPOSE_TEXT_FIELD_EXTRA = "EDIT_DISPOSE_TEXT_FIELD_EXTRA";
    public final static String EDIT_DISPOSE_RESULT = "EDIT_DISPOSE_RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_disposal_reference_number);

        titleTextView = (TextView)findViewById(R.id.edit_disposal_title_text);
        editText = (EditText)findViewById(R.id.edit_text_of_edit_disposal);

        globalData = GlobalData.getInstance();
        String fieldValue = getIntent().getStringExtra(DisposalReferenceEditorActivity.EDIT_DISPOSE_TEXT_FIELD_EXTRA);
        if(fieldValue!=null) {
            editText.setText(String.valueOf(fieldValue));
        }

        /*Receive intent value..*/
        String intentValue = getIntent().getStringExtra(EDIT_DISPOSE_EXTRA);
        if(intentValue!=null){
            titleTextView.setText(String.valueOf(intentValue));
        }
    }

    public void done(View view){
        Intent i = new Intent();
        String editTextValue = String.valueOf(editText.getText()).trim();
        if(editTextValue!=null&&!editTextValue.isEmpty()){
            switch (GlobalData.ACTIVE_MAIN_MENU){
                case CiresonConstants.RECEIVED_ASSETS:
                    if(GlobalData.SCAN_TO_ADD_ASSETS){
                        switch(GlobalData.ACTIVE_MANUFACTURER_OR_MODEL_LIST_FOR_ADD_ASSETS){
                            case CiresonConstants.ACTIVE_LIST_MANUFACTURER_FOR_ADD_ASSETS:
                                globalData.selectedManufacturer = editTextValue;
                                globalData.getTemporaryAsset().manufacturer = editTextValue;
                                break;
                            case CiresonConstants.ACTIVE_LIST_MODEL_FOR_ADD_ASSETS:
                                globalData.selectedModel = editTextValue;
                                globalData.getTemporaryAsset().model = editTextValue;
                                break;
                        }
                    }
                    i.putExtra(AssetsEditorActivity.EDIT_ASSETS_RESULT,editTextValue);
                    break;
                case CiresonConstants.DISPOSE_ASSETS:
                    i.putExtra(EDIT_DISPOSE_RESULT,editTextValue);
                    globalData.getTemporaryAsset().disposalReference = editTextValue;
                    break;
                default:
                    i.putExtra(AssetsEditorActivity.EDIT_ASSETS_RESULT,editTextValue);
                    break;
            }
        }
        setResult(RESULT_OK,i);
        finish();
    }

    public void clear(View view){
        editText.setText("");
        globalData.getTemporaryAsset().manufacturer = null;
        globalData.getTemporaryAsset().model = null;
        globalData.getTemporaryAsset().disposalReference = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        CiresonUtilites.hideSoftKeyboard(this);
        return true;
    }

}


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


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.util.CiresonUtilites;

/**This activity displays a note editor which is used during swap process..*/
public class SwapNoteEditorActivity extends CiresonBaseActivitySecond {

    /*Instances..*/
    private EditText editText;
    private GlobalData globalData;

    public static final String SWAP_NOTE_EDIT_TEXT_EXTRA = "SWAP_NOTE_EDIT_TEXT_EXTRA";

    /**Defualt constructor..*/
    public SwapNoteEditorActivity(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_swap_note_editor);
        editText = (EditText)findViewById(R.id.idSwapNoteEditorTextBox);
        String editTextValue = getIntent().getStringExtra(SWAP_NOTE_EDIT_TEXT_EXTRA);
        if(editTextValue!=null){
            editText.setText(editTextValue);
        }
        globalData = GlobalData.getInstance();
    }

    /*When a done button is clicked..*/
    public void done(View view){
        StringBuilder note = new StringBuilder();
        if(editText.getText()!=null){
            note.append(String.valueOf(String.valueOf(editText.getText()).trim()));
        }
        /*Check if string from edit text contains more than 4000 characters.
        If it is true, display message that note can not exceed 4000 characters.*/
        if(note.toString().length()>4000){
            CiresonUtilites.displayMessage(getString(R.string.message),
                    getString(R.string.swap_assets_note_editor_text_limit_exceeded),
                    this).show(getFragmentManager(),"");
        }else{
            if(note.toString()==null||note.toString().isEmpty()){
                note.append("");
            }else{
                /*Only if text from edit text is not empty or null assign to the notes property of temporary asset.*/
                globalData.getTemporaryAsset().notes = note.toString();
            }
            globalData.currentNotesForSwapAssets = note;
            setResult(RESULT_OK,getIntent().putExtra(AssetsEditorActivity.EDIT_ASSETS_RESULT,note.toString()));
            finish();
        }
    }

    /*When a clear button is clicked..*/
    public void clear(View view){
        globalData.getTemporaryAsset().notes = null;
        editText.setText(new StringBuilder(""));
        globalData.currentNotesForSwapAssets.replace(0,globalData.currentNotesForSwapAssets.length(),"");
    }

}

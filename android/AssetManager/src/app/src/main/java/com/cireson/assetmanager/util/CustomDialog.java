
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


package com.cireson.assetmanager.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cireson.assetmanager.R;

import static android.R.layout.simple_list_item_1;


public class CustomDialog extends Dialog {
    private Context context;
    private String title;
    private String message;
    private String positiveButtonMessage = "";
    private String negativeButtonMessage = "";
    private String neutralButtonMessage = "";
    private int numberOfButtons = 0;
    private String[] listItems;
    Button okButton;
    Button noButton;
    Button cancelButton;
    TextView txtTitle;
    TextView txtMessage;
    ListView listView;
    private final int BUTTON_POSITIVE=-1;
    private final int BUTTON_NEGATIVE=-2;
    private final int BUTTON_CANCEL=-3;
    int index;
    private int buttonsClicked=-1;
    /*This interface holds any calling object to implement different actions on buttons click..*/
    private CustomDialogActions customDialogActions;

    public CustomDialog(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);
        //tell the Dialog to use the dialog.xml as it's layout description
        okButton=(Button)findViewById(R.id.ok_button);
        noButton=(Button)findViewById(R.id.no_button);
        cancelButton=(Button)findViewById(R.id.cancel_button);
        txtTitle=(TextView)findViewById(R.id.title);
        txtMessage=(TextView)findViewById(R.id.message);
        listView=(ListView)findViewById(R.id.dialog_list);



        setContents();
        setDialog();



//        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButton);
//          cancelButton.setOnClickListener(new View.OnClickListener() {
//              @Override
//              public void onClick(View v) {
//                        dialog.dismiss();
//              }
//
//
//          });

//        dialog.show();


    }

    private void setDialog() {

        if(numberOfButtons==0) {
            okButton.setVisibility(View.GONE);
            noButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
        }
        else if(numberOfButtons==1) {
            okButton.setVisibility(View.VISIBLE);
            noButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);

        }
        else if(numberOfButtons==2) {
            okButton.setVisibility(View.VISIBLE);
            noButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.GONE);

        }
        else  {
            okButton.setVisibility(View.VISIBLE);
            noButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);

        }
        if(listItems==null ) {
            listView.setVisibility(View.GONE);
        }
        else {
            listView.setVisibility(View.VISIBLE);
        }
        if(message==null|| message=="") {
            txtMessage.setVisibility(View.GONE);
        }
        else {
            txtMessage.setVisibility(View.VISIBLE);
        }
    }

    private void setContents() {

            txtTitle.setText(title);


            txtMessage.setText(message);

        okButton.setText(positiveButtonMessage);
        noButton.setText(negativeButtonMessage);
        cancelButton.setText(neutralButtonMessage);
        setItemsInListView();

    }

    public void setItemsInListView(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, simple_list_item_1, listItems);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                customDialogActions.handleDialogsClickEvent(buttonsClicked, position);
                dismiss();
            }

        });
    }

    public void setTitle(String title){

    this.title=title;

    }

    public void setMessage(String message){
        this.message = message;


    }

    public void setNumberOfButtons(int numberOfButtons){
        this.numberOfButtons = numberOfButtons;


    }

    public void setPositiveButtonMessage(String positiveButtonMessage) {
        this.positiveButtonMessage = positiveButtonMessage;


    }

    public void setNegativeButtonMessage(String negativeButtonMessage) {
        this.negativeButtonMessage = negativeButtonMessage;


    }

    public void setNeutralButtonMessage(String neutralButtonMessage) {
        this.neutralButtonMessage = neutralButtonMessage;


    }

    public void setListItems(String[] listItems) {

        this.listItems = listItems;


    }

    public void setCustomDialogActions(CustomDialogActions customDialogActions) {
        this.customDialogActions = customDialogActions;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_button:
                buttonsClicked=BUTTON_POSITIVE;
                break;
            case R.id.no_button:
                buttonsClicked=BUTTON_NEGATIVE;
                break;
            case R.id.cancel_button:
                buttonsClicked=BUTTON_CANCEL;
                break;
            default:
                buttonsClicked=BUTTON_POSITIVE;

        }
        dismiss();

        customDialogActions.handleDialogsClickEvent(buttonsClicked, 0);
    }

//    /*Any activity that requires this dialog has to implement this interface to perform actions in positive or negative or neutral state..*/
    public interface CustomDialogActions<T>{
        public void handleDialogsClickEvent(int dialogInterface,int i);
    }

}

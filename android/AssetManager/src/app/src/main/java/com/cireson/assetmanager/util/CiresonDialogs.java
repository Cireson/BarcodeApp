
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

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

/**
 * Created by welcome on 4/10/2014.
 */
public class CiresonDialogs extends DialogFragment{

    /*Instances..*/
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private String title;
    private String message;
    private String positiveButtonMessage = "";
    private String negativeButtonMessage = "";
    private String neutralButtonMessage = "";
    private int numberOfButtons = 0;
    private String[] listItems;
    private Context context;

    /*This interface holds any calling object to implement different actions on buttons click..*/
    private CiresonDialogActions ciresonDialogActions;

    /*Dialog's click event listener..*/
    private CiresonDialogsClickListener ciresonDialogsClickListener;

    public CiresonDialogs(){
        super();
    }

    /*called when dialog is created..*/
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        /*First of all create this dialog..*/
        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);

        if(listItems==null||listItems.length<1){
            alertDialogBuilder.setMessage(message);
        }
        ciresonDialogsClickListener = new CiresonDialogsClickListener();
        switch(numberOfButtons){
            case 0:
                    if(listItems!=null&&listItems.length>0){
                        alertDialogBuilder.setItems(listItems,ciresonDialogsClickListener);
                    }
                break;
            case 1:
                    if(negativeButtonMessage.isEmpty()){
                        alertDialogBuilder.setPositiveButton(positiveButtonMessage,ciresonDialogsClickListener);
                    }else{
                        alertDialogBuilder.setNegativeButton(negativeButtonMessage, ciresonDialogsClickListener);
                    }
                break;
            case 2:
                alertDialogBuilder.setPositiveButton(positiveButtonMessage,ciresonDialogsClickListener);
                alertDialogBuilder.setNegativeButton(negativeButtonMessage, ciresonDialogsClickListener);
                break;
            default:
                alertDialogBuilder.setPositiveButton(positiveButtonMessage, ciresonDialogsClickListener);
                alertDialogBuilder.setNegativeButton(negativeButtonMessage, ciresonDialogsClickListener);
                alertDialogBuilder.setNeutralButton(neutralButtonMessage, ciresonDialogsClickListener);
                break;
        }
        alertDialog = alertDialogBuilder.create();
        return alertDialog;
    }

    /*Setters getters..*/

    public void setContext(Context context) {
        this.context = context;
    }

    public CiresonDialogs setTitle(String title){
        this.title = title;
        return this;
    }

    public CiresonDialogs setMessage(String message){
        this.message = message;
        return this;
    }

    public CiresonDialogs setNumberOfButtons(int numberOfButtons){
        this.numberOfButtons = numberOfButtons;
        return this;
    }

    public CiresonDialogs setPositiveButtonMessage(String positiveButtonMessage) {
        this.positiveButtonMessage = positiveButtonMessage;
        return this;
    }

    public CiresonDialogs setNegativeButtonMessage(String negativeButtonMessage) {
        this.negativeButtonMessage = negativeButtonMessage;
        return this;
    }

    public CiresonDialogs setNeutralButtonMessage(String neutralButtonMessage) {
        this.neutralButtonMessage = neutralButtonMessage;
        return this;
    }

    public CiresonDialogs setCiresonDialogActions(CiresonDialogActions ciresonDialogActions) {
        this.ciresonDialogActions = ciresonDialogActions;
        return this;
    }

    public CiresonDialogs setListItems(String[] listItems) {

        this.listItems = listItems;
        return this;
    }

    /*Handle dialog buttons clicks..*/
    private class  CiresonDialogsClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                }
            else {
                    ciresonDialogActions.handleDialogsClickEvent(dialogInterface, i);
                }
        }
    }

    /*Any activity that requires this dialog has to implement this interface to perform actions in positive or negative or neutral state..*/
    public interface CiresonDialogActions<T>{
        public void handleDialogsClickEvent(DialogInterface dialogInterface,int i);
    }

}

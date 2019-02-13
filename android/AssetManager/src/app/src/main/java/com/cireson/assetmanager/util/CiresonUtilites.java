
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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.controller.LoginActivity;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by welcome on 4/16/2014.
 */
public class CiresonUtilites {

    /*Instances..*/
    /*An arraylist to hold group title alphabets..*/
    public static ArrayList<String> groupOfTitleAlphabets = new ArrayList<String>();
    public static LinkedHashMap<String,Integer> alphaIndexes = new LinkedHashMap<String, Integer>();

    private static CiresonDialogs dialog = null;
    private static ProgressDialog progressDialog = null;
    private static Intent intent = null;

    /*This instance is required to clear current user from the local store..*/
    private static User user = null;

    public CiresonUtilites(){
    }

    /*Return an array of string that is sorted alphabetically and grouped in ascending order.*/
    public static List<String> getAlphaSortedListsOfString(String[] list){
        Arrays.sort(list);
        char character = list[0].charAt(0);
        List<String> all = new LinkedList<String>();

        /*First clear static lists if any items already exist.*/
        groupOfTitleAlphabets.clear();
        alphaIndexes.clear();

        groupOfTitleAlphabets.add(String.valueOf(character));
        all.add(String.valueOf(character));
        alphaIndexes.put(String.valueOf(character),all.indexOf(String.valueOf(character)));

        for(String s:list){
            if(character!=s.charAt(0)){
                character = s.charAt(0);
                groupOfTitleAlphabets.add(String.valueOf(character));
                all.add(String.valueOf(character));
                alphaIndexes.put(String.valueOf(character),all.indexOf(String.valueOf(character)));
            }
            all.add(s);
        }
        return all;
    }

    public static String getRequestJsonStringForStatus(){
        String token = "";
        if(GlobalData.getInstance().getCurrentUser()!=null) {
            token = GlobalData.getInstance().getCurrentUser().getToken();
        }
         return "{\"Token\":\""+token+"\",\"Id\":\"6b7304c4-1b09-bffc-3fe3-1cfd3eb630cb\",\"Action\":\"GetEnumeration\"}";
    }

    public static String getRequestJsonString(String action, String token, String typeId, String propertyId, String operator, String value,String id){
        String assetsSearchResultJsonString =
                "{"+
                        "\"Action\": \"%s\","+
                        "\"Token\": %s,"+
                        "\"Criteria\": {"+
                        "\"Base\": {"+
                        "\"Expression\": {"+
                        "\"SimpleExpression\": {"+
                        "\"ValueExpressionLeft\": {"+
                        "\"Property\":\"$Context\\/Property[Type='%s']\\/%s$\""+
                        "},"+
                        "\"Operator\": \"%s\","+
                        "\"ValueExpressionRight\": {"+
                        "\"Value\":\"%s\""+
                        "}"+
                        "}"+
                        "}"+
                        "}"+
                        "},"+
                        "\"Id\": \"%s\""+
                "}";
        return  String.format(assetsSearchResultJsonString,action,token,typeId,propertyId,operator,value,id);
    }

    public static String getRequestJsonString(String type,String displayName,String operator,String value){
        String assetsSearchResultJsonString = "{\"Base\": {"+
                "\"Expression\": {"+
                "\"SimpleExpression\": {"+
                "\"ValueExpressionLeft\": {"+
                "\"Property\":\"$Context\\/Property[Type='%s']\\/%s$\""+
                "},"+
                "\"Operator\": \"%s\","+
                "\"ValueExpressionRight\": {"+
                "\"Value\":\"%s\""+
                "}"+
                "}"+
                "}"+
                "}}";
        return  String.format(assetsSearchResultJsonString,type,displayName,operator,value);
    }

    /*Validate if a given string is a number..*/
    public static boolean isPositiveNumber(String value){
        if(value==null||value.isEmpty()){
            return false;
        }
        for(int i=0;i<value.length();i++){
            Character c = value.charAt(i);
            if(c<'0'&&c>'9'){
                return false;
            }
        }
        return true;
    }

    public static boolean isWifiConnectionAvailable(Context context){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected();
    }

    /*A generic static dialog display for a simple message..*/
    public static CiresonDialogs displayMessage(String title,String message,Context context){
        dialog = new CiresonDialogs();
        dialog.setContext(context);
        dialog.setTitle(title)
                .setMessage(message)
                .setNumberOfButtons(1)
                .setPositiveButtonMessage(context.getResources().getString(R.string.button_ok));
        class MessageDialog implements CiresonDialogs.CiresonDialogActions<MessageDialog> {
            public void handleDialogsClickEvent(DialogInterface di, int i) {
                /*Simply close/dismiss the dialog.*/
                dialog.dismiss();
            }
        }
        dialog.setCiresonDialogActions(new MessageDialog());
        return dialog;
    }

    /*A generic static progress dialog to be displayed for indeterminate loading view.*/
    public static void initLoadingDialog(int dialogMessage,int themeStyle, final Activity activity){
        progressDialog = new ProgressDialog(activity,themeStyle);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.incrementSecondaryProgressBy(50);
        progressDialog.setMessage(activity.getResources().getString(dialogMessage));
    }

    /*Return this generic progress dialog..*/
    public static ProgressDialog getLoadingDialog(){
        return progressDialog;
    }

    public static void hideSoftKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),0);
    }

    public static View setUpListenerToHideSoftKeyboard(View view){
        if(!(view instanceof EditText)){
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    hideSoftKeyboard((Activity)view.getContext());
                    return false;
                }
            });
        }
        return view;
    }

    public static ViewGroup setUpListenerToHideSoftKeyboard(ViewGroup viewGroup){
        viewGroup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard((Activity)view.getContext());
                return false;
            }
        });
        return viewGroup;
    }

    public static StringBuilder getURLWithMethod(StringBuilder baseUrl,String previousMethod,String currentMethod){
        if(previousMethod!=null&&baseUrl.indexOf(previousMethod)!=-1) {
            baseUrl = baseUrl.replace(0, baseUrl.length(), baseUrl.substring(0, baseUrl.indexOf(previousMethod)));
        }
        baseUrl.append(currentMethod);
        return baseUrl;
    }

    /*Help to redirect navigation to the Login page..*/
    public static void redirectForLogin(Context context){
        user=new User();
        user.removeUserFromStorage();
        context.startActivity(new Intent(context,LoginActivity.class));
    }

    /*Returns supplied string within double quotes pair.
    Example :   Json would be "Json"
            Maggie, Json would be "Maggie, Json".*/
    public static String escape( String str_value ){
        StringBuilder row = new StringBuilder();
        if (str_value.indexOf("\"") != -1 || str_value.indexOf(",") != -1) {
            str_value = str_value.replaceAll("\"", "\"\"");
            row.append("\"");
            row.append(str_value);
            row.append("\"");
        } else {
            row.append(str_value);
        }
        return row.toString();
    }

    /**Log the message..*/
    public static void logMessage(String tag,String message){
        Log.d(tag,message);
    }
}

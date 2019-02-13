
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


package com.cireson.assetmanager.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.model.User;
import com.cireson.assetmanager.util.CiresonConstants;
import com.cireson.assetmanager.util.CiresonUtilites;
import com.cireson.assetmanager.controller.MainNavigationActivity;
import com.google.gson.annotations.SerializedName;

/**
 * Created by welcome on 4/7/14.
 */
public class LogIn extends BaseJsonObject implements APICaller.Callback<User>{
    /*Instances..*/
    /*These fields are parsed as JSON fields..*/
    /*Fundamental login instances..*/
    @SerializedName("UserName")
    private String userName = null;
    @SerializedName("Password")
    private String password = null;
    /*These fields (transient) are ignored during JSON parsing.*/
    /*Holds a URL that a user has to specify..*/
    private transient String url;
    /*Context of this application. It is required by asynchronous client request.*/
    private transient Context context;
    private transient User user;

    public LogIn(){
    }

    /*Private operations..*/
    /*Validate the form before initiating post request..*/
    private boolean valid(){
        /*Simple validation...*/
        if(url==null||url.isEmpty()||userName==null||userName.isEmpty()||password==null||password.isEmpty()){
            CiresonUtilites.getLoadingDialog().dismiss();
            Toast.makeText(context, context.getResources().getString(R.string.login_prompt_incomplete_credentials), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /*Proceed Login..*/
    public void logIn(String url,String userName,String password,Context context){
        user  = new User();
        this.url = url;
        this.action = "Login";
        this.userName = userName;
        this.password = password;
        this.context = context;
        if(valid()){
            APICaller<User> apiCaller = new APICaller<User>(User.class,context);
            apiCaller.setMethodForBaseUrl("Login");
            apiCaller.callApi(this,this);
        }
    }

    /*After service has responded..*/
    @Override
    public void onApiCallComplete(User object, String error){
        CiresonUtilites.getLoadingDialog().dismiss();
        if(object!=null){
            Toast.makeText(context, context.getResources().getString(R.string.login_success_message), Toast.LENGTH_SHORT).show();
            object.saveUserOnStorage();
            /*If application flow has already started, just destroy login activity such that user reaches to the previous state from where
            * login was instantiated, otherwise, navigation is directed to main menu page..*/
            ((Activity)context).finish();
            if(!GlobalData.APPLICATION_FLOW_HAS_STARTED){
                Intent i = new Intent(context, MainNavigationActivity.class);
                context.startActivity(i);
            }
        }else{
            if(error!=null&&!error.trim().isEmpty()){
                CiresonUtilites.displayMessage(context.getResources().getString(R.string.error),error,context).show(((Activity)context).getFragmentManager(),"");
            }
        }
    }

}


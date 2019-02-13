
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.service.APICaller;
import com.cireson.assetmanager.service.LogIn;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.util.CiresonUtilites;

public class LoginActivity extends CiresonBaseActivity implements View.OnTouchListener {

    private static String TAG = "Cireson Scanner LoginActivity";

    private LogIn login;
    private EditText urlEditText;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private GlobalData globalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            overridePendingTransition(R.anim.slide_up,R.anim.fade_out);
            setContentView(R.layout.activity_login);
        }catch(Exception ex){
        }

        /*Instantiate a globaldata such that application context can be retrieved globally..*/
        globalData = GlobalData.getInstance();
        globalData.setContext(this);

        login = new LogIn();
        urlEditText = (EditText)findViewById(R.id.editUrl);
        userNameEditText = (EditText)findViewById(R.id.editUser);
        passwordEditText = (EditText)findViewById(R.id.editPassword);
        //setData();
    }
    public void setData(){
        //urlEditText.setText("http://ciresonam.cloudapp.net");

        urlEditText.setText("http://ciresonbc.cloudapp.net");
        userNameEditText.setText("test.analyst");
        passwordEditText.setText("NextIsNow!");
    }
    public void tryLogin(View sender){
        EditText url = (EditText)findViewById(R.id.editUrl);
        String baseUrl = url.getText().toString().trim();
        if(!baseUrl.endsWith("/")){
            baseUrl +="/api/DataService/";
        }
        else{
            baseUrl +="api/DataService/";
        }

        APICaller.setBaseUrl(baseUrl);
        CiresonUtilites.hideSoftKeyboard(this);
        CiresonUtilites.initLoadingDialog(R.string.progress_dialog_message, R.style.cireson_progress_dialog_theme, this);
        CiresonUtilites.getLoadingDialog().show();
        login.logIn(urlEditText.getText().toString(),userNameEditText.getText().toString(),passwordEditText.getText().toString(),this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        CiresonUtilites.hideSoftKeyboard(this);
        return true;
    }
}


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


package com.cireson.assetmanager.model;

import com.cireson.assetmanager.service.BaseJsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by welcome on 4/8/14.
 */
public class User extends BaseJsonObject{
    /*user fields..*/
    /*User detail after logged in..*/
    @SerializedName("ClassTypeId")
    public String classTypeId = null;
    @SerializedName("firstName")
    private String firstName = "";
    @SerializedName("lastName")
    private String lastName = "";
    @SerializedName("token")
    private String token = "";
    @SerializedName("Url")
    private String url = "";

    /*These instances are ignored for JSON parsing..*/
    private static transient GlobalData globalData = null;
    private static final transient String KEY = "com.cireson.assetmanager.model.User";

    static{
        globalData = GlobalData.getInstance();
    }

    public User(){
       /*Default..*/
    }

    /*Save user details..*/
    public void saveUserOnStorage(){
       globalData.saveToStorage(KEY, this);
    }

    /*Retrieve user detail if saved..*/
    public static User loadUserFromStorage(){
       return globalData.getFromStorage(KEY, User.class);
    }

    public void removeUserFromStorage(){
        globalData.deleteFromStorage();
    }

    /*Getters and setters..*/
    public String getUrl() {
        return url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String jpt) {
        token = jpt;
    }
}

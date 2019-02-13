
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
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by welcome on 7/10/2014.
 */
public class CommonSaveModel extends BaseJsonObject{

    /*Here @Expose annotation is used only by the baseIdSerializer to serialize the fields while saving added assets..*/

    /*Instances..*/
    @Expose
    @SerializedName("ProjectionObject")
    public Assets projectionObject = null;
    @Expose
    public String Token;

    public CommonSaveModel(){
        if(GlobalData.getInstance().getCurrentUser()!=null) {
            Token = GlobalData.getInstance().getCurrentUser().getToken();
        }
    }
}

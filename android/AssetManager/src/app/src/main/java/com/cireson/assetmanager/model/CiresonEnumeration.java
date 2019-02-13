
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

import com.cireson.assetmanager.service.CiresonCriteria;
import com.cireson.assetmanager.service.AssetAttributeBase;
import com.cireson.assetmanager.util.CiresonConstants;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by devkhadka on 4/16/14.
 */
public class CiresonEnumeration extends AssetAttributeBase {
    @SerializedName("Id")
    public String id = null;
    @SerializedName("Text")
    public String text = null;
    @SerializedName("HasChildren")
    public boolean hasChildren;
    @SerializedName("EnumNodes")
    public ArrayList<CiresonEnumeration> children;
    @GsonExclude
    public boolean isChecked;

    public CiresonEnumeration(){
        CiresonCriteria.SimpleExpression simpExp = new CiresonCriteria.SimpleExpression();
        simpExp.typeId = CiresonConstants.HardwareAsset_Type;
        simpExp.relationshipTypeId = null;
        simpExp.propertyId = CiresonConstants.HardwareAsset_Property_Status;
        simpExp.isGenericProperty = false;
        simpExp.operator = CiresonConstants.Equals_Operator;
        simpExp.value = getValueProperty();
        setSimpleExpressionTemplate(simpExp);
    }

    @Override
    public String getValueProperty() {
        return id;
    }
}

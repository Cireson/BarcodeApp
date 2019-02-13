
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

/**
 * Created by welcome on 4/15/2014.
 */
public class CustodianUser extends AssetAttributeBase {

    @SerializedName("ClassTypeId")
    public String classTypeId = null;
    @SerializedName("DisplayName")
    public String displayName = null;

    public CustodianUser(){
        CiresonCriteria.SimpleExpression simpExp = new CiresonCriteria.SimpleExpression();
        simpExp.typeId = CiresonConstants.CUSTODIAN_USER_TYPE;
        simpExp.relationshipTypeId = CiresonConstants.HardwareAsset_HasPrimaryUser_RelationshipType;
        simpExp.propertyId = CiresonConstants.Property_Id;
        simpExp.isGenericProperty = true;
        simpExp.operator = CiresonConstants.Equals_Operator;
        simpExp.value = getValueProperty();
        setSimpleExpressionTemplate(simpExp);
    }

    @Override
    public int hashCode() {
        return displayName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        if(o!=null && o.getClass()==CustodianUser.class){
            isEqual = ((CustodianUser)o).displayName.equals(displayName);
        }
        return isEqual;
    }
}


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

public class CostCenter extends AssetAttributeBase {
    /*Instances..*/
    @SerializedName("ClassTypeId")
    public String classTypeId = null;
    @SerializedName("DisplayName")
    public String displayName = null;

    /*operations..*/
    public CostCenter(){
        CiresonCriteria.SimpleExpression simpExp = new CiresonCriteria.SimpleExpression();
        simpExp.typeId = CiresonConstants.Cost_Center_Type;
        simpExp.relationshipTypeId = CiresonConstants.HardwareAsset_HasCostCenter_RelationshipType;
        simpExp.propertyId = CiresonConstants.Property_Id;
        simpExp.isGenericProperty = true;
        simpExp.operator = CiresonConstants.Equals_Operator;
        simpExp.value = getValueProperty();
        setSimpleExpressionTemplate(simpExp);
    }
    /*Sort an array of cost center names, form a linked list containing first character of cost center (single unique character) and all cost centers..*/

    @Override
    public int hashCode() {
        return displayName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        if(o!=null && o.getClass()==CostCenter.class){
            isEqual = ((CostCenter)o).displayName.equals(displayName);
        }
        return isEqual;
    }
}

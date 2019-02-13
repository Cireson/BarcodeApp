
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

import com.cireson.assetmanager.util.CiresonConstants;

/**
 * Created by welcome on 4/24/2014.
 */
public class CustodianRequest extends BaseCriteriaRequest{
    public CustodianRequest(String value){
        CiresonCriteria.SimpleExpression exp =
                new CiresonCriteria.SimpleExpression(CiresonConstants.CUSTODIAN_USER_TYPE, CiresonConstants.Custodian_User_Property_Display_Name, CiresonConstants.Like_Operator,value);
        Criteria = new CiresonCriteria(exp);
        Id = CiresonConstants.User_ProjectionType;
    }
}

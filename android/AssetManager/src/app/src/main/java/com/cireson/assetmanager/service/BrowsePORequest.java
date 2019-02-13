
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class BrowsePORequest extends BaseCriteriaRequest {

    public BrowsePORequest(){
        CiresonCriteria.SimpleExpression exp1 =
                new CiresonCriteria.SimpleExpression(CiresonConstants.Purchase_Order_Type, CiresonConstants.Purchase_Order_Display_Name, CiresonConstants.Like_Operator, CiresonConstants.WildcardValue);
        CiresonCriteria.SimpleExpression exp2 =
                new CiresonCriteria.SimpleExpression(CiresonConstants.Purchase_Order_Type, CiresonConstants.PurchaseOrder_property_PurchaseOrderDate, CiresonConstants.GreaterThanOrEqual_Operator, getDateBefore(-500));
        ArrayList<CiresonCriteria.Expression> expressions = new ArrayList<CiresonCriteria.Expression>();
        expressions.add(new CiresonCriteria.Expression(exp1));
        expressions.add(new CiresonCriteria.Expression(exp2));


        CiresonCriteria.Expression exp = CiresonCriteria.Expression.AndExpressions(expressions);
        Criteria = new CiresonCriteria(exp);
        Id = CiresonConstants.PurchaseOrder_MobileProjectionType;
    }

    private String getDateBefore(int days){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, days);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss");
        return sf.format(c.getTime());
    }

}


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

import com.cireson.assetmanager.model.Assets;
import com.cireson.assetmanager.util.CiresonConstants;

import java.util.ArrayList;

/**
 * Created by welcome on 5/2/2014.
 */
public class SearchAssetsWithBarCode extends BaseCriteriaRequest {

    /*Instances..*/
    private transient CiresonCriteria.Expression expr;

    public SearchAssetsWithBarCode(ArrayList<Assets> scanedAssets){
        ArrayList<CiresonCriteria.Expression> assetsBarcodeExpression= new ArrayList<CiresonCriteria.Expression>();
        CiresonCriteria.SimpleExpression simpleExpression1;
        CiresonCriteria.SimpleExpression simpleExpression2;
        CiresonCriteria.Expression expression, orExpressionOfAssetBarCodes;

        for(Assets a:scanedAssets){
            simpleExpression1 = new CiresonCriteria.SimpleExpression(
                                                        CiresonConstants.HardwareAsset_Type,
                                                        CiresonConstants.HardwareAsset_Property_AssetTag,
                                                        CiresonConstants.Equals_Operator,
                                                        a.assetTag,
                                                        null,
                                                        false);
            simpleExpression2 = new CiresonCriteria.SimpleExpression(
                    CiresonConstants.HardwareAsset_Type,
                    CiresonConstants.HardwareAsset_Property_SerialNumber,
                    CiresonConstants.Equals_Operator,
                    a.assetTag,
                    null,
                    false);
            /*Get the expression and add to the list..*/
            expression = new CiresonCriteria.Expression(simpleExpression1);
            assetsBarcodeExpression.add(expression);
            expression = new CiresonCriteria.Expression(simpleExpression2);
            assetsBarcodeExpression.add(expression);
        }
        /*Get the OredExpression of expressions in the array list..*/
        orExpressionOfAssetBarCodes = CiresonCriteria.Expression.OrExpressions(assetsBarcodeExpression);
        /*Get the final criteria now..*/
        Criteria = new CiresonCriteria(orExpressionOfAssetBarCodes);
        Id = CiresonConstants.HardwareAsset_MobileProjectionType;
    }
}

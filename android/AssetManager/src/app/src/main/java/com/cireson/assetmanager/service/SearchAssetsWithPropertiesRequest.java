
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

import com.cireson.assetmanager.model.CiresonEnumeration;
import com.cireson.assetmanager.model.CostCenter;
import com.cireson.assetmanager.model.CustodianUser;
import com.cireson.assetmanager.model.Location;
import com.cireson.assetmanager.model.Organization;
import com.cireson.assetmanager.util.CiresonConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SearchAssetsWithPropertiesRequest extends BaseCriteriaRequest {

    /*Instances..*/
    private transient CiresonCriteria.Expression
            compoundOrExpressionForStatus,
            compoundOrExpressionForLocation,
            compoundOrExpressionForOrganization,
            compoundOrExpressionForCostCenter,
            compoundOrExpressionForCustodianUser,
            expressionForDate,
            compoundAndExpression;


    public static String testSearchJson(){
        SearchAssetsWithPropertiesRequest req = new SearchAssetsWithPropertiesRequest(
            AssetAttributeBase.getDummyInstances(CiresonEnumeration.class),
                AssetAttributeBase.getDummyInstances(Location.class),
                AssetAttributeBase.getDummyInstances(Organization.class),
                AssetAttributeBase.getDummyInstances(CostCenter.class),
                AssetAttributeBase.getDummyInstances(CustodianUser.class),
                "2014/05/24"
        );
        return req.toJson();
    }

    public SearchAssetsWithPropertiesRequest(ArrayList<CiresonEnumeration> status,ArrayList<Location> locations,ArrayList<Organization> organizations,ArrayList<CostCenter> costCenters,ArrayList<CustodianUser> custodianUsers,String date){

        compoundOrExpressionForStatus = getOredExpression(status);
        compoundOrExpressionForLocation = getOredExpression(locations);
        compoundOrExpressionForOrganization = getOredExpression(organizations);
        compoundOrExpressionForCostCenter = getOredExpression(costCenters);
        compoundOrExpressionForCustodianUser = getOredExpression(custodianUsers);

        // received date.
        if (date != null && !"".equals(date)) {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_MONTH,-1);
            yesterday.set(Calendar.HOUR, 23);
            yesterday.set(Calendar.MINUTE, 59);
            yesterday.set(Calendar.SECOND, 59);
            yesterday.set(Calendar.MILLISECOND, 0);

            String yesterdayStr = format.format(yesterday.getTime());


            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_MONTH,1);
            tomorrow.set(Calendar.HOUR, 0);
            tomorrow.set(Calendar.MINUTE, 0);
            tomorrow.set(Calendar.SECOND, 0);
            tomorrow.set(Calendar.MILLISECOND, 0);

            String tomorrowStr = format.format(tomorrow.getTime());

            CiresonCriteria.SimpleExpression simpExp1 = new CiresonCriteria.SimpleExpression(
                    CiresonConstants.HardwareAsset_Type,
                    CiresonConstants.HardwareAsset_Property_ReceivedDate,
                    CiresonConstants.GreaterThanOrEqual_Operator,
                    yesterdayStr
            );

            CiresonCriteria.SimpleExpression simpExp2 = new CiresonCriteria.SimpleExpression(
                    CiresonConstants.HardwareAsset_Type,
                    CiresonConstants.HardwareAsset_Property_ReceivedDate,
                    CiresonConstants.LessThanOrEqual_Operator,
                    tomorrowStr
            );

            ArrayList<CiresonCriteria.Expression> expressions = new ArrayList<CiresonCriteria.Expression>();
            expressions.add(new CiresonCriteria.Expression(simpExp1));
            expressions.add(new CiresonCriteria.Expression(simpExp2));
            expressionForDate = CiresonCriteria.Expression.AndExpressions(expressions);

        }


        /*Get compunded AND Expression..*/
        ArrayList<CiresonCriteria.Expression> allOrCompundedExpression = new ArrayList<CiresonCriteria.Expression>();
        if(compoundOrExpressionForStatus!=null) {
            allOrCompundedExpression.add(compoundOrExpressionForStatus);
        }
        if(compoundOrExpressionForLocation!=null) {
            allOrCompundedExpression.add(compoundOrExpressionForLocation);
        }
        if(compoundOrExpressionForOrganization!=null) {
            allOrCompundedExpression.add(compoundOrExpressionForOrganization);
        }
        if(compoundOrExpressionForCostCenter!=null) {
            allOrCompundedExpression.add(compoundOrExpressionForCostCenter);
        }
        if(compoundOrExpressionForCustodianUser!=null) {
            allOrCompundedExpression.add(compoundOrExpressionForCustodianUser);
        }
        if(expressionForDate!=null) {
            allOrCompundedExpression.add(expressionForDate);
        }

        compoundAndExpression = CiresonCriteria.Expression.AndExpressions(allOrCompundedExpression);
        this.Id = CiresonConstants.HardwareAsset_MobileProjectionType;
        /*Get the criteria..*/
        Criteria = new CiresonCriteria(compoundAndExpression);
    }

    /*Get Compound Simple Expression for each category..*/
    private static <T extends AssetAttributeBase> CiresonCriteria.Expression getOredExpression(ArrayList<T> properties) {
        ArrayList<CiresonCriteria.Expression> expressions = null;
        CiresonCriteria.Expression retVal = null;
        if(properties!=null && properties.size()>0) {
            expressions = new ArrayList<CiresonCriteria.Expression>();
            /*Create compound simple expression for each category..*/
            for (int i = 0; i < properties.size(); i++) {
                expressions.add(properties.get(i).getExpression());
            }
            retVal = CiresonCriteria.Expression.OrExpressions(expressions);
        }
        return retVal;
    }

}

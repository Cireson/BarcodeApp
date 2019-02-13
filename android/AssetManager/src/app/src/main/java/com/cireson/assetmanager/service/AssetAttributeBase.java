
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
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by devkhadka on 4/29/14.
 */
public class AssetAttributeBase extends BaseJsonObject {
    @SerializedName("BaseId")
    public String baseId = null;

    @BaseJsonObject.GsonExclude
    private CiresonCriteria.SimpleExpression simpleExpressionTemplate;

    protected void setSimpleExpressionTemplate(CiresonCriteria.SimpleExpression simpExp){
        simpleExpressionTemplate = simpExp;
    }

    public String getValueProperty(){
        return baseId;
    }

    public CiresonCriteria.Expression getExpression(){
        simpleExpressionTemplate.value = getValueProperty();
        return new CiresonCriteria.Expression(simpleExpressionTemplate);
    }

    public static <T extends AssetAttributeBase> ArrayList<T> getDummyInstances(Class<T> type){
        ArrayList<T> list = new ArrayList<T>();
        int randCount = 1 +(int) (Math.random() * 2);
        for(int i=0;i<randCount;i++){
            try {
                T item = type.newInstance();
                item.baseId = type.getSimpleName() + i;
                if(type==CiresonEnumeration.class){
                    ((CiresonEnumeration)item).id = type.getSimpleName() + i;
                }
                list.add(item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}


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
import com.cireson.assetmanager.service.BaseJsonObject;
import com.cireson.assetmanager.util.BooleanSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by welcome on 4/9/14.
 */
public class PurchaseOrder extends BaseJsonObject {

    /*Serializable Instances..*/
    @SerializedName("Amount")
    public float amount = 0;
    @SerializedName("DisplayName")
    public String displayName = null;
    @SerializedName("PurchaseOrderDate")
    public String purchaseOrderDate = null;
    @SerializedName("ClassTypeId")
    public String classTypeId = null;
    @SerializedName("ObjectStatus")
    public CiresonObjectStatus objectStatus = null;
    @SerializedName("Source_HardwareAssetHasPurchaseOrder")
    public ArrayList<Assets> associatedAssets = null;
    @SerializedName("Target_PurchaseOrderHasCostCenter")
    public Object targetPurchaseOrderHasCostCenter = null;
    @SerializedName("PurchaseOrderStatus")
    public PurchaseOrderStatus purchaseOrderStatus = null;

    public static class PurchaseOrderStatus{
        @Expose
        @SerializedName("Id")
        public String id = null;
        @Expose
        @SerializedName("Name")
        public String name = null;
    }

    public PurchaseOrder(){/*Default..*/}

    /*Required Inner Classes..*/
    /*This gson helps in serializing and deserializing PurchaseOrder model..*/
    private static transient Gson gson;
    private JsonElement originalJson;
    /*Custom serializer and deserializer for PurchaseOrder model.*/
     /*Custom JSON Serializer and deserializer of Assets model..*/
    static{
        gson = new GsonBuilder()
                .setExclusionStrategies(new MyExclusionStrategy())
                .registerTypeAdapter(Boolean.class, new BooleanSerializer())
                .registerTypeAdapter(CiresonCriteria.SimpleExpression.class, new CiresonCriteria.SimpleExpressionSerializer())
                .create();
    }

    public static class OriginalJsonSerializer implements JsonSerializer<PurchaseOrder> {
        @Override
        public JsonElement serialize(PurchaseOrder o, Type type, JsonSerializationContext jsonSerializationContext) {
            if(o.originalJson!=null){
                JsonObject object = o.originalJson.getAsJsonObject();
                if(object!=null){
                    if(object.has("Source_HardwareAssetHasPurchaseOrder")){
                        JsonArray newJsonArray = new JsonArray();
                        for(Assets a: GlobalData.getInstance().getSavedReceiveAssets().projectionObject.associatedAssets){
                            JsonObject newAssetObject = a.originalJson.getAsJsonObject();
                            newJsonArray.add(newAssetObject);
                            object.add("Source_HardwareAssetHasPurchaseOrder",newJsonArray);
                        }
                    }
                }
            }
            return o.originalJson;
        }
    }

    public static class OriginalJsonDeserializer implements JsonDeserializer<PurchaseOrder> {
        @Override
        public PurchaseOrder deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            PurchaseOrder purchaseOrder = gson.fromJson(jsonElement,type);
            purchaseOrder.originalJson = jsonElement;
            if(purchaseOrder.originalJson!=null){
                JsonObject jsonObject = purchaseOrder.originalJson.getAsJsonObject();
                final Type classType = new TypeToken<ArrayList<Assets>>(){}.getType();
                ArrayList<Assets> associatedAssets = jsonDeserializationContext.deserialize(jsonObject.get("Source_HardwareAssetHasPurchaseOrder"), classType);
                purchaseOrder.associatedAssets = associatedAssets;
            }
            return purchaseOrder;
        }
    }

    /*Getters and setters..*/

}

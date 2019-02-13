
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
import com.cireson.assetmanager.model.CiresonEnumeration;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.model.PurchaseOrder;
import com.cireson.assetmanager.util.BooleanSerializer;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class BaseJsonObject {

    @SerializedName("Domain")
    public String domain = null;
    @SerializedName("Action")
    public String action = null;

    private transient String apicall_status = "failed";
    private static transient  Gson gson, baseIdSerializerGson, originalJsonSerializer;

    static {
        gson = new GsonBuilder()
                .setExclusionStrategies(new MyExclusionStrategy())
                .registerTypeAdapter(Boolean.class, new BooleanSerializer())
                .registerTypeAdapter(CiresonCriteria.SimpleExpression.class, new CiresonCriteria.SimpleExpressionSerializer())
                .registerTypeHierarchyAdapter(Assets.class, new Assets.OriginalJsonSerializer())
                .registerTypeHierarchyAdapter(Assets.class, new Assets.OriginalJsonDeserializer())
                .registerTypeHierarchyAdapter(PurchaseOrder.class,new PurchaseOrder.OriginalJsonSerializer())
                .registerTypeHierarchyAdapter(PurchaseOrder.class,new PurchaseOrder.OriginalJsonDeserializer())
                .create();
        /*To add new assets, this serializer is used.
        First of all this json serializer excludes field without @Expose annotation and then,
        it serializes null values too. This ensures that, while saving newly added assets, required
        null values are also sent.*/
        baseIdSerializerGson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeHierarchyAdapter(Assets.class, new Assets.OriginalJsonSerializer())
                .registerTypeHierarchyAdapter(Assets.class, new Assets.OriginalJsonDeserializer())
                .serializeNulls()
                .create();
        /**/
    }

    public BaseJsonObject(){
    }

    /* Deserialize or Convert a json string to a corresponding class type..*/
    public static <T> T fromJson(String json, Class<T> cls) {
        return gson.fromJson(json, cls);
    }

    /*Deserialization or Convert a json string to a corresponding class type..*/
    public static <T>ArrayList<T> arrayFromJson(String json, Type type){
        ArrayList<CiresonEnumeration> retVal = null;
        retVal = gson.fromJson(json, type);
        return (ArrayList<T>)retVal;
    }

    /*Serialize or Convert respective class to json string as per the gson build configuration..*/
    public String toJson() {
        /*If serialization is to be done when saving newly added assets..*/
        if(GlobalData.SAVE_NEW_ADDED_ASSETS){
            return baseIdSerializerGson.toJson(this);
        }
        return gson.toJson(this);
    }

    public File toJsonFile(File file){
        try {
            FileWriter writer = new FileWriter(file, false);
            gson.toJson(this, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public static @interface GsonExpose {
        // Field tag only annotation
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public static @interface GsonExclude {
        // Field tag only annotation
    }

    public static class MyExclusionStrategy implements ExclusionStrategy {

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return (f.getAnnotation(GsonExclude.class) != null);
        }
    }
}

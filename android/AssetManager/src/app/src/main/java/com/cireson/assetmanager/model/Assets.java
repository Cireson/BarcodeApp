
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

import com.cireson.assetmanager.service.BaseJsonObject;
import com.cireson.assetmanager.service.CiresonCriteria;
import com.cireson.assetmanager.util.BooleanSerializer;
import com.cireson.assetmanager.util.CiresonConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Assets extends BaseJsonObject {

    /*Here @Expose annotation is used only by the baseIdSerializer (a gson object created in BaseJsonObject class) to serialize the fields while saving new added assets..*/

    @Expose
    @SerializedName("AssetTag")
    public String assetTag = null;
    @Expose
    @SerializedName("SerialNumber")
    public String serialNumber = null;
    @Expose
    @SerializedName("Manufacturer")
    public String manufacturer = null;
    @Expose
    @SerializedName("Model")
    public String model = null;
    @Expose
    @SerializedName("Name")
    public String name = null;
    @Expose
    @SerializedName("DisplayName")
    public String displayName = null;

    @Expose
    @SerializedName("HardwareAssetStatus")
    public HardwareAssetsStatus hardwareAssetStatus = new HardwareAssetsStatus();
    @Expose
    @SerializedName("Target_HardwareAssetHasLocation")
    public LocOrgCCPrimaryUserClassTypeId targetHardwareAssetHasLocation = new LocOrgCCPrimaryUserClassTypeId();
    @Expose
    @SerializedName("Target_HardwareAssetHasOrganization")
    public LocOrgCCPrimaryUserClassTypeId targetHardwareAssetHasOrganization = new LocOrgCCPrimaryUserClassTypeId();
    @Expose
    @SerializedName("Target_HardwareAssetHasCostCenter")
    public LocOrgCCPrimaryUserClassTypeId targetHardwareAssetHasCostCenter = new LocOrgCCPrimaryUserClassTypeId();
    @Expose
    @SerializedName("Target_HardwareAssetHasPrimaryUser")
    public LocOrgCCPrimaryUserClassTypeId targetHardwareAssetHasPrimaryUser = new LocOrgCCPrimaryUserClassTypeId();
    @Expose
    @SerializedName("OwnedBy")
    public OwnedBy owndedBy = new OwnedBy();
    @Expose
    @SerializedName("ReceivedDate")
    public String receivedDate = null;
    @SerializedName("DisposalDate")
    public String disposalDate = null;
    @SerializedName("LoanedDate")
    public String loanedDate = null;
    @SerializedName("LoanReturnDate")
    public String loanReturnedDate = null;
    @SerializedName("DisposalReference")
    public String disposalReference = null;
    @SerializedName("Notes")
    public String notes = null;
    @Expose
    @SerializedName("ClassTypeId")
    public String classTypeId = null;
    @Expose
    @SerializedName("BaseId")
    public String baseId = null;
    @SerializedName("FullName")
    public String fullName = null;
    @SerializedName("HardwareAssetType")
    public CiresonObjectStatus hardwareAssetType = null;
    @Expose
    @SerializedName("HardwareAssetID")
    public String hardwareAssetID = null;
    @SerializedName("NameRelationship")
    public ArrayList<NameRelationship> nameRelationShip = null;

    public transient boolean isMatched;
    public transient boolean isNew;
    /*These fields are used to check if this asset is verified from service and is already available.*/
    public transient boolean isForwarded= false;
    /*If an asset is not processed, it indicates there is some error in processing in it.*/
    public transient boolean isProcessed = false;
    public transient boolean isAvailable = false;

    /*These instances are to be used as a copy of asset tag or serial numbers of assets..*/
    public transient String temporaryAssetTag = null;
    public transient String temporarySerialNumber = null;

    public Assets(){/*Default..*/}

    /*Inner model class for Status , Organization, Location and Cost center..*/
    public class LocOrgCCPrimaryUserClassTypeId {
        @Expose
        @SerializedName("ClassTypeId")
        public String classTypeId = null;
        @Expose
        @SerializedName("DisplayName")
        public String displayName = null;
        @Expose
        @SerializedName("UserName")
        public String userName = null;
        @Expose
        @SerializedName("BaseId")
        public String baseId = null;
    }

    public class OwnedBy{
        @Expose
        @SerializedName("ClassTypeId")
        public String classTypeId = null;
        @Expose
        @SerializedName("UserName")
        public String userName = null;
        @Expose
        @SerializedName("BaseId")
        public String baseId = null;
    }

    /*Inner model for HardwareAssetStatus*/
    public class HardwareAssetsStatus {
        @Expose
        @SerializedName("Id")
        public String id = null;
        @Expose
        @SerializedName("Name")
        public String name = null;
    }

    /*Inner model for NameRelationship*/
    public class NameRelationship{
        @Expose
        @SerializedName("Name")
        public String name = null;
        @Expose
        @SerializedName("RelationshipId")
        public String relationshipId = null;
    }

    /*Here Assets model is serialized and deserialized by gson field which is a transient
    (excluded naturally during serialization and deserialization).*/
    private static transient Gson gson;
    public JsonElement originalJson;
    /*Custom JSON Serializer and deserializer of Assets model..*/
    static{
        gson = new GsonBuilder()
                .setExclusionStrategies(new MyExclusionStrategy())
                .registerTypeAdapter(Boolean.class, new BooleanSerializer())
                .registerTypeAdapter(CiresonCriteria.SimpleExpression.class, new CiresonCriteria.SimpleExpressionSerializer())
                .create();
    }

    public static class OriginalJsonSerializer implements JsonSerializer<Assets> {
        @Override
        public JsonElement serialize(Assets o, Type type, JsonSerializationContext jsonSerializationContext) {
            return o.originalJson;
        }
    }

    public static class OriginalJsonDeserializer implements JsonDeserializer<Assets> {
        @Override
        public Assets deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Assets asset = gson.fromJson(jsonElement,type);
            asset.originalJson = jsonElement;
            return asset;
        }
    }

    /*Getter and setters*/
    public void setTemporaryAssetTag(String assetTag){
        this.temporaryAssetTag = assetTag;
    }

    public String getTemporaryAssetTag() {
        return temporaryAssetTag;
    }

    public void setTemporarySerialNumber(String serialNumber){
        this.temporarySerialNumber = serialNumber;
    }

    public String getTemporarySerialNumber() {
        return temporarySerialNumber;
    }

    public void setAssetTag(String assetTag) {
        this.assetTag = assetTag;
        if(this.originalJson!=null){
            JsonObject jsonObject = this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.addProperty("AssetTag",assetTag);
            }
        }
    }

    public String getAssetTag() {
        return assetTag;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        if(this.originalJson!=null){
            JsonObject jsonObject = this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.addProperty("SerialNumber", serialNumber);
            }
        }
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setManufacturer(String manufacturer){
        this.manufacturer = manufacturer;
        if(this.originalJson!=null){
            JsonObject jsonObject = this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.addProperty("Manufacturer", manufacturer);
            }
        }
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setModel(String model){
        this.model = model;
        if(this.originalJson!=null){
            JsonObject jsonObject = this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.addProperty("Model", model);
            }
        }
    }

    public String getModel() {
        return model;
    }

    public void setName(String name){
        this.name = name;
        if(this.originalJson!=null){
            JsonObject jsonObject = this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.addProperty("Name", name);
            }
        }
    }

    public void setDisplayName(String displayName){
        this.displayName = displayName;
        if(this.originalJson!=null){
            JsonObject jsonObject = this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.addProperty("DisplayName", displayName);
            }
        }
    }

    public void setClassTypeId(String classTypeId){
        this.classTypeId = classTypeId;
        if(this.originalJson!=null){
            JsonObject jsonObject = this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.addProperty("ClassTypeId", classTypeId);
            }
        }
    }

    public void setHardwareAssetId(String hardwareAssetId){
        this.hardwareAssetID = hardwareAssetId;
        if(this.originalJson!=null){
            JsonObject jsonObject = this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.addProperty("HardwareAssetID", hardwareAssetId);
            }
        }
    }

    public void setBaseId(String baseId){
        this.baseId = baseId;
        if(this.originalJson!=null){
            JsonObject jsonObject = this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.addProperty("BaseId", baseId);
            }
        }
    }

    public String getBaseId() {
        return baseId;
    }

    public void setHardwareStatusId(String statusId){
        this.hardwareAssetStatus.id = statusId;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("HardwareAssetStatus")){
                    jsonObject.get("HardwareAssetStatus").getAsJsonObject().addProperty("Id", statusId);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("Id",statusId);
                    jsonObject.add("HardwareAssetStatus",newObject);
                }
            }
        }
    }

    public void setHardwareStatusName(String name){
        this.hardwareAssetStatus.name = name;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("HardwareAssetStatus")) {
                    jsonObject.get("HardwareAssetStatus").getAsJsonObject().addProperty("Name", name);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("Name",name);
                    jsonObject.add("HardwareAssetStatus",newObject);
                }
            }
        }
    }

    public HardwareAssetsStatus getHardwareAssetStatus() {
        return hardwareAssetStatus;
    }


    public void setLocationClassTypeId(String classTypeId){
        this.targetHardwareAssetHasLocation.classTypeId = classTypeId;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("Target_HardwareAssetHasLocation")) {
                    jsonObject.get("Target_HardwareAssetHasLocation").getAsJsonObject().addProperty("ClassTypeId", classTypeId);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("ClassTypeId",classTypeId);
                    jsonObject.add("Target_HardwareAssetHasLocation", newObject);
                }
            }
        }
    }

    public void setLocationDisplayName(String displayName){
        this.targetHardwareAssetHasLocation.displayName = displayName;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("Target_HardwareAssetHasLocation")) {
                    jsonObject.get("Target_HardwareAssetHasLocation").getAsJsonObject().addProperty("DisplayName", displayName);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("DisplayName",displayName);
                    jsonObject.add("Target_HardwareAssetHasLocation", newObject);
                }
            }
        }
    }

    public void setLocationBaseId(String baseId){
        this.targetHardwareAssetHasLocation.baseId = baseId;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("Target_HardwareAssetHasLocation")) {
                    jsonObject.get("Target_HardwareAssetHasLocation").getAsJsonObject().addProperty("BaseId", baseId);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("BaseId",baseId);
                    jsonObject.add("Target_HardwareAssetHasLocation", newObject);
                }
            }
        }
    }

    public LocOrgCCPrimaryUserClassTypeId getTargetHardwareAssetHasLocation() {
        return targetHardwareAssetHasLocation;
    }

    public void setOrganizationClassTypeId(String classTypeId){
        this.targetHardwareAssetHasOrganization.classTypeId = classTypeId;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("Target_HardwareAssetHasOrganization")){
                    jsonObject.get("Target_HardwareAssetHasOrganization").getAsJsonObject().addProperty("ClassTypeId",classTypeId);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("ClassTypeId", classTypeId);
                    jsonObject.add("Target_HardwareAssetHasOrganization",newObject);
                }
            }
        }
    }

    public void setOrganizationDisplayName(String displayName){
        this.targetHardwareAssetHasOrganization.displayName = displayName;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("Target_HardwareAssetHasOrganization")){
                    jsonObject.get("Target_HardwareAssetHasOrganization").getAsJsonObject().addProperty("DisplayName",displayName);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("DisplayName", displayName);
                    jsonObject.add("Target_HardwareAssetHasOrganization",newObject);
                }
            }
        }
    }

    public void setOrganizationBaseId(String baseId){
        this.targetHardwareAssetHasOrganization.baseId = baseId;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("Target_HardwareAssetHasOrganization")){
                    jsonObject.get("Target_HardwareAssetHasOrganization").getAsJsonObject().addProperty("BaseId",baseId);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("BaseId", baseId);
                    jsonObject.add("Target_HardwareAssetHasOrganization",newObject);
                }
            }
        }
    }

    public LocOrgCCPrimaryUserClassTypeId getTargetHardwareAssetHasOrganization() {
        return targetHardwareAssetHasOrganization;
    }

    public void setCostCenterClassTypeId(String classTypeId){
        this.targetHardwareAssetHasCostCenter.classTypeId = classTypeId;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("Target_HardwareAssetHasCostCenter")){
                    jsonObject.get("Target_HardwareAssetHasCostCenter").getAsJsonObject().addProperty("ClassTypeId",classTypeId);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("ClassTypeId",classTypeId);
                    jsonObject.add("Target_HardwareAssetHasCostCenter", newObject);
                }
            }
        }
    }

    public void setCostCenterDisplayName(String displayName){
        this.targetHardwareAssetHasCostCenter.displayName = displayName;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("Target_HardwareAssetHasCostCenter")){
                    jsonObject.get("Target_HardwareAssetHasCostCenter").getAsJsonObject().addProperty("DisplayName",displayName);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("DisplayName",displayName);
                    jsonObject.add("Target_HardwareAssetHasCostCenter", newObject);
                }
            }
        }
    }

    public void setCostCenterBaseId(String baseId){
        this.targetHardwareAssetHasCostCenter.baseId = baseId;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("Target_HardwareAssetHasCostCenter")){
                    jsonObject.get("Target_HardwareAssetHasCostCenter").getAsJsonObject().addProperty("BaseId",baseId);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("BaseId",baseId);
                    jsonObject.add("Target_HardwareAssetHasCostCenter", newObject);
                }
            }
        }
    }

    public void setPrimaryUsersClassTypeId(String primaryUsersClassTypeId){
        this.targetHardwareAssetHasPrimaryUser.classTypeId = primaryUsersClassTypeId;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("Target_HardwareAssetHasPrimaryUser")){
                    jsonObject.get("Target_HardwareAssetHasPrimaryUser").getAsJsonObject().addProperty("ClassTypeId",primaryUsersClassTypeId);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("ClassTypeId",primaryUsersClassTypeId);
                    jsonObject.add("Target_HardwareAssetHasPrimaryUser",newObject);
                }
            }
        }
    }

    public void setPrimaryUserDisplayName(String displayName){
        this.targetHardwareAssetHasPrimaryUser.displayName = displayName;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("Target_HardwareAssetHasPrimaryUser")){
                    jsonObject.get("Target_HardwareAssetHasPrimaryUser").getAsJsonObject().addProperty("DisplayName",displayName);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("DisplayName",displayName);
                    jsonObject.add("Target_HardwareAssetHasPrimaryUser", newObject);
                }
            }
        }
    }

    public void setPrimaryUserBaseId(String baseId){
        this.targetHardwareAssetHasPrimaryUser.baseId = baseId;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("Target_HardwareAssetHasPrimaryUser")){
                    jsonObject.get("Target_HardwareAssetHasPrimaryUser").getAsJsonObject().addProperty("BaseId",baseId);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("BaseId",baseId);
                    jsonObject.add("Target_HardwareAssetHasPrimaryUser", newObject);
                }
            }
        }
    }

    /*For dispose assets this method is used to set field value to null..*/
    public void setTargetHardwareAssetHasPrimaryUser(LocOrgCCPrimaryUserClassTypeId targetHardwareAssetHasPrimaryUser) {
        this.targetHardwareAssetHasPrimaryUser = targetHardwareAssetHasPrimaryUser;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.add("Target_HardwareAssetHasPrimaryUser",null);
            }
        }
    }

    public void setOwndedBy(OwnedBy owndedBy){
        this.owndedBy = owndedBy;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.add("OwnedBy",null);
            }
        }
    }

    public void setCustodianUsersClassTypeId(String classTypeId){
        this.owndedBy.classTypeId = classTypeId;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("OwnedBy")){
                    jsonObject.get("OwnedBy").getAsJsonObject().addProperty("ClassTypeId",classTypeId);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("ClassTypeId",classTypeId);
                    jsonObject.add("OwnedBy",newObject);
                }
            }
        }
    }

    public void setCustodianUserName(String userName) {
        this.owndedBy.userName = userName;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("OwnedBy")){
                    jsonObject.get("OwnedBy").getAsJsonObject().addProperty("UserName",userName);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("UserName",userName);
                    jsonObject.add("OwnedBy",newObject);
                }
            }
        }
    }

    public void setCustodianBaseId(String baseId){
        this.owndedBy.baseId = baseId;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                if(jsonObject.has("OwnedBy")){
                    jsonObject.get("OwnedBy").getAsJsonObject().addProperty("BaseId",baseId);
                }else{
                    JsonObject newObject = new JsonObject();
                    newObject.addProperty("BaseId",baseId);
                    jsonObject.add("OwnedBy",newObject);
                }
            }
        }
    }

    public LocOrgCCPrimaryUserClassTypeId getTargetHardwareAssetHasPrimaryUser() {
        return targetHardwareAssetHasPrimaryUser;
    }

    public LocOrgCCPrimaryUserClassTypeId getTargetHardwareAssetHasCostCenter() {
        return targetHardwareAssetHasCostCenter;
    }

    public void setDisposalReference(String disposalReference){
        this.disposalReference = disposalReference;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.addProperty("DisposalReference", disposalReference);
            }
        }
    }

    public void setReceivedDate(String receivedDate){
        this.receivedDate = receivedDate;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.addProperty("ReceivedDate", receivedDate);
            }
        }
    }

    public void setDisposalDate(String disposalDate){
        this.disposalDate = disposalDate;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.addProperty("DisposalDate", disposalDate);
            }
        }
    }

    public void setLoanReturnedDate(String loanReturnedDate) {
        this.loanReturnedDate = loanReturnedDate;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.addProperty("LoanReturnedDate", loanReturnedDate);
            }
        }
    }

    public void setLoanedDate(String loanedDate){
        this.loanedDate = loanedDate;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.addProperty("LoanedDate", loanedDate);
            }
        }
    }

    public void setNotes(String notes) {
        this.notes = notes;
        if(this.originalJson!=null){
            JsonObject jsonObject= this.originalJson.getAsJsonObject();
            if(jsonObject!=null){
                jsonObject.addProperty("Notes", notes);
            }
        }
    }

    public void setNameRelationship(){

        if(this.originalJson!=null){
            JsonObject jsonObject = this.originalJson.getAsJsonObject();

            JsonObject jo1 = new JsonObject();
            JsonObject jo2 = new JsonObject();
            JsonObject jo3 = new JsonObject();
            JsonObject jo4 = new JsonObject();
            JsonObject jo5 = new JsonObject();
            JsonObject jo6 = new JsonObject();
            JsonObject jo7 = new JsonObject();
            JsonObject jo8 = new JsonObject();

            if(jo1!=null){
                jo1.addProperty("Name",CiresonConstants.Target_HardwareAssetHasVendor);
                jo1.addProperty("RelationshipId",CiresonConstants.RelationshipId_Vendor);

                jo2.addProperty("Name",CiresonConstants.Target_HardwareAssetHasLocation);
                jo2.addProperty("RelationshipId",CiresonConstants.RelationshipId_Location);

                jo3.addProperty("Name",CiresonConstants.Target_HardwareAssetHasPurchaseOrder);
                jo3.addProperty("RelationshipId",CiresonConstants.RelationshipId_PurchaseOrder);

                jo4.addProperty("Name",CiresonConstants.Target_HardwareAssetHasOrganization);
                jo4.addProperty("RelationshipId",CiresonConstants.RelationshipId_Organization);

                jo5.addProperty("Name",CiresonConstants.Target_HardwareAssetHasCostCenter);
                jo5.addProperty("RelationshipId", CiresonConstants.RelationshipId_CostCenter);

                jo6.addProperty("Name",CiresonConstants.OwnedBy);
                jo6.addProperty("RelationshipId",CiresonConstants.RelationshipId_OwnedBy);

                jo7.addProperty("Name",CiresonConstants.Target_HardwareAssetHasPrimaryUser);
                jo7.addProperty("RelationshipId",CiresonConstants.RelationshipId_PrimaryUser);

                jo8.addProperty("Name",CiresonConstants.Target_HardwareAssetHasCatalogItem);
                jo8.addProperty("RelationshipId",CiresonConstants.RelationshipId_CatalogItem);

                JsonArray jsonArray = new JsonArray();
                if(jsonArray!=null){
                    jsonArray.add(jo1);
                    jsonArray.add(jo2);
                    jsonArray.add(jo3);
                    jsonArray.add(jo4);
                    jsonArray.add(jo5);
                    jsonArray.add(jo6);
                    jsonArray.add(jo7);
                    jsonArray.add(jo8);
                }
                jsonObject.add("NameRelationship", jsonArray);
                /**/
                jsonObject.add("BaseId",JsonNull.INSTANCE);
            }
        }
    }

    public String getAssetTypeDisplayName() {
        if(hardwareAssetType!=null){
            return String.format("%s (%s, %s)", hardwareAssetType.name, model, manufacturer);
        }
        return "";
    }

    /*Send received assets back to storage..*/
    public static void setNamesForAddedAssets(ArrayList<Assets> assets){
        /*Proceed to send received assets back to service for storage. Before sending format display name and name attribute of each asset..*/
        String guidCode = "";
        for(Assets a:assets){
            if(a.assetTag==null) {
                guidCode = a.serialNumber;
                a.setSerialNumber(a.serialNumber);
            }else {
                guidCode = a.assetTag;
                a.setAssetTag(a.assetTag);
            }
            /*If both manufacturer and model number are available..*/
            if(a.manufacturer!=null&&a.model!=null){
                a.displayName = new StringBuilder(a.manufacturer+" "+a.model+" - "+guidCode).toString();
                a.name = new StringBuilder(a.manufacturer+" "+a.model+" - "+guidCode).toString();
            }
            /*If manufacturer is not available but model number is available..*/
            else if(a.manufacturer==null&&a.model!=null){
                a.displayName = new StringBuilder(a.model+" - "+guidCode).toString();
                a.name = new StringBuilder(a.model+" - "+guidCode).toString();
            }
            /*If manufacturer is available but model number is not available..*/
            else if(a.manufacturer!=null&&a.model==null){
                a.displayName = new StringBuilder(a.manufacturer+" - "+guidCode).toString();
                a.name = new StringBuilder(a.manufacturer+" - "+guidCode).toString();
            }
            /*If both manufacturer and  model number are not available..*/
            else{
                a.displayName = guidCode;
                a.name = guidCode;
            }

            /*Set name and display name to the original json..*/
            a.setName(a.name);
            a.setDisplayName(a.displayName);
        }
    }

}

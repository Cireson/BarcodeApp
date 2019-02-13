
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


package com.cireson.assetmanager.assistance;

import android.util.Log;

import com.cireson.assetmanager.model.Assets;
import com.cireson.assetmanager.model.GlobalData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by welcome on 8/12/2014.
 */
public class Swaper {
    /*Instances..*/
    private Assets firstAsset, secondAsset, copyOfFirstAsset;
    private ArrayList<String> propertiesOfFirstAssets, propertiesOfSecondAsset;
    private HashMap<String,String> properitesMapForFirstAsset, properitesMapForSecondAsset;
    private StringBuilder noteToAppendForFirstAsset = null;
    private StringBuilder noteToAppendForSecondAsset = null;
    private GlobalData globalData;

    private static boolean showMessageDialog = false;
    private static boolean usePrimaryUser = false;
    private static boolean swap = false;
    private static String message = "";

    public static boolean SKIP_ON_BACK_PRESS = false;

    public Swaper(){
        globalData = GlobalData.getInstance();
    }

    public Assets getFirstAsset() {
        return firstAsset;
    }

    public void setFirstAsset(Assets firstAsset) {
        this.firstAsset = firstAsset;
    }

    public Assets getSecondAsset() {
        return secondAsset;
    }

    public void setSecondAsset(Assets secondAsset) {
        this.secondAsset = secondAsset;
        /*As soon as second asset is found, assign the original status detail of first asset to the status of second assets..*/
        this.secondAsset.setHardwareStatusId(this.properitesMapForFirstAsset.get("statusId"));
        this.secondAsset.setHardwareStatusName(this.properitesMapForFirstAsset.get("statusName"));
    }

    public Assets getCopyOfFirstAsset() {
        return copyOfFirstAsset;
    }

    public void setCopyOfFirstAsset(Assets copyOfFirstAsset) {
        this.copyOfFirstAsset = copyOfFirstAsset;
    }

    public ArrayList<String> getPropertiesOfFirstAssets() {
        return propertiesOfFirstAssets;
    }

    public ArrayList<String> getPropertiesOfSecondAsset() {
        return propertiesOfSecondAsset;
    }

    public void setPropertiesOfSecondAsset(ArrayList<String> propertiesOfSecondAsset) {
        this.propertiesOfSecondAsset = propertiesOfSecondAsset;
    }

    public void setProperitesMapForFirstAsset(Assets asset) {
        String firstAssetsJsonString = asset.toJson();
        copyOfFirstAsset = Assets.fromJson(firstAssetsJsonString, Assets.class);

        properitesMapForFirstAsset = new HashMap<String, String>();
        properitesMapForFirstAsset.put("statusId",copyOfFirstAsset.hardwareAssetStatus.id);
        properitesMapForFirstAsset.put("statusName",copyOfFirstAsset.hardwareAssetStatus.name);
        properitesMapForFirstAsset.put("locationBaseId",copyOfFirstAsset.targetHardwareAssetHasLocation.baseId);
        properitesMapForFirstAsset.put("locationClassTypeId",copyOfFirstAsset.targetHardwareAssetHasLocation.classTypeId);
        properitesMapForFirstAsset.put("locationDisplayName",copyOfFirstAsset.targetHardwareAssetHasLocation.displayName);
        properitesMapForFirstAsset.put("organizationBaseId",copyOfFirstAsset.targetHardwareAssetHasOrganization.baseId);
        properitesMapForFirstAsset.put("organizationClassTypeId",copyOfFirstAsset.targetHardwareAssetHasOrganization.classTypeId);
        properitesMapForFirstAsset.put("organizationDisplayName",copyOfFirstAsset.targetHardwareAssetHasOrganization.displayName);
        properitesMapForFirstAsset.put("costCenterBaseId",copyOfFirstAsset.targetHardwareAssetHasCostCenter.baseId);
        properitesMapForFirstAsset.put("costCenterClassTypeId",copyOfFirstAsset.targetHardwareAssetHasCostCenter.classTypeId);
        properitesMapForFirstAsset.put("costCenterDisplayName",copyOfFirstAsset.targetHardwareAssetHasCostCenter.displayName);
        properitesMapForFirstAsset.put("loanReturnedDate",copyOfFirstAsset.loanReturnedDate);
        properitesMapForFirstAsset.put("loanedDate",copyOfFirstAsset.loanedDate);
        properitesMapForFirstAsset.put("notes",copyOfFirstAsset.notes);
        properitesMapForFirstAsset.put("primaryUserBaseId",copyOfFirstAsset.targetHardwareAssetHasPrimaryUser.baseId);
        properitesMapForFirstAsset.put("primaryUserClassTypeId",copyOfFirstAsset.targetHardwareAssetHasPrimaryUser.classTypeId);
        properitesMapForFirstAsset.put("primaryUserDisplayName",copyOfFirstAsset.targetHardwareAssetHasPrimaryUser.displayName);
        properitesMapForFirstAsset.put("custodianUserBaseId", copyOfFirstAsset.owndedBy.baseId);
        properitesMapForFirstAsset.put("custodianUserClassTypeId", copyOfFirstAsset.owndedBy.classTypeId);
        properitesMapForFirstAsset.put("custodianUserDisplayName",copyOfFirstAsset.owndedBy.userName);

        Log.d("Check hasmap statusName",properitesMapForFirstAsset.get("statusName"));
    }

    public HashMap<String, String> getProperitesMapForFirstAsset() {
        return properitesMapForFirstAsset;
    }

    public void setProperitesMapForSecondAsset(Assets asset) {
        properitesMapForSecondAsset = new HashMap<String, String>();
        properitesMapForSecondAsset.put("statusId",secondAsset.hardwareAssetStatus.id);
        properitesMapForSecondAsset.put("statusName",secondAsset.hardwareAssetStatus.name);
        properitesMapForSecondAsset.put("locationBaseId",secondAsset.targetHardwareAssetHasLocation.baseId);
        properitesMapForSecondAsset.put("locationClassTypeId",secondAsset.targetHardwareAssetHasLocation.classTypeId);
        properitesMapForSecondAsset.put("locationDisplayName",secondAsset.targetHardwareAssetHasLocation.displayName);
        properitesMapForSecondAsset.put("organizationBaseId",secondAsset.targetHardwareAssetHasOrganization.baseId);
        properitesMapForSecondAsset.put("organizationClassTypeId",secondAsset.targetHardwareAssetHasOrganization.classTypeId);
        properitesMapForSecondAsset.put("organizationDisplayName",secondAsset.targetHardwareAssetHasOrganization.displayName);
        properitesMapForSecondAsset.put("costCenterBaseId",secondAsset.targetHardwareAssetHasCostCenter.baseId);
        properitesMapForSecondAsset.put("costCenterClassTypeId",secondAsset.targetHardwareAssetHasCostCenter.classTypeId);
        properitesMapForSecondAsset.put("costCenterDisplayName",secondAsset.targetHardwareAssetHasCostCenter.displayName);
        properitesMapForSecondAsset.put("loanReturnedDate",secondAsset.loanReturnedDate);
        properitesMapForSecondAsset.put("loanedDate",secondAsset.loanedDate);
        properitesMapForSecondAsset.put("notes",secondAsset.notes);
        properitesMapForSecondAsset.put("primaryUserBaseId",secondAsset.targetHardwareAssetHasPrimaryUser.baseId);
        properitesMapForSecondAsset.put("primaryUserClassTypeId",secondAsset.targetHardwareAssetHasPrimaryUser.classTypeId);
        properitesMapForSecondAsset.put("primaryUserDisplayName",secondAsset.targetHardwareAssetHasPrimaryUser.displayName);
        properitesMapForSecondAsset.put("custodianUserBaseId", secondAsset.owndedBy.baseId);
        properitesMapForSecondAsset.put("custodianUserClassTypeId", secondAsset.owndedBy.classTypeId);
        properitesMapForSecondAsset.put("custodianUserDisplayName",secondAsset.owndedBy.userName);
    }

    /*Initally sets properties of temporary asset in global scope from the property of first asset.
    These properties become default properites to be set for second asset.*/
    public void mapPropertiesFromFirstAsset(){
        /*Also set properties of first asset to the properties of a temporary asset..*/
        globalData.getTemporaryAsset().hardwareAssetStatus.id = globalData.getSwaper().getProperitesMapForFirstAsset().get("statusId");
        globalData.getTemporaryAsset().hardwareAssetStatus.name = globalData.getSwaper().getProperitesMapForFirstAsset().get("statusName");
        globalData.getTemporaryAsset().targetHardwareAssetHasLocation.baseId = globalData.getSwaper().getProperitesMapForFirstAsset().get("locationBaseId");
        globalData.getTemporaryAsset().targetHardwareAssetHasLocation.classTypeId = globalData.getSwaper().getProperitesMapForFirstAsset().get("locationClassTypeId");
        globalData.getTemporaryAsset().targetHardwareAssetHasLocation.displayName = globalData.getSwaper().getProperitesMapForFirstAsset().get("locationDisplayName");
        globalData.getTemporaryAsset().targetHardwareAssetHasOrganization.baseId = globalData.getSwaper().getProperitesMapForFirstAsset().get("organizationBaseId");
        globalData.getTemporaryAsset().targetHardwareAssetHasOrganization.classTypeId = globalData.getSwaper().getProperitesMapForFirstAsset().get("organizationClassTypeId");
        globalData.getTemporaryAsset().targetHardwareAssetHasOrganization.displayName = globalData.getSwaper().getProperitesMapForFirstAsset().get("organizationDisplayName");
        globalData.getTemporaryAsset().targetHardwareAssetHasCostCenter.baseId = globalData.getSwaper().getProperitesMapForFirstAsset().get("costCenterBaseId");
        globalData.getTemporaryAsset().targetHardwareAssetHasCostCenter.classTypeId = globalData.getSwaper().getProperitesMapForFirstAsset().get("costCenterClassTypeId");
        globalData.getTemporaryAsset().targetHardwareAssetHasCostCenter.displayName = globalData.getSwaper().getProperitesMapForFirstAsset().get("costCenterDisplayName");
        globalData.getTemporaryAsset().targetHardwareAssetHasPrimaryUser.baseId = globalData.getSwaper().getProperitesMapForFirstAsset().get("primaryUserBaseId");
        globalData.getTemporaryAsset().targetHardwareAssetHasPrimaryUser.classTypeId = globalData.getSwaper().getProperitesMapForFirstAsset().get("primaryUserClassTypeId");
        globalData.getTemporaryAsset().targetHardwareAssetHasPrimaryUser.displayName = globalData.getSwaper().getProperitesMapForFirstAsset().get("primaryUserDisplayName");
        globalData.getTemporaryAsset().owndedBy.baseId = globalData.getSwaper().getProperitesMapForFirstAsset().get("custodianUserBaseId");
        globalData.getTemporaryAsset().owndedBy.classTypeId = globalData.getSwaper().getProperitesMapForFirstAsset().get("custodianUserClassTypeId");
        globalData.getTemporaryAsset().owndedBy.userName = globalData.getSwaper().getProperitesMapForFirstAsset().get("custodianUserDisplayName");
    }

    public void setProperitesMapForSecondAsset(HashMap<String, String> properitesMapForSecondAsset) {
        this.properitesMapForSecondAsset = properitesMapForSecondAsset;
    }

    public HashMap<String, String> getProperitesMapForSecondAsset() {
        return properitesMapForSecondAsset;
    }

    /*Set and get notes for assets..*/
    public void setNoteForFirstAsset(String note){
        if(noteToAppendForFirstAsset == null){
            noteToAppendForFirstAsset = new StringBuilder();
        }
        noteToAppendForFirstAsset.replace(0,noteToAppendForFirstAsset.length(),note);
    }

    /*Set and get notes for assets..*/
    public void setNoteForSecondAsset(String note){
        if(noteToAppendForSecondAsset==null){
            noteToAppendForSecondAsset = new StringBuilder();
        }
        noteToAppendForSecondAsset.replace(0,noteToAppendForSecondAsset.length(),note);
    }

    public void setNoteToAppendForFirstAsset(StringBuilder noteToAppendForFirstAsset) {
        this.noteToAppendForFirstAsset = noteToAppendForFirstAsset;
    }

    public StringBuilder getNoteToAppendForFirstAsset() {
        return noteToAppendForFirstAsset;
    }

    public void setNoteToAppendForSecondAsset(StringBuilder noteToAppendForSecondAsset) {
        this.noteToAppendForSecondAsset = noteToAppendForSecondAsset;
    }

    public StringBuilder getNoteToAppendForSecondAsset() {
        return noteToAppendForSecondAsset;
    }

    public static void setShowMessageDialog(boolean showMessageDialog) {
        Swaper.showMessageDialog = showMessageDialog;
    }

    public static boolean getShowMessageDialog(){
        return Swaper.showMessageDialog;
    }

    public static void setMessage(String message) {
        Swaper.message = message;
    }

    public static String getMessage() {
        return Swaper.message;
    }

    public static void setUsePrimaryUser(boolean forPrimaryUser) {
        Swaper.usePrimaryUser = forPrimaryUser;
    }

    public boolean getUsePrimaryUser(){
        return Swaper.usePrimaryUser;
    }

    public static void setSwap(boolean swap) {
        Swaper.swap = swap;
    }

    public boolean getSwap(){
        return Swaper.swap;
    }
}

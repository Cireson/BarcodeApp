
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


package com.cireson.assetmanager.util;

/**
 * Created by welcome on 4/16/2014.
 */
public class CiresonConstants {

    // general actions
    public static final String Action = "GetData";
    public static final String Token = "-12333";

    //Projection types
    public static final String PurchaseOrder_ProjectionType = "f27daae2-280c-dd8b-24e7-9bdb5120d6d2";
    public static final String PurchaseOrder_Views_ProjectionType = "05334c4f-4bd7-1197-1182-f594497fd625";
    public static final String PurchaseOrder_MobileProjectionType = "8da529da-0271-4b11-fc54-8a7ddd9730c5";
    public static final String HardwareAsset_MobileProjectionType = "fcf03353-bab2-0fbc-1abc-ffff6a97b064";
    public static final String PurchaseOrder_Minimum_ProjectionType = "f544e33c-111a-0c17-262a-5fd33052a887";
    public static final String Organization_Minimum_ProjectionType = "a8cec808-1b3e-72cf-1a1d-be4d82945b44";
    public static final String CostCenter_Minimum_ProjectType = "93b64a04-6d39-b47e-a3dd-24da746444df";
    public static final String HardwareAsset_Minimum_ProjectionType = "6fd42dd3-81b4-ec8d-14d6-08af1e83f63a";
    public static final String Location_Minimum_ProjectionType = "f1a64b57-42ce-5a20-c052-15e79a3b33f6";
    public static final String User_ProjectionType = "0e1313ab-dc5c-cf9d-d6b0-e2e9835a132a";
    public static final String Property_BaseId = "BaseId";
    public static final String Property_Name = "Name";
    public static final String Property_Id = "Id";

    // Purchase order
    public static final String Purchase_Order_Type = "2afe355c-24a7-b20f-36e3-253b7249818d";
    public static final String Purchase_Order_Display_Name = "ca464f4b-d48c-ea7a-cf8f-de682e7d439c";
    public static final String Purchase_Order_ID = "14569535-d229-1448-b51c-8650b5b3103a";
    public static final String PurchaseOrder_property_PurchaseOrderDate = "48ff2cd1-6ec0-cc61-eef7-d49bdeaa3a28";

    // cost center
    public static final String Cost_Center_Type = "128bdb2d-f5bd-f8b6-440e-e3f7d8ab4858";
    public static final String Cost_Center_Property_Display_Name = "afb4f9e6-bf48-1737-76ad-c9b3ec325b97";
    public static final String Cost_Center_Property_Id = "f18fd8a7-daf5-a5ff-21b8-e82979c14211";

    // organization
    public static final String Organization_Type = "ed0d8659-fba9-6e08-c213-5cd88f5480a8";
    public static final String Organization_Property_DisplayName = "afb4f9e6-bf48-1737-76ad-c9b3ec325b97";
    public static final String Organization_Property_Id = "4631ca6a-50da-5be4-1746-8f263d0bbf32";

    // hardware asset
    public static final String HardwareAsset_Type = "c0c58e7f-7865-55cc-4600-753305b9be64";
    public static final String HardwareAsset_Property_DisplayName = "afb4f9e6-bf48-1737-76ad-c9b3ec325b97";
    public static final String HardwareAsset_Property_Status = "b0c5e33d-ad12-e3a3-ebf9-0173c8a02c83";
    public static final String HardwareAsset_Property_ReceivedDate = "474de927-e822-c823-6b06-e7ad4fc1589e";
    public static final String HardwareAsset_Property_AssetTag = "6894136c-4d47-4f4c-0d6b-4c1df730099b";
    public static final String HardwareAsset_Property_SerialNumber = "1db947bc-c869-f206-f611-fe943c6d8ee6";

    // location
    public static final String Location_Type = "b1ae24b1-f520-4960-55a2-62029b1ea3f0";
    public static final String Location_Property_DisplayName = "afb4f9e6-bf48-1737-76ad-c9b3ec325b97";
    public static final String Location_Property_Id = "aaf055aa-fa16-fff8-46ff-b00a2aec08ff";

    // custodian
    public static final String CUSTODIAN_USER_TYPE = "eca3c52a-f273-5cdc-f165-3eb95a2b26cf";
    public static final String Custodian_User_Property_Display_Name = "afb4f9e6-bf48-1737-76ad-c9b3ec325b97";
    public static final String Custodian_User_Property_Id = "0e1313ab-dc5c-cf9d-d6b0-e2e9835a132a";

    //----------------------- enum ids
    public static final String HardwareAsset_Status_Enumeration_Id = "6b7304c4-1b09-bffc-3fe3-1cfd3eb630cb";

    //----------------------- Other constants
    public static final String HardwareAsset_Property_LoanReturnedDate =  "9f5bfd79-ae7b-5606-8bd2-673db732574a";
    public static final String HardwareAsset_Property_LoanedDate = "70b20bf5-daa1-f047-ee5e-acd531655278";
    public static final String HardwareAsset_Property_Notes = "55124dce-750d-bd54-4b92-d1fddf99adec";

    //----------------------- Operators
    public static final String Equals_Operator = "Equal";
    public static final String Like_Operator = "Like";
    public static final String WildcardValue = "%%";
    public static final String GreaterThanOrEqual_Operator = "GreaterEqual";
    public static final String LessThanOrEqual_Operator = "LessEqual";

    /*Constants to be used in intent calls..*/
    /*Constants for navigate from..*/
    public final static String NAV_FROM_MAIN_MENU = "Navigate_From_Main_Menu";
    public final static String NAV_FROM_SEARCH_ASSETS = "Navigate_From_Search_Assets";
    public final static String NAV_FROM_EDIT = "Navigate_From_Edit";

    /*Constants for navigate to..*/
    /*Menus..*/
    public final static int RECEIVED_ASSETS = 1;
    public final static int SWAP_ASSETS = 2;
    public final static int EDIT_ASSETS = 3;
    public final static int INVENTORY_AUDIT = 4;
    public final static int DISPOSE_ASSETS = 5;
    public final static int SETTINGS = 5;

    /*Lists for add assets..*/
    public final static int ACTIVE_LIST_MANUFACTURER_FOR_ADD_ASSETS = 0;
    public final static int ACTIVE_LIST_MODEL_FOR_ADD_ASSETS = 1;

    /*Activities..*/
    public final static int Main_Nav_Ex_Activity = 1;
    public final static int Scan_Assets_Activity = 2;

    /*Constants for button actions..*/
    /*Useed in Search Assets..*/
    public final static int SEARCH_ASSETS = 1;
    public final static int BROWSE_ORDERS = 2;

    /*Used in Edit Assets..*/
    //------------------------ relationship types
    public final static String HardwareAsset_HasLocation_RelationshipType = "98dad5a4-3880-7a7e-c2ae-b5a89aa964ae";
    public final static String HardwareAsset_HasOrganization_RelationshipType = "29234d2d-1e45-febe-5ef2-45bf9d3c5dad";
    public final static String HardwareAsset_HasCostCenter_RelationshipType = "5ce07bee-d4f3-9ed4-2e95-7eb6558c34b5";
    public final static String HardwareAsset_HasPrimaryUser_RelationshipType = "28ba5ae2-dfaa-4b7a-ecd6-10cec092c957";

    /*Constants for NameRelationships..*/
    public final static String Target_HardwareAssetHasVendor = "Target_HardwareAssetHasVendor";
    public final static String RelationshipId_Vendor = "829383eb-3e95-e0ba-0eda-a67d1a4a9f94";

    public final static String Target_HardwareAssetHasLocation = "Target_HardwareAssetHasLocation";
    public final static String RelationshipId_Location = "98dad5a4-3880-7a7e-c2ae-b5a89aa964ae";

    public final static String Target_HardwareAssetHasPurchaseOrder = "Target_HardwareAssetHasPurchaseOrder";
    public final static String RelationshipId_PurchaseOrder = "514b5285-1f7b-398c-7839-f43c2e1a3148";

    public final static String Target_HardwareAssetHasOrganization = "Target_HardwareAssetHasOrganization";
    public final static String RelationshipId_Organization = "29234d2d-1e45-febe-5ef2-45bf9d3c5dad";

    public final static String Target_HardwareAssetHasCostCenter = "Target_HardwareAssetHasCostCenter";
    public final static String RelationshipId_CostCenter = "5ce07bee-d4f3-9ed4-2e95-7eb6558c34b5";

    public final static String OwnedBy = "OwnedBy";
    public final static String RelationshipId_OwnedBy = "cbb45424-b0a2-72f0-d535-541941cdf8e1";

    public final static String Target_HardwareAssetHasPrimaryUser = "Target_HardwareAssetHasPrimaryUser";
    public final static String RelationshipId_PrimaryUser = "28ba5ae2-dfaa-4b7a-ecd6-10cec092c957";

    public final static String Target_HardwareAssetHasCatalogItem = "Target_HardwareAssetHasCatalogItem";
    public final static String RelationshipId_CatalogItem = "9c76eda6-7603-9d1b-673f-e1e06999ebf5";

    /*Hardware property status name and id.. */
    public final static String HARDWARE_STATUS_ID_DISPOSED = "020822bd-7d3a-4c0f-1be4-325ef1aa52b4";
    public final static String HARDWARE_STATUS_NAME_DISPOSED = "Disposed";

    /*These fields are used in Editor assistant class while setting assets properites after edit..*/
    public final static int EDIT_MANUFACTURER = 0;
    public final static int EDIT_MODEL = 1;
    public final static int EDIT_STATUS_ID = 2;
    public final static int EDIT_STATUS_NAME = 3;
    public final static int EDIT_LOCATION_CLASS_TYPE_ID = 4;
    public final static int EDIT_LOCATION_DISPLAY_NAME = 5;
    public final static int EDIT_ORGANIZATION_CLASS_TYPE_ID = 6;
    public final static int EDIT_ORGANIZATION_DISPLAY_NAME = 7;
    public final static int EDIT_COST_CENTER_CLASS_TYPE_ID = 8;
    public final static int EDIT_COST_CENTER_DISPLAY_NAME = 9;
    public final static int EDIT_PRIMARY_USER_CLASS_TYPE_ID = 10;
    public final static int EDIT_PRIMARY_USER_DISPLAY_NAME = 11;
    public final static int EDIT_CUSTODIAN_USER_CLASS_TYPE_ID = 12;
    public final static int EDIT_CUSTODIAN_USER_DISPLAY_NAME = 13;
    public final static int EDIT_DISPOSAL_REFERENCE_NUMBER = 14;
    public final static int EDIT_DISPOSAL_dATE = 15;
    public final static int EDIT_RECEIVE_DATE = 16;
    public final static int EDIT_LOAN_RETURNED_DATE = 17;
    public final static int EDIT_lOANED_DATE = 18;

    /*These fields are constants for intent message between the pages..*/
    /*Intent extras string names to hold intent messages.*/
    public static final String MESSAGE_FROM_INVENTORY_SEARCH_TO_ASSETS_EDITOR_ERROR = "MESSAGE_FROM_INVENTORY_SEARCH_TO_ASSETS_EDITOR_ERROR";
    public static final String MESSAGE_FROM_INVENTORY_SEARCH_TO_ASSETS_EDITOR_OBJECT_STATE = "MESSAGE_FROM_INVENTORY_SEARCH_TO_ASSETS_EDITOR_OBJECT_STATE";

    /*This static field is used in a APICaller's callback. Other components check this constant and identify that token has expired..*/
    public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String TOKEN_EXPIRED_MESSAGE = "Your token has expired. Please login in and try again.";
    public static final String EMPTY_STRING = "";
}

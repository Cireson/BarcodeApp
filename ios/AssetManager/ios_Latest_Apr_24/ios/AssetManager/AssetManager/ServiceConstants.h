
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
//
//  ServiceConstants.h
//  AssetManager
//

#import <Foundation/Foundation.h>

// type projection ids
extern NSString * const PurchaseOrder_ProjectionType;
extern NSString * const PurchaseOrder_Minimum_ProjectionType;
extern NSString * const PurchaseOrder_Views_ProjectionType;
extern NSString * const PurchaseOrder_MobileProjectionType;
extern NSString * const Organization_Minimum_ProjectionType;
extern NSString * const CostCenter_Minimum_ProjectType;
extern NSString * const HardwareAsset_Minimum_ProjectionType;
extern NSString * const HardwareAsset_MobileProjectionType;
extern NSString * const Location_Minimum_ProjectionType;
extern NSString * const User_ProjectionType;
extern NSString * const Property_BaseId;
extern NSString * const Property_Name;
extern NSString * const Property_Id;

//----------------------- type ids
// purchase order
extern NSString * const PurchaseOrder_Type;
extern NSString * const PurchaseOrder_Property_PurchaseOrderNumber;
extern NSString * const PurchaseOrder_Property_DisplayName;
extern NSString * const PurchaseOrder_property_PurchaseOrderDate;

// cost center
extern NSString * const CostCenter_Type;
extern NSString * const CostCenter_Property_DisplayName;

// organization
extern NSString * const Organization_Type;
extern NSString * const Organization_Property_DisplayName;

// hardware asset
extern NSString * const HardwareAsset_Type;
extern NSString * const HardwareAsset_Property_DisplayName;
extern NSString * const HardwareAsset_Property_Status;
extern NSString * const HardwareAsset_Property_ReceivedDate;
extern NSString * const HardwareAsset_Property_AssetTag;
extern NSString * const HardwareAsset_Property_SerialNumber;
extern NSString * const HardwareAsset_Property_LoanReturnedDate;
extern NSString * const HardwareAsset_Property_Notes;
extern NSString * const HardwareAsset_LoanedDate;

// location
extern NSString * const Location_Type;
extern NSString * const Location_Property_DisplayName;

// user
extern NSString * const User_Type;
extern NSString * const User_Property_DisplayName;

//----------------------- enum ids
extern NSString * const HardwareAsset_Status_Enumeration_Id;

//----------------------- Operators
extern NSString * const Equals_Operator;
extern NSString * const Like_Operator;
extern NSString * const WildcardValue;
extern NSString * const GreaterThanOrEqual_Operator;
extern NSString * const LessThanOrEqual_Operator;


//----------------------- method names
extern NSString * const LoginMethod;
extern NSString * const GetProjectionDataMethod;
extern NSString * const GetEnumerationMethod;
extern NSString * const UpdateProjectionMethod;

// relationship types
extern NSString * const HardwareAsset_HasLocation_RelationshipType;
extern NSString * const HardwareAsset_HasOrganization_RelationshipType;
extern NSString * const HardwareAsset_HasCostCenter_RelationshipType;
extern NSString * const HardwareAsset_HasPrimaryUser_RelationshipType;
extern NSString * const HardwareAsset_HasPurchaseOrder_RelationshipType;
extern NSString * const HardwareAsset_HasVendor_RelationshipType;
extern NSString * const HardwareAsset_HasCatelogItem_RelationshipType;
extern NSString * const HardwareAsset_OwnedBy_RelationsipType;

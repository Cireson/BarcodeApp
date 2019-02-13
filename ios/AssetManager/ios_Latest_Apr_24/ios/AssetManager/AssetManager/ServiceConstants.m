
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
//  ServiceConstants.m
//  AssetManager
//

#import "ServiceConstants.h"

// projection ids
NSString * const PurchaseOrder_ProjectionType = @"f27daae2-280c-dd8b-24e7-9bdb5120d6d2";
NSString * const PurchaseOrder_Views_ProjectionType = @"05334c4f-4bd7-1197-1182-f594497fd625";
NSString * const PurchaseOrder_MobileProjectionType = @"8da529da-0271-4b11-fc54-8a7ddd9730c5";
NSString * const PurchaseOrder_Minimum_ProjectionType = @"f544e33c-111a-0c17-262a-5fd33052a887";
NSString * const Organization_Minimum_ProjectionType = @"a8cec808-1b3e-72cf-1a1d-be4d82945b44";
NSString * const CostCenter_Minimum_ProjectType = @"93b64a04-6d39-b47e-a3dd-24da746444df";
NSString * const HardwareAsset_Minimum_ProjectionType = @"6fd42dd3-81b4-ec8d-14d6-08af1e83f63a";
NSString * const HardwareAsset_MobileProjectionType = @"fcf03353-bab2-0fbc-1abc-ffff6a97b064";
NSString * const Location_Minimum_ProjectionType = @"f1a64b57-42ce-5a20-c052-15e79a3b33f6";
NSString * const User_ProjectionType = @"0e1313ab-dc5c-cf9d-d6b0-e2e9835a132a";
NSString * const Property_BaseId = @"BaseId";
NSString * const Property_Name = @"Name";
NSString * const Property_Id = @"Id";

//----------------------- type ids
// purchase order
NSString * const PurchaseOrder_Type = @"2afe355c-24a7-b20f-36e3-253b7249818d";
NSString * const PurchaseOrder_Property_PurchaseOrderNumber = @"ca464f4b-d48c-ea7a-cf8f-de682e7d439c";
NSString * const PurchaseOrder_Property_DisplayName = @"afb4f9e6-bf48-1737-76ad-c9b3ec325b97";
NSString * const PurchaseOrder_property_PurchaseOrderDate = @"48ff2cd1-6ec0-cc61-eef7-d49bdeaa3a28";


// cost center
NSString * const CostCenter_Type = @"128bdb2d-f5bd-f8b6-440e-e3f7d8ab4858";
NSString * const CostCenter_Property_DisplayName = @"afb4f9e6-bf48-1737-76ad-c9b3ec325b97";

// organization
NSString * const Organization_Type= @"ed0d8659-fba9-6e08-c213-5cd88f5480a8";
NSString * const Organization_Property_DisplayName = @"afb4f9e6-bf48-1737-76ad-c9b3ec325b97";


// hardware asset
NSString * const HardwareAsset_Type = @"c0c58e7f-7865-55cc-4600-753305b9be64";
NSString * const HardwareAsset_Property_DisplayName = @"afb4f9e6-bf48-1737-76ad-c9b3ec325b97";
NSString * const HardwareAsset_Property_Status = @"b0c5e33d-ad12-e3a3-ebf9-0173c8a02c83";
NSString * const HardwareAsset_Property_ReceivedDate = @"474de927-e822-c823-6b06-e7ad4fc1589e";
NSString * const HardwareAsset_Property_AssetTag = @"6894136c-4d47-4f4c-0d6b-4c1df730099b";
NSString * const HardwareAsset_Property_SerialNumber = @"1db947bc-c869-f206-f611-fe943c6d8ee6";
NSString * const HardwareAsset_Property_LoanReturnedDate = @"9f5bfd79-ae7b-5606-8bd2-673db732574a";
NSString * const HardwareAsset_Property_Notes = @"55124dce-750d-bd54-4b92-d1fddf99adec";
NSString * const HardwareAsset_Property_LoanedDate = @"70b20bf5-daa1-f047-ee5e-acd531655278";

// location
NSString * const Location_Type = @"b1ae24b1-f520-4960-55a2-62029b1ea3f0";
NSString * const Location_Property_DisplayName = @"afb4f9e6-bf48-1737-76ad-c9b3ec325b97";

// custodian
NSString * const User_Type = @"eca3c52a-f273-5cdc-f165-3eb95a2b26cf";
NSString * const User_Property_DisplayName = @"afb4f9e6-bf48-1737-76ad-c9b3ec325b97";

//----------------------- enum ids
NSString * const HardwareAsset_Status_Enumeration_Id = @"6b7304c4-1b09-bffc-3fe3-1cfd3eb630cb";

//----------------------- Operators
NSString * const Equals_Operator = @"Equal";
NSString * const Like_Operator = @"Like";
NSString * const WildcardValue = @"%%";
NSString * const GreaterThanOrEqual_Operator = @"GreaterEqual";
NSString * const LessThanOrEqual_Operator = @"LessEqual";


//------------------------ method names
NSString * const LoginMethod = @"Login";
NSString * const GetProjectionDataMethod = @"GetProjectionByCriteria";
NSString * const GetEnumerationMethod = @"GetEnumeration";
NSString * const UpdateProjectionMethod = @"UpdateProjection";

//------------------------ relationship types
NSString * const HardwareAsset_HasLocation_RelationshipType = @"98dad5a4-3880-7a7e-c2ae-b5a89aa964ae";
NSString * const HardwareAsset_HasOrganization_RelationshipType = @"29234d2d-1e45-febe-5ef2-45bf9d3c5dad";
NSString * const HardwareAsset_HasCostCenter_RelationshipType = @"5ce07bee-d4f3-9ed4-2e95-7eb6558c34b5";
NSString * const HardwareAsset_HasPrimaryUser_RelationshipType = @"28ba5ae2-dfaa-4b7a-ecd6-10cec092c957";
NSString * const HardwareAsset_HasPurchaseOrder_RelationshipType = @"514b5285-1f7b-398c-7839-f43c2e1a3148";
NSString * const HardwareAsset_HasVendor_RelationshipType = @"829383eb-3e95-e0ba-0eda-a67d1a4a9f94";
NSString * const HardwareAsset_HasCatelogItem_RelationshipType = @"9c76eda6-7603-9d1b-673f-e1e06999ebf5";
NSString * const HardwareAsset_OwnedBy_RelationsipType = @"cbb45424-b0a2-72f0-d535-541941cdf8e1";

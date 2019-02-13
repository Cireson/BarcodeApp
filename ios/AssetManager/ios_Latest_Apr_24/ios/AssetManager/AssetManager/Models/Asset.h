
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
//  Asset.h
//  AssetManager
//

#import <Foundation/Foundation.h>
#import "CiresonModelBase.h"

@class CiresonLocation;
@class CostCenter;
@class Organization;
@class CiresonUser;

typedef enum  {
    MatchedBarCodeActionNone = 0, // means asset's serial no or tag is already assigned to the matched bar code data
    MatchedBarCodeActionAssignToSerialNumber, // means the matched bar code needs to be assigned to Serial no property
    MatchedBarCodeActionAssignToTag // means matched bar code needs to be assigned to asset tag property
} MatchedBarCodeAction;

@interface Asset : CiresonModelBase

@property (nonatomic, strong) NSString* statusId;
@property (nonatomic, readonly) NSString* statusName;

@property (nonatomic, strong, readonly) NSString* assignedDate;
@property (nonatomic, strong, readonly) NSNumber* cost;
@property (nonatomic, strong, readonly) NSString* currency;
@property (nonatomic, strong, readonly) NSString* fullName;
@property (nonatomic, strong, readonly) NSString* assetType;
@property (nonatomic, strong) NSString* hardwareAssetId;
@property (nonatomic, strong) NSString* model;
@property (nonatomic, strong) NSString* manufacturer;
@property (nonatomic, strong) NSString* serialNumber;
@property (nonatomic, strong) NSString* tag;
@property (nonatomic, strong) NSString* receivedDate;
@property (nonatomic, strong) NSString* loanReturnedDate;
@property (nonatomic, strong) NSString* loanedDate;
@property (nonatomic, strong) NSString* matchedBarcodeData;
@property (nonatomic, readonly) NSString* locationName;
@property (nonatomic, readonly) NSString* organizationName;
@property (nonatomic, readonly) NSString* custodianName;
@property (nonatomic, readonly) NSString* primaryUserName;
@property (nonatomic, readonly) NSString* costCenterName;
@property (nonatomic, getter=getAssetTypeDisplayName, readonly) NSString* assetTypeDisplayName;
@property (nonatomic, getter=getHasSerialNumber, readonly) BOOL hasSerialNumber;
@property (nonatomic, getter=getHasAssetTag, readonly) BOOL hasAssetTag;
@property (nonatomic, getter=getHasMatchedBarcodeData, readonly) BOOL hasMatchedBarcodeData;
@property (nonatomic, getter=getNeedsBarcodeMatching, readonly) BOOL needsBarcodeMatching;
@property (nonatomic, strong) NSString* name;

// temporary helper to figure out whether to assign the matched barcode to asset tag or serial number.
@property (nonatomic, assign) MatchedBarCodeAction matchedBarCodeAction;

+(Asset*)loadFromDict:(NSDictionary*)dict;
+(NSArray*)loadFromArray:(NSArray*)arr;

-(BOOL)matchesBarcodeWithData:(NSString*)barCodeData;
-(NSString*)getAssetTypeDisplayName;
-(void)setLocation:(CiresonLocation*)location;
-(void)setOrganization:(Organization*)organization;
-(void)setCostCenter:(CostCenter*)costCenter;
-(void)setCustodian:(CiresonUser*)custodian;
-(void)clearPrimaryUser;
-(void)setDisposalReferenceNumber:(NSString*)drn;
-(void)setDisposalDate:(NSString*)date;
-(void)setOriginalStatusFromAsset:(Asset*)asset;
-(void)appendToNotes:(NSString*)notes;


@end

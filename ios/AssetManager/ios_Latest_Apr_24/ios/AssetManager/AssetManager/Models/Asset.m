
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
//  Asset.m
//  AssetManager
//

#import "Asset.h"

@implementation Asset

@synthesize matchedBarcodeData, matchedBarCodeAction;

+(Asset*)loadFromDict:(NSDictionary*)dict {
    Asset* a = [[Asset alloc] init];
    if ([a load:dict])
        return a;
    return Nil;
}

+(NSArray*)loadFromArray:(NSArray*)arr {
    if (arr == Nil) return Nil;
    NSMutableArray* tmpArr = [[NSMutableArray alloc] init];
    for(NSDictionary* aObj in arr) {
        Asset* a = [Asset loadFromDict:aObj];
        if (a != Nil) {
            [tmpArr addObject:a];
        }
    }
    return tmpArr;
}

-(id)init {
    if (self = [super init]) {
        self.jsonObject = [[NSMutableDictionary alloc] init];
    }
    return self;
}

-(NSString*)statusId {
    NSDictionary* hardwareStatusObject = [self.jsonObject objectForKey:@"HardwareAssetStatus"];
    if (hardwareStatusObject != Nil) {
        return [self readStringFromObject:hardwareStatusObject key:@"Id"];
    }
    return @"";
}

-(NSString*)statusName {
    NSDictionary* hardwareStatusObject = [self.jsonObject objectForKey:@"HardwareAssetStatus"];
    if (hardwareStatusObject != Nil) {
        return [self readStringFromObject:hardwareStatusObject key:@"Name"];
    }
    return @"";
}

-(NSString*)locationName {
    NSDictionary* location = [self.jsonObject objectForKey:@"Target_HardwareAssetHasLocation"];
    return [self readStringFromObject:location key:@"DisplayName"];
}

-(NSString*)organizationName {
    NSDictionary* organizationName = [self.jsonObject objectForKey:@"Target_HardwareAssetHasOrganization"];
    return [self readStringFromObject:organizationName key:@"DisplayName"];
}

-(NSString*)custodianName {
    NSDictionary* organizationName = [self.jsonObject objectForKey:@"OwnedBy"];
    return [self readStringFromObject:organizationName key:@"DisplayName"];
}

-(NSString*)costCenterName {
    NSDictionary* cc = [self.jsonObject objectForKey:@"Target_HardwareAssetHasCostCenter"];
    return [self readStringFromObject:cc key:@"DisplayName"];
}

-(NSString*)primaryUserName {
    NSDictionary* organizationName = [self.jsonObject objectForKey:@"Target_HardwareAssetHasPrimaryUser"];
    return [self readStringFromObject:organizationName key:@"DisplayName"];
}

-(void)setStatusId:(NSString *)stId {
    NSMutableDictionary* hardwareStatusObject = [[NSMutableDictionary alloc] init];
    [hardwareStatusObject setValue:stId forKey:@"Id"];
    [self.jsonObject setValue:hardwareStatusObject forKey:@"HardwareAssetStatus"];
}

-(void)setLocation:(CiresonLocation*)location {
    NSMutableDictionary* hardwareAssetHasLocation = [[NSMutableDictionary alloc] init];
    [hardwareAssetHasLocation setValue:Location_Type forKey:@"ClassTypeId"];
    [hardwareAssetHasLocation setValue:location.objectId forKey:@"BaseId"];
    [self.jsonObject setValue:hardwareAssetHasLocation forKey:@"Target_HardwareAssetHasLocation"];
}

-(void)setOrganization:(Organization*)organization {
    NSMutableDictionary* hardwareAssetHasOrganization = [[NSMutableDictionary alloc] init];
    [hardwareAssetHasOrganization setValue:Organization_Type forKey:@"ClassTypeId"];
    [hardwareAssetHasOrganization setValue:organization.objectId forKey:@"BaseId"];
    [self.jsonObject setValue:hardwareAssetHasOrganization forKey:@"Target_HardwareAssetHasOrganization"];
}

-(void)setCostCenter:(CostCenter*)costCenter {
    NSMutableDictionary* hardwareAssetHasCostCenter = [[NSMutableDictionary alloc] init];
    [hardwareAssetHasCostCenter setValue:CostCenter_Type forKey:@"ClassTypeId"];
    [hardwareAssetHasCostCenter setValue:costCenter.objectId forKey:@"BaseId"];
    [self.jsonObject setValue:hardwareAssetHasCostCenter forKey:@"Target_HardwareAssetHasCostCenter"];
    
}

-(void)setCustodian:(CiresonUser*)custodian {
    NSMutableDictionary* hardwareAssetHasCustodian = [[NSMutableDictionary alloc] init];
    [hardwareAssetHasCustodian setValue:User_Type forKey:@"ClassTypeId"];
    [hardwareAssetHasCustodian setValue:custodian.objectId forKey:@"BaseId"];
    [self.jsonObject setValue:hardwareAssetHasCustodian forKey:@"OwnedBy"];
}

//
// This is used in swap asset case where the second asset needs to be set to the original
// status value of the first asset.
//
-(void)setOriginalStatusFromAsset:(Asset*)asset {
    NSDictionary* status = [[asset.originalJson valueForKey:@"HardwareAssetStatus"] copy];
    [self.jsonObject setValue:status forKey:@"HardwareAssetStatus"];
}

-(void)clearPrimaryUser {
    [self.jsonObject setValue:Nil forKeyPath:@"Target_HardwareAssetHasPrimaryUser"];
}

-(void)setDisposalReferenceNumber:(NSString*)val {
    [self.jsonObject setValue:val forKey:@"DisposalReference"];
}

-(void)setDisposalDate:(NSString*)date {
    [self.jsonObject setValue:date forKey:@"DisposalDate"];
}


-(NSString*)receivedDate {
    return [self readStringFromObject:self.jsonObject key:@"ReceivedDate"];
}

-(void)setReceivedDate:(NSString *)d{
    [self.jsonObject setValue:d forKey:@"ReceivedDate"];
}

-(NSString*)tag {
    return [self readStringFromObject:self.jsonObject key:@"AssetTag"];
}

-(void)setTag:(NSString*)tag {
    [self.jsonObject setValue:tag forKey:@"AssetTag"];
}

-(NSString*)assignedDate {
    return [self readStringFromObject:self.jsonObject key:@"AssignedDate"];
}

-(NSNumber*)cost {
    return [self readNumberFromObject:self.jsonObject key:@"Cost"];
}

-(NSString*)currency {
    return [self readStringFromObject:self.jsonObject key:@"Currency"];
}

-(NSString*)fullName {
    return [self readStringFromObject:self.jsonObject key:@"FullName"];
}

-(NSString*)hardwareAssetId {
    return [self readStringFromObject:self.jsonObject key:@"HardwareAssetID"];
}

-(void)setHardwareAssetId:(NSString *)hardwareAssetId {
    return [self.jsonObject setValue:hardwareAssetId forKey:@"HardwareAssetID"];
}

-(NSString*)displayName {
    NSLog(@"jsonObject:%@",self.jsonObject);
    return [self readStringFromObject:self.jsonObject key:@"DisplayName"];
}

-(void)setDisplayName:(NSString *)displayName {
    
    [self.jsonObject setValue:displayName forKey:@"DisplayName"];
}

-(NSString*)name {
    return [self readStringFromObject:self.jsonObject key:@"Name"];
}

-(void)setName:(NSString*)name {
    [self.jsonObject setValue:name forKey:@"Name"];
}

-(NSString*)serialNumber {
    return [self readStringFromObject:self.jsonObject key:@"SerialNumber"];
}

-(void)setSerialNumber:(NSString *)serialNumber {
    [self.jsonObject setValue:serialNumber forKey:@"SerialNumber"];
}

-(NSString*)model {
    return [self readStringFromObject:self.jsonObject key:@"Model"];
}
-(void)setModel:(NSString*)model {
    return [self.jsonObject setValue:model forKey:@"Model"];
}

-(NSString*)manufacturer {
    return [self readStringFromObject:self.jsonObject key:@"Manufacturer"];
}

-(void)setManufacturer:(NSString *)manufacturer {
    [self.jsonObject setValue:manufacturer forKey:@"Manufacturer"];
}

-(NSString*)assetType {
    NSDictionary* assetTypeObj = [self.jsonObject objectForKey:@"HardwareAssetType"];
    if (assetTypeObj != nil) {
        return [self readStringFromObject:assetTypeObj key:@"Name"];
    }else {
        return @"NA";
    }
}

-(NSString*)getAssetTypeDisplayName {
    return [NSString stringWithFormat:@"%@ (%@, %@)", self.assetType, self.model, self.manufacturer];
}

-(BOOL)getHasSerialNumber {
    return self.serialNumber != Nil && self.serialNumber.length != 0;
}

-(BOOL)getHasAssetTag {
    return self.tag != Nil && self.tag.length != 0;
}

-(BOOL)getHasMatchedBarcodeData {
    return self.matchedBarcodeData != Nil && self.matchedBarcodeData.length != 0;
}

-(BOOL)getNeedsBarcodeMatching {
    return !self.matchedBarcodeData;
}

-(BOOL)load:(NSDictionary*)obj {
    BOOL result = [super load:obj];
    return result;
}

-(BOOL)matchesBarcodeWithData:(NSString*)barCodeData {
    if ([barCodeData caseInsensitiveCompare:self.tag] == NSOrderedSame ||
        [barCodeData caseInsensitiveCompare:self.serialNumber] == NSOrderedSame) {
        return YES;
    }
    return NO;
}

-(void)appendToNotes:(NSString*)notes {
    NSString* existingNotes = [self readStringFromObject:self.originalJson key:@"Notes"];
    if ([existingNotes length] == 0) {
        existingNotes = notes;
    }
    else {
        existingNotes = [NSString stringWithFormat:@"%@\r\n%@", existingNotes, notes];
    }
    [self.jsonObject setValue:existingNotes forKeyPath:@"Notes"];
}

-(NSString*)loanedDate {
    return [self readStringFromObject:self.jsonObject key:@"LoanedDate"];
}

-(void)setLoanedDate:(NSString *)date {
    [self.jsonObject setValue:date forKeyPath:@"LoanedDate"];
}

-(NSString*)loanReturnedDate {
    return [self readStringFromObject:self.jsonObject key:@"LoanReturnedDate"];
}

-(void)setLoanReturnedDate:(NSString*)date {
    [self.jsonObject setValue:date forKeyPath:@"LoanReturnedDate"];
}

@end

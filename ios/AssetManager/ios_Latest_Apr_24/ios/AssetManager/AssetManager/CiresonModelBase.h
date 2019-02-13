
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
//  CiresonModelBase.h
//  AssetManager
//

#import <Foundation/Foundation.h>

@interface CiresonModelBase : NSObject

@property(nonatomic, strong) NSString* displayName;
@property(nonatomic, strong) NSString* objectId;
@property(nonatomic, strong, readonly) NSString* objectStatus;
@property(nonatomic, strong) NSString* classTypeId;
@property(nonatomic, strong) NSMutableArray *nameRelationship;
//@property(nonatomic, strong) NSString* HALocationRelationID;
//@property(nonatomic, strong) NSString* HAOrganisationRelationID;
//@property(nonatomic, strong) NSString* HACostCenterRelationID;
//@property(nonatomic, strong) NSString* HAPrimaryUserRelationID;
//@property(nonatomic, strong) NSString* HAPurchaseOrderRelationID;
//@property(nonatomic, strong) NSString* HAVendorRelationID;
//@property(nonatomic, strong) NSString* HACatelogRelationID;
//@property(nonatomic, strong) NSString* HAOwnedByRelationID;
@property (copy, atomic)NSMutableDictionary* originalJson;

// json representing the object
@property (nonatomic, strong) NSMutableDictionary* jsonObject;

-(BOOL)load:(NSDictionary*)obj;

//revert to the original state that the object was created with.
-(void)reset;
-(void)setObjectIdToNull; // this is to make sure that the json serialization gets null

-(NSString*)readStringFromObject:(NSDictionary*) obj key:(NSString*)key;
-(NSNumber*)readNumberFromObject:(NSDictionary*)obj key:(NSString*)key;

+(BOOL)containsObjectInArray:(NSArray*)arr object:(CiresonModelBase*)obj;
+(void)removeObjectWithSameIdInArray:(NSArray*)arr obj:(CiresonModelBase*)obj;

@end

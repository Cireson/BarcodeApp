
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
//  PurchaseOrder.m
//  AssetManager
//

#import "PurchaseOrder.h"

@implementation PurchaseOrder

@synthesize orderNumber, totalCost, purchaseOrderDate, purchaseOrderStatus, associatedAssets;

-(BOOL)load:(NSDictionary*)obj {
    BOOL result = [super load:obj];
    if (result) {
        self.associatedAssets = Nil; // reset the current asset list.
        NSArray* assets = [obj objectForKey:@"Source_HardwareAssetHasPurchaseOrder"];
        if (Nil != assets && [assets count] > 0) {
            NSMutableArray* tempAssets = [[NSMutableArray alloc] init];
            for(NSDictionary* assetObj in assets) {
                Asset* asset = [[Asset alloc] init];
                if ([asset load:assetObj]) {
                    [tempAssets addObject:asset];
                }
            }
            self.associatedAssets = tempAssets;
        }
        result = YES;
    }
    return result;
}

+(PurchaseOrder*)loadFromDict:(NSDictionary*)dict {
    PurchaseOrder* po = [[PurchaseOrder alloc] init];
    if ([po load:dict])
        return po;
    return Nil;
}

+(NSArray*)loadFromArray:(NSArray*)arr {
    if (arr == Nil) return Nil;
    NSMutableArray* tmpArr = [[NSMutableArray alloc] init];
    for(NSDictionary* poObj in arr) {
        PurchaseOrder* po = [PurchaseOrder loadFromDict:poObj];
        if (po != Nil) {
            [tmpArr addObject:po];
        }
    }
    return tmpArr;
}

-(NSString*)orderNumber {
    return [self readStringFromObject:self.jsonObject key:@"PurchaseOrderNumber"];
}

-(NSNumber*)totalCost {
    return [self readNumberFromObject:self.jsonObject key:@"Amount"];
}

-(NSString*)displayName {
    return [self readStringFromObject:self.jsonObject key:@"DisplayName"];
}

-(NSString*)purchaseOrderDate {
    return [self readStringFromObject:self.jsonObject key:@"PurchaseOrderDate"];
}

-(NSString*)purchaseOrderStatus {
    NSDictionary* statusObj = [self.jsonObject objectForKey:@"PurchaseOrderStatus"];
    return [self readStringFromObject:statusObj key:@"Name"];
}

-(void)updateAssets {
    
    NSMutableArray* jsonArray = [[NSMutableArray alloc] init];
    
    // create json array
    for (Asset* a in self.associatedAssets) {
        [jsonArray addObject:a.jsonObject];
    }
    
    // because this is a contained object we also need to udpate the current json
    [self.jsonObject setValue:jsonArray forKey:@"Source_HardwareAssetHasPurchaseOrder"];
}

@end

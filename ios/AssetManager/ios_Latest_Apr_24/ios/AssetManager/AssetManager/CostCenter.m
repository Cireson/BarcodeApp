
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
//  CostCenter.m
//  AssetManager
//

#import "CostCenter.h"

@implementation CostCenter

-(BOOL)load:(NSDictionary *)obj {
    BOOL result = [super load:obj];
    return result;
}

+(NSArray*)loadFromArray:(NSArray*)costCenters {
    if (costCenters == Nil) return Nil;
    NSMutableArray* tmpArr = [[NSMutableArray alloc] init];
    for(NSDictionary* orgObj in costCenters) {
        CostCenter* org = [CostCenter loadFromDict:orgObj];
        if (org != Nil) {
            [tmpArr addObject:org];
        }
    }
    return tmpArr;
}

+(CostCenter*)loadFromDict:(NSDictionary*)dict {
    CostCenter* cc = [[CostCenter alloc] init];
    if ([cc load:dict])
        return cc;
    return Nil;
}

-(NSString*)displayName {
    return [self readStringFromObject:self.jsonObject key:@"DisplayName"];
}

-(NSString*)fullName {
    return [self readStringFromObject:self.jsonObject key:@"FullName"];
}

-(NSString*)name {
    return [self readStringFromObject:self.jsonObject key:@"Name"];
}


@end

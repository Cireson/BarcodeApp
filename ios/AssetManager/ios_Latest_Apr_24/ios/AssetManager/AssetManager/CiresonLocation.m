
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
//  CiresonLocation.m
//  AssetManager
//

#import "CiresonLocation.h"

@implementation CiresonLocation

-(BOOL)load:(NSDictionary *)obj {
    BOOL result = [super load:obj];
    return result;
}

+(CiresonLocation*)loadFromDict:(NSDictionary*)dict {
    CiresonLocation* l = [[CiresonLocation alloc] init];
    if ([l load:dict])
        return l;
    return Nil;
}

+(NSArray*)loadFromArray:(NSArray*)arr {
    if (arr == Nil) return Nil;
    NSMutableArray* tmpArr = [[NSMutableArray alloc] init];
    for(NSDictionary* locationObj in arr) {
        CiresonLocation* loc = [CiresonLocation loadFromDict:locationObj];
        if (loc != Nil) {
            [tmpArr addObject:loc];
        }
    }
    return tmpArr;
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


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
//  CiresonUser.m
//  AssetManager
//

#import "CiresonUser.h"

@implementation CiresonUser

-(BOOL)load:(NSDictionary *)obj {
    BOOL result = [super load:obj];
    return result;
}

+(NSArray*)loadFromArray:(NSArray*)users {
    if (users == Nil) return Nil;
    NSMutableArray* tmpArr = [[NSMutableArray alloc] init];
    for(NSDictionary* orgObj in users) {
        CiresonUser* org = [CiresonUser loadFromDict:orgObj];
        if (org != Nil) {
            [tmpArr addObject:org];
        }
    }
    return tmpArr;
}

+(CiresonUser*)loadFromDict:(NSDictionary*)dict {
    CiresonUser* u = [[CiresonUser alloc] init];
    if ([u load:dict])
        return u;
    return Nil;
}

-(NSString*)displayName {
    return [self readStringFromObject:self.jsonObject key:@"DisplayName"];
}

-(NSString*)firstName {
    return [self readStringFromObject:self.jsonObject key:@"FirstName"];
}

-(NSString*)lastName {
    return [self readStringFromObject:self.jsonObject key:@"LastName"];
}

-(NSString*)domain {
    return [self readStringFromObject:self.jsonObject key:@"Domain"];
}




@end

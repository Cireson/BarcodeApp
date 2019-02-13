
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
//  CiresonModelBase.m
//  AssetManager
//

#import "CiresonModelBase.h"


@implementation CiresonModelBase

@synthesize jsonObject, originalJson;

// helper method
+(BOOL)containsObjectInArray:(NSArray*)arr object:(CiresonModelBase*)obj {
    for (CiresonModelBase* o in arr) {
        if ([o.objectId isEqualToString:obj.objectId]) {
            return YES;
        }
    }
    return NO;
}

+(void)removeObjectWithSameIdInArray:(NSMutableArray*)arr obj:(CiresonModelBase*)obj{
    
    CiresonModelBase* match = Nil;
    for (CiresonModelBase* o in arr) {
        if ([o.objectId isEqualToString:obj.objectId]) {
            match = o;
            break;
        }
    }
    
    if (match) {
        [arr removeObject:match];
    }
}

-(BOOL)load:(NSDictionary *)obj {
    if (Nil == obj) {
        return NO;
    }
    if (obj == Nil) {
        return NO;
    }
    if ([[obj allKeys] count] == 0) {
        return NO;
    }
    
    self.jsonObject = [NSMutableDictionary dictionaryWithDictionary:obj];
    self.originalJson = self.jsonObject;
    return YES;
}

-(BOOL) loadFromArray:(NSArray*)arr {
    if (arr == Nil) {
        return NO;
    }
    return YES;
}

-(NSString*)displayName {
    NSLog(@"%@", @"override in the derived classes");
    return @"";
}

-(NSString*)objectId {
    return [self readStringFromObject:self.jsonObject key:@"BaseId"];
}

-(void)setObjectId:(NSString *)objId {
    [self.jsonObject setValue:objId forKey:@"BaseId"];
}

-(void)setObjectIdToNull {
    [self.jsonObject setValue:[NSNull null] forKey:@"BaseId"];
}

-(void)setJsonObject:(NSMutableDictionary *)jo {
    jsonObject = jo;
}


-(NSString*)readStringFromObject:(NSDictionary*) obj key:(NSString*)key {
    const id nul = [NSNull null];
    NSString* strValue = @"";
    if (obj != nil) {
        id val = [obj objectForKey:key];
        if (val != Nil && val != nul) {
            strValue = val;
        }
    }
    return strValue;
}

-(NSNumber*)readNumberFromObject:(NSDictionary*)obj key:(NSString*)key {
    const id nul = [NSNull null];
    NSNumber* numberVal = Nil;
    id val = [obj objectForKey:key];
    if (val != Nil && val != nul) {
        numberVal = val;
    }
    else {
        numberVal = [NSNumber numberWithInteger:0];
    }
    return numberVal;
}

-(NSString*)getClassTypeId {
    return [self readStringFromObject:self.jsonObject key:@"ClassTypeId"];
}
-(void)setClassTypeId:(NSString *)classTypeId {
    [self.jsonObject setValue:classTypeId forKey:@"ClassTypeId"];
}
-(void) setNameRelationship :(NSMutableArray *)nameRelationDict{
    [self.jsonObject setValue:nameRelationDict forKey:@"NameRelationship"];
}

// If we make changes to the model and we want to revert that. this can come in handy for various reasons.
//
-(void)reset {
    self.jsonObject = self.originalJson;
    [self load:jsonObject];
}

@end

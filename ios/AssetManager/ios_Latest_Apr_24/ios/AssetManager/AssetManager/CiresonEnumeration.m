
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
//  CiresonEnumeration.m
//  AssetManager
//

#import "CiresonEnumeration.h"

@implementation CiresonEnumeration

@synthesize isRoot;

+(CiresonEnumeration*) loadFromServiceResponse:(id)response {
    if (response == Nil) {
        NSLog(@"Response for enumeration is Nil, check the id");
        return Nil;
    }
    CiresonEnumeration* root = [[CiresonEnumeration alloc] init];
    root.isRoot = YES;
    NSArray* topLevel = (NSArray*)response;
    NSMutableArray* topLevelArr = [[NSMutableArray alloc] init];
    for(NSDictionary* topLevelNode in topLevel) {
        CiresonEnumeration* e = [[CiresonEnumeration alloc] init];
        if ([e load:topLevelNode]) {
            [topLevelArr addObject:e];
        }
    }
    root.children = topLevelArr;
    return root;
}

-(BOOL)hasChildren {
    if (isRoot)
        return YES;
    return[[self.jsonObject objectForKey:@"HasChildren"]  boolValue];
}

-(NSString*)displayName {
    return [self readStringFromObject:self.jsonObject key:@"Text"];
}

-(NSString*)enumId {
    return [self readStringFromObject:self.jsonObject key:@"Id"];
}

-(BOOL)load:(NSDictionary *)obj {
    BOOL result = [super load:obj];
    if (result) {
        if (self.hasChildren) {
            NSMutableArray* tmp = [[NSMutableArray alloc] init];
            NSArray* enumNodes = [obj objectForKey:@"EnumNodes"];
            for(NSDictionary* childObj in enumNodes) {
                CiresonEnumeration* child = [[CiresonEnumeration alloc] init];
                [child load:childObj];
                [tmp addObject:child];
            }
            self.children = tmp;
        }
        result = YES;
    }
    return result;
}


-(NSString*)objectId {
    return self.enumId;
}

@end

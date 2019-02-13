
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
//  AddAssetsData.m
//  AssetManager
//

#import "AddAssetsData.h"

@implementation AddAssetsData
@synthesize foundAssets, notFoundAssets, useSerialNumber, createdAssets;

-(id)init {
    if ( self = [super init] ) {
        self.foundAssets = [[NSMutableArray alloc] init];
        self.notFoundAssets = [[NSMutableArray alloc] init];
    }
    return self;
}

-(BOOL)findBarcode:(NSString *)barcode {
    for (Asset* a in self.foundAssets) {
        if ([a matchesBarcodeWithData:barcode]){
            return YES;
        }
    }
    return NO;
}

-(void)reset {
    self.foundAssets = [[NSMutableArray alloc] init];
    self.notFoundAssets = [[NSMutableArray alloc] init];
    self.useSerialNumber = NO;
}

@end


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
//  GenericCiresonCall.m
//  AssetManager
//

#import "GenericCiresonApiCall.h"

@interface GenericCiresonApiCall()
@end

@implementation GenericCiresonApiCall
@synthesize params, typeId;

-(void)call {
    [self makeRequestWithParameters:params];
}

-(void)parseSuccessResponse:(id)responseObject {
    if (responseObject != Nil) {

    }
}

@end

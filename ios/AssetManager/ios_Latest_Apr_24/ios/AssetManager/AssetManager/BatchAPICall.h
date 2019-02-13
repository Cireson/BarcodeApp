
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
//  BatchAPICall.h
//  AssetManager
//

#import <Foundation/Foundation.h>

@interface BatchAPICall : CiresonRequestBase

@property(nonatomic, strong)NSMutableArray* responses;
@property(nonatomic, strong)NSMutableArray* errors;
@property(nonatomic, assign, readonly, getter=getCheckFinished)BOOL checkFinished;
@property(nonatomic, readonly, getter=getHasErrors)BOOL hasErrors;
@property(nonatomic, readonly, getter=getPartialSuccess)BOOL partialSuccess;
-(void)addRequest:(GenericCiresonApiCall*)callRequest;
-(void)call;

@end

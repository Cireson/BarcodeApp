
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
//  BatchAPICall.m
//  AssetManager
//

#import "BatchAPICall.h"

@interface BatchAPICall ()
@property(nonatomic, strong)NSMutableArray* batchedCalls;
@property(nonatomic, assign)NSInteger responseCount;
@end

@implementation BatchAPICall

@synthesize batchedCalls, responseCount;

-(id)init {
    if (self = [super init]) {
        self.batchedCalls = [[NSMutableArray alloc] init];
        self.responses = [[NSMutableArray alloc] init];
        self.errors = [[NSMutableArray alloc] init];
    }
    return self;
}

-(void)addRequest:(GenericCiresonApiCall *)callRequest {
    __weak BatchAPICall* weakSelf = self;
    [self.batchedCalls  addObject:callRequest];
    callRequest.successHandler = ^(NSDictionary* response){
        [weakSelf.responses addObject:response];
        weakSelf.responseCount = weakSelf.responseCount + 1;
        self.successHandler(response);
    };
    callRequest.errorHandler = ^(NSError* error){
        [weakSelf.errors addObject:error];
        weakSelf.responseCount = weakSelf.responseCount + 1;
        self.errorHandler(error);
    };
}

-(void)call {
    for (GenericCiresonApiCall* c in self.batchedCalls) {
        [c call];
    }
}

-(BOOL)getCheckFinished {
    return self.batchedCalls.count == self.responseCount;
}

-(BOOL)getHasErrors {
    return self.errors.count > 0;
}

-(BOOL)getPartialSuccess {
    return self.errors.count > 0 && self.errors.count < self.batchedCalls.count;
}

@end


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
//  LoginRequest.m
//  AssetManager
//

#import "LoginRequest.h"
#import "UIAlertView+Cireson.h"

@interface LoginRequest ()
+(NSMutableDictionary*) getParamsForLoginWithUserName:(NSString*)userName
                                             password:(NSString*)password;
@end

@implementation LoginRequest

+(NSMutableDictionary*) getParamsForLoginWithUserName:(NSString*)userName password:(NSString*)password {
    /*    {"Action": "Login",
     "UserName": "Gary",
     "Password": "G307407g"} */
    NSDictionary* params =
        @{@"UserName": userName,
          @"Password": password};
    
    NSMutableDictionary* mutable = [NSMutableDictionary dictionaryWithDictionary:params];
    return mutable;
}

-(void)parseSuccessResponse:(id) responseObject {
    NSDictionary* response = (NSDictionary*)responseObject;
    [User login:[response valueForKey:@"token"]];
}

-(void)onError:(NSError*) error {
}


-(void)loginWithUrl:(NSString*)url userName:(NSString*)userName password:(NSString*)password {
    NSDictionary* loginParams = [LoginRequest getParamsForLoginWithUserName:userName password:password];
    NSLog(@"%@", loginParams);
    self.method = LoginMethod;
    [[CiresonApiService sharedService] setBaseUrl:url];
    [self makeRequestWithParameters:loginParams];
}

     

@end

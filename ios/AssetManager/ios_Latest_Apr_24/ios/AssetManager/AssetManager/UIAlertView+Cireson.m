
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
//  UIAlertView+Cireson.m
//  AssetManager
//

#import "UIAlertView+Cireson.h"
#import "UIAlertView+AFNetworking.h"

@implementation UIAlertView (Cireson)

+(UIAlertView*)showErrorWithMsg: (NSString*)errorMsg {
    UIAlertView* view = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil) message:errorMsg delegate:nil cancelButtonTitle:NSLocalizedString(@"OK", nil) otherButtonTitles:Nil, nil];
    [view show];
    return view;
}

+(UIAlertView*)showSuccessWithMsg: (NSString*)successMsg {
    UIAlertView* view = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Success", nil) message:successMsg delegate:nil cancelButtonTitle:NSLocalizedString(@"OK", nil) otherButtonTitles:Nil, nil];
    [view show];
    return view;
}

+(UIAlertView*)showWarningWithMsg: (NSString*)msg {
    UIAlertView* view = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Warning", nil) message:msg delegate:nil cancelButtonTitle:NSLocalizedString(@"Yes", nil) otherButtonTitles:@"Cancel", nil];
    [view show];
    return view;
}

+(UIAlertView*)showErrorFromService:(NSError*)error {
    NSString *title, *message;
    if (error.localizedDescription && (error.localizedRecoverySuggestion || error.localizedFailureReason)) {
        title = error.localizedDescription;
        
        if (error.localizedRecoverySuggestion) {
            message = error.localizedRecoverySuggestion;
        } else {
            message = error.localizedFailureReason;
        }
    } else if (error.localizedDescription) {
        title = NSLocalizedStringFromTable(@"Error", @"Network", @"Fallback Error Description");
        message = error.localizedDescription;
    } else {
        title = NSLocalizedStringFromTable(@"Error", @"AFNetworking", @"Fallback Error Description");
        message = [NSString stringWithFormat:NSLocalizedStringFromTable(@"%@ Error: %lu", @"AFNetworking", @"Fallback Error Failure Reason Format"), error.domain, (NSUInteger)error.code];
    }

    UIAlertView* view = [[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:NSLocalizedString(@"OK", nil) otherButtonTitles:Nil, nil];
    [view show];
    return view;
}

+(UIAlertView*)showErrorForBatchCall:(NSArray *)errors partialSuccess:(BOOL)partial {
    NSString* title = NSLocalizedString(@"Error", nil);
    NSString* message = @"";
    
    for(NSError* error in errors) {
        message = [NSString stringWithFormat:@"%@\r\n%@", message, error.localizedDescription != Nil ? error.localizedDescription : @""];
    }
    
    UIAlertView* view = Nil;
    message = [NSString stringWithFormat:@"%@ - %@", @"There were one or more errors - ", message];
    if (!partial) {
        view = [[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:NSLocalizedString(@"OK", nil) otherButtonTitles:Nil, nil];
    }
    else {
        view = [[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:NSLocalizedString(@"OK", nil) otherButtonTitles:@"Ignore", nil];
    }
    [view show];
    return view;
}

@end

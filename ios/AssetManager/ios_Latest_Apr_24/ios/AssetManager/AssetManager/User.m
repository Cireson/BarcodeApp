
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
//  User.m
//  AssetManager
//

#import "User.h"
#import "AppDelegate.h"

@implementation User

@dynamic token;
@dynamic url;

+(User*)createNew {
    User* user = [NSEntityDescription insertNewObjectForEntityForName:@"User" inManagedObjectContext:[CiresonModelHelper managedObjectContext]];
    return user;
}

+(void)deleteAllUsers{
    NSManagedObjectContext* moc = [CiresonModelHelper managedObjectContext];
    
    NSFetchRequest * allUsers = [[NSFetchRequest alloc] init];
    [allUsers setEntity:[NSEntityDescription entityForName:@"User" inManagedObjectContext:moc]];
    [allUsers setIncludesPropertyValues:NO]; //only fetch the managedObjectID
    
    NSError * error = nil;
    NSArray * users = [moc executeFetchRequest:allUsers error:&error];
    
    //error handling goes here
    for (NSManagedObject * user in users) {
        [moc deleteObject:user];
    }
    NSError *saveError = nil;
    [moc save:&saveError];
    //more error handling here
}

+(User*)loggedInUser {
    NSManagedObjectContext* moc = [CiresonModelHelper managedObjectContext];
    
    NSFetchRequest * allUsers = [[NSFetchRequest alloc] init];
    [allUsers setEntity:[NSEntityDescription entityForName:@"User" inManagedObjectContext:moc]];
    NSError * error = nil;
    NSArray * users = [moc executeFetchRequest:allUsers error:&error];
    
    for (User * user in users) {
        if (user.url == nil) {
            return nil;
        }
        return user;
    }
    return nil;
}

+(void)logout {
    [User deleteAllUsers]; // if there are any background processing, we will need to cancel them, right now we don't
}

+(void)login:(NSString*)token {
    [User deleteAllUsers];
    User* user = [User createNew];
    user.token = token;
    user.url = [[CiresonApiService sharedService] baseUrl];
    NSError* error2 = Nil;
    [[user managedObjectContext] save:&error2];
}

@end

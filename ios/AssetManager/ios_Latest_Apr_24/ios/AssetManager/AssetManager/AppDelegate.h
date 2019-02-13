
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
//  AppDelegate.h
//  AssetManager
//

#import <UIKit/UIKit.h>

#define DEFAULTS [NSUserDefaults standardUserDefaults]

#define MOC [((AppDelegate*)[UIApplication sharedApplication].delegate) managedObjectContext]

#define APPDELEGATE ((AppDelegate*)[UIApplication sharedApplication].delegate)

typedef NS_ENUM(NSInteger, CurrentWorkFlow) {
    ReceiveAssetByPO,
    InventoryAudit,
    SwapAsset,
    EditAsset,
    DisposeAsset,
    AddAssets
};

@interface AppDelegate : UIResponder <UIApplicationDelegate>
@property (strong, nonatomic) UIWindow *window;

@property (readonly, strong, nonatomic) NSManagedObjectContext *managedObjectContext;
@property (readonly, strong, nonatomic) NSManagedObjectModel *managedObjectModel;
@property (readonly, strong, nonatomic) NSPersistentStoreCoordinator *persistentStoreCoordinator;
@property (nonatomic) int flag;

@property (nonatomic, assign) CurrentWorkFlow workFlow;


- (void)saveContext;
- (NSURL *)applicationDocumentsDirectory;
- (void)printCurrentWorkflow; // debugging aid


@end

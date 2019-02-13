
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
//  UIHelper.h
//  AssetManager
//

#import <Foundation/Foundation.h>

@interface UIHelper : NSObject

+(NSString*) stringForMonth:(NSInteger)month;

+(void)launchStatusPickerWithEnumerations:(CiresonEnumeration*)enumeration
                  withSelectedEnumeration:(NSArray*)selectedValues
                       multiselectionMode:(BOOL)multiSelection
                     parentViewController:(UIViewController*)parentViewController;

+(void)launchLocationPickerWithLocations:(NSArray*)locations
                       selectedLocations:(NSArray*)selectedLocations
                    parentViewController:(UIViewController*)parentViewController;

+(void)launchOrganizationPickerWithOrganizations:(NSArray*)organizations
                           selectedOrganizations:(NSArray*)selectedOrganizations
                            parentViewController:(UIViewController*)parentViewController;

+(void)launchCostCenterPickerWithCostCenters:(NSArray*)costCenters
                          selectedCostCenter:(NSArray*)selectedCostCenters
                        parentViewController:(UIViewController*)parentViewController;

+(void)launchCustodianPickerWithSelectedUser:(NSArray*)selectedUsers
                        parentViewController:(UIViewController*)parentViewController;

+(void)launchDatePickerWithDate:(NSDate*)date
               forAssetProperty:(AssetProperties)prop
           parentViewController:(UIViewController*)parentViewController;

+(void)launchLongTextPicker:(NSString*)textValue
                       mode:(LongTextViewMode)mode
              maxCharacters:(NSUInteger)maxChars
                  viewTitle:(NSString*)title
       parentViewController:(UIViewController*)parentViewController;

+(void)launchTextPicker:(NSString *)editTextValue textPickerItemMode:(TextPickerItemMode)mode viewTitle:(NSString*)viewTitle parentViewController:(UIViewController *)parentViewController;

+(void)launchBluetoothPicker:(UIViewController*)parentViewController;

+(void)launchPurchaseOrdersViewWithPurchaseOrders:(NSArray*)purchaseOrders
                             parentViewController:(UIViewController*)parentViewController title:(NSString*)title workflowData:(WorkflowData*)data;

+(void)launchHomeviewAfterLogin:(UIViewController*)parentViewController;

+(void)launchSearchPurchaseOrdersViewController:(UIViewController*)parentViewController;

+(void)launchHomeWithParentViewController:(UIViewController*)parentViewController;

+(void)launchLoginWithParentViewController:(UIViewController*)parentViewController;

+(void)launchEditAssetPropertiesWithParentViewController:(UIViewController *)parentViewController
                                            workflowData:(WorkflowData*)data;

+(void)launchAssetViewControllerWithParentViewController:(UIViewController*)parentViewController
                                                   title:(NSString*)title
                                                subtitle:(NSString*)subtitle
                                            workflowData:(WorkflowData*)wfd;

+(void)launchSettingsViewController:(UIViewController*)parentViewController;

+(void)launchScannerViewControllerWithParentViewController:(UIViewController*)parentViewController
                                                  workflowData:(WorkflowData*)workflowData;

+(void)launchReviewEmailViewControllerWithAssets:(NSArray *)assets
                            parentViewController:(UIViewController *)parentViewController;

+(void)presentLoginWithParentViewController:(UIViewController *)parentViewController;

@end


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
//  UIHelper.m
//  AssetManager
//

#import "UIHelper.h"
#import "EnumPickerViewController.h"
#import "LocationViewController.h"
#import "OrganizationViewController.h"
#import "CostCenterViewController.h"
#import "ChooseDateViewController.h"
#import "UserPickerViewController.h"
#import "POViewController.h"
#import "LoginViewController.h"
#import "AssetPropertiesViewController.h"
#import "ScannerViewController.h"
#import "AssetViewController.h"
#import "ReviewEmailViewController.h"
#import "TextPickerViewController.h"
#import "BluetoothDeviceViewController.h"
#import "LongTextViewController.h"

@implementation UIHelper
+(NSString*)stringForMonth:(NSInteger)month {
    NSArray* months = [NSArray arrayWithObjects:
                       NSLocalizedString(@"Error", Nil),
                       NSLocalizedString(@"January",Nil),
                       NSLocalizedString(@"February",Nil),
                       NSLocalizedString(@"March",Nil),
                       NSLocalizedString(@"April",Nil),
                       NSLocalizedString(@"May",Nil),
                       NSLocalizedString(@"June",Nil),
                       NSLocalizedString(@"July",Nil),
                       NSLocalizedString(@"August",Nil),
                       NSLocalizedString(@"September",Nil),
                       NSLocalizedString(@"October",Nil),
                       NSLocalizedString(@"November",Nil),
                       NSLocalizedString(@"December",Nil),
                       Nil];
    return [months objectAtIndex:month];
}

+(void)launchStatusPickerWithEnumerations:(CiresonEnumeration*)enumeration
                 withSelectedEnumeration:(NSArray*)selectedValues
                       multiselectionMode:(BOOL)multiSelection
                    parentViewController:(UIViewController*)parentViewController {
    UIStoryboard* storyboard = parentViewController.storyboard;
    EnumPickerViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"statusVCStoryboard"];
    vc.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
    vc.enumeration = enumeration;
    vc.selectedValues = selectedValues;
    if ([[parentViewController class] conformsToProtocol:@protocol(EnumPickerViewControllerDelegate)]) {
        vc.delegate = (id<EnumPickerViewControllerDelegate>)parentViewController;
    }
    vc.multiSelectionMode = multiSelection;
    [parentViewController presentViewController:vc animated:YES completion:Nil];
    
}

+(void)launchLocationPickerWithLocations:(NSArray*)locations
                       selectedLocations:(NSArray*)selectedLocations
                    parentViewController:(UIViewController*)parentViewController {
    
    UIStoryboard* storyboard = parentViewController.storyboard;
    LocationViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"locationVCStoryboard"];
    vc.selectedLocations = selectedLocations;
    vc.locations = locations;
    if ([[parentViewController class] conformsToProtocol:@protocol(LocationViewControllerDelegate)]) {
        vc.delegate = (id<LocationViewControllerDelegate>)parentViewController;
    }
    [parentViewController presentViewController:vc animated:YES completion:Nil];
}

+(void)launchOrganizationPickerWithOrganizations:(NSArray*)organizations
                           selectedOrganizations:(NSArray*)selectedOrganizations
                            parentViewController:(UIViewController*)parentViewController {
    
    UIStoryboard* storyboard = parentViewController.storyboard;
    OrganizationViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"organizationVCStoryboard"];
    vc.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
    vc.selectedOrganizations = selectedOrganizations;
    vc.organizations = organizations;
    if ([[parentViewController class] conformsToProtocol:@protocol(OrganizationViewControllerDelegate)]) {
        vc.delegate = (id<OrganizationViewControllerDelegate>)parentViewController;
    }
    [parentViewController presentViewController:vc animated:YES completion:Nil];

}
+(void)launchCostCenterPickerWithCostCenters:(NSArray*)costCenters
                          selectedCostCenter:(NSArray*)selectedCostCenters
                        parentViewController:(UIViewController*)parentViewController {
    UIStoryboard* storyboard = parentViewController.storyboard;
    CostCenterViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"costCenterVCStoryboard"];
    vc.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
    vc.selectedCostCenters = selectedCostCenters;
    vc.costCenters = costCenters;
    if ([[parentViewController class] conformsToProtocol:@protocol(CostCenterViewControllerDelegate)]) {
        vc.delegate = (id<CostCenterViewControllerDelegate>)parentViewController;
    }
    [parentViewController presentViewController:vc animated:YES completion:Nil];
}

+(void)launchCustodianPickerWithSelectedUser:(NSArray*)selectedUsers
                        parentViewController:(UIViewController*)parentViewController {
    UIStoryboard* storyboard = parentViewController.storyboard;
    UserPickerViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"userVCStoryboard"];
    vc.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
    vc.selectedUsers = selectedUsers;
    if ([[parentViewController class] conformsToProtocol:@protocol(UserPickerViewControllerDelegate)]) {
        vc.delegate = (id<UserPickerViewControllerDelegate>)parentViewController;
    }
    [parentViewController presentViewController:vc animated:YES completion:Nil];
}

+(void)launchDatePickerWithDate:(NSDate*)date
               forAssetProperty:(AssetProperties)prop
           parentViewController:(UIViewController*)parentViewController {
    UIStoryboard* storyboard = parentViewController.storyboard;
    ChooseDateViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"chooseDateVCStoryboard"];
    vc.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
    vc.selectedDate = date;
    vc.affectedProperty = prop;
    if ([[parentViewController class] conformsToProtocol:@protocol(ChooseDateViewControllerDelegate)]) {
        vc.delegate = (id<ChooseDateViewControllerDelegate>)parentViewController;
    }
    [parentViewController presentViewController:vc animated:YES completion:Nil];
}

+(void)launchLongTextPicker:(NSString*)textValue
                       mode:(LongTextViewMode)mode
              maxCharacters:(NSUInteger)maxChars
                  viewTitle:(NSString*)title
       parentViewController:(UIViewController*)parentViewController
 {
    UIStoryboard* storyboard = parentViewController.storyboard;
    LongTextViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"longtextViewController"];
    vc.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
    vc.editTextValue = textValue;
    vc.viewTitle = title;
     vc.maxCharacters = maxChars;
    if ([[parentViewController class] conformsToProtocol:@protocol(LongTextViewControllerDelegate)]) {
        vc.delegate = (id<LongTextViewControllerDelegate>)parentViewController;
    }
    [parentViewController presentViewController:vc animated:YES completion:Nil];
}

+(void)launchBluetoothPicker:(UIViewController*)parentViewController {
    UIStoryboard* storyboard = parentViewController.storyboard;
    BluetoothDeviceViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"bluetoothVCStoryboard"];
    vc.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
    if ([[parentViewController class] conformsToProtocol:@protocol(BluetoothDeviceViewControllerDelegate)]) {
        vc.delegate = (id<BluetoothDeviceViewControllerDelegate>)parentViewController;
    }
    [parentViewController presentViewController:vc animated:YES completion:Nil];
}

+(void)launchPurchaseOrdersViewWithPurchaseOrders:(NSArray*)purchaseOrders
                             parentViewController:(UIViewController*)parentViewController
                                            title:(NSString*)title
                                         workflowData:(WorkflowData*)workflowData{
    UIStoryboard* storyboard = parentViewController.storyboard;
    POViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"searchResultVCStoryboard"];
    if (purchaseOrders == Nil) {
        vc.loadAllFromService = YES; // if we don't have purchase orders, we need to load in the view.
    }
    vc.workflowData = workflowData;
    vc.initialTitle = title;
    vc.purchaseOrders = purchaseOrders;
    vc.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
    [parentViewController.navigationController pushViewController:vc animated:YES];
}

+(void)launchSearchPurchaseOrdersViewController:(UIViewController*)parentViewController {
    UIStoryboard* storyboard = parentViewController.storyboard;
    UIViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"searchAssetVCStoryboard"];
    vc.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
    [parentViewController.navigationController pushViewController:vc animated:YES];
}

+(void)launchHomeWithParentViewController:(UIViewController *)parentViewController {
    [parentViewController.navigationController popToRootViewControllerAnimated:YES];
}

+(void)launchHomeviewAfterLogin:(UIViewController *)parentViewController {
    UIStoryboard* storyboard = parentViewController.storyboard;
    UINavigationController* nvc = parentViewController.navigationController;
    NSMutableArray* allVcs = [[NSMutableArray alloc] initWithArray:nvc.viewControllers];
    [allVcs  removeObjectAtIndex:0];
    UIViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"navigation"];
    [allVcs addObject:vc];
    [nvc setViewControllers:allVcs animated:YES];
}

+(void)launchLoginWithParentViewController:(UIViewController *)parentViewController {
    UIStoryboard* storyboard = parentViewController.storyboard;
    UIViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"login"];
    vc.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
    NSArray* vcs = [NSArray arrayWithObject:vc];
    [parentViewController.navigationController setViewControllers:vcs animated:YES];
}

+(void)presentLoginWithParentViewController:(UIViewController *)parentViewController {
    UIStoryboard* storyboard = parentViewController.storyboard;
    UIViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"login"];
    vc.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
    APPDELEGATE.flag = 1;
    [parentViewController presentViewController:vc animated:YES completion:nil];
    
}


+(void)launchEditAssetPropertiesWithParentViewController:(UIViewController *)parentViewController
                                            workflowData:(WorkflowData*)data {
    UIStoryboard* storyboard = parentViewController.storyboard;
    AssetPropertiesViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"editingPOVCStoryboard"];
    vc.workflowData = data;
    [parentViewController.navigationController pushViewController:vc animated:YES];
}

+(void)launchAssetViewControllerWithParentViewController:(UIViewController*)parentViewController
                                                   title:(NSString*)title
                                                subtitle:(NSString*)subtitle
                                          workflowData:(WorkflowData*)wfd{
    UIStoryboard* storyboard = [parentViewController storyboard];
	AssetViewController *vc = [storyboard instantiateViewControllerWithIdentifier:@"assetViewController"];
    vc.viewTitle = title;
    vc.viewSubtitle = subtitle;
    vc.workflowData = wfd;
    if (APPDELEGATE.workFlow == ReceiveAssetByPO || APPDELEGATE.workFlow == InventoryAudit) {
        if ([parentViewController conformsToProtocol:@protocol(AssetViewControllerDelegate)]) {
            vc.delegate = (id<AssetViewControllerDelegate>)parentViewController;
        }
        vc.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
        [parentViewController presentViewController:vc animated:YES completion:Nil];
    }
    else {
        [parentViewController.navigationController pushViewController:vc animated:YES];
    }
}


+(void)launchSettingsViewController:(UIViewController *)parentViewController {
    UIStoryboard* storyboard = parentViewController.storyboard;
    UIViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"settingsVCStoryboard"];
    vc.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
    [parentViewController.navigationController pushViewController:vc animated:YES];
}

+(void)launchScannerViewControllerWithParentViewController:(UIViewController*)parentViewController
                                                  workflowData:(WorkflowData*) workflowData
{
    UIStoryboard* storyboard = parentViewController.storyboard;
	ScannerViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"scanner"];
    vc.workflowData = workflowData;
	[parentViewController.navigationController pushViewController:vc animated:YES];
}

+(void)launchReviewEmailViewControllerWithAssets:(NSArray *)assets
                            parentViewController:(UIViewController *)parentViewController {
    UIStoryboard* storyboard = parentViewController.storyboard;
    ReviewEmailViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"reviewEmailVCStoryboard"];
    vc.inventoriedAssets = [assets objectAtIndex:0];
    vc.notInventoriedAssets = [assets objectAtIndex:1];
    vc.noMatchAssets = [assets objectAtIndex:2];
    [parentViewController.navigationController pushViewController:vc animated:YES];

}

+(void)launchTextPicker:(NSString *)editTextValue textPickerItemMode:(TextPickerItemMode)mode viewTitle:(NSString*)viewTitle parentViewController:(UIViewController *)parentViewController {
    UIStoryboard* storyboard = parentViewController.storyboard;
    TextPickerViewController* vc = [storyboard instantiateViewControllerWithIdentifier:@"textPickerVCStoryboard"];
    vc.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
    vc.editTextValue = editTextValue;
    vc.textItemTypeMode = mode;
    vc.viewTitle = viewTitle;
    if ([[parentViewController class] conformsToProtocol:@protocol(TextPickerViewControllerDelegate)]) {
        vc.delegate = (id<TextPickerViewControllerDelegate>)parentViewController;
    }
    [parentViewController presentViewController:vc animated:YES completion:Nil];
}


@end

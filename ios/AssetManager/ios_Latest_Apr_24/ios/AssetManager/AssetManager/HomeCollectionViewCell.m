
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
//  HomeCollectionViewCell.m
//  AssetManager
//

#import "HomeCollectionViewCell.h"
#import "UIAlertView+Cireson.h"
#import "AppDelegate.h"
#import "AssetPropertiesViewController.h"
#import "UIHelper.h"

@interface HomeCollectionViewCell ()
-(IBAction)performReceiveAssets:(id)sender;
-(IBAction)performSwapAssets:(id)sender;
-(IBAction)performDisposeAssets:(id)sender;
-(IBAction)performInventory:(id)sender;
-(IBAction)performEditAssets:(id)sender;
-(IBAction)configureSettings:(id)sender;
@end

@implementation HomeCollectionViewCell

@synthesize parent;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
    }
    return self;
}


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

-(void)performReceiveAssets:(id)sender {
    APPDELEGATE.workFlow = ReceiveAssetByPO;
    [UIHelper launchSearchPurchaseOrdersViewController:self.parent];
}


-(void)performSwapAssets:(id)sender {
    APPDELEGATE.workFlow = SwapAsset;
    SwapAssetData* data = [[SwapAssetData alloc] init];
    [UIHelper launchScannerViewControllerWithParentViewController:self.parent workflowData:data];
}

-(void)performDisposeAssets:(id)sender {
    APPDELEGATE.workFlow = DisposeAsset;
    [UIHelper launchScannerViewControllerWithParentViewController:self.parent workflowData:Nil];
}

-(void)performInventory:(id)sender {
    APPDELEGATE.workFlow = InventoryAudit;
    [UIHelper launchEditAssetPropertiesWithParentViewController:self.parent workflowData:Nil];
}

-(void)performEditAssets:(id)sender {
    APPDELEGATE.workFlow = EditAsset;
    [UIHelper launchScannerViewControllerWithParentViewController:self.parent workflowData:Nil];
}

-(void)configureSettings:(id)sender {
    [UIHelper launchSettingsViewController:self.parent];
}

@end

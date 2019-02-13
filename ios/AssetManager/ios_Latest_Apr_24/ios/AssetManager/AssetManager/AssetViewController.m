
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
//  AssetViewController.m
//  AssetManager
//

#import "AssetViewController.h"
#import "UIColor+Cireson.h"
#import "AssetTableViewCell.h"
#import "AppDelegate.h"
#import "Asset.h"
#import "ScannerViewController.h"
#import "UIImage+Cireson.h"
#import "UIAlertView+Cireson.h"
#import "UIHelper.h"
@interface AssetViewController () <UIAlertViewDelegate>
@property (nonatomic, strong) Asset* selectedAsset;
@property (strong, nonatomic) NSArray* assets;
@property (nonatomic,strong) NSString* barcodeToMatch;
@end

@implementation AssetViewController

@synthesize tableView;
@synthesize titleLabel, subTitleLabel;
@synthesize assets, selectedAsset, viewTitle, viewSubtitle, delegate, barcodeToMatch;
@synthesize doneButtonName, cancelButtonName;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
	// Do any additional setup after loading the view.
	self.tableView.backgroundColor = [UIColor backgroundColor];
	self.view.backgroundColor = [UIColor backgroundColor];
    self.tableView.separatorColor = [UIColor separatorColor];
    self.titleLabel.text = self.viewTitle;
    self.subTitleLabel.text = self.viewSubtitle;
    [self.doneButtonName setTitle:[self doneButtonTitle]];
    [self.cancelButtonName setTitle:[self cancelButtonTitle]];
    self.tableView.allowsMultipleSelection = NO;
    if (APPDELEGATE.workFlow == EditAsset || APPDELEGATE.workFlow == DisposeAsset) {
        self.tableView.allowsSelection = NO;
    }
    
    if (APPDELEGATE.workFlow == ReceiveAssetByPO) {
        ReceiveAssetsData* data = (ReceiveAssetsData*)self.workflowData;
        self.barcodeToMatch = data.barcodeToMatch;
        self.assets = data.assetsToChooseFrom;
    }
    else if (APPDELEGATE.workFlow == EditAsset) {
        EditAssetsData* data = (EditAssetsData*)self.workflowData;
        self.assets = data.assetsToEdit;
    }
    else if (APPDELEGATE.workFlow == DisposeAsset) {
        DisposeAssetsData* data = (DisposeAssetsData*)self.workflowData;
        self.assets = data.assetsToDispose;
    }
}

-(NSString *)doneButtonTitle {
    return (APPDELEGATE.workFlow == ReceiveAssetByPO || APPDELEGATE.workFlow == InventoryAudit) ? @"Done" : @"Next";
}

-(NSString *)cancelButtonTitle {
    return (APPDELEGATE.workFlow == ReceiveAssetByPO || APPDELEGATE.workFlow == InventoryAudit) ? @"Cancel" : @"Back";
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return [self.assets count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *CellIdentifier = @"ChooseAssetViewCell";
    
	AssetTableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:CellIdentifier];
	
    if(cell == nil){
        cell = [[AssetTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
	}
    
    Asset* asset = [self.assets objectAtIndex:indexPath.row];
    if (APPDELEGATE.workFlow == ReceiveAssetByPO || APPDELEGATE.workFlow == InventoryAudit || APPDELEGATE.workFlow == SwapAsset) {
        if (asset == selectedAsset) {
            cell.accessoryType = UITableViewCellAccessoryCheckmark;
//            [cell setSelected:YES];
        }
        else {
            cell.accessoryType = UITableViewCellAccessoryNone;
        }
    }
    else {
        cell.accessoryType = UITableViewCellAccessoryNone;
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
	cell.backgroundColor = [UIColor backgroundColor];
    if (![asset.name isEqualToString:@""]) {
        cell.assetNameLabel.text = [NSString stringWithFormat:@"%@",asset.name];
    }else{
        cell.assetNameLabel.text = [NSString stringWithFormat:NSLocalizedString(@"N/A", nil)];
    }
    if (![asset.manufacturer isEqualToString:@""]) {
        cell.assetTypeLabel.text = [NSString stringWithFormat:@"%@",asset.manufacturer];
    }else{
        cell.assetTypeLabel.text = [NSString stringWithFormat:NSLocalizedString(@"N/A", nil)];
    }
    if (![asset.manufacturer isEqualToString:@""]) {
        cell.assetModeLabel.text = [NSString stringWithFormat:@"%@",asset.model];
    }else{
        cell.assetModeLabel.text = [NSString stringWithFormat:NSLocalizedString(@"N/A", nil)];
    }
     if (![asset.serialNumber isEqualToString:@""]) {
         cell.currentSerialNo.text = [NSString stringWithFormat:NSLocalizedString(@"%@", nil), asset.serialNumber];
     }else{
         cell.currentSerialNo.text = [NSString stringWithFormat:NSLocalizedString(@"N/A", nil)];
     }
    if (![asset.tag isEqualToString:@""]) {
        cell.currentTag.text = [NSString stringWithFormat:NSLocalizedString(@"%@", nil), asset.tag];
    }else{
        cell.currentTag.text = [NSString stringWithFormat:NSLocalizedString(@"N/A", nil)];
    }
    
    return cell;
}

-(void)tableView:(UITableView *)tv didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    self.selectedAsset = [self.assets objectAtIndex:indexPath.row];
    UITableViewCell *cell = [tv cellForRowAtIndexPath:indexPath];
    cell.accessoryType = UITableViewCellAccessoryCheckmark;
}

-(void)tableView:(UITableView *)tableView didDeselectRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [self.tableView cellForRowAtIndexPath:indexPath];
    cell.accessoryType = UITableViewCellAccessoryNone;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
	return 120.0f;
}

-(void)cancelButtonClicked:(id)sender {
    if (self.delegate) {
        [self.delegate didCancelSelection];
    }
    if (APPDELEGATE.workFlow == ReceiveAssetByPO || APPDELEGATE.workFlow == InventoryAudit) {
        [self dismissViewControllerAnimated:YES completion:nil];
    }
    else {
        [self.navigationController popViewControllerAnimated:YES];
    }
}

-(void)doneButtonClicked:(id)sender {
    [APPDELEGATE printCurrentWorkflow];
    if (APPDELEGATE.workFlow == ReceiveAssetByPO || APPDELEGATE.workFlow == InventoryAudit) {
        if (self.selectedAsset == Nil) {
            [UIAlertView showErrorWithMsg:NSLocalizedString(@"Please select the asset to match the barcode with",Nil)];
            return;
        }
        UIAlertView* alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Confirmation", Nil) message:NSLocalizedString(@"Which property do you want to assign the barcode to?", Nil) delegate:self cancelButtonTitle:NSLocalizedString(@"Cancel", Nil) otherButtonTitles:NSLocalizedString(@"Asset tag", Nil), NSLocalizedString(@"Serial number", Nil), nil];
        [alertView show];
    }
    else if (APPDELEGATE.workFlow == SwapAsset) {
        if (self.selectedAsset == Nil) {
            [UIAlertView showErrorWithMsg:NSLocalizedString(@"Please select an asset to swap", Nil)];
            return;
        }
        //NSArray *selAsset = [NSArray arrayWithObject:self.selectedAsset];
        [UIHelper launchEditAssetPropertiesWithParentViewController:self workflowData:self.workflowData];
    }
    else {
        [UIHelper launchEditAssetPropertiesWithParentViewController:self workflowData:self.workflowData];
    }
}

#pragma UIAlertViewDelegate
// Called when a button is clicked. The view will be automatically dismissed after this call returns
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    
    NSAssert(APPDELEGATE.workFlow == ReceiveAssetByPO, @"Logic needs to be modified if used from a different workflow"); // update the logic if called from a different place.
    
    if (buttonIndex == 0) {
        return; // cancel
    }
    
    MatchedBarCodeAction action = MatchedBarCodeActionAssignToSerialNumber;
    if (buttonIndex == 1) {
        action = MatchedBarCodeActionAssignToTag;
    }
    self.selectedAsset.matchedBarcodeData = self.barcodeToMatch;
    self.selectedAsset.matchedBarCodeAction = action;
    [self.delegate didFinishSelectingAsset:self.selectedAsset];
    [self dismissViewControllerAnimated:YES completion:Nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

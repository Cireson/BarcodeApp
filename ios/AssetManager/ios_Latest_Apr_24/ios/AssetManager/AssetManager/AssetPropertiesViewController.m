
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
//  EditAssetPropertiesViewController.m
//  AssetManager
//

#import "AssetPropertiesViewController.h"
#import "UIColor+Cireson.h"
#import "UIImage+Cireson.h"
#import "UIAlertView+Cireson.h"
#import "EnumPickerViewController.h"
#import "UIHelper.h"
#import "LocationViewController.h"
#import "OrganizationViewController.h"
#import "CostCenterViewController.h"
#import "ChooseDateViewController.h"
#import "UserPickerViewController.h"
#import "AppDelegate.h"
#import "UIAlertView+Cireson.h"
#import "AssetViewController.h"
#import "TextPickerViewController.h"
#import "LongTextViewController.h"

@interface AssetPropertiesViewController () <EnumPickerViewControllerDelegate, LocationViewControllerDelegate
, OrganizationViewControllerDelegate, CostCenterViewControllerDelegate, ChooseDateViewControllerDelegate, UserPickerViewControllerDelegate, TextPickerViewControllerDelegate, UIAlertViewDelegate, LongTextViewControllerDelegate>
{
    BOOL keepPrimaryUser;
    BOOL keepCustodian;
    UIAlertView* _saveErrorAlertView;
    NSDateFormatter* _dateReaderFormatter;
}

@property (strong, nonatomic) NSDate*   selectedDisposedDate;
@property (strong, nonatomic) NSString* assetName;
@property (strong, nonatomic) NSString* assetSerialNumber;
@property (strong, nonatomic) NSString* assetTagNumber;
@property (strong, nonatomic) NSString* disposalReferenceNumber;
@property (strong, nonatomic) NSString* manufacturer;
@property (strong, nonatomic) NSString* model;
@property (strong, nonatomic, readonly) NSMutableArray* assetsToEdit;
@property (strong, nonatomic) NSString* selectedNotes;
@property (strong, nonatomic) NSDate*   selectedLoanReturnedDate;
@property (strong, nonatomic) NSDate*   selectedLoanedDate;




// loaded and cached temporarily from service, we can't cache users because they could be huge (FYI).
@property (strong, nonatomic) CiresonEnumeration* loadedStatus; // this is the root enumeration value
@property (strong, nonatomic) NSArray* loadedLocations;
@property (strong, nonatomic) NSArray* loadedOrganizations;
@property (strong, nonatomic) NSArray* loadedCostCenters;

-(void)saveForEditAssets;
-(void)saveForReceiveAssets;
-(void)performSave:(NSArray*)assets;
@end

@implementation AssetPropertiesViewController

@synthesize titleLabel, numberOfAssetsLabel, toolBar;
@synthesize saveButtonName;
@synthesize tableView;
@synthesize loadedStatus, loadedLocations, loadedOrganizations, loadedCostCenters;
@synthesize titleArray, selectedStatuses, selectedLocations, selectedOrganizations, selectedCostCenters, selectedReceivedDate, selectedCustodians, selectedDisposedDate, manufacturer, model, selectedLoanReturnedDate, selectedLoanedDate, selectedNotes;
@synthesize workflowData;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
	// Do any additional setup after loading the view.
	self.view.backgroundColor = [UIColor backgroundColor];
    self.tableView.backgroundColor = [UIColor backgroundColor];
    [self setupTitle];
    [self.saveButtonName setTitle:[self barButtonTitle]];
    
    NSInteger count = 0;
    if (APPDELEGATE.workFlow != AddAssets) {
	    count = [self.assetsToEdit count];
    }
    else {
        AddAssetsData* data = (AddAssetsData*)workflowData;
        count = data.notFoundAssets.count;
    }
    self.numberOfAssetsLabel.text = [NSString stringWithFormat:@"%ld", (long)count];

    [self setupTableViewDataSource];
	UITapGestureRecognizer * tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTap:)];
	[tap setCancelsTouchesInView:NO];
    [self.view addGestureRecognizer:tap];
    self.tableView.separatorColor = [UIColor separatorColor];
    
    // special case for swap asset where status needs to be set to the one
    // of the second asset
    if (APPDELEGATE.workFlow == SwapAsset) {
        if (![self isFirstSwapAsset]) {
            SwapAssetData* swapData = (SwapAssetData*)self.workflowData;
            [swapData.secondAsset setOriginalStatusFromAsset:swapData.firstAsset];
        }
    }
    
    _dateReaderFormatter = [[NSDateFormatter alloc] init];
    [_dateReaderFormatter setDateFormat:@"yyyy-MM-dd'T'HH:mm:ss"];
    [_dateReaderFormatter setLocale:[NSLocale systemLocale]];

}

// Utility function to figure out if we are working on first asset
// in the swap workflow
-(BOOL)isFirstSwapAsset {
    if (APPDELEGATE.workFlow == SwapAsset) {
        SwapAssetData* swapData = (SwapAssetData*)self.workflowData;
        if (swapData.secondAsset == Nil) {
            return YES;
        }
    }
    return NO;
}

-(void)setupTableViewDataSource {
    if (APPDELEGATE.workFlow == SwapAsset) {
        if ([self isFirstSwapAsset]) {
            self.titleArray =  [NSArray arrayWithObjects:
                                [NSNumber numberWithInt:APStatus],
                                [NSNumber numberWithInt:APLocation],
                                [NSNumber numberWithInt:APOrganization],
                                [NSNumber numberWithInt:APCostCenter],
                                [NSNumber numberWithInt:APLoanReturnedDate],
                                [NSNumber numberWithInt:APNotes],
                                Nil
                                ];
        }
        else {
            self.titleArray = [NSArray arrayWithObjects:
                                [NSNumber numberWithInt:APStatus],
                                [NSNumber numberWithInt:APLocation],
                                [NSNumber numberWithInt:APOrganization],
                                [NSNumber numberWithInt:APCustodian],
                                [NSNumber numberWithInt:APCostCenter],
                                [NSNumber numberWithInt:APLoanedDate],
                                [NSNumber numberWithInt:APNotes],
                                Nil
                                ];
        }
    }
    else if (APPDELEGATE.workFlow == DisposeAsset) {
        self.titleArray = [NSArray arrayWithObjects:
                            [NSNumber numberWithInt:APDisposalDate],
                            [NSNumber numberWithInt:APDisposalReferenceNumber],
                            [NSNumber numberWithInt:APClearPrimaryUser],
                            [NSNumber numberWithInt:APClearCustodian],
                            Nil];
    }
    else if (APPDELEGATE.workFlow == ReceiveAssetByPO || APPDELEGATE.workFlow == InventoryAudit || APPDELEGATE.workFlow == EditAsset) {
        self.titleArray = [NSArray arrayWithObjects:
                           [NSNumber numberWithInt:APStatus],
                           [NSNumber numberWithInt:APLocation],
                           [NSNumber numberWithInt:APOrganization],
                           [NSNumber numberWithInt:APCustodian],
                           [NSNumber numberWithInt:APCostCenter],
                           [NSNumber numberWithInt:APReceivedDate],
                           Nil];
    }
    else if (APPDELEGATE.workFlow == AddAssets) {
        self.titleArray = [NSMutableArray arrayWithObjects:
                           [NSNumber numberWithInt:APManufacturer],
                           [NSNumber numberWithInt:APModel],
                           [NSNumber numberWithInt:APStatus],
                           [NSNumber numberWithInt:APLocation],
                           [NSNumber numberWithInt:APOrganization],
                           [NSNumber numberWithInt:APCustodian],
                           [NSNumber numberWithInt:APCostCenter],
                           [NSNumber numberWithInt:APReceivedDate],
                           Nil];
    }
}

-(NSString *)barButtonTitle {
    if (APPDELEGATE.workFlow == SwapAsset) {
        SwapAssetData* data = (SwapAssetData*)self.workflowData;
        if (data.secondAsset != Nil) {
            return @"Save";
        }
        else {
            return @"Next";
        }
    }
    return (APPDELEGATE.workFlow == ReceiveAssetByPO || APPDELEGATE.workFlow == EditAsset|| APPDELEGATE.workFlow == DisposeAsset || APPDELEGATE.workFlow == AddAssets) ? @"Save" : @"Next";
}

-(void)setupTitle {
    if (APPDELEGATE.workFlow == InventoryAudit) {
        self.numberOfAssetsLabel.hidden = YES;
        [self.titleLabel setTextAlignment:NSTextAlignmentLeft];
        [self.titleLabel setText:NSLocalizedString(@"Search for Assets", Nil)];
    }
    else if (APPDELEGATE.workFlow == DisposeAsset) {
        [self.titleLabel setText:NSLocalizedString(@"Assets Being Disposed", Nil)];
    }
    else if (APPDELEGATE.workFlow == SwapAsset) {
        
        [self.titleLabel setText:NSLocalizedString(@"Assets Being Edited", Nil)];
    }
    else if (APPDELEGATE.workFlow == AddAssets) {
        [self.titleLabel setText:NSLocalizedString(@"Assets Being Created", Nil)];
    }
}

-(void)handleTap:(UIEvent*)event {
    [self.view endEditing:YES];
}

-(NSMutableArray*)assetsToEdit {
    if (APPDELEGATE.workFlow == ReceiveAssetByPO) {
        ReceiveAssetsData* data = (ReceiveAssetsData*)self.workflowData;
        NSMutableArray* list = [[NSMutableArray alloc] init];
        for (Asset* a in data.purchaseOrder.associatedAssets) {
            if (a.hasMatchedBarcodeData && a.matchedBarCodeAction != MatchedBarCodeActionNone) {
                [list addObject:a];
            }
        }
        return list;
    }
    else if (APPDELEGATE.workFlow == EditAsset) {
        EditAssetsData* data = (EditAssetsData*)self.workflowData;
        return [NSMutableArray arrayWithArray:data.assetsToEdit];
    }
    else if (APPDELEGATE.workFlow == DisposeAsset) {
        DisposeAssetsData* data = (DisposeAssetsData*)self.workflowData;
        return [NSMutableArray arrayWithArray:data.assetsToDispose];
    }
    else if (APPDELEGATE.workFlow == SwapAsset) {
        SwapAssetData* data = (SwapAssetData*)self.workflowData;
        return [NSMutableArray arrayWithObjects:data.firstAsset, data.secondAsset, Nil];
    }
    else if (APPDELEGATE.workFlow == AddAssets) {
        AddAssetsData* data = (AddAssetsData*)self.workflowData;
        return
            data.createdAssets == Nil ?
                [[NSMutableArray alloc] init] : [[NSMutableArray alloc] initWithArray:data.createdAssets];
    }
    return Nil;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.titleArray.count;
}

-(NSString*)getDisplayText:(NSArray*)objects {
    if (objects == Nil || objects.count == 0) {
        return @"";
    }
    
    NSString* displayText = @"";
    
    for (CiresonModelBase* obj in objects) {
        if ([displayText  isEqual: @""]) {
            displayText = obj.displayName;
        }
        else {
            displayText = [displayText stringByAppendingString:[NSString stringWithFormat:@", %@", obj.displayName]];
        }
    }
    return displayText;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:@"EditAssetPropertyCell" forIndexPath:indexPath];
    UIImage *cellImage = [[UIImage alloc]init];
    long row = indexPath.row;
    NSString* detailText = @"";
    AssetProperties propertyType = [[self.titleArray objectAtIndex:row] integerValue];
    if (APPDELEGATE.workFlow == ReceiveAssetByPO || APPDELEGATE.workFlow == EditAsset || APPDELEGATE.workFlow == InventoryAudit || APPDELEGATE.workFlow == AddAssets) {
        if (propertyType == APStatus) {
            cellImage = [UIImage imageNamed:@"status"]; // using costcenter image for status
            detailText = [self getDisplayText:self.selectedStatuses];
        }
        else if (propertyType == APLocation) {
            cellImage = [UIImage imageNamed:@"location"];
            detailText = [self getDisplayText:self.selectedLocations];
        }
        else if (propertyType == APOrganization) {
            cellImage = [UIImage imageNamed:@"organization"];
            detailText = [self getDisplayText:self.selectedOrganizations];
        }
        else if (propertyType == APCustodian) {
            cellImage = [UIImage imageNamed:@"custodian"];
            detailText = [self getDisplayText:self.selectedCustodians];
        }
        else if (propertyType == APCostCenter) {
            cellImage = [UIImage imageNamed:@"costCenter"];
            detailText = [self getDisplayText:self.selectedCostCenters];
        }
        else if (propertyType == APReceivedDate) {
            cellImage = [UIImage imageNamed:@"receivedDate"];
            if (self.selectedReceivedDate) {
                NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
                [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
                [dateFormatter setTimeStyle:NSDateFormatterNoStyle];
                
                NSString *formattedDateString = [dateFormatter stringFromDate:self.selectedReceivedDate];
                detailText = formattedDateString;
            }
        }
        else if (propertyType == APManufacturer) {
            cellImage = [UIImage imageNamed:@"manufacturer"];
            detailText = self.manufacturer;
        }
        else if (propertyType == APModel) {
            cellImage = [UIImage imageNamed:@"model"];
            detailText = self.model;
        }
    }
    else if (APPDELEGATE.workFlow == SwapAsset) {
        SwapAssetData* data = (SwapAssetData*)self.workflowData;
        Asset* asset = Nil;
        if ([self isFirstSwapAsset]) {
            asset = data.firstAsset;
        }
        else {
            asset = data.secondAsset;
        }
        
        if (propertyType == APStatus) {
            cellImage = [UIImage imageNamed:@"status"]; // using costcenter image for status
            if (self.selectedStatuses != Nil) {
                detailText = [self getDisplayText:self.selectedStatuses];
            }
            else {
                detailText = asset.statusName;
            }
        }
        else if (propertyType == APLocation) {
            cellImage = [UIImage imageNamed:@"location"];
            if (self.selectedLocations != Nil) {
                detailText = [self getDisplayText:self.selectedLocations];
            }
            else {
                detailText = asset.locationName;
            }
        }
        else if (propertyType == APOrganization) {
            cellImage = [UIImage imageNamed:@"organization"];
            if (self.selectedLocations != Nil) {
                detailText = [self getDisplayText:self.selectedOrganizations];
            }
            else {
                detailText = asset.organizationName;
            }
        }
        else if (propertyType == APCustodian) {
            cellImage = [UIImage imageNamed:@"custodian"];
            if (self.selectedCustodians != Nil) {
                detailText = [self getDisplayText:self.selectedCustodians];
            }
            else {
                detailText = asset.custodianName;
            }
            
        }
        else if (propertyType == APCostCenter) {
            cellImage = [UIImage imageNamed:@"costCenter"];
            if (self.selectedCostCenters != Nil) {
                detailText = [self getDisplayText:self.selectedCostCenters];
            }
            else {
                detailText = asset.costCenterName;
            }
        }
        else if (propertyType == APLoanReturnedDate) {
            cellImage = [UIImage imageNamed:@"receivedDate"];
            NSDate* date = Nil;
            if (self.selectedLoanReturnedDate) {
                date = self.selectedLoanReturnedDate;
            }
            else {
                if (asset.loanReturnedDate) {
                    date = [_dateReaderFormatter dateFromString:asset.loanReturnedDate];
                }
            }
            if (date != Nil) {
                NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
                [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
                [dateFormatter setTimeStyle:NSDateFormatterNoStyle];
                
                NSString *formattedDateString = [dateFormatter stringFromDate:date];
                detailText = formattedDateString;
            }
        }
        else if (propertyType == APLoanedDate) {
            cellImage = [UIImage imageNamed:@"receivedDate"];
            NSDate* date = Nil;
            if (self.selectedLoanedDate) {
                date = self.selectedLoanedDate;
            }
            else {
                if (asset.loanedDate) {
                    date = [_dateReaderFormatter dateFromString:asset.loanedDate];
                }
            }
            if (date != Nil) {
                NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
                [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
                [dateFormatter setTimeStyle:NSDateFormatterNoStyle];
                
                NSString *formattedDateString = [dateFormatter stringFromDate:date];
                detailText = formattedDateString;
            }
        }
        else if (propertyType == APNotes) {
            cellImage = [UIImage imageNamed:@"Notes"];
            detailText = self.selectedNotes;
        }
    }
    else if (APPDELEGATE.workFlow == DisposeAsset) {
        if (propertyType == APDisposalDate) {
            cellImage = [UIImage imageNamed:@"receivedDate"]; // using receivedDate image for Disposal Date
            if (self.selectedDisposedDate) {
                NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
                [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
                [dateFormatter setTimeStyle:NSDateFormatterNoStyle];
                
                NSString *formattedDateString = [dateFormatter stringFromDate:self.selectedDisposedDate];
                detailText = formattedDateString;
            }
        }
        else if (propertyType == APDisposalReferenceNumber) {
            cellImage = [UIImage imageNamed:@"disposalRef"]; // using costcenter image for Disposal Reference Number
            detailText = self.disposalReferenceNumber;
        }
        else if (propertyType == APClearPrimaryUser) {
            cellImage = [UIImage imageNamed:@"custodian"]; // using costcenter image for Clear Primary User
            UIButton *switchView = [[UIButton alloc] initWithFrame:CGRectMake(40, 80, 40.0f, 30.0f)];
            [switchView setTintColor:[UIColor whiteColor]];
            [switchView addTarget:self
                       action:@selector(updateSwitchAtIndexPath:)
             forControlEvents:UIControlEventTouchUpInside];
            [switchView setTitle:@"NO" forState:UIControlStateNormal];
            switchView.selected = NO;
            switchView.tag = 1;
            cell.accessoryView = switchView;
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
        }
        else if (propertyType == APClearCustodian) {
            cellImage = [UIImage imageNamed:@"custodian"]; // using costcenter image for Clear Custodian
            UIButton *switchView = [[UIButton alloc] initWithFrame:CGRectMake(40, 80, 40.0f, 30.0f)];
            [switchView setTintColor:[UIColor whiteColor]];
            [switchView addTarget:self
                           action:@selector(updateSwitchAtIndexPath:)
                 forControlEvents:UIControlEventTouchUpInside];
            [switchView setTitle:@"NO" forState:UIControlStateNormal];
            switchView.selected = NO;
            switchView.tag = 2;
            cell.accessoryView = switchView;
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
        }
    }
    
    // Configure the cell...
    [cell.imageView setImage:cellImage];
    cell.textLabel.font = [UIFont systemFontOfSize:15.0];
    AssetProperties properties = (AssetProperties)[[self.titleArray objectAtIndex:indexPath.row] integerValue];
    NSString* title = [self getTitleForProperty:properties];
    cell.textLabel.text = title;
    cell.backgroundColor = [UIColor backgroundColor];
    cell.detailTextLabel.numberOfLines = 0;
    cell.detailTextLabel.text = detailText;
    [cell.detailTextLabel sizeToFit];
    [cell layoutIfNeeded];
    return cell;
}

-(NSString*)getTitleForProperty:(AssetProperties)p {
    switch(p) {
        case APStatus:
            return @"Status";
        case APLocation:
            return @"Location";
        case APCostCenter:
            return @"Cost Center";
        case APOrganization:
            return @"Organization";
        case APCustodian:
            return @"Custodian";
        case APLoanReturnedDate:
            return @"Loan Return Date";
        case APNotes:
            return @"Append Notes";
        case APLoanedDate:
            return @"Loaned Date";
        case APDisposalDate:
            return @"Disposal Date";
        case APDisposalReferenceNumber:
            return @"Disposal Reference";
        case APClearPrimaryUser:
            return @"Clear Primary User";
        case APClearCustodian:
            return @"Clear Custodian";
        case APReceivedDate:
            return @"Received Date";
        case APModel:
            return @"Model";
        case APManufacturer:
            return @"Manufacturer";
        case APAssetName:
            return @"Asset Name";
        case APAssetTag:
            return @"Asset Tag";
        case APSerialNumber:
            return @"Serial Number";
        default:
            return @"Unknown";
    }
}

- (void)updateSwitchAtIndexPath:(UIButton *)switchView {
    
    NSLog(@"switch tag: %ld", (long)switchView.tag);
    if (switchView.tag == 1) {
        if ([switchView isSelected] == NO) {
            keepPrimaryUser = NO;
            NSLog(@"Clear Primary User switch on");
            [switchView setTitle:@"YES" forState:UIControlStateNormal];
            switchView.selected = YES;
        }
        else {
            if ([switchView isSelected] == YES) {
                keepPrimaryUser = YES;
                NSLog(@"Clear Primary User switch off");
                [switchView setTitle:@"NO" forState:UIControlStateNormal];
                switchView.selected = NO;
            }
        }
    }
    else {
        if ([switchView isSelected] == NO) {
            keepCustodian = NO;
            NSLog(@"Clear Custodian switch on");
            [switchView setTitle:@"YES" forState:UIControlStateNormal];
            switchView.selected = YES;
        }
        else {
            if ([switchView isSelected] == YES) {
                keepCustodian = YES;
                NSLog(@"Clear Custodian switch off");
                [switchView setTitle:@"NO" forState:UIControlStateNormal];
                switchView.selected = NO;
            }
        }
    }
}

-(void)showStatusPicker {
    if (self.loadedStatus == Nil) {
        __block MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.mode = MBProgressHUDModeIndeterminate;
        hud.labelText = NSLocalizedString(@"Loading status...", Nil);
        
        GenericCiresonApiCall* enumRequest = [[CiresonApiService sharedService] getEnumeration:HardwareAsset_Status_Enumeration_Id];
        
        __block __weak AssetPropertiesViewController* me = self;
        
        enumRequest.successHandler = ^(id response){
            [MBProgressHUD hideHUDForView:me.view animated:YES];
            me.loadedStatus = [CiresonEnumeration loadFromServiceResponse:response];
            [UIHelper launchStatusPickerWithEnumerations:me.loadedStatus withSelectedEnumeration:self.selectedStatuses multiselectionMode:(APPDELEGATE.workFlow == InventoryAudit) parentViewController:me];
        };
        enumRequest.errorHandler = ^(NSError* error){
            [MBProgressHUD hideHUDForView:me.view animated:YES];
            [UIAlertView showErrorFromService:error];
            [self logout];
        };
        
        [enumRequest call];
        
    }else {
        [UIHelper launchStatusPickerWithEnumerations:self.loadedStatus withSelectedEnumeration:self.selectedStatuses multiselectionMode:(APPDELEGATE.workFlow == InventoryAudit) parentViewController:self];
    }
}

-(void)showLocationPicker {
    if (self.loadedLocations == Nil) {
        __block MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.mode = MBProgressHUDModeIndeterminate;
        hud.labelText = NSLocalizedString(@"Loading locations...", Nil);
        
        GenericCiresonApiCall* locationRequest = [[CiresonApiService sharedService] getLocations];
        __block __weak AssetPropertiesViewController* me = self;
        
        locationRequest.successHandler = ^(id response){
            [MBProgressHUD hideHUDForView:me.view animated:YES];
            self.loadedLocations = [CiresonLocation loadFromArray:response];
            [UIHelper launchLocationPickerWithLocations:self.loadedLocations selectedLocations:self.selectedLocations parentViewController:me];
        };
        locationRequest.errorHandler = ^(NSError* error){
            [MBProgressHUD hideHUDForView:me.view animated:YES];
            [UIAlertView showErrorFromService:error];
            [self logout];
            
        };
        
        [locationRequest call];
        
    }else {
        [UIHelper launchLocationPickerWithLocations:self.loadedLocations selectedLocations:self.selectedLocations parentViewController:self];
    }
}

-(void) showOrganizationPicker {
    if (self.loadedOrganizations == Nil) {
        __block MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.mode = MBProgressHUDModeIndeterminate;
        hud.labelText = NSLocalizedString(@"Loading organizations...", Nil);
        
        GenericCiresonApiCall* locationRequest = [[CiresonApiService sharedService] getOrganizations];
        __block __weak AssetPropertiesViewController* me = self;
        
        locationRequest.successHandler = ^(id response){
            [MBProgressHUD hideHUDForView:me.view animated:YES];
            self.loadedOrganizations = [Organization loadFromArray:response];
            [UIHelper launchOrganizationPickerWithOrganizations:self.loadedOrganizations selectedOrganizations:self.selectedOrganizations parentViewController:me];
        };
        locationRequest.errorHandler = ^(NSError* error){
            [MBProgressHUD hideHUDForView:me.view animated:YES];
            [UIAlertView showErrorFromService:error];
            [self logout];
        };
        
        [locationRequest call];
        
    }else {
        [UIHelper launchOrganizationPickerWithOrganizations:self.loadedOrganizations selectedOrganizations:self.selectedOrganizations parentViewController:self];
    }
}

-(void)showCustodianPicker {
    [UIHelper launchCustodianPickerWithSelectedUser:self.selectedCustodians parentViewController:self];
}

-(void) showCostCenterPicker {
    if (self.loadedCostCenters == Nil) {
        __block MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.mode = MBProgressHUDModeIndeterminate;
        hud.labelText = NSLocalizedString(@"Loading cost centers...", Nil);
        
        GenericCiresonApiCall* costCenterRequst = [[CiresonApiService sharedService] getCostCenters];
        __block __weak AssetPropertiesViewController* me = self;
        
        costCenterRequst.successHandler = ^(id response){
            [MBProgressHUD hideHUDForView:me.view animated:YES];
            me.loadedCostCenters = [CostCenter loadFromArray:response];
            [UIHelper launchCostCenterPickerWithCostCenters:self.loadedCostCenters selectedCostCenter:self.selectedCostCenters parentViewController:me];
        };
        costCenterRequst.errorHandler = ^(NSError* error){
            [MBProgressHUD hideHUDForView:me.view animated:YES];
            [UIAlertView showErrorFromService:error];
            [self logout];
        };
        
        [costCenterRequst call];
        
    }else {
        [UIHelper launchCostCenterPickerWithCostCenters:self.loadedCostCenters selectedCostCenter:self.selectedCostCenters parentViewController:self];
    }
}


-(void)showReceivedDatePicker {
    [UIHelper launchDatePickerWithDate:self.selectedReceivedDate
                  forAssetProperty:APReceivedDate
                  parentViewController:self];
}

-(void)showTextPicker:(NSString*)text withMode:(TextPickerItemMode)mode viewTitle:(NSString*)viewTitle {
    [UIHelper launchTextPicker:text textPickerItemMode:mode viewTitle:viewTitle parentViewController:self];
}

-(void)showDisposalDatePicker {
    [UIHelper launchDatePickerWithDate:self.selectedReceivedDate forAssetProperty:APDisposalDate  parentViewController:self];
}

-(void)showLoanReturnedDatePicker {
    [UIHelper launchDatePickerWithDate:self.selectedLoanReturnedDate
                    forAssetProperty:APLoanReturnedDate
                  parentViewController:self];
}

-(void)showLoanedDatePicker {
    [UIHelper launchDatePickerWithDate:self.selectedLoanedDate
        forAssetProperty:APLoanedDate
        parentViewController:self];
}

-(void)showNotesPicker {
    Asset* asset = Nil;
    if (APPDELEGATE.workFlow == SwapAsset) {
        SwapAssetData* swd = (SwapAssetData*)self.workflowData;
        if ([self isFirstSwapAsset]) {
            asset = swd.firstAsset;
        }
        else {
            asset = swd.secondAsset;
        }
    }
    [UIHelper launchLongTextPicker:self.selectedNotes mode:Notes maxCharacters:4000 viewTitle:NSLocalizedString(@"Append to Notes", nil) parentViewController:self];
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSUInteger row = [indexPath row];
//    Asset *asset = [self.assets firstObject];
    Asset* asset = [[Asset alloc] init];
    NSString *pickerText = [[NSString alloc] init];
    
    AssetProperties propertyType = (AssetProperties)[[self.titleArray objectAtIndex:row] integerValue];
    if (propertyType == APStatus) {
        [self showStatusPicker];
    }
    else if (propertyType == APLocation) {
        [self showLocationPicker];
    }
    else if (propertyType == APOrganization) {
        NSLog(@"organization");
        [self showOrganizationPicker];
    }
    else if (propertyType == APCustodian) {
        [self showCustodianPicker];
    }
    else if (propertyType == APCostCenter) {
        [self showCostCenterPicker];
    }
    else if (propertyType == APReceivedDate) {
        [self showReceivedDatePicker];
    }
    else if (propertyType == APAssetName) {
        pickerText = (self.assetName == nil) ? asset.name : self.assetName;
        [self showTextPicker:pickerText withMode:AssetName viewTitle:@"Edit Asset Name"];
    }
    else if (propertyType == APSerialNumber) {
        pickerText = (self.assetSerialNumber == nil) ? asset.serialNumber : self.assetSerialNumber;
        [self showTextPicker:pickerText withMode:AssetSerialNumber viewTitle:@"Edit Asset Serial Number"];
    }
    else if (propertyType == APAssetTag) {
        pickerText = (self.assetTagNumber == nil) ? asset.tag : self.assetTagNumber;
        [self showTextPicker:pickerText withMode:AssetTagNumber viewTitle:@"Edit Asset Tag"];
    }
    else if (propertyType == APDisposalDate) {
        [self showDisposalDatePicker];
    }
    else if (propertyType == APLoanReturnedDate) {
        [self showLoanReturnedDatePicker];
    }
    else if (propertyType == APLoanedDate) {
        [self showLoanedDatePicker];
    }
    else if (propertyType == APNotes) {
        [self showNotesPicker];
    }
    else if (propertyType == APDisposalReferenceNumber) {
        [self showTextPicker:@"" withMode:DisposalReferenceNumber viewTitle:@"Edit Disposal Reference"];
    }
    else if (propertyType == APModel) {
        [self showTextPicker:self.model withMode:Model viewTitle:@"Edit Model"];
    }
    else if (propertyType == APManufacturer) {
        [self showTextPicker:self.manufacturer withMode:Manufacturer viewTitle:@"Edit Manufacturer"];
    }
}

-(void)saveButtonClicked:(id)sender {
    if (APPDELEGATE.workFlow == ReceiveAssetByPO) {
        [self saveForReceiveAssets];
        
    }
    else if (APPDELEGATE.workFlow == InventoryAudit){
        [self searchForAssets];
    }
    else if (APPDELEGATE.workFlow == EditAsset) {
        [self saveForEditAssets];
    }
    else if (APPDELEGATE.workFlow == SwapAsset) {
        if ([self isFirstSwapAsset]) {
            [self saveForSwapAssets];
            [UIHelper launchScannerViewControllerWithParentViewController:self workflowData:self.workflowData];
        }
        else {
            [self saveForSwapAssets];
        }
    }
    else if (APPDELEGATE.workFlow == DisposeAsset) {
        [self saveForDispose];
    }
    else if (APPDELEGATE.workFlow == AddAssets) {
        [self saveForAddAssets];
    }
}

//
// For lack of my ability to come up with a good name for this. This is a
// function that updates asset properties for few different workflows and they
// happen to be the same.
//
-(void)updateAssetProperties {
    NSArray* assets = [self assetsToEdit];
    
    // save status
    if (self.selectedStatuses.count > 0) {
        CiresonEnumeration* status = [self.selectedStatuses firstObject];
        for (Asset* a in assets) {
            [a setStatusId:status.objectId];
        }
    }
    
    
    // save location
    if (self.selectedLocations.count > 0) {
        CiresonLocation* location = [self.selectedLocations firstObject];
        for (Asset* a in assets) {
            [a setLocation:location];
        }
    }
    
    // save organization
    if (self.selectedOrganizations.count > 0) {
        Organization* organization = [self.selectedOrganizations firstObject];
        for (Asset* a in assets) {
            [a setOrganization:organization];
        }
    }
    
    // save custodian
    if (self.selectedCustodians.count > 0) {
        CiresonUser* user = [self.selectedCustodians firstObject];
        for (Asset* a in assets) {
            [a setCustodian:user];
        }
    }
    
    // save cost center
    if (self.selectedCostCenters.count > 0) {
        CostCenter* costCenter = [self.selectedCostCenters firstObject];
        for (Asset* a in assets) {
            [a setCostCenter:costCenter];
        }
    }
    
    // save received dates
    if (self.selectedReceivedDate != Nil) {
        for (Asset* a in assets) {
            [a setReceivedDate:[CiresonRequestBase getDateString:self.selectedReceivedDate]];
        }
    }
}

-(void)saveForSwapAssets {
    
    SwapAssetData* swapData = (SwapAssetData*)self.workflowData;
    
    Asset* asset = Nil;
    if ([self isFirstSwapAsset]) {
        asset = swapData.firstAsset;
        if (self.selectedLoanReturnedDate) {
            [asset setLoanReturnedDate:[CiresonRequestBase getDateString:self.selectedLoanReturnedDate]];
        }
    }
    else {
        asset = swapData.secondAsset;
        if (self.selectedLoanedDate) {
            [asset setLoanedDate:[CiresonRequestBase getDateString:self.selectedLoanedDate]];
        }
    }
    
    //
    // save status
    //
    if (self.selectedStatuses.count > 0) {
        CiresonEnumeration* status = [self.selectedStatuses firstObject];
        [asset setStatusId:status.objectId];
    }
    
    //
    // save location
    //
    if (self.selectedLocations.count > 0) {
        CiresonLocation* location = [self.selectedLocations firstObject];
        [asset setLocation:location];
    }
    
    //
    // save organization
    //
    if (self.selectedOrganizations.count > 0) {
        Organization* organization = [self.selectedOrganizations firstObject];
        [asset setOrganization:organization];
    }
    
    //
    // save custodian
    //
    if (self.selectedCustodians.count > 0) {
        CiresonUser* user = [self.selectedCustodians firstObject];
        [asset setCustodian:user];
    }
    
    //
    // save cost center
    //
    if (self.selectedCostCenters.count > 0) {
        CostCenter* costCenter = [self.selectedCostCenters firstObject];
        [asset setCostCenter:costCenter];
    }
    
    //
    // save notes
    //
    if ([self.selectedNotes length] > 0) {
        [asset appendToNotes:self.selectedNotes];
    }
    
    if (![self isFirstSwapAsset]) {
        NSArray* assetsToSave = [self assetsToEdit];
        __block MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.mode = MBProgressHUDModeIndeterminate;
        hud.labelText = NSLocalizedString(@"Saving Assets...", Nil);
        [self performSave:assetsToSave];
    }
}

-(void)saveForReceiveAssets {
    __block MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.mode = MBProgressHUDModeIndeterminate;
    hud.labelText = NSLocalizedString(@"Saving asset properties...", Nil);

    [self updateAssetProperties];
    
    __weak AssetPropertiesViewController* weakSelf = self;
    GenericCiresonApiCall* saveReceivedDataCall = [[CiresonApiService sharedService] getSaveReceiveAssetsRequest:(ReceiveAssetsData*)self.workflowData];
                                                   
    saveReceivedDataCall.successHandler = ^(id response){
        [MBProgressHUD hideHUDForView:weakSelf.view animated:YES];
        [UIHelper launchHomeWithParentViewController:weakSelf];
    };
    saveReceivedDataCall.errorHandler = ^(NSError* error){
        [MBProgressHUD hideHUDForView:weakSelf.view animated:YES];
        [UIAlertView showErrorFromService:error];
        [self logout];
    };
    
    [saveReceivedDataCall call];
}

-(void)saveForAddAssets {
    __block MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.mode = MBProgressHUDModeIndeterminate;
    hud.labelText = NSLocalizedString(@"Creating New Assets...", Nil);
    AddAssetsData* data = (AddAssetsData*)self.workflowData;
    NSMutableArray* assets = [[NSMutableArray alloc] init];
    
    NSDictionary *vendor = [NSDictionary dictionaryWithObjectsAndKeys:
                                      @"Target_HardwareAssetHasVendor", @"Name",
                                      HardwareAsset_HasVendor_RelationshipType, @"RelationshipId",
                                      nil];
    NSDictionary *location = [NSDictionary dictionaryWithObjectsAndKeys:
                            @"Target_HardwareAssetHasLocation", @"Name",
                            HardwareAsset_HasLocation_RelationshipType, @"RelationshipId",
                            nil];
    NSDictionary *organisation = [NSDictionary dictionaryWithObjectsAndKeys:
                            @"Target_HardwareAssetHasOrganization", @"Name",
                            HardwareAsset_HasOrganization_RelationshipType, @"RelationshipId",
                            nil];
    NSDictionary *purchaseOrder = [NSDictionary dictionaryWithObjectsAndKeys:
                            @"Target_HardwareAssetHasPurchaseOrder", @"Name",
                            HardwareAsset_HasPurchaseOrder_RelationshipType, @"RelationshipId",
                            nil];
    NSDictionary *costCenter = [NSDictionary dictionaryWithObjectsAndKeys:
                            @"Target_HardwareAssetHasCostCenter", @"Name",
                            HardwareAsset_HasCostCenter_RelationshipType, @"RelationshipId",
                            nil];
    NSDictionary *ownedBy = [NSDictionary dictionaryWithObjectsAndKeys:
                            @"OwnedBy", @"Name",
                            HardwareAsset_OwnedBy_RelationsipType, @"RelationshipId",
                            nil];
    NSDictionary *primaryUser = [NSDictionary dictionaryWithObjectsAndKeys:
                            @"Target_HardwareAssetHasPrimaryUser", @"Name",
                            HardwareAsset_HasPrimaryUser_RelationshipType, @"RelationshipId",
                            nil];
    NSDictionary *catelogItem = [NSDictionary dictionaryWithObjectsAndKeys:
                            @"Target_HardwareAssetHasCatalogItem", @"Name",
                            HardwareAsset_HasCatelogItem_RelationshipType, @"RelationshipId",
                            nil];
    NSMutableArray * nameRelationArr = [[NSMutableArray alloc] init];
    
    [nameRelationArr addObject:vendor];
    [nameRelationArr addObject:location];
    [nameRelationArr addObject:organisation];
    [nameRelationArr addObject:purchaseOrder];
    [nameRelationArr addObject:costCenter];
    [nameRelationArr addObject:ownedBy];
    [nameRelationArr addObject:primaryUser];
    [nameRelationArr addObject:catelogItem];
    
    
    //
    // set the display name and barcodes in assets that are being created.
    //
    
    for(NSString* barcode in data.notFoundAssets) {
        Asset* a = [[Asset alloc] init];
        [a setManufacturer:self.manufacturer];
        [a setModel:self.model];
        if (data.useSerialNumber) {
            [a setSerialNumber:barcode];
        }
        else {
            [a setTag:barcode];
        }
        NSString *assetId = [[NSUUID UUID] UUIDString];
        [a setHardwareAssetId:assetId];

        [a setDisplayName:[self getGeneratedDisplayNameWithBarcode:barcode]];
        [a setName:a.displayName];
        [a setObjectIdToNull];
        [a setClassTypeId:HardwareAsset_Type];
        [a setNameRelationship:nameRelationArr];

        [assets addObject:a];
        
    }
    data.createdAssets = assets;
    
    [self updateAssetProperties];
    
    [self performSave:assets];
}

-(NSString*)getGeneratedDisplayNameWithBarcode:(NSString*)barcode {
    NSString* displayName = @"";
    
    if (self.manufacturer.length > 0) {
        displayName = manufacturer;
    }
    
    if (self.model.length > 0) {
        if (displayName.length == 0) {
            displayName = self.model;
        }
        else {
            displayName = [NSString stringWithFormat:@"%@ %@", displayName, self.model];
        }
    }
    if (displayName.length > 0) {
        displayName = [NSString stringWithFormat:@"%@ - %@", displayName, barcode];
    }
    else {
        displayName = barcode;
    }
    return displayName;
}

-(void)saveForEditAssets {
    __block MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.mode = MBProgressHUDModeIndeterminate;
    hud.labelText = NSLocalizedString(@"Saving asset properties...", Nil);
    
    [self updateAssetProperties];
    
    NSArray* assets = [self assetsToEdit];
    
    [self performSave:assets];
}

-(void)saveForDispose {
    __block MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.mode = MBProgressHUDModeIndeterminate;
    hud.labelText = NSLocalizedString(@"Disposing assets...", Nil);
    
    NSArray* assets = [self assetsToEdit];
    
    // save disposal date
    if (self.selectedDisposedDate != Nil) {
        for (Asset* a in assets) {
            [a setDisposalDate:[CiresonRequestBase getDateString:self.selectedDisposedDate]];
        }
    }
    
    // save disposal reference number
    if (self.disposalReferenceNumber != Nil && self.disposalReferenceNumber.length > 0) {
        for (Asset* a in assets) {
            [a setDisposalReferenceNumber:self.disposalReferenceNumber];
        }
    }
    
    // clear primary user
    if (!keepPrimaryUser) {
        for (Asset* a in assets) {
            [a clearPrimaryUser];
        }
    }
    
    // clear custodian user
    if (!keepCustodian) {
        for (Asset* a in assets) {
            [a setCustodian:Nil];
        }
    }
    
    // save cost center
    if (self.selectedCostCenters.count > 0) {
        CostCenter* costCenter = [self.selectedCostCenters firstObject];
        for (Asset* a in assets) {
            [a setCostCenter:costCenter];
        }
    }
    // save status
    for (Asset* a in assets) {
        [a setStatusId:@"020822bd-7d3a-4c0f-1be4-325ef1aa52b4"];
    }
    
    [self performSave:assets];
}

-(void)performSave:(NSArray*)assets {
    __weak AssetPropertiesViewController* weakSelf = self;
    BatchAPICall* saveAssets = [[CiresonApiService sharedService] getSaveAssetsRequest:assets];
    __block BatchAPICall* strongSaveAssets = saveAssets;
    
    void (^testSuccess)() = ^{
        if (strongSaveAssets == Nil)
            return;
        if (strongSaveAssets.checkFinished) {
            [MBProgressHUD hideHUDForView:weakSelf.view animated:YES];
            if (strongSaveAssets.hasErrors) {
                _saveErrorAlertView = [UIAlertView showErrorForBatchCall:strongSaveAssets.errors partialSuccess:strongSaveAssets.partialSuccess];
                if (strongSaveAssets.partialSuccess) {
                    _saveErrorAlertView.delegate = self;
                }
            }
            else {
                [UIHelper launchHomeWithParentViewController:weakSelf];
            }
            strongSaveAssets = Nil; // break the cycle
        }
    };
    
    saveAssets.successHandler = ^(id response){
        NSLog(@"%@",response);
        testSuccess();
    };
    saveAssets.errorHandler = ^(NSError* error){
        testSuccess();
    };
    
    [saveAssets call];
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (alertView == _saveErrorAlertView) {
        _saveErrorAlertView = Nil;
        if (buttonIndex == 0) {
            return; // do nothing.
        }
        else {
            // we ignore the error and move on.
            [UIHelper launchHomeWithParentViewController:self];
        }
    }
}

-(void)searchForAssets {
    NSAssert(APPDELEGATE.workFlow == InventoryAudit, @"Update this logic if other workflow uses this method");
    if (self.selectedLocations == nil && self.selectedOrganizations == Nil && self.selectedCustodians == Nil && self.selectedCostCenters == Nil && self.selectedStatuses == Nil && self.selectedReceivedDate == Nil) {
        [UIAlertView showErrorWithMsg:NSLocalizedString(@"Please select at least one criteria to search with", Nil)];
    }
    else {
        __block MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.mode = MBProgressHUDModeIndeterminate;
        hud.labelText = NSLocalizedString(@"Searching assets...", Nil);
        
        __weak AssetPropertiesViewController* weakSelf = self;
        GenericCiresonApiCall* searchRequest = [[CiresonApiService sharedService] getSearchByAssetPropertiesWithLocations:self.selectedLocations status:self.selectedStatuses organizations:self.selectedOrganizations custodians:self.selectedCustodians costcenters:self.selectedCostCenters receivedDate:self.selectedReceivedDate];
        searchRequest.successHandler = ^(id response){
            [MBProgressHUD hideHUDForView:weakSelf.view animated:YES];
            NSArray* searchResults = [Asset loadFromArray:response];
            if (searchResults == Nil || searchResults.count == 0) {
                [UIAlertView showErrorWithMsg:NSLocalizedString(@"No assets found for the criteria. Please change the criteria and try again", Nil)];
                return;
            }
            NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
            [defaults setObject:[self getDisplayText:self.selectedStatuses] forKey:@"status"];
            [defaults setObject:[self getDisplayText:self.selectedLocations] forKey:@"location"];
            [defaults setObject:[self getDisplayText:self.selectedOrganizations] forKey:@"organisation"];
            [defaults setObject:[self getDisplayText:self.selectedCustodians] forKey:@"custodian"];
            [defaults setObject:[self getDisplayText:self.selectedCostCenters] forKey:@"costcenter"];
            [defaults setObject:self.selectedReceivedDate forKey:@"receiveddate"];
            InventoryAuditData* data = [[InventoryAuditData alloc] init];
            data.assets = searchResults;
            
            [UIHelper launchScannerViewControllerWithParentViewController:weakSelf workflowData:data];
        };
        searchRequest.errorHandler = ^(NSError* error){
            [MBProgressHUD hideHUDForView:weakSelf.view animated:YES];
            [UIAlertView showErrorFromService:error];
            [self logout];
        };
        
        [searchRequest call];
    }
    
}

-(void)backButtonClicked:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
    if (APPDELEGATE.workFlow == SwapAsset) {
        SwapAssetData* data = (SwapAssetData*)self.workflowData;
        if (data.secondAsset != Nil) {
            data.secondAsset = Nil;
        }
        else {
            data.firstAsset = Nil;
        }
        
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma delegates of pickers
-(void)didFinishSelectingEnums:(NSArray*)enums {
    self.selectedStatuses = enums;
    [self.tableView reloadData];
}
-(void)didFinishSelectingLocations:(NSArray*)locations {
    self.selectedLocations = locations;
    [self.tableView reloadData];
}
-(void)didFinishSelectingOrganization:(NSArray *)organizations {
    self.selectedOrganizations = organizations;
    [self.tableView reloadData];
}

-(void)didFinishSelectingCostCenter:(NSArray *)costCenters {
    self.selectedCostCenters = costCenters;
    [self.tableView reloadData];
}


-(void)didFinishChoosingDate:(NSDate *)date affectedProperty:(AssetProperties)property {
    if (property == APReceivedDate) {
        self.selectedReceivedDate = date;
    }
    else if (property == APDisposalDate) {
        self.selectedDisposedDate = date;
    }
    else if (property == APLoanedDate) {
        self.selectedLoanedDate = date;
    }
    else if (property == APLoanReturnedDate) {
        self.selectedLoanReturnedDate = date;
    }
    [self.tableView reloadData];
}
-(void)didFinishSelectingUser:(NSArray *)users {
    self.selectedCustodians = users;
    [self.tableView reloadData];
}

-(void)didFinishEditingText:(NSString *)text withItemMode:(TextPickerItemMode)mode {
    if (mode == AssetName) {
        self.assetName = text;
    }
    else if (mode == AssetSerialNumber) {
        self.assetSerialNumber = text;
    }
    else if (mode == AssetTagNumber) {
        self.assetTagNumber = text;
    }
    else if (mode == DisposalReferenceNumber) {
        self.disposalReferenceNumber = text;
    }
    else if (mode == Manufacturer) {
        self.manufacturer = text;
    }
    else if (mode == Model) {
        self.model = text;
    }
    [self.tableView reloadData];
}

-(void)didFinishEditingLongText:(NSString *)text withItemMode:(LongTextViewMode)mode {
    if (mode == Notes) {
        self.selectedNotes = text;
    }
    [self.tableView reloadData];
}
-(void) logout{
    [User logout];
    [UIHelper presentLoginWithParentViewController:self];
}

@end

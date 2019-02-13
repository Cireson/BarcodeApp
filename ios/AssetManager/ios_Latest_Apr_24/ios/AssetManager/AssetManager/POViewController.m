
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
//  SearchResultsViewController.m
//  AssetManager
//

#import "POViewController.h"
#import "PurchaseOrderTableViewCell.h"
#import "UIColor+Cireson.h"
#import "UIImage+Cireson.h"
#import "UIHelper.h"
#import "ScannerViewController.h"
#import "UIAlertView+Cireson.h"

@interface POViewController ()
{
    NSDateFormatter * __dateFormatter;
    NSCalendar * __calendar;
    NSNumberFormatter* __totalCostFormatter;
}

-(void)loadPurchaseOrders;

@end

@implementation POViewController

@synthesize tableView;
@synthesize totalPurchaseOrdersLabel, initialTitle;
@synthesize purchaseOrders;
@synthesize loadAllFromService;

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
	self.tableView.backgroundColor = [UIColor clearColor];
	self.view.backgroundColor = [UIColor backgroundColor];
    self.tableView.separatorColor = [UIColor separatorColor];
    NSInteger count = [self.purchaseOrders count];
	self.totalPurchaseOrdersLabel.text = [NSString stringWithFormat:@"%ld", (long)count];
    self.purchaseOrderNumberLabel.text = self.initialTitle;
    
    __dateFormatter = [[NSDateFormatter alloc] init];
    [__dateFormatter setDateFormat:@"yyyy-MM-dd'T'HH:mm:ss"];
    [__dateFormatter setLocale:[NSLocale systemLocale]];
    __calendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    
    __totalCostFormatter = [[NSNumberFormatter alloc] init];
    __totalCostFormatter.numberStyle = kCFNumberFormatterDecimalStyle;
    __totalCostFormatter.generatesDecimalNumbers = YES;
    __totalCostFormatter.minimumFractionDigits = 2;
    __totalCostFormatter.maximumFractionDigits = 2;
    if (self.loadAllFromService) {
        [self loadPurchaseOrders];
    }
    
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (self.purchaseOrders != nil) {
        return [self.purchaseOrders count];
    }
    return 0;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *CellIdentifier = @"cellPurchaseOrder";
    
	PurchaseOrderTableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:CellIdentifier];
	
    if(cell == nil) {
        cell = [[PurchaseOrderTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
	}
    
    PurchaseOrder* po = [self.purchaseOrders objectAtIndex:indexPath.row];
	
    cell.backgroundColor = [UIColor backgroundColor];
	cell.titleLabel.text = po.displayName;
    [cell.calendarImageView setImage:[UIImage calendarImage]];
    
    NSDate *localDate = [__dateFormatter dateFromString:po.purchaseOrderDate];

    [cell.calendarImageView setImage:[UIImage calendarImage]];
    if (localDate != nil) {
        NSDateComponents *components    = [__calendar components:(NSYearCalendarUnit  |
                                                                NSMonthCalendarUnit |
                                                                NSDayCalendarUnit) fromDate:localDate];
        cell.monthLabel.text = [UIHelper stringForMonth:components.month];
        cell.dayLabel.text = [NSString stringWithFormat:@"%ld", (long)[components day]];

        cell.yearLabel.text = [NSString stringWithFormat:@"%ld", (long)[components year]];
    }
    else {
        cell.monthLabel.text = @"";
        cell.dayLabel.text = NSLocalizedString(@"N/A", Nil);
        cell.yearLabel.text = @"";
    }
    
    NSString* statusStr = @"N/A";
    if (po.purchaseOrderStatus != nil && [po.purchaseOrderStatus length] != 0) {
        statusStr = po.purchaseOrderStatus;
    }
   
    statusStr = [NSString stringWithFormat:NSLocalizedString(@"Status: %@", Nil), statusStr];
    
    cell.purchaseOrderStatusLabel.text = statusStr;
    
    NSString* totalCost = @"N/A";
    if (![po.totalCost isKindOfClass:[NSNull class]]) {
        totalCost = [__totalCostFormatter stringFromNumber:po.totalCost];
    }
    
    cell.purchaseOrderTotalCostLabel.text = [NSString stringWithFormat:NSLocalizedString(@"Total Cost: %@", Nil), totalCost];
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	[self.tableView deselectRowAtIndexPath:indexPath animated:YES];
    PurchaseOrder* po = [self.purchaseOrders objectAtIndex:indexPath.row];
    [APPDELEGATE printCurrentWorkflow];
    if (APPDELEGATE.workFlow == ReceiveAssetByPO) {
        ReceiveAssetsData* wfd = [[ReceiveAssetsData alloc] init];
        self.workflowData = wfd;
        wfd.purchaseOrder = po;

        // we want to revert to the original values for PO so that any changes, to revert changes
        // made if the user comes back to this screen after making changes
        [po reset];
        [UIHelper launchScannerViewControllerWithParentViewController:self workflowData:self.workflowData];
    }
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
	return 80.0f;
}

-(void)loadPurchaseOrders {
    __block MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.mode = MBProgressHUDModeIndeterminate;
    hud.labelText = NSLocalizedString(@"Loading purchase orders...", Nil);
    
    __weak POViewController* weakSelf = self;
    
    GenericCiresonApiCall* loadRequest = [[CiresonApiService sharedService] getAllPurchaseOrders];
    
    loadRequest.successHandler = ^(id response){
        [MBProgressHUD hideHUDForView:weakSelf.view animated:YES];
        NSArray* pos = [PurchaseOrder loadFromArray:response];
        weakSelf.purchaseOrders = pos;
        NSInteger count = 0;
        if (pos) {
            count = pos.count;
        }
        weakSelf.totalPurchaseOrdersLabel.text = [NSString stringWithFormat:@"%ld", (long)count];
        [weakSelf.tableView reloadData];
    };
    loadRequest.errorHandler = ^(NSError* error){
        [MBProgressHUD hideHUDForView:weakSelf.view animated:YES];
        [UIAlertView showErrorFromService:error];
        [User logout];
        [UIHelper presentLoginWithParentViewController:self];
    };
    
    [loadRequest call];
    

}

-(void)backButtonClicked:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end


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
//  SearchAssetViewController.m
//  AssetManager
//

#import "SearchPOViewController.h"
#import "UIColor+Cireson.h"
#import "UIAlertView+Cireson.h"
#import "POViewController.h"
#import "UIHelper.h"


@interface SearchPOViewController ()
@property(nonatomic, strong) GenericCiresonApiCall* searchRequest;
-(void)handleTap:(UIEvent*)event;
@end

@implementation SearchPOViewController

@synthesize searchField, searchButton, browsePurchaseOrdersButton, startNewPurchaseButton, cancelBarItem;

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
	
	self.searchButton.layer.cornerRadius = 5.0;
	self.browsePurchaseOrdersButton.layer.cornerRadius = 5.0;
	self.startNewPurchaseButton.layer.cornerRadius = 5.0;
	
	self.view.backgroundColor = [UIColor backgroundColor];
	
	UITapGestureRecognizer * tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTap:)];
	[tap setCancelsTouchesInView:NO];
    [self.view addGestureRecognizer:tap];
	
//	self.searchField.text = @"PO-00057";
	
}

-(void)handleTap:(UIEvent*)event {
    [self.view endEditing:YES];
}


-(void)searchButtonClicked:(id)sender {
	[self.view endEditing:YES];

    // this can be reset if user clicks on "Receive Assets" button and comes back.
    APPDELEGATE.workFlow = ReceiveAssetByPO;

    // search in progress?
    if (self.searchRequest != nil) {
        return;
    }
	
	// validate
    UITextField* textField = nil;
    if (![self.searchField.text length]) {
        textField = self.searchField;
    }
	
	if (textField != Nil) {
        if (![textField isFirstResponder]) {
            [textField becomeFirstResponder];
        }
        [UIAlertView showErrorWithMsg:NSLocalizedString(@"Search field must not be empty", Nil)];
    }
	else {
        
        __block MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.mode = MBProgressHUDModeIndeterminate;
        hud.labelText = [NSString stringWithFormat:NSLocalizedString(@"Searching for %@...", Nil), self.searchField.text];
        
        __weak SearchPOViewController* weakSelf = self;
        
        self.searchRequest = [[CiresonApiService sharedService] getSearchByPurchaseOrderRequest:self.searchField.text];
        
        self.searchRequest.successHandler = ^(id response){
            [MBProgressHUD hideHUDForView:weakSelf.view animated:YES];
            NSArray* pos = [PurchaseOrder loadFromArray:response];
            PurchaseOrder* po = Nil;
            if (pos && pos.count > 0) {
                po = [pos objectAtIndex:0];
            }
            if (!po) {
                [UIAlertView showErrorWithMsg:NSLocalizedString(@"Purchase order not found", Nil)];
            }
            else {
                [UIHelper launchPurchaseOrdersViewWithPurchaseOrders:[NSArray arrayWithObject:po] parentViewController:weakSelf title:weakSelf.searchField.text workflowData:nil];
            }
            weakSelf.searchRequest = Nil;
        };
        self.searchRequest.errorHandler = ^(NSError* error){
            [MBProgressHUD hideHUDForView:weakSelf.view animated:YES];
            [UIAlertView showErrorFromService:error];
            [User logout];
            [UIHelper presentLoginWithParentViewController:weakSelf];
            weakSelf.searchRequest = nil;
        };
        
        [self.searchRequest call];

	}
}

-(void)browsePurchaseOrdersButtonClicked:(id)sender {
	//TODO: this probably shouldn't be hard coded (90)
    APPDELEGATE.workFlow = ReceiveAssetByPO; // this can be reset if receive assets is clicked and user clicks on browse without going to the home screen.
    [UIHelper launchPurchaseOrdersViewWithPurchaseOrders:Nil parentViewController:self title:NSLocalizedString(@"Last 90 days", Nil) workflowData:Nil];
}

-(void)receiveAssetsClicked:(id)sender {
    APPDELEGATE.workFlow = AddAssets;
    AddAssetsData* data = [[AddAssetsData alloc] init];
    [UIHelper launchScannerViewControllerWithParentViewController:self workflowData:data];
}

-(IBAction)cancelButtonClicked:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

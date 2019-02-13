
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
//  UserPickerViewController.m
//  AssetManager
//

#import "UserPickerViewController.h"
#import "UIColor+Cireson.h"
#import "UserTableViewCell.h"
#import "UIAlertView+Cireson.h"
#import "UIHelper.h"

@interface UserPickerViewController () <UITableViewDataSource, UITableViewDelegate, UISearchBarDelegate> {
    NSMutableArray *__userselectedCustodians;
}

-(void)handleTap:(UIEvent*)event;

@property(strong, nonatomic)NSArray* currentUserlist;
@property(strong, nonatomic)NSArray* searchResult;
@property(strong, nonatomic)GenericCiresonApiCall* searchRequest;

-(void)performSearch:(NSString*)searchText;

@end

@implementation UserPickerViewController

@synthesize delegate, selectedUsers, tableView, searchBar, currentUserlist;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor backgroundColor];
    self.tableView.backgroundColor = [UIColor backgroundColor];
    self.tableView.separatorColor = [UIColor separatorColor];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    __userselectedCustodians = [[NSMutableArray alloc] init];
    if (self.selectedUsers != Nil && self.selectedUsers.count != 0) {
        [__userselectedCustodians addObjectsFromArray:self.selectedUsers];
    }
    
    if (APPDELEGATE.workFlow == InventoryAudit) {
        self.tableView.allowsMultipleSelection = YES; //multiple selection for inventory audit.
    }
    else {
        self.tableView.allowsMultipleSelection = NO;
    }
    
    UITapGestureRecognizer * tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTap:)];
	[tap setCancelsTouchesInView:NO];
    [self.view addGestureRecognizer:tap];
}


-(void)handleTap:(UIEvent*)event {
    [self.view endEditing:YES];
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

#pragma search delegates
- (void)searchBarSearchButtonClicked:(UISearchBar *)sb {
    NSString* searchText = sb.text;
    if (searchText != Nil && searchText.length >= 3) {
        [self performSearch:searchText];
        [sb endEditing:YES];
    }else{
        [UIAlertView showErrorWithMsg:NSLocalizedString(@"Please enter at least 3 characters to search", Nil)];
    }
}

-(void)performSearch:(NSString *)searchText {
    if (self.searchRequest) {
        MBProgressHUD* p = [MBProgressHUD HUDForView:self.view];
        if (p) {
            p.labelText = NSLocalizedString(@"Still searching...", Nil);
        }
        return;
    }
    self.searchRequest = [[CiresonApiService sharedService] getSearchUsers:searchText];
    
    __block MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.mode = MBProgressHUDModeIndeterminate;
    NSString* statusText = [NSString stringWithFormat:NSLocalizedString(@"Searching users...", Nil), searchText];
    hud.labelText = NSLocalizedString(statusText, Nil);
    
    __block __weak UserPickerViewController* me = self;
    
    self.searchRequest.successHandler = ^(id response){
        [MBProgressHUD hideHUDForView:me.view animated:YES];
        me.searchResult = [CiresonUser loadFromArray:response];
        me.currentUserlist = me.searchResult;
        me.searchRequest = Nil;
        [me.tableView reloadData];
    };
    self.searchRequest.errorHandler = ^(NSError* error){
        [MBProgressHUD hideHUDForView:me.view animated:YES];
        me.searchRequest = Nil;
        [UIAlertView showErrorFromService:error];
        [User logout];
        [UIHelper presentLoginWithParentViewController:me];
    };
    
    [self.searchRequest call];
}


#pragma tableview

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    // Return the number of rows in the section.
    return [self.currentUserlist count];
}

- (UITableViewCell *)tableView:(UITableView *)tv cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    UserTableViewCell *cell = [tv dequeueReusableCellWithIdentifier:@"UserCell" forIndexPath:indexPath];
    
    // Configure the cell...
    cell.backgroundColor = [UIColor backgroundColor];
    CiresonUser *user = [self.currentUserlist objectAtIndex:indexPath.row];
    
    if ([CiresonModelBase containsObjectInArray:__userselectedCustodians object:user]) {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
        [cell setSelected:YES];
        [tableView selectRowAtIndexPath:indexPath animated:YES scrollPosition:UITableViewScrollPositionNone];
    }
    else {
        cell.accessoryType = UITableViewCellAccessoryNone;
        [cell setSelected:NO];
    }
    
    cell.displayNameLabel.text = user.displayName;
    cell.domainLabel.text = user.domain;
    return cell;
}

-(void)tableView:(UITableView *)tv didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    CiresonUser *user = [self.currentUserlist objectAtIndex:indexPath.row];
    if (![CiresonModelBase containsObjectInArray:__userselectedCustodians object:user]) {
        [__userselectedCustodians addObject:user];
    }
    UITableViewCell *cell = [tv cellForRowAtIndexPath:indexPath];
    cell.accessoryType = UITableViewCellAccessoryCheckmark;
}

-(void)tableView:(UITableView *)tv didDeselectRowAtIndexPath:(NSIndexPath *)indexPath {
    CiresonUser *user = [self.currentUserlist objectAtIndex:indexPath.row];
    [CiresonModelBase removeObjectWithSameIdInArray:__userselectedCustodians obj:user];
    UITableViewCell *cell = [tv cellForRowAtIndexPath:indexPath];
    cell.accessoryType = UITableViewCellAccessoryNone;
}

-(IBAction)didClickDoneButton:(id)sender {
    if (self.delegate) {
        NSArray *result = Nil;
        if (__userselectedCustodians.count > 0) {
            result = __userselectedCustodians;
        }
        [self.delegate didFinishSelectingUser:result];
    }
    [self dismissViewControllerAnimated:YES completion:Nil];
}

-(IBAction)didClickClearButton:(id)sender {
    [__userselectedCustodians removeAllObjects];
    [self.tableView reloadData];
}


@end

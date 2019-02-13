
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
//  CostCenterViewController
//  AssetManager
//

#import "CostCenterViewController.h"
#import "UIColor+Cireson.h"

@interface CostCenterViewController () {
    NSMutableDictionary *__costCenterDict;
    NSArray *__costCenterSectionTitles;
    NSArray *__costCenterIndexTitles;
    NSMutableArray *__userselectedCostCenters;
}
@end

@implementation CostCenterViewController

@synthesize costCenters, selectedCostCenters, delegate;

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
    self.tableView.sectionIndexBackgroundColor = [UIColor backgroundColor];
    self.tableView.sectionIndexColor = [UIColor whiteColor];
    
    __userselectedCostCenters = [[NSMutableArray alloc] init];
    if (self.selectedCostCenters != Nil && self.selectedCostCenters.count != 0) {
        [__userselectedCostCenters addObjectsFromArray:self.selectedCostCenters];// initial Cost Centers
    }
    
    if (APPDELEGATE.workFlow == InventoryAudit) {
        self.tableView.allowsMultipleSelection = YES; // multiple selection for inventory audit.
    }
    else {
        self.tableView.allowsMultipleSelection = NO;
    }
    
    if (self.costCenters) {
        __costCenterDict = [[NSMutableDictionary alloc] init];
        for (CostCenter* cc in self.costCenters) {
            NSString* displayName = cc.displayName;
            char firstChar = [displayName characterAtIndex:0];
            firstChar = toupper(firstChar);
            NSString* key = [NSString stringWithFormat:@"%c", firstChar];
            NSMutableArray* list = [__costCenterDict objectForKey:key];
            if (list == Nil) {
                list = [[NSMutableArray alloc] init];
                [__costCenterDict setObject:list forKey:key];
            }
            [list addObject:cc];
        }
    }
    
    __costCenterSectionTitles = [[__costCenterDict allKeys] sortedArrayUsingSelector:@selector(localizedCaseInsensitiveCompare:)];
    __costCenterIndexTitles = __costCenterSectionTitles;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return [__costCenterIndexTitles count];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    // Return the number of rows in the section.
    NSString *sectionTitle = [__costCenterSectionTitles objectAtIndex:section];
    NSArray *sectioncostCenters = [__costCenterDict objectForKey:sectionTitle];
    return [sectioncostCenters count];
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section{
    return [__costCenterSectionTitles objectAtIndex:section];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"CostCenterCell" forIndexPath:indexPath];
    
    // Configure the cell...
    cell.backgroundColor = [UIColor backgroundColor];
    NSString *sectionTitle = [__costCenterSectionTitles objectAtIndex:indexPath.section];
    NSArray *sectioncostCenters = [__costCenterDict objectForKey:sectionTitle];
    CostCenter *cc = [sectioncostCenters objectAtIndex:indexPath.row];
    if ([CiresonModelBase containsObjectInArray:__userselectedCostCenters object:cc]) {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
        [cell setSelected:YES];
        [tableView selectRowAtIndexPath:indexPath animated:YES scrollPosition:UITableViewScrollPositionNone];
    }
    else {
        cell.accessoryType = UITableViewCellAccessoryNone;
        [cell setSelected:NO];
    }
    cell.textLabel.font = [UIFont systemFontOfSize:15.0];
    cell.textLabel.textColor = [UIColor whiteColor];
    cell.textLabel.text = cc.displayName;
    return cell;
}

- (NSArray *)sectionIndexTitlesForTableView:(UITableView *)tableView{
    return __costCenterIndexTitles;
}

- (NSInteger)tableView:(UITableView *)tableView sectionForSectionIndexTitle:(NSString *)title atIndex:(NSInteger)index {
    return [__costCenterSectionTitles indexOfObject:title];
}

-(UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, tableView.bounds.size.width, 30)];
    [headerView setBackgroundColor:[UIColor grayColor]];
    
    UILabel *headerLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 3, tableView.bounds.size.width - 10, 18)];
    headerLabel.text = [__costCenterSectionTitles objectAtIndex:section];
    headerLabel.textColor = [UIColor colorWithRed:1.0 green:1.0 blue:1.0 alpha:0.75];
    headerLabel.backgroundColor = [UIColor clearColor];
    [headerView addSubview:headerLabel];
    
    return headerView;
}

-(void)tableView:(UITableView *)tv didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString *sectionTitle = [__costCenterSectionTitles objectAtIndex:indexPath.section];
    NSArray *sectioncostCenters = [__costCenterDict objectForKey:sectionTitle];
    CostCenter *cc = [sectioncostCenters objectAtIndex:indexPath.row];
    if (![CiresonModelBase containsObjectInArray:__userselectedCostCenters object:cc]) {
        [__userselectedCostCenters addObject:cc];
    }
    UITableViewCell *cell = [tv cellForRowAtIndexPath:indexPath];
    cell.accessoryType = UITableViewCellAccessoryCheckmark;
}

-(void)tableView:(UITableView *)tv didDeselectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString *sectionTitle = [__costCenterIndexTitles objectAtIndex:indexPath.section];
    NSArray *sectionCostCenters = [__costCenterDict objectForKey:sectionTitle];
    CostCenter *cc = [sectionCostCenters objectAtIndex:indexPath.row];
    [CiresonModelBase removeObjectWithSameIdInArray:__userselectedCostCenters obj:cc];
    UITableViewCell *cell = [tv cellForRowAtIndexPath:indexPath];
    cell.accessoryType = UITableViewCellAccessoryNone;
}

#pragma mark - UISearchBar Delegates

-(IBAction)didClickDoneButton:(id)sender {
    if (self.delegate) {
        NSArray* result = Nil;
        if (__userselectedCostCenters.count > 0) {
            result = __userselectedCostCenters;
        }
        [self.delegate didFinishSelectingCostCenter:result];
    }
    [self dismissViewControllerAnimated:YES completion:Nil];
}

-(IBAction)didClickClearButton:(id)sender {
    [__userselectedCostCenters removeAllObjects];
    [self.tableView reloadData];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

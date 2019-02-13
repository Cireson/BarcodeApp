
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
//  LocationViewController.m
//  AssetManager
//

#import "LocationViewController.h"
#import "UIColor+Cireson.h"

@interface LocationViewController () {
    NSMutableDictionary *__locationDict;
    NSArray *__locationSectionTitles;
    NSArray *__locationIndexTitles;
    NSMutableArray* __userselectedLocations;
}
@end

@implementation LocationViewController

@synthesize locations, selectedLocations, delegate;

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
    
    __userselectedLocations = [[NSMutableArray alloc] init];
    if (self.selectedLocations != Nil && self.selectedLocations.count != 0) {
        [__userselectedLocations addObjectsFromArray:self.selectedLocations];// initial locations
    }
    
    if (APPDELEGATE.workFlow == InventoryAudit) {
        self.tableView.allowsMultipleSelection = YES; // multiple selection for inventory audit.
    }
    else {
        self.tableView.allowsMultipleSelection = NO;
    }
    
    if (self.locations) {
        __locationDict = [[NSMutableDictionary alloc] init];
        for (CiresonLocation* loc in self.locations) {
            NSString* displayName = loc.displayName;
            char firstChar = [displayName characterAtIndex:0];
            firstChar = toupper(firstChar);
            NSString* key = [NSString stringWithFormat:@"%c", firstChar];
            NSMutableArray* list = [__locationDict objectForKey:key];
            if (list == Nil) {
                list = [[NSMutableArray alloc] init];
                [__locationDict setObject:list forKey:key];
            }
            [list addObject:loc];
        }
    }
    
    __locationSectionTitles = [[__locationDict allKeys] sortedArrayUsingSelector:@selector(localizedCaseInsensitiveCompare:)];
    __locationIndexTitles = __locationSectionTitles;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return [__locationIndexTitles count];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    // Return the number of rows in the section.
    NSString *sectionTitle = [__locationSectionTitles objectAtIndex:section];
    NSArray *sectionLocations = [__locationDict objectForKey:sectionTitle];
    return [sectionLocations count];
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section{
    return [__locationSectionTitles objectAtIndex:section];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"LocationCell" forIndexPath:indexPath];
    
    // Configure the cell...
    cell.backgroundColor = [UIColor backgroundColor];
    NSString *sectionTitle = [__locationSectionTitles objectAtIndex:indexPath.section];
    NSArray *sectionLocations = [__locationDict objectForKey:sectionTitle];
    CiresonLocation *location = [sectionLocations objectAtIndex:indexPath.row];
    
    if ([CiresonModelBase containsObjectInArray:__userselectedLocations object:location]) {
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
    cell.textLabel.numberOfLines = 0;
    cell.textLabel.text = location.displayName;
    [cell.textLabel sizeToFit];
    return cell;
}

- (NSArray *)sectionIndexTitlesForTableView:(UITableView *)tableView{
    return __locationIndexTitles;
}

- (NSInteger)tableView:(UITableView *)tableView sectionForSectionIndexTitle:(NSString *)title atIndex:(NSInteger)index{
    return [__locationSectionTitles indexOfObject:title];
}

-(UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, tableView.bounds.size.width, 30)];
    [headerView setBackgroundColor:[UIColor grayColor]];
    
    UILabel *headerLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 3, tableView.bounds.size.width - 10, 18)];
    headerLabel.text = [__locationSectionTitles objectAtIndex:section];
    headerLabel.textColor = [UIColor colorWithRed:1.0 green:1.0 blue:1.0 alpha:0.75];
    headerLabel.backgroundColor = [UIColor clearColor];
    [headerView addSubview:headerLabel];
    
    return headerView;
}

-(void)tableView:(UITableView *)tv didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString *sectionTitle = [__locationSectionTitles objectAtIndex:indexPath.section];
    NSArray *sectionLocations = [__locationDict objectForKey:sectionTitle];
    CiresonLocation *location = [sectionLocations objectAtIndex:indexPath.row];
    if (![CiresonModelBase containsObjectInArray:__userselectedLocations object:location]) {
        [__userselectedLocations addObject:location];
    }
    UITableViewCell *cell = [tv cellForRowAtIndexPath:indexPath];
    cell.accessoryType = UITableViewCellAccessoryCheckmark;
}

-(void)tableView:(UITableView *)tv didDeselectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString *sectionTitle = [__locationSectionTitles objectAtIndex:indexPath.section];
    NSArray *sectionLocations = [__locationDict objectForKey:sectionTitle];
    CiresonLocation *location = [sectionLocations objectAtIndex:indexPath.row];
    [CiresonModelBase removeObjectWithSameIdInArray:__userselectedLocations obj:location];
    UITableViewCell *cell = [tv cellForRowAtIndexPath:indexPath];
    cell.accessoryType = UITableViewCellAccessoryNone;
}

#pragma mark - UISearchBar Delegates

-(IBAction)didClickDoneButton:(id)sender {
    if (self.delegate) {
        NSArray* result = Nil;
        if (__userselectedLocations.count > 0) {
            result = __userselectedLocations;
        }
        [self.delegate didFinishSelectingLocations:result];
    }
    [self dismissViewControllerAnimated:YES completion:Nil];
}

-(IBAction)didClickClearButton:(id)sender {
    [__userselectedLocations removeAllObjects];
    [self.tableView reloadData];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end


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
//  EditAssetPropertiesViewController.h
//  AssetManager
//

#import <UIKit/UIKit.h>

@interface AssetPropertiesViewController : CiresonViewController <UITableViewDelegate, UITableViewDataSource>

@property (nonatomic, weak) IBOutlet UILabel *titleLabel;
@property (nonatomic, weak) IBOutlet UILabel *numberOfAssetsLabel;

@property (nonatomic, retain) IBOutlet UIBarButtonItem *saveButtonName;

@property (nonatomic, weak) IBOutlet UIToolbar *toolBar;

@property (nonatomic, weak) IBOutlet UITableView *tableView;
@property (nonatomic, retain) NSArray *titleArray;

// specific workflow specific data for help in saving
@property (nonatomic, retain) WorkflowData* workflowData;

@property(strong, nonatomic) NSArray* assets;

@property (strong, nonatomic) NSArray*  selectedStatuses;
@property (strong, nonatomic) NSArray*  selectedLocations;
@property (strong, nonatomic) NSArray*  selectedOrganizations;
@property (strong, nonatomic) NSArray*  selectedCostCenters;
@property (strong, nonatomic) NSArray*  selectedCustodians;
@property (strong, nonatomic) NSDate*   selectedReceivedDate;

-(IBAction) backButtonClicked:(id)sender;
-(IBAction) saveButtonClicked:(id)sender;
-(NSString*)getDisplayText:(NSArray*)objects;
@end

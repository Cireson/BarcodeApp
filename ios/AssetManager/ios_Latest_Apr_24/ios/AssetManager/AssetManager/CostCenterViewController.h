
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
//  CostCenterViewController.h
//  AssetManager
//

#import <UIKit/UIKit.h>

@protocol CostCenterViewControllerDelegate <NSObject>
@required
-(void)didFinishSelectingCostCenter:(NSArray*)costCenters; // nil means none selected
@end

@interface CostCenterViewController : CiresonViewController <UITableViewDataSource, UITableViewDelegate>

@property (nonatomic, weak) IBOutlet UITableView *tableView;

@property (nonatomic, strong)NSArray* costCenters;
@property (nonatomic, strong)NSArray* selectedCostCenters;
@property (assign) id<CostCenterViewControllerDelegate> delegate;

-(IBAction)didClickDoneButton:(id)sender;
-(IBAction)didClickClearButton:(id)sender;
@end



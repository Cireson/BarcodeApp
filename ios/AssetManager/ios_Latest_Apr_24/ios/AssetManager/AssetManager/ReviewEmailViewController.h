
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
//  ReviewEmailViewController.h
//  AssetManager
//

#import <UIKit/UIKit.h>
#import <MessageUI/MessageUI.h>

@interface ReviewEmailViewController : CiresonViewController <UITableViewDataSource, UITableViewDelegate, MFMailComposeViewControllerDelegate>

@property (nonatomic, weak) IBOutlet UISegmentedControl *segmentedControl;
@property (nonatomic, weak) IBOutlet UILabel *countLabel;
@property (nonatomic, weak) IBOutlet UITableView *tableView;

@property (nonatomic, retain) NSArray *noMatchAssets;
@property (nonatomic, retain) NSArray *inventoriedAssets;
@property (nonatomic, retain) NSArray *notInventoriedAssets;

-(IBAction)segmentedControlValueChanged:(id)sender;

-(IBAction)backButtonClicked:(id)sender;
-(IBAction)emailButtonClicked:(id)sender;

@end

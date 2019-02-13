
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
//  AssetViewController.h
//  AssetManager
//

#import <UIKit/UIKit.h>
#import "PurchaseOrder.h"

@protocol AssetViewControllerDelegate <NSObject>
    @required
-(void)didFinishSelectingAsset:(Asset*)asset;
    -(void)didCancelSelection;
@end 

@interface AssetViewController : CiresonViewController <UITableViewDataSource, UITableViewDelegate>

// these need to be set by the caller
@property (nonatomic, assign) id<AssetViewControllerDelegate> delegate;

@property (nonatomic, strong) NSString* viewTitle;
@property (nonatomic, strong) NSString* viewSubtitle;

@property (nonatomic, retain) IBOutlet UIBarButtonItem *doneButtonName;
@property (nonatomic, retain) IBOutlet UIBarButtonItem *cancelButtonName;

@property (nonatomic, weak) IBOutlet UITableView *tableView;

@property (nonatomic, weak) IBOutlet UILabel *subTitleLabel;
@property (nonatomic, weak) IBOutlet UILabel *titleLabel;


-(IBAction)cancelButtonClicked:(id)sender;
-(IBAction)doneButtonClicked:(id)sender;

@end


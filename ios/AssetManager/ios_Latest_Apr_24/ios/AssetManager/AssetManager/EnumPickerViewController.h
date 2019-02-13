
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
//  StatusViewController.h
//  AssetManager
//

#import <UIKit/UIKit.h>
#import "RATreeView.h"

@protocol EnumPickerViewControllerDelegate <NSObject>
@required
-(void)didFinishSelectingEnums:(NSArray*)enums; // nil means none selected
@end

@interface EnumPickerViewController : CiresonViewController <RATreeViewDelegate, RATreeViewDataSource>

@property (nonatomic, weak) RATreeView *treeView;
@property (nonatomic, assign) id<EnumPickerViewControllerDelegate> delegate;
@property (nonatomic, strong) CiresonEnumeration* enumeration; // root enumeration
@property (nonatomic, strong) NSArray* selectedValues; // currently selected enumerations
@property (nonatomic, weak) IBOutlet UIView* treeViewPlacerHolder;
@property (nonatomic, weak) IBOutlet UILabel* titleLabel;

@property (nonatomic, assign) BOOL multiSelectionMode; // allows multiselection.

-(IBAction)didClickDoneButton:(id)sender;
-(IBAction)didClickClearButton:(id)sender;

@end

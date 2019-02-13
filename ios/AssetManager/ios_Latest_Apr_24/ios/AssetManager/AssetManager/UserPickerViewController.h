
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
//  UserPickerViewController.h
//  AssetManager
//

#import <UIKit/UIKit.h>

@protocol UserPickerViewControllerDelegate <NSObject>
@required
-(void)didFinishSelectingUser:(NSArray*)users; // nil means none selected
@end

@interface UserPickerViewController : CiresonViewController

@property (nonatomic, strong)NSArray* selectedUsers;
@property (nonatomic, weak)id<UserPickerViewControllerDelegate> delegate;

@property (nonatomic, weak) IBOutlet UITableView* tableView;
@property (nonatomic, weak) IBOutlet UISearchBar* searchBar;
-(IBAction)didClickDoneButton:(id)sender;
-(IBAction)didClickClearButton:(id)sender;

@end

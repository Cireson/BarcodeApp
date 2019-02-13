
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
//  SettingsViewController.h
//  AssetManager
//

#import <UIKit/UIKit.h>

@interface SettingsViewController : CiresonViewController <UITextFieldDelegate, UITextViewDelegate>

@property (nonatomic, weak) IBOutlet UITextField *urlField;
@property (nonatomic, weak) IBOutlet UITextField *usernameField;
@property (nonatomic, weak) IBOutlet UITextField *passwordField;

@property (nonatomic, weak) IBOutlet UITextView *prefixesView;
@property (nonatomic, weak) IBOutlet UIButton* logoutButton;
@property (nonatomic, weak) IBOutlet UIButton* bluetoothButton;


@property (nonatomic, weak) IBOutlet UILabel *bluetoothSelectLabel;

-(IBAction)logoutButtonClicked:(id)sender;
-(IBAction)bluetoothButtonClicked:(id)sender;

-(IBAction)backButtonClicked:(id)sender;
-(IBAction)saveButtonClicked:(id)sender;
@end

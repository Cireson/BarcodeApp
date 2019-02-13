
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
//  TextPickerViewController.h
//  AssetManager
//

#import <UIKit/UIKit.h>

@protocol TextPickerViewControllerDelegate <NSObject>

@required
-(void)didFinishEditingText:(NSString *)text withItemMode:(TextPickerItemMode)mode;

@end

@interface TextPickerViewController : CiresonViewController <UITextFieldDelegate>
@property (nonatomic, weak) IBOutlet UITextField *editTextField;
@property (nonatomic, weak) IBOutlet UILabel *titleLabel;

@property (nonatomic, retain) NSString *editTextValue;
@property (nonatomic, retain) NSString *viewTitle;
@property (nonatomic, assign) TextPickerItemMode textItemTypeMode;
@property (assign) id<TextPickerViewControllerDelegate> delegate;

-(IBAction)didClickDoneButton:(id)sender;
-(IBAction)didClickCancelButton:(id)sender;

@end

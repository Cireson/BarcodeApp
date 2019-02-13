
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
//  TextPickerViewController.m
//  AssetManager
//

#import "TextPickerViewController.h"
#import "UIAlertView+Cireson.h"
#import "UIColor+Cireson.h"

@interface TextPickerViewController ()
-(void)handleTap:(UIEvent*)event;
@end

@implementation TextPickerViewController

@synthesize editTextField, delegate, editTextValue, textItemTypeMode, titleLabel, viewTitle;

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
    self.editTextField.delegate = self;
    [self.titleLabel setText:viewTitle];
    self.editTextField.text = editTextValue;
    
    self.view.backgroundColor = [UIColor backgroundColor];
	
	UITapGestureRecognizer * tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTap:)];
	[tap setCancelsTouchesInView:NO];
    [self.view addGestureRecognizer:tap];
}


-(void)handleTap:(UIEvent*)event {
    [self.view endEditing:YES];
}

#pragma mark - Picker Delegate
-(void)didClickDoneButton:(id)sender {
    if ([self.editTextField.text length] == 0) {
        [UIAlertView showErrorWithMsg:@"Text field can't be empty."];
        return;
    }
    if (self.delegate) {
        [self.delegate didFinishEditingText:self.editTextField.text withItemMode:textItemTypeMode];
    }
    [self dismissViewControllerAnimated:YES completion:Nil];
}

-(void)didClickCancelButton:(id)sender {
    [self dismissViewControllerAnimated:YES completion:Nil];
}

-(BOOL)textFieldShouldReturn:(UITextField *)textField {
    [self.editTextField resignFirstResponder];
    return NO;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

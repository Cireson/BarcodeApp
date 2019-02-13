
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
//  LongTextViewController.m
//  AssetManager
//

#import "LongTextViewController.h"
#import "UIAlertView+Cireson.h"
#import "UIColor+Cireson.h"

@interface LongTextViewController ()
-(void)handleTap:(UIEvent*)event;
@end

@implementation LongTextViewController

@synthesize textView, delegate, editTextValue, textItemTypeMode, titleLabel, viewTitle, maxCharacters;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
    }
    return self;
}

- (void)viewDidLoad{
    [super viewDidLoad];
    self.textView.delegate = self;
    [self.titleLabel setText:self.viewTitle];
    self.textView.text = editTextValue;
    
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
    if ([self.textView.text length] == 0) {
        [UIAlertView showErrorWithMsg:@"You must enter some text"];
        return;
    }
    NSUInteger length = [self.textView.text length];
    if (length > maxCharacters) {
        [UIAlertView showErrorWithMsg:
         [NSString stringWithFormat:@"You will exceed the maximum limit of %lu characters by %u. Please shorten the notes", (unsigned long)maxCharacters, maxCharacters - length]];
        return;
    }
    if (self.delegate) {
        [self.delegate didFinishEditingLongText:self.textView.text withItemMode:textItemTypeMode];
    }
    [self dismissViewControllerAnimated:YES completion:Nil];
}

-(void)didClickCancelButton:(id)sender {
    [self dismissViewControllerAnimated:YES completion:Nil];
}

//-(BOOL)textFieldShouldReturn:(UITextField *)textField {
//    [self.textView resignFirstResponder];
//    return NO;
//}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

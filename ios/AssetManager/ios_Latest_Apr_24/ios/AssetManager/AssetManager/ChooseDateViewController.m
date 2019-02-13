
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
//  ChooseDateViewController.m
//  AssetManager
//

#import "ChooseDateViewController.h"
#import "UIColor+Cireson.h"

@implementation ChooseDateViewController

@synthesize pickerView, selectedDate, delegate, onoff, affectedProperty;

-(void)viewDidLoad {
    [super viewDidLoad];
    if (self.selectedDate != Nil) {
        self.pickerView.date = selectedDate;
    }
    else {
        self.pickerView.date = [NSDate dateWithTimeIntervalSinceNow:0];
    }
    self.view.backgroundColor = [UIColor backgroundColor];
    self.pickerView.backgroundColor = [UIColor clearColor];
    [self switchStateChanged:self.onoff];
}

-(IBAction)didClickClearButton:(id)sender{
    self.pickerView.date = [NSDate dateWithTimeIntervalSinceNow:0];
}

-(IBAction)didClickDoneButton:(id)sender {
    if (self.delegate) {
        NSDate* selected = self.onoff.on ? Nil : self.pickerView.date;
        [self.delegate didFinishChoosingDate:selected affectedProperty:self.affectedProperty];
    }
    [self dismissViewControllerAnimated:YES completion:nil];
}

-(IBAction)switchStateChanged:(id)sender {
    self.pickerView.userInteractionEnabled = !self.onoff.on;
    self.pickerView.enabled = !self.onoff.on;
}

- (IBAction)didClickCancelbutton:(id)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}

@end


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
//  SettingsViewController.m
//  AssetManager
//

#import "SettingsViewController.h"
#import "UIColor+Cireson.h"
#import "User.h"
#import "UIAlertView+Cireson.h"
#import "UIHelper.h"
#import "BluetoothDeviceViewController.h"

static const CGFloat KEYBOARD_ANIMATION_DURATION = 0.3;
static const CGFloat MINIMUM_SCROLL_FRACTION = 0.2;
static const CGFloat MAXIMUM_SCROLL_FRACTION = 0.6;
static const CGFloat PORTRAIT_KEYBOARD_HEIGHT = 216;
static const CGFloat LANDSCAPE_KEYBOARD_HEIGHT = 162;
CGFloat animatedDistance;

@interface SettingsViewController () <UIAlertViewDelegate, BluetoothDeviceViewControllerDelegate>

@end

@implementation SettingsViewController

@synthesize urlField, usernameField, passwordField, prefixesView, logoutButton, bluetoothSelectLabel, bluetoothButton;

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
	// Do any additional setup after loading the view.
	
	self.view.backgroundColor = [UIColor backgroundColor];
    self.logoutButton.layer.cornerRadius = 5;
    self.bluetoothButton.layer.cornerRadius = 5;
    
	passwordField.secureTextEntry = YES;
	
	UITapGestureRecognizer * tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTap:)];
	[tap setCancelsTouchesInView:NO];
    [self.view addGestureRecognizer:tap];
}

-(void)handleTap:(UIEvent*)event {
    [self.view endEditing:YES];
}

-(NSString *)titleForBluetoothLabel {
    NSString *bluetoothDeviceName = [DEFAULTS objectForKey:SelectedBluetoothDeviceName];
    if (bluetoothDeviceName == nil) {
        bluetoothDeviceName = NSLocalizedString(@"None", Nil);
    }
    return [NSString stringWithFormat:NSLocalizedString(@"Currently using: %@", Nil), bluetoothDeviceName];
}

#pragma mark TextFieldDelegate

-(void)textFieldDidBeginEditing:(UITextField *)textField {
	CGRect textFieldRect =
	[self.view.window convertRect:textField.bounds fromView:textField];
    CGRect viewRect =
	[self.view.window convertRect:self.view.bounds fromView:self.view];
	
	CGFloat midline = textFieldRect.origin.y + 0.5 * textFieldRect.size.height;
	CGFloat numerator =
    midline - viewRect.origin.y
	- MINIMUM_SCROLL_FRACTION * viewRect.size.height;
	CGFloat denominator =
    (MAXIMUM_SCROLL_FRACTION - MINIMUM_SCROLL_FRACTION)
	* viewRect.size.height;
	CGFloat heightFraction = numerator / denominator;
	
	if (heightFraction < 0.0)
	{
		heightFraction = 0.0;
	}
	else if (heightFraction > 1.0)
	{
		heightFraction = 1.0;
	}
	
	UIInterfaceOrientation orientation =
    [[UIApplication sharedApplication] statusBarOrientation];
	if (orientation == UIInterfaceOrientationPortrait ||
		orientation == UIInterfaceOrientationPortraitUpsideDown)
	{
		animatedDistance = floor(PORTRAIT_KEYBOARD_HEIGHT * heightFraction);
	}
	else
	{
		animatedDistance = floor(LANDSCAPE_KEYBOARD_HEIGHT * heightFraction);
	}
	
	CGRect viewFrame = self.view.frame;
    viewFrame.origin.y -= animatedDistance;
	
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationBeginsFromCurrentState:YES];
    [UIView setAnimationDuration:KEYBOARD_ANIMATION_DURATION];
	
    [self.view setFrame:viewFrame];
	
    [UIView commitAnimations];
}

- (void)textFieldDidEndEditing:(UITextField *)textField
{
    CGRect viewFrame = self.view.frame;
    viewFrame.origin.y += animatedDistance;
	
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationBeginsFromCurrentState:YES];
    [UIView setAnimationDuration:KEYBOARD_ANIMATION_DURATION];
	
    [self.view setFrame:viewFrame];
	
    [UIView commitAnimations];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

#pragma mark TextViewDelegate

-(void)textViewDidBeginEditing:(UITextView *)textView {
	CGRect textFieldRect =
	[self.view.window convertRect:textView.bounds fromView:textView];
    CGRect viewRect =
	[self.view.window convertRect:self.view.bounds fromView:self.view];
	
	CGFloat midline = textFieldRect.origin.y + 0.5 * textFieldRect.size.height;
	CGFloat numerator =
    midline - viewRect.origin.y
	- MINIMUM_SCROLL_FRACTION * viewRect.size.height;
	CGFloat denominator =
    (MAXIMUM_SCROLL_FRACTION - MINIMUM_SCROLL_FRACTION)
	* viewRect.size.height;
	CGFloat heightFraction = numerator / denominator;
	
	if (heightFraction < 0.0)
	{
		heightFraction = 0.0;
	}
	else if (heightFraction > 1.0)
	{
		heightFraction = 1.0;
	}
	
	UIInterfaceOrientation orientation =
    [[UIApplication sharedApplication] statusBarOrientation];
	if (orientation == UIInterfaceOrientationPortrait ||
		orientation == UIInterfaceOrientationPortraitUpsideDown)
	{
		animatedDistance = floor(PORTRAIT_KEYBOARD_HEIGHT * heightFraction);
	}
	else
	{
		animatedDistance = floor(LANDSCAPE_KEYBOARD_HEIGHT * heightFraction);
	}
	
	CGRect viewFrame = self.view.frame;
    viewFrame.origin.y -= animatedDistance;
	
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationBeginsFromCurrentState:YES];
    [UIView setAnimationDuration:KEYBOARD_ANIMATION_DURATION];
	
    [self.view setFrame:viewFrame];
	
    [UIView commitAnimations];
}

-(void)didFinishSelectingBluetoothDevice:(EAAccessory*)accessory {
    [self.bluetoothSelectLabel setText:[self titleForBluetoothLabel]];
}

-(void)textViewDidEndEditing:(UITextView *)textView {
	CGRect viewFrame = self.view.frame;
    viewFrame.origin.y += animatedDistance;
	
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationBeginsFromCurrentState:YES];
    [UIView setAnimationDuration:KEYBOARD_ANIMATION_DURATION];
	
    [self.view setFrame:viewFrame];
	
    [UIView commitAnimations];
}

-(BOOL)textViewShouldEndEditing:(UITextView *)textView {
	[textView resignFirstResponder];
    return YES;
}

-(void)backButtonClicked:(id)sender {
	[self.navigationController popViewControllerAnimated:YES];
}

-(void)logoutButtonClicked:(id)sender {
    NSString* msg = NSLocalizedString(@"Are you sure you want to logout?", nil);
    UIAlertView* av = [UIAlertView showWarningWithMsg:msg];
    av.delegate = self;
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (buttonIndex == 0) {
        [User logout];
        [UIHelper launchLoginWithParentViewController:self];
    }
    else {
        
    }
}

-(void)saveButtonClicked:(id)sender {
}

-(void)bluetoothButtonClicked:(id)sender {
    [UIHelper launchBluetoothPicker:self];
}

-(void)viewWillAppear:(BOOL)animated {
    [self.bluetoothSelectLabel setText:[self titleForBluetoothLabel]];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

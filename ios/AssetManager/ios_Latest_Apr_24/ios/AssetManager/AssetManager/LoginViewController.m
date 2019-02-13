
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
//  LoginViewController.m
//  AssetManager
//

#define TEST_SERVER

#import "LoginViewController.h"
#import "UIColor+Cireson.h"
#import "UIAlertView+Cireson.h"
#import "LoginRequest.h"
#import "UIHelper.h"
#import "MBProgressHUD.h"
#import "AppDelegate.h"

@interface LoginViewController () {
}
@property (nonatomic, strong) LoginRequest* loginRequest;
-(void)handleTap:(UIEvent*)event;
@end

@implementation LoginViewController

@synthesize urlField, passwordField, usernameField, loginButton, loginRequest;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    self.tableView.backgroundColor = [UIColor backgroundColor];
    self.loginButton.layer.cornerRadius = 5;
    
    UITapGestureRecognizer * tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTap:)];
    [self.view addGestureRecognizer:tap];
    
#ifdef TEST_SERVER
    // INTERNET
    [self.urlField setText:@"http://ciresonbc.cloudapp.net"];
    // AT OFFICE
    //[self.urlField setText:@"http://10.1.10.58:10000/"];
    
    [self.usernameField setText:@"test.analyst"];
    [self.passwordField setText:@"NextIsNow!"];
#endif
//#ifdef TEST_SERVER
//    // INTERNET
//    [self.urlField setText:@"http://173.160.128.86:11000/"];
//    // AT OFFICE
//    //[self.urlField setText:@"http://10.1.10.58:10000/"];
//    
//    [self.usernameField setText:@"CONTOSO\\travis"];
//    [self.passwordField setText:@"SMX#2001"];
//#endif
//    NSLog(@"%d",APPDELEGATE.flag);
}

-(void)handleTap:(UIEvent*)event {
    [self.view endEditing:YES];
    [self.tableView scrollRectToVisible:CGRectMake(0, 0, 1, 1) animated:YES];
}


-(void)doneButtonClicked:(id)sender {
    [self.view endEditing:YES];
	
    // validate
    UITextField* textField = nil;
    if (![self.urlField.text length]) {
        textField = self.urlField;
    }
    else if (![self.passwordField.text length]){
        textField = self.passwordField;
    }
    else if (![self.usernameField.text length]) {
        textField = self.usernameField;
    }
    
    if (textField != Nil) {
        if (![textField isFirstResponder]) {
            [textField becomeFirstResponder];
        }
        [UIAlertView showErrorWithMsg:NSLocalizedString(@"Field must not be empty", Nil)];
        return;
    }
    if (APPDELEGATE.flag == 0) {
        [self performLogin];
    }else if(APPDELEGATE.flag == 1){
        [self redirectLogin];
    }
    
    
}

-(void)performLogin {
    if (self.loginRequest != nil) {
        return;
    }
    
    __block MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.mode = MBProgressHUDModeIndeterminate;
    hud.labelText = NSLocalizedString(@"Logging in...", Nil);

    self.loginRequest = [[LoginRequest alloc] init];
    
    __weak LoginViewController* weakSelf = self;
    
    self.loginRequest.successHandler = ^(NSDictionary* response){
        [MBProgressHUD hideHUDForView:weakSelf.view animated:YES];
        weakSelf.loginRequest = Nil;
        [UIHelper launchHomeviewAfterLogin:weakSelf];
    };
    self.loginRequest.errorHandler = ^(NSError* error){
        [MBProgressHUD hideHUDForView:weakSelf.view animated:YES];
        [UIAlertView showErrorFromService:error];
        weakSelf.loginRequest = nil;
    };
    NSString *strURL = [NSString stringWithFormat:@"%@/",self.urlField.text];
    
    [self.loginRequest loginWithUrl:strURL userName:self.usernameField.text password:self.passwordField.text];
}
-(void)redirectLogin {
    if (self.loginRequest != nil) {
        return;
    }
    
    __block MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.mode = MBProgressHUDModeIndeterminate;
    hud.labelText = NSLocalizedString(@"Logging in...", Nil);
    
    self.loginRequest = [[LoginRequest alloc] init];
    
    __weak LoginViewController* weakSelf = self;
    
    self.loginRequest.successHandler = ^(NSDictionary* response){
        [MBProgressHUD hideHUDForView:weakSelf.view animated:YES];
        weakSelf.loginRequest = Nil;
        [weakSelf dismissViewControllerAnimated:YES completion:nil];
    };
    self.loginRequest.errorHandler = ^(NSError* error){
        [MBProgressHUD hideHUDForView:weakSelf.view animated:YES];
        [UIAlertView showErrorFromService:error];
        weakSelf.loginRequest = nil;
    };
    NSString *strURL = [NSString stringWithFormat:@"%@/",self.urlField.text];
    
    [self.loginRequest loginWithUrl:strURL userName:self.usernameField.text password:self.passwordField.text];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end

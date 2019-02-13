
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
//  BluetoothDeviceViewController.m
//  AssetManager
//

#import "BluetoothDeviceViewController.h"
#import "UIColor+Cireson.h"
#import <ExternalAccessory/ExternalAccessory.h>
#import "UIAlertView+Cireson.h"

@interface BluetoothDeviceViewController () <UIActionSheetDelegate>

@property (nonatomic, strong)NSString* selectedProtocolString;

@end

@implementation BluetoothDeviceViewController

@synthesize tableView, bluetoothDevices, selectedBluetoothDevice, delegate;

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor backgroundColor];
    self.tableView.backgroundColor = [UIColor backgroundColor];
    self.tableView.separatorColor = [UIColor separatorColor];
    self.tableView.allowsMultipleSelection = NO;
    
    //creating the list of dummy Bluetooth Devices.
    self.bluetoothDevices = [[EAAccessoryManager sharedAccessoryManager] connectedAccessories];
    if (self.bluetoothDevices == Nil || self.bluetoothDevices.count == 0) {
        [UIAlertView showErrorWithMsg:NSLocalizedString(@"No connected bluetooth devices found. Please pair your bluetooth scanner and refresh this screen", Nil)];
    }
    
    if (self.bluetoothDevices == Nil) {
        self.bluetoothDevices = [[NSArray alloc] init];
    }
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.bluetoothDevices.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:@"BluetoothCell" forIndexPath:indexPath];
    
    // Configure the cell...
    cell.backgroundColor = [UIColor backgroundColor];
    cell.accessoryType = UITableViewCellAccessoryNone;
    cell.textLabel.font = [UIFont systemFontOfSize:15.0];
    cell.textLabel.textColor = [UIColor whiteColor];
    EAAccessory* accessory = [self.bluetoothDevices objectAtIndex:indexPath.row];
    cell.textLabel.text = [accessory name];
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    self.selectedBluetoothDevice = [self.bluetoothDevices objectAtIndex:indexPath.row];
    UITableViewCell *cell = [self.tableView cellForRowAtIndexPath:indexPath];
    cell.accessoryType = UITableViewCellAccessoryCheckmark;
}

-(void)tableView:(UITableView *)tableView didDeselectRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [self.tableView cellForRowAtIndexPath:indexPath];
    cell.accessoryType = UITableViewCellAccessoryNone;
}

-(void)cancelButtonClicked:(id)sender {
    [self dismissViewControllerAnimated:YES completion:Nil];
}

-(void)doneButtonClicked:(id)sender {
    if (self.selectedBluetoothDevice == Nil) return;
    NSArray *protocolStrings = [self.selectedBluetoothDevice protocolStrings];
    if (protocolStrings == Nil || protocolStrings.count == 0) {
        NSString* errorMsg = NSLocalizedString(@"This device is not compatible with the app because required protocol is not defined for communication. Please select another device", Nil);
        [UIAlertView showErrorWithMsg:errorMsg];
        return;
    }
    
    if (protocolStrings.count == 1) {
        NSLog(@"Using protocol %@", [protocolStrings objectAtIndex:0]);
        self.selectedProtocolString = [protocolStrings objectAtIndex:0];
        [self save];
    }
    else {
        UIActionSheet* protocolSelectionActionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"Select Protocol", Nil) delegate:self cancelButtonTitle:Nil destructiveButtonTitle:Nil otherButtonTitles:Nil];
        for(NSString *protocolString in protocolStrings) {
            [protocolSelectionActionSheet addButtonWithTitle:protocolString];
        }
    
        [protocolSelectionActionSheet setCancelButtonIndex:[protocolSelectionActionSheet addButtonWithTitle:@"Cancel"]];
        [protocolSelectionActionSheet showInView:[self tableView]];
    }
}

-(void)save {
    if (self.selectedBluetoothDevice != Nil && self.selectedProtocolString != Nil) {
        [DEFAULTS setObject:self.selectedBluetoothDevice.name forKey:SelectedBluetoothDeviceName];
        [DEFAULTS setObject:self.selectedProtocolString forKey:SelectedProtocolStringName];
        NSLog(@"selected device: %@, protocol: %@", self.selectedBluetoothDevice.name, self.selectedProtocolString);
        [DEFAULTS synchronize];
        if (self.delegate) {
            [self.delegate didFinishSelectingBluetoothDevice:self.selectedBluetoothDevice];
        }
        [self dismissViewControllerAnimated:YES completion:Nil];
    }
}

-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (self.selectedBluetoothDevice && (buttonIndex >= 0) && (buttonIndex < [[self.selectedBluetoothDevice protocolStrings] count])) {
        
        self.selectedProtocolString = [[self.selectedBluetoothDevice protocolStrings] objectAtIndex:buttonIndex];
        [self save];
    }
}

-(void)refreshButtonClicked:(id)sender {
    NSArray* connectedDevices = [[EAAccessoryManager sharedAccessoryManager] connectedAccessories];
    if (!connectedDevices) {
        connectedDevices = [[NSArray alloc] init];
    }
    self.bluetoothDevices = connectedDevices;
    [self.tableView reloadData];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

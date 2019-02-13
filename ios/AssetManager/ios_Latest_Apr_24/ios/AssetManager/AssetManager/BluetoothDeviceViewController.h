
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
//  BluetoothDeviceViewController.h
//  AssetManager
//

#import <UIKit/UIKit.h>
@class EAAccessory;

@protocol BluetoothDeviceViewControllerDelegate
    -(void)didFinishSelectingBluetoothDevice:(EAAccessory*)accessory;
@end

@interface BluetoothDeviceViewController : CiresonViewController <UITableViewDelegate, UITableViewDataSource>

@property (nonatomic, weak) IBOutlet UITableView *tableView;

@property (nonatomic, retain) NSArray *bluetoothDevices;
@property (nonatomic, retain) EAAccessory* selectedBluetoothDevice;
@property (nonatomic, assign) id<BluetoothDeviceViewControllerDelegate>delegate;

-(IBAction)cancelButtonClicked:(id)sender;
-(IBAction)refreshButtonClicked:(id)sender;
-(IBAction)doneButtonClicked:(id)sender;


@end

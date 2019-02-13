
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
//  OverlayView.m
//  AssetManager
//

#import "OverlayView.h"

@interface OverlayView ()
-(IBAction) didClickStartScanningButton:(id)sender;
-(IBAction) didClickUsebluetoothButton:(id)sender;
@end

@implementation OverlayView

@synthesize scanButton, useBluetoothButton, delegate;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        self.scanButton.layer.cornerRadius = 10;
        self.useBluetoothButton.layer.cornerRadius = 10;
    }
    return self;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

-(void)didClickStartScanningButton: (id)sender {
    if (self.delegate && [self.delegate respondsToSelector:@selector(startScanning)]) {
        [self.delegate startScanning];
    }
}

-(void)didClickUsebluetoothButton: (id)sender {
    if (self.delegate && [self.delegate respondsToSelector:@selector(useBluetooth)]) {
        [self.delegate useBluetooth];
    }
}
@end

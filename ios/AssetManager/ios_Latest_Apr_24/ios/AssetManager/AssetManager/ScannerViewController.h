
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
//  ScannerViewController.h
//  AssetManager
//

#import <UIKit/UIKit.h>
#import "PurchaseOrder.h"


@interface ScannerViewController : CiresonViewController

{
    
    
}

@property(strong, nonatomic) NSMutableArray* scannedItems;

@property(weak, nonatomic) IBOutlet UIView* barcodeReaderView;
@property(weak, nonatomic) IBOutlet UIBarButtonItem* stopButton;
@property(weak, nonatomic) IBOutlet UITableView* tableView;
@property(weak, nonatomic) IBOutlet UIImageView* topLeftImageView;
@property(weak, nonatomic) IBOutlet UIImageView* topRightImageView;
@property(weak, nonatomic) IBOutlet UIImageView* bottomLeftImageView;
@property(weak, nonatomic) IBOutlet UIImageView* bottomRightImageView;
@property(strong, nonatomic) NSMutableArray* allowedBarcodeTypes;

// this must be set if workinng in purchase order mode.
@property(nonatomic, retain) PurchaseOrder* purchaseOrder;

@property (nonatomic, retain) IBOutlet UIBarButtonItem *nextButtonName;

-(IBAction)didClickNextButton:(id)sender;
-(IBAction)didClickCancelButton:(id)sender;
-(IBAction)didClickStopButton:(id)sender;

@end

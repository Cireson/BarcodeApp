
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
//  ScannerViewController.m
//  AssetManager
//

#import "ScannerViewController.h"
#import "UIAlertView+Cireson.h"
#import "UIColor+Cireson.h"
#import "OverlayView.h"
#import "Barcode.h"
#import <AudioToolbox/AudioToolbox.h>
#import "MBProgressHUD.h"
#import "AppDelegate.h"
#import "AssetViewController.h"
#import "AssetTableViewCell.h"
#import "Asset.h"
#import "UIImage+Cireson.h"
#import "UIColor+Cireson.h"
#import "AssetPropertiesViewController.h"
#import "ReviewEmailViewController.h"
#import "UIHelper.h"
#import "BluetoothDeviceViewController.h"
#import "EADSessionController.h"

@import AVFoundation;

@interface ScannerViewController () <UITableViewDataSource, UITableViewDelegate, OverlayViewDelegate, AVCaptureMetadataOutputObjectsDelegate, AssetViewControllerDelegate, UIAlertViewDelegate, BluetoothDeviceViewControllerDelegate>

@property(strong, nonatomic)AVAudioPlayer* audioPlayer;
@property(strong, nonatomic, readonly) NSArray* assetsBeingProcessed; // internal variable

-(BOOL)addUnique:(NSString*) newItem;
-(void)setOverlayView;
-(void)setTableHeaderView;
-(void)setupCaptureSession;
-(void)startRunning;
-(void)stopRunning;
-(void)loadBeepSound;
-(void)restartRunning;
-(BOOL)hasBeenScannedWithAsset:(Asset*)asset;
-(void)stopScanning;
-(void)searchForAssetsWithBarcodes:(NSArray*)barcodes;

// assetview controller delegate
-(void)didFinishSelectingAsset:(Asset*)asset;
-(void)didCancelSelection;
@end

@implementation ScannerViewController{
    
    OverlayView* overlayView;
    /*
     1. _captureSession – AVCaptureSession is the core media handling class in AVFoundation. It talks to the hardware to retrieve, process, and output video. A capture session wires together inputs and outputs, and controls the format and resolution of the output frames.
     
     2. _videoDevice – AVCaptureDevice encapsulates the physical camera on a device. Modern iPhones have both front and rear cameras, while other devices may only have a single camera.
     
     3. _videoInput – To add an AVCaptureDevice to a session, wrap it in an AVCaptureDeviceInput. A capture session can have multiple inputs and multiple outputs.
     
     4. _previewLayer – AVCaptureVideoPreviewLayer provides a mechanism for displaying the current frames flowing through a capture session; it allows you to display the camera output in your UI.
     5. _running – This holds the state of the session; either the session is running or it’s not.
     6. _metadataOutput - AVCaptureMetadataOutput provides a callback to the application when metadata is detected in a video frame. AV Foundation supports two types of metadata: machine readable codes and face detection.
     7. _backgroundQueue - Used for showing alert using a separate thread.
     */
    AVCaptureSession *_captureSession;
    AVCaptureDevice *_videoDevice;
    AVCaptureDeviceInput *_videoInput;
    AVCaptureVideoPreviewLayer *_previewLayer;
    BOOL _running;
    AVCaptureMetadataOutput *_metadataOutput;
    CALayer* outlineLayer;
    
    BOOL _wasRunning;
    BOOL _usingBluetooth;
    UIAlertView* _unmatchedBarcodeAlertView;
    UIAlertView* _createAssetsAlertView;
    
    // keeps track of background searches in AddAssets mode.
    NSMutableArray* _backgroundSearchErrors;
    // keeps track of currently searching assets
    NSMutableArray* _currentlySearchingBarcodes;
}

@synthesize tableView, barcodeReaderView, topLeftImageView, topRightImageView, bottomLeftImageView, bottomRightImageView, allowedBarcodeTypes, audioPlayer, scannedItems, purchaseOrder, nextButtonName, stopButton;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

#pragma mark - AV capture methods

- (void)setupCaptureSession {
    // 1
    if (_captureSession) return;
    // 2
    _videoDevice = [AVCaptureDevice
                    defaultDeviceWithMediaType:AVMediaTypeVideo];
    if (!_videoDevice) {
        NSLog(@"No video camera on this device!");
        return;
    }
    // 3
    _captureSession = [[AVCaptureSession alloc] init];
    // 4
    _videoInput = [[AVCaptureDeviceInput alloc]
                   initWithDevice:_videoDevice error:nil];
    // 5
    if ([_captureSession canAddInput:_videoInput]) {
        [_captureSession addInput:_videoInput];
    }
    // 6
    _previewLayer = [[AVCaptureVideoPreviewLayer alloc]
                     initWithSession:_captureSession];
    _previewLayer.videoGravity =
    AVLayerVideoGravityResizeAspectFill;
    
    
    // capture and process the metadata
    _metadataOutput = [[AVCaptureMetadataOutput alloc] init];
    dispatch_queue_t metadataQueue =
    dispatch_queue_create("com.1337labz.featurebuild.metadata", 0);
    [_metadataOutput setMetadataObjectsDelegate:self
                                          queue:metadataQueue];
    if ([_captureSession canAddOutput:_metadataOutput]) {
        [_captureSession addOutput:_metadataOutput];
    }
    
    // set default allowed barcode types
    self.allowedBarcodeTypes = [NSMutableArray new];
    [self.allowedBarcodeTypes addObject:@"org.iso.PDF417"];
    [self.allowedBarcodeTypes addObject:@"org.gs1.UPC-E"];
    [self.allowedBarcodeTypes addObject:@"org.iso.Aztec"];
    [self.allowedBarcodeTypes addObject:@"org.iso.Code39"];
    [self.allowedBarcodeTypes addObject:@"org.iso.Code39Mod43"];
    [self.allowedBarcodeTypes addObject:@"org.gs1.EAN-13"];
    [self.allowedBarcodeTypes addObject:@"org.gs1.EAN-8"];
    [self.allowedBarcodeTypes addObject:@"com.intermec.Code93"];
    [self.allowedBarcodeTypes addObject:@"org.iso.Code128"];
}

- (void)startRunning {
    if (_running) return;
    [_captureSession startRunning];
    _metadataOutput.metadataObjectTypes =
    _metadataOutput.availableMetadataObjectTypes;
    self.stopButton.enabled = YES;
    _running = YES;
}
- (void)stopRunning {
    if (!_running) return;
    self.stopButton.enabled = NO;
    [_captureSession stopRunning];
    _running = NO;
}

-(void)loadBeepSound{
    NSString *beepFilePath = [[NSBundle mainBundle] pathForResource:@"beep" ofType:@"mp3"];
    NSURL *beepURL = [NSURL URLWithString:beepFilePath];
    NSError *error;
    
    audioPlayer = [[AVAudioPlayer alloc] initWithContentsOfURL:beepURL error:&error];
    if (error) {
        NSLog(@"Could not play beep file.");
        NSLog(@"%@", [error localizedDescription]);
    }
    else{
        [audioPlayer prepareToPlay];
    }
}


-(BOOL)addUnique:(NSString *)newItem {
    
    // we already have the item in the non-matched list.
    for(NSString* item in self.scannedItems) {
        if (NSOrderedSame == [item compare:newItem options:NSCaseInsensitiveSearch]) {
            return NO;
        }
    }
    
    // has already been matched to an asset.
    for(Asset* asset in self.assetsBeingProcessed) {
        if (NSOrderedSame == [newItem compare:asset.matchedBarcodeData options:NSCaseInsensitiveSearch]) {
            return NO;
        }
    }
    
    // now find out if the new item matches any of the asset.
    BOOL matched = NO;
    for (Asset* asset in self.assetsBeingProcessed) {
        if (!asset.hasMatchedBarcodeData) {
            if ([asset matchesBarcodeWithData:newItem]) {
                asset.matchedBarcodeData = newItem;
                matched = YES;
                break;
            }
        }
    }
    
    if (!matched) {
        [self.scannedItems insertObject:newItem atIndex:0];
    }
    
    [self.tableView reloadData];
    NSIndexPath *path = [NSIndexPath indexPathForItem:0 inSection:0];
    [self.tableView scrollToRowAtIndexPath:path atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    
    if (APPDELEGATE.workFlow == SwapAsset) {
        [self stopScanning];
        self.nextButtonName.enabled = YES;
        [overlayView.scanButton setEnabled:NO];
        [overlayView.useBluetoothButton setEnabled:NO];
    }
    else if (APPDELEGATE.workFlow == AddAssets) {
        AddAssetsData* data = (AddAssetsData*)self.workflowData;
        __strong ScannerViewController* weakSelf = self;
        GenericCiresonApiCall* searchRequest = [[CiresonApiService sharedService] getSearchAssetsWithBarcodeValues:[NSArray arrayWithObject:newItem]];
        searchRequest.successHandler = ^(id response){
            [_currentlySearchingBarcodes removeObject:newItem];
            NSArray* searchResults = [Asset loadFromArray:response];
            if (searchResults != Nil && searchResults.count > 0) {
                [data.foundAssets addObject:[searchResults firstObject]];
            }
            else {
                [data.notFoundAssets addObject:newItem];
            }
                
            [weakSelf.tableView reloadData];
        };
        searchRequest.errorHandler = ^(NSError* error){
            if ([_currentlySearchingBarcodes containsObject:newItem]) {
                // user could have deleted the entry while searching.
                [_backgroundSearchErrors addObject:newItem];
            }
            [_currentlySearchingBarcodes removeObject:newItem];
            [weakSelf.tableView reloadData];
        };
        [_currentlySearchingBarcodes addObject:newItem];
        [searchRequest call];
    }
    return YES;
}


-(void)didClickStopButton:(id)sender {
    [self stopScanning];
}

-(void)didClickNextButton:(id)sender {
    if (APPDELEGATE.workFlow == ReceiveAssetByPO) {
        BOOL unmatchedExists = NO;
        BOOL atLeastOneMatchExists = NO;
        for(Asset* asset in self.assetsBeingProcessed) {
            if (!asset.hasMatchedBarcodeData) {
                unmatchedExists = YES;
                break;
            }
            
            if (asset.hasMatchedBarcodeData) {
                atLeastOneMatchExists = YES;
                break;
            }
        }
        if (!atLeastOneMatchExists) {
            [UIAlertView showErrorWithMsg:NSLocalizedString(@"Please match at least one asset", Nil)];
            return;
        }
        if (unmatchedExists || self.scannedItems.count > 0) {
            _unmatchedBarcodeAlertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Confirmation", Nil) message:NSLocalizedString(@"There are unknown barcodes or unmatched assets, are you sure you want to continue?", Nil) delegate:self cancelButtonTitle:NSLocalizedString(@"No", Nil) otherButtonTitles:NSLocalizedString(@"Yes", Nil), nil];
            [_unmatchedBarcodeAlertView show];
        }
        else {
            [self gotoNextView];
        }
    }
    else if (APPDELEGATE.workFlow == AddAssets) {
        
        if (_currentlySearchingBarcodes.count > 0) {
            [UIAlertView showErrorWithMsg:
             NSLocalizedString(@"We are still checking if the assets you have scanned already exist in the system, please try again in a few moments", Nil)];
            return;
        }
        
        AddAssetsData* data = (AddAssetsData*)self.workflowData;
        if (self.scannedItems.count == 0) {
            [UIAlertView showErrorWithMsg:NSLocalizedString(@"Please scan at least one asset", Nil)];
            return;
        }
        if (data.notFoundAssets.count == 0) {
            [UIAlertView showErrorWithMsg:NSLocalizedString(@"No new hardware asset can be created, if there were errors, delete the entry and try again", Nil)];
            return;
        }
        
        _createAssetsAlertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Confirmation", Nil) message:NSLocalizedString(@"New Assets will be created with the scanned barcodes. Were the barcodes scanned with the asset tag or serial number of the hardware asset?", Nil) delegate:self cancelButtonTitle:NSLocalizedString(@"Asset Tag", Nil) otherButtonTitles:NSLocalizedString(@"Serial Number", Nil), nil];
        [_createAssetsAlertView show];

    }
    else if (APPDELEGATE.workFlow == InventoryAudit) {
        [self gotoNextView];
    }
    else {
        [self searchForAssetsWithBarcodes:self.scannedItems];
    }
}

-(void)searchForAssetsWithBarcodes:(NSArray*)barcodes {
    if (barcodes == Nil || barcodes.count == 0) {
        if (APPDELEGATE.workFlow == EditAsset) {
            [UIAlertView showErrorWithMsg:NSLocalizedString(@"Please scan at least one asset to edit.", Nil)];
            return;
        }
        else if (APPDELEGATE.workFlow == DisposeAsset) {
            [UIAlertView showErrorWithMsg:NSLocalizedString(@"Please scan at least one asset to dispose.", Nil)];
            return;
        }
    }
    else {
        __block MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.mode = MBProgressHUDModeIndeterminate;
        hud.labelText = NSLocalizedString(@"Searching assets...", Nil);
        
        __weak ScannerViewController* weakSelf = self;
        GenericCiresonApiCall* searchRequest = [[CiresonApiService sharedService] getSearchAssetsWithBarcodeValues:barcodes];
        
        searchRequest.successHandler = ^(id response){
           
            [MBProgressHUD hideHUDForView:weakSelf.view animated:YES];
            NSArray* searchResults = [Asset loadFromArray:response];
            if (searchResults == Nil || searchResults.count == 0) {
                [UIAlertView showErrorWithMsg:NSLocalizedString(@"No assets found with the scanned serial number or asset tag. Please scan assets that exist in the system.", Nil)];
                return;
            }
            
            if (APPDELEGATE.workFlow == SwapAsset) {
                SwapAssetData* data = (SwapAssetData*)self.workflowData;
                if (data.firstAsset == Nil) {
                    data.firstAsset = [searchResults firstObject];
                }
                else {
                    data.secondAsset = [searchResults firstObject];
                }
                [UIHelper launchEditAssetPropertiesWithParentViewController:self workflowData:self.workflowData];
            }
            else if (APPDELEGATE.workFlow == EditAsset || APPDELEGATE.workFlow == DisposeAsset) {
                WorkflowData* data = nil;
                if (APPDELEGATE.workFlow == EditAsset) {
                    EditAssetsData* ead = [[EditAssetsData alloc] init];
                    ead.assetsToEdit = searchResults;
                    data = ead;
                }
                else {
                    DisposeAssetsData* dad = [[DisposeAssetsData alloc] init];
                    dad.assetsToDispose = searchResults;
                    for(Asset *a in searchResults){
                        
                        if ([a.statusId isEqualToString:@"020822bd-7d3a-4c0f-1be4-325ef1aa52b4"]) {
                            [UIAlertView showErrorWithMsg:NSLocalizedString(@"This asset has already been disposed", Nil)];
                        }else{
                            data = dad;
                        }
                    }
                    
                }
                NSString *subtitle = [NSString stringWithFormat:NSLocalizedString(@"Found %lu of %lu scanned items", Nil), (unsigned long) searchResults.count, (unsigned long) self.scannedItems.count];
                //need to change the title and subtitle here.
                if (data !=nil) {
                    [UIHelper launchAssetViewControllerWithParentViewController:weakSelf title:@"Search Results" subtitle:subtitle workflowData:data];
                }
                
            }
            else {
                NSAssert(false, @"update logic here for other scenarios");
            }
        };
        searchRequest.errorHandler = ^(NSError* error){
            [MBProgressHUD hideHUDForView:weakSelf.view animated:YES];
            [UIAlertView showErrorFromService:error];
            [User logout];
            [UIHelper presentLoginWithParentViewController:self];
        };
        
        [searchRequest call];
    }
    
}


-(void)gotoNextView {
    [self stopScanning];

    //check for the workflow
	if (APPDELEGATE.workFlow == ReceiveAssetByPO) {
        for (Asset* a in self.assetsBeingProcessed) {
            if (a.matchedBarCodeAction == MatchedBarCodeActionAssignToSerialNumber) {
                a.serialNumber = a.matchedBarcodeData;
            }
            else {
                a.tag = a.matchedBarcodeData;
            }
        }
        [UIHelper launchEditAssetPropertiesWithParentViewController:self workflowData:self.workflowData];
	}
	else if (APPDELEGATE.workFlow == InventoryAudit){
		NSMutableArray *notInventoriedAssets = [[NSMutableArray alloc] init];
		NSMutableArray *inventoriedAssets = [[NSMutableArray alloc] init];
		NSMutableArray *noMatchAssets = [[NSMutableArray alloc] initWithArray:self.scannedItems];
        
		for (Asset *asset in self.assetsBeingProcessed) {
			if (asset.needsBarcodeMatching) {
				[notInventoriedAssets addObject:asset];
			}
			else {
				[inventoriedAssets addObject:asset];
			}
		}
        
        NSArray *assets = [NSArray arrayWithObjects:inventoriedAssets, notInventoriedAssets, noMatchAssets, nil];
		[UIHelper launchReviewEmailViewControllerWithAssets:assets parentViewController:self];
	}
    else if (APPDELEGATE.workFlow == AddAssets) {
        [UIHelper launchEditAssetPropertiesWithParentViewController:self workflowData:self.workflowData];
    }
    else {
        NSAssert(false, @"Update logic to handle the new workflow");
    }

}

#pragma UIAlertViewDelegate
// Called when a button is clicked. The view will be automatically dismissed after this call returns
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (alertView == _unmatchedBarcodeAlertView) {
        _unmatchedBarcodeAlertView = Nil;
        if (buttonIndex != 0) {
            [self gotoNextView];
        }
    }
    else if (alertView == _createAssetsAlertView) {
        AddAssetsData* data = (AddAssetsData*)self.workflowData;
        data.useSerialNumber = buttonIndex == 1;
        [self gotoNextView];
    }
}


-(void)didClickCancelButton:(id)sender {
    [self stopRunning];
	[self.navigationController popViewControllerAnimated:YES];
    // cancel all operations
    [[[AFHTTPRequestOperationManager manager] operationQueue] cancelAllOperations];
    if (APPDELEGATE.workFlow == AddAssets) {
        AddAssetsData* data = (AddAssetsData*)self.workflowData;
        [data reset];
    }
}

-(void)addTestData {
    if (APPDELEGATE.workFlow == SwapAsset) {
        SwapAssetData* data = (SwapAssetData*)self.workflowData;
        if (data.firstAsset == nil) {
            [self addUnique:@"test233"];
        }
        else {
            [self addUnique:@"12356"];
        }
    }
    else {
        [self addUnique:@"test233"];
        [self addUnique:@"12356"];
        [self addUnique:@"Hello4"];
        [self addUnique:@"Hello1"];
        [self addUnique:@"Hello2"];
        [self addUnique:@"Hello4"];
        [self addUnique:@"Hello5"];
        [self addUnique:@"world"];
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self.tableView setSeparatorColor:[UIColor separatorColor]];
    
    if (!self.scannedItems) {
        self.scannedItems = [[NSMutableArray alloc] init];
    }
    
    _backgroundSearchErrors = [[NSMutableArray alloc] init];
    _currentlySearchingBarcodes = [[NSMutableArray alloc] init];
    self.stopButton.enabled = NO;
    
    if (APPDELEGATE.workFlow == ReceiveAssetByPO) {
        ReceiveAssetsData* data = (ReceiveAssetsData*)self.workflowData;
        self.purchaseOrder = data.purchaseOrder;
    }
    else if (APPDELEGATE.workFlow == DisposeAsset || APPDELEGATE.workFlow == EditAsset || APPDELEGATE.workFlow == InventoryAudit || APPDELEGATE.workFlow == AddAssets) {
        self.tableView.allowsSelection = NO;
    }
    else if (APPDELEGATE.workFlow == SwapAsset) {
        self.tableView.allowsSelection = NO;
        self.nextButtonName.enabled = NO;
    }
    
    // test data
    /*NSArray* testData = [NSMutableArray arrayWithObjects:
                         @"54327",
                         @"12357",
                         @"54328",
                         @"12350",
                         @"12356",
                         @"1232134234234234",
                         @"test233",
                         nil];
    
    [self performSelector:@selector(addTestData) withObject:Nil afterDelay:1];
    
    for (NSString* item in testData) {
        [self addUnique:item];
    }*/

    //end test data
    
    [self setupCaptureSession];
    _previewLayer.frame = self.barcodeReaderView.bounds;
    [self.barcodeReaderView.layer addSublayer:_previewLayer];

    [self loadBeepSound];
    [self.tableView setBackgroundColor:[UIColor backgroundColor]];
	
    [self setTableHeaderView];
    [self setOverlayView];
    
    [self.tableView reloadData];
}

-(NSArray*)assetsBeingProcessed {
    if (APPDELEGATE.workFlow == ReceiveAssetByPO) {
        ReceiveAssetsData* data = (ReceiveAssetsData*)self.workflowData;
        self.purchaseOrder = data.purchaseOrder;
        return self.purchaseOrder.associatedAssets;
    }
    else if (APPDELEGATE.workFlow == InventoryAudit) {
        InventoryAuditData* data = (InventoryAuditData*)self.workflowData;
        return data.assets;
    }
    return Nil;
}

-(void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [self stopScanning];
}

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(bluetoothDataReceived:) name:EADSessionDataReceivedNotification object:nil];
}

-(void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:EADSessionDataReceivedNotification object:nil];
}

-(void) setTableHeaderView {
    self.tableView.tableHeaderView = [[UITextField alloc] initWithFrame:CGRectMake(0, 0, self.tableView.frame.size.width, 30)];
    [self.tableView.tableHeaderView setBackgroundColor:[UIColor clearColor]];
    UITextField* label = (UITextField*)self.tableView.tableHeaderView;
    label.contentHorizontalAlignment = UIControlContentHorizontalAlignmentCenter;
    label.textAlignment = NSTextAlignmentCenter;
    label.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    
    label.font = [UIFont boldSystemFontOfSize:16];
    label.userInteractionEnabled = NO;
    label.textColor = [UIColor whiteColor];
    self.tableView.tableHeaderView.backgroundColor = [UIColor backgroundHeaderColor];
    
    if (APPDELEGATE.workFlow == DisposeAsset) {
        label.text = [NSString stringWithFormat:NSLocalizedString(@"Scan Assets to Dispose", Nil)];
    }
    else if (APPDELEGATE.workFlow == EditAsset) {
        label.text = [NSString stringWithFormat:NSLocalizedString(@"Scan Assets to Edit", Nil)];
    }
    else if (APPDELEGATE.workFlow == SwapAsset) {
        SwapAssetData* data = (SwapAssetData*)self.workflowData;
        if (data.firstAsset == Nil) {
            label.text = [NSString stringWithFormat:NSLocalizedString(@"Scan Asset to Swap", Nil)];
        }
        else
        {
            label.text = [NSString stringWithFormat:NSLocalizedString(@"Scan Asset to Replace With", Nil)];
        }
    }
    else if (APPDELEGATE.workFlow == ReceiveAssetByPO) {
        label.Text = [NSString stringWithFormat:NSLocalizedString(@"Match Assets for %@", Nil), self.purchaseOrder.displayName];
    }
    else if (APPDELEGATE.workFlow == AddAssets) {
        label.Text = [NSString stringWithFormat:NSLocalizedString(@"Scan Assets to Add", Nil)];
    }
    else {
        label.Text = NSLocalizedString(@"Match Assets", nil);
    }
}

-(void)setOverlayView {
    
    if (overlayView != nil) {
        return;
    }
    
    NSArray *nibContents = [[NSBundle mainBundle] loadNibNamed:@"OverlayView"
                                                         owner:self
                                                      options:nil];
    overlayView = (OverlayView*)[nibContents objectAtIndex:0];
    overlayView.frame = self.view.frame;
    overlayView.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y, self.view.frame.size.width, self.view.frame.size.height/2.0);
    overlayView.delegate = self;
    [self.barcodeReaderView addSubview:overlayView];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    NSInteger count = self.scannedItems.count;
    if (self.assetsBeingProcessed != nil) {
        count += self.assetsBeingProcessed.count;
    }
    return count;
}

// Row display. Implementers should *always* try to reuse cells by setting each cell's reuseIdentifier and querying for available reusable cells with dequeueReusableCellWithIdentifier:
// Cell gets various attributes set automatically based on table (separators) and data source (accessory views, editing controls)

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (indexPath.row >= self.scannedItems.count) {
        NSInteger index = indexPath.row;
        if (self.scannedItems.count > 0) {
            index = indexPath.row - self.scannedItems.count;
        }
        Asset* asset = [self.assetsBeingProcessed objectAtIndex:index];
        AssetTableViewCell* cell = [self.tableView dequeueReusableCellWithIdentifier:@"AssetCell"];
        
        if(cell == nil){
            cell = [[AssetTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"AssetCell"];
        }
        cell.backgroundColor = [UIColor backgroundColor];
        cell.assetNameLabel.text = asset.displayName;
        cell.assetTypeLabel.text = asset.assetTypeDisplayName;
        cell.backgroundColor = [UIColor clearColor];
        cell.statusImageView.image = Nil;
        if ([self hasBeenScannedWithAsset:asset]) {
            cell.statusImageView.image = [UIImage checkmarkImage];
        }
        else {
            cell.statusImageView.image = [UIImage questionMarkImage];
        }
        cell.accessoryType = UITableViewCellAccessoryNone;
        return cell;
        
    }
    else {
        UITableViewCell* cell = [self.tableView dequeueReusableCellWithIdentifier:@"ScannerCell"];
        if (!cell) {
            cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"ScannerCell"];
        }
        
        if (APPDELEGATE.workFlow == ReceiveAssetByPO ||APPDELEGATE.workFlow == InventoryAudit || APPDELEGATE.workFlow == DisposeAsset || APPDELEGATE.workFlow == SwapAsset || APPDELEGATE.workFlow == EditAsset || APPDELEGATE.workFlow == AddAssets) {
            [cell setAccessoryType:UITableViewCellAccessoryNone];
        }
        
        NSString* barcode = [self.scannedItems objectAtIndex:indexPath.row];
        
        if (APPDELEGATE.workFlow == ReceiveAssetByPO) {
            cell.textLabel.text = [NSString stringWithFormat:NSLocalizedString(@"%@ (unknown)", nil), barcode];
        }
        else {
            cell.textLabel.text = [self.scannedItems objectAtIndex:indexPath.row];
        }
        
        if (APPDELEGATE.workFlow == AddAssets) {
            AddAssetsData* data = (AddAssetsData*)self.workflowData;
            bool found = [data findBarcode:barcode];
            UIImage* image = Nil;
            if (found) {
                // already in the system.
                image = [UIImage checkmarkImage];
            }
            else if ([_currentlySearchingBarcodes indexOfObject:barcode] != NSNotFound) {
                cell.textLabel.text =
                [NSString stringWithFormat:NSLocalizedString(@"%@ (searching...)", nil), barcode];
            }
            else if ([_backgroundSearchErrors indexOfObject:barcode] != NSNotFound) {
                image = Nil;
                cell.textLabel.text =
                    [NSString stringWithFormat:NSLocalizedString(@"%@ (error)", nil), barcode];
            }
//            else {
//                image = [UIImage plusImage];
//            }
            [cell setAccessoryType:UITableViewCellAccessoryCheckmark];
            UIImageView *checkmark = [[UIImageView alloc] initWithImage:image];
            checkmark.contentMode = UIViewContentModeScaleAspectFit ;
            CGRect frame = checkmark.frame;
            frame.size = CGSizeMake(37, 34);
            checkmark.frame = frame;
            cell.accessoryView = checkmark;
        }
        return cell;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row >= self.scannedItems.count) {
        return;
    }
    
    if (APPDELEGATE.workFlow == ReceiveAssetByPO) {
        NSString* barcodeToMatch = [self.scannedItems objectAtIndex:indexPath.row];
        NSMutableArray* assetsToChooseFrom = [[NSMutableArray alloc] init];
        for (Asset* asset in self.assetsBeingProcessed) {
            if (asset.needsBarcodeMatching) {
                [assetsToChooseFrom addObject:asset];
            }
        }
        
        if (assetsToChooseFrom.count == 0) {
            [UIAlertView showErrorWithMsg:NSLocalizedString(@"All assets for this purchase order have been assigned", Nil)];
            return;
        }
        
        WorkflowData* data = Nil;
        if (APPDELEGATE.workFlow == ReceiveAssetByPO) {
            ReceiveAssetsData* tmp = (ReceiveAssetsData*)self.workflowData;
            tmp.assetsToChooseFrom = assetsToChooseFrom;
            tmp.barcodeToMatch = barcodeToMatch;
            data = tmp;
        }
        
        [UIHelper launchAssetViewControllerWithParentViewController:self title:NSLocalizedString(@"Match Asset for Barcode", Nil) subtitle:barcodeToMatch workflowData:data];
        
        _wasRunning = _running;
        [self stopRunning];
    }
}

// Individual rows can opt out of having the -editing property set for them. If not implemented, all rows are assumed to be editable.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row < self.scannedItems.count) {
        return YES;
    }
    return NO;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString* barcode = [self.scannedItems objectAtIndex:indexPath.row];
    [self.scannedItems removeObjectAtIndex:indexPath.row];
    // Note: only valid during AddAssets workflow.
    if (_currentlySearchingBarcodes) {
        [_currentlySearchingBarcodes removeObject:barcode];
        [_backgroundSearchErrors removeObject:barcode];
    }
    [self.tableView reloadData];
}

#pragma -- OverlayView delegate

-(void)startScanning {
    [self startRunning];
    [overlayView setHidden:YES];
}

-(void)stopScanning {
    [self stopRunning];
    
    [overlayView setHidden:NO];
}

-(void)useBluetooth {
    NSString *bluetoothDeviceName = [DEFAULTS objectForKey:SelectedBluetoothDeviceName];
    NSString *bluetoothProtocol = [DEFAULTS objectForKey:SelectedProtocolStringName];
    if (!bluetoothDeviceName || !bluetoothProtocol || [bluetoothProtocol length] == 0 || [bluetoothDeviceName length] == 0) {
        [UIHelper launchBluetoothPicker:self];
    }
    else {
        EAAccessory* accessoryToConnect = Nil;
        // find the accessory in the connected accessory list.
        NSArray* connectedDevices = [[EAAccessoryManager sharedAccessoryManager] connectedAccessories];
        for (EAAccessory* accessory in connectedDevices) {
            if ([bluetoothDeviceName compare:accessory.name options:NSCaseInsensitiveSearch] == 0) {
                accessoryToConnect = accessory;
                break;
            }
        }
        if (accessoryToConnect != Nil) {
            if ([EADSessionController sharedController].accessory == accessoryToConnect) {
                NSLog(@"Already connected");
                [self showBluetoothReady];
                return; // already connected
            }
            else {
                EADSessionController* sessionController = [EADSessionController sharedController];
                if (sessionController.accessory) {
                    [sessionController closeSession];
                }
                [[EADSessionController sharedController] setupControllerForAccessory:accessoryToConnect withProtocolString:bluetoothProtocol];
                if (![[EADSessionController sharedController] openSession]) {
                    [UIAlertView showErrorWithMsg:NSLocalizedString(@"Unable to communicate with the bluetooth scanner, please check if it is connected to your mobile device", Nil)];
                }
                else {
                    [self showBluetoothReady];
                }
            }
        }
        else {
            [UIAlertView showErrorWithMsg:NSLocalizedString(@"Unable to communicate with the bluetooth scanner, please check if it is connected to your mobile device", Nil)];
        }
    }
}

-(void)showBluetoothReady {
    MBProgressHUD* hud = [MBProgressHUD HUDForView:self.view];
    if (Nil == hud) {
        hud = [MBProgressHUD showHUDAddedTo:self.view animated:NO];
    }
    hud.mode = MBProgressHUDModeText;
    hud.labelText = NSLocalizedString(@"Scanner Initialized", Nil);
    [hud show:YES];
    [hud hide:YES afterDelay:1.5];
}

#pragma BluetoothViewControllerDelegate
-(void)didFinishSelectingBluetoothDevice:(EAAccessory *)accessory {
    [self useBluetooth];
}

#pragma UITableViewDelegate
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    // rendering unknown barcode.
    if (indexPath.row < self.scannedItems.count) {
        return 44.0f;
    }
    // rendering asset.
    return 66.0f;
}

-(BOOL)hasBeenScannedWithAsset:(Asset*)asset {
    return asset.hasMatchedBarcodeData;
}

#pragma mark - AssetViewController
-(void)didFinishSelectingAsset:(Asset*)asset{
    if (_wasRunning) {
        [self startRunning];
    }
    [self.scannedItems removeObject:asset.matchedBarcodeData];
    [self.tableView reloadData];
}

-(void)didCancelSelection {
    if (_wasRunning) {
        [self startRunning];
    }
}


#pragma AVCaptureMetadataOutputDelegate
#pragma mark - Delegate functions

- (void)captureOutput:(AVCaptureOutput *)captureOutput didOutputMetadataObjects:(NSArray *)metadataObjects
       fromConnection:(AVCaptureConnection *)connection {
    [metadataObjects
     enumerateObjectsUsingBlock:^(AVMetadataObject *obj,
                                  NSUInteger idx,
                                  BOOL *stop) {
         if ([obj isKindOfClass:
              [AVMetadataMachineReadableCodeObject class]])
         {
             // 3
             AVMetadataMachineReadableCodeObject *code =
             (AVMetadataMachineReadableCodeObject*)
             [_previewLayer transformedMetadataObjectForMetadataObject:obj];
             // 4
             Barcode * barcode = [Barcode processMetadataObject:code];
             
             for(NSString * str in self.allowedBarcodeTypes){
                 if([barcode.getBarcodeType isEqualToString:str]){
                     [self validBarcodeFound:barcode];
                     return;
                 }
             }
         }
     }];
}

-(void)bluetoothDataReceived:(NSNotification*)notification {
    EADSessionController *sessionController = (EADSessionController *)[notification object];
    NSUInteger bytesAvailable = 0;
    
    NSMutableData* data = [[NSMutableData alloc] init];
    while ((bytesAvailable = [sessionController readBytesAvailable]) > 0) {
        NSData* temp = [sessionController readData:bytesAvailable];
        [data appendBytes:[temp bytes] length:temp.length];
    }
    if (data && data.length > 0) {
        // custom processing for socketmobile scanners
        NSString *barcode = Nil;
        NSString *bluetoothProtocol = [DEFAULTS objectForKey:SelectedProtocolStringName];
        if ([bluetoothProtocol rangeOfString:@"com.socketmobile" options:NSCaseInsensitiveSearch].length > 0) {
            if (data.length >= 8) {
                NSUInteger actualLength = data.length - 7;
                const void* dataBuf = [data bytes];
                dataBuf += 5;
                barcode = [[NSString alloc] initWithBytes:dataBuf length:actualLength encoding:NSUTF8StringEncoding];
                
            }
        }
        else {
            barcode = [[NSString alloc] initWithBytes:data.bytes length:data.length encoding:NSUTF8StringEncoding];
        }
        if (barcode != Nil) {
            BOOL alreadyExists = YES;
            if ([self addUnique:barcode]) {
                alreadyExists = NO;
            }
            if (alreadyExists) {
                AudioServicesPlaySystemSound(kSystemSoundID_Vibrate);
                MBProgressHUD* hud = [MBProgressHUD HUDForView:self.view];
                if (Nil == hud) {
                    hud = [MBProgressHUD showHUDAddedTo:self.view animated:NO];
                }
                hud.mode = MBProgressHUDModeText;
                hud.labelText = NSLocalizedString(@"Item Already Scanned", Nil);
                [hud show:YES];
                [hud hide:YES afterDelay:1.5];
            }
        }
    }
}

- (void) validBarcodeFound:(Barcode *)barcode {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self stopRunning];
            NSString* data = [barcode getBarcodeData];
            if ([data length] != 0) {
                BOOL alreadyExists = YES;
                if ([self addUnique:data]) {
                    alreadyExists = NO;
                }
                
                if (outlineLayer == Nil) {
                    outlineLayer = [CALayer layer];
                }
                outlineLayer.backgroundColor = [UIColor clearColor].CGColor;
                outlineLayer.shadowRadius = 5.0;
                outlineLayer.shadowColor = [UIColor blackColor].CGColor;
                outlineLayer.shadowOpacity = 0.8;
                if (!alreadyExists) {
                    [audioPlayer play];
                    outlineLayer.borderColor = [UIColor greenColor].CGColor;
                }
                else {
                    AudioServicesPlaySystemSound(kSystemSoundID_Vibrate);
                    outlineLayer.borderColor = [UIColor redColor].CGColor;
                    
                    MBProgressHUD* hud = [MBProgressHUD HUDForView:self.view];
                    if (Nil == hud) {
                        hud = [MBProgressHUD showHUDAddedTo:self.view animated:NO];
                    }
                    hud.mode = MBProgressHUDModeText;
                    hud.labelText = NSLocalizedString(@"Item already scanned", Nil);
                    [hud show:YES];
                }
                outlineLayer.borderWidth = 2.0;
                outlineLayer.frame = [barcode.boundingBoxPath bounds];
                outlineLayer.frame = CGRectMake(outlineLayer.frame.origin.x, outlineLayer.frame.origin.y, outlineLayer.frame.size.width, 2);
                [_previewLayer addSublayer:outlineLayer];
            }
            // run the scanner in half a second again.
            [self performSelector:@selector(restartRunning) withObject:Nil afterDelay:1.5];
        });
}

-(void)restartRunning {
    MBProgressHUD* hud = [MBProgressHUD HUDForView:self.view];
    [hud hide:NO];
    [outlineLayer removeFromSuperlayer];
    [self startRunning];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

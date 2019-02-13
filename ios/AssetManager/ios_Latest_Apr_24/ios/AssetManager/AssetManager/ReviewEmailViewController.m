
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
//  ReviewEmailViewController.m
//  AssetManager
//

#import "ReviewEmailViewController.h"
#import "AssetTableViewCell.h"
#import "UIColor+Cireson.h"
#import "AssetPropertiesViewController.h"

@interface ReviewEmailViewController () {
	NSArray *tableViewDataSource;
}

@end

@implementation ReviewEmailViewController

@synthesize segmentedControl, tableView;
@synthesize noMatchAssets, inventoriedAssets, notInventoriedAssets, countLabel;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
	// Do any additional setup after loading the view.
	self.view.backgroundColor = [UIColor backgroundColor];
	self.tableView.backgroundColor = [UIColor backgroundColor];
    self.tableView.separatorColor = [UIColor separatorColor];
    [self segmentedControlValueChanged:self.segmentedControl];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return tableViewDataSource.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"cellReviewEmail";
    
	AssetTableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    cell.backgroundColor = [UIColor backgroundColor];
	
    if (self.segmentedControl.selectedSegmentIndex <= 1) {
        Asset *asset = [tableViewDataSource objectAtIndex:indexPath.row];
        cell.assetNameLabel.text = asset.displayName;
        cell.assetTypeLabel.text = asset.assetTypeDisplayName;
    }
    else {
        cell.assetNameLabel.text = [tableViewDataSource objectAtIndex:indexPath.row];
        cell.assetTypeLabel.text = @"";
    }
	
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	[self.tableView deselectRowAtIndexPath:indexPath animated:YES];
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.segmentedControl.selectedSegmentIndex <= 1) {
        return 65.0f;
    }
    else {
        return 44.0f;
    }
}

-(void)backButtonClicked:(id)sender {
	[self.navigationController popViewControllerAnimated:YES];
}

-(void)emailButtonClicked:(id)sender {

    if ([MFMailComposeViewController canSendMail]) {
        
        NSString *csvMatched = [self csv:@"Inventoried" withAssets:self.inventoriedAssets];
        NSString *csvReturned = [self csv:@"Not Inventoried" withAssets:self.notInventoriedAssets];
        NSString *csvNoMatched = [self csv:@"No Match" withBarcodes:self.noMatchAssets];
        
        NSString *docFolder = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
        
        NSLog(@"Directory is : %@", docFolder);
        
        //saving CSVs
        NSString *csvFilePathMatched = [NSString stringWithFormat:@"%@/Inventoried.csv", docFolder];
        NSString *csvFilePathReturned = [NSString stringWithFormat:@"%@/NotInventoried.csv", docFolder];
        NSString *csvFilePathNoMatched = [NSString stringWithFormat:@"%@/NoMatch.csv", docFolder];
        
        NSError* error = Nil;
        [[NSFileManager defaultManager] removeItemAtPath:csvFilePathMatched error: &error];
        [[NSFileManager defaultManager] removeItemAtPath:csvFilePathReturned error: &error];
        [[NSFileManager defaultManager] removeItemAtPath:csvFilePathNoMatched error: &error];
                                          
        BOOL success = ([csvMatched writeToFile:csvFilePathMatched
                                            atomically:YES
                                              encoding:NSUTF8StringEncoding
                                                 error:&error]
                       && [csvReturned writeToFile:csvFilePathReturned
                                                atomically:YES encoding:NSUTF8StringEncoding
                                                     error:&error]
                       && [csvNoMatched writeToFile:csvFilePathNoMatched
                                                 atomically:YES encoding:NSUTF8StringEncoding
                                                      error:&error]
                       
                       );
        if (!success) {
            NSLog(@"Error while writing");
        }
        
        NSDate *currentDateTime = [NSDate date];
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setLocale:[NSLocale systemLocale]];
        [dateFormatter setDateFormat:@"yyyy-MM-dd'T'HH:mm"];
        NSString *dateString = [dateFormatter stringFromDate:currentDateTime];
        
        NSString *subject = [NSString stringWithFormat: NSLocalizedString(@"Inventory Report - (%@)", Nil), dateString];
        NSString *messageBody = NSLocalizedString(@"Inventory audit reports are attached.", nil);
        
        MFMailComposeViewController *mailComposer = [[MFMailComposeViewController alloc] init];
        mailComposer.mailComposeDelegate = self;
        [mailComposer setSubject:subject];
        
        [mailComposer addAttachmentData:[NSData dataWithContentsOfFile:csvFilePathMatched]
                               mimeType:@"text/csv"
                               fileName:@"Inventoried.csv"];
        [mailComposer addAttachmentData:[NSData dataWithContentsOfFile:csvFilePathReturned]
                               mimeType:@"text/csv"
                               fileName:@"NotInventoried.csv"];
        [mailComposer addAttachmentData:[NSData dataWithContentsOfFile:csvFilePathNoMatched]
                               mimeType:@"text/csv"
                               fileName:@"NoMatched.csv"];
        
        [mailComposer setMessageBody:messageBody isHTML:NO];
        
        [self presentViewController:mailComposer animated:YES completion:NULL];
    }
    else {
        NSLog(@"can't send email");
    }
    
}

-(void)mailComposeController:(MFMailComposeViewController *)controller didFinishWithResult:(MFMailComposeResult)result error:(NSError *)error {
	switch (result) {
        case MFMailComposeResultCancelled:
            NSLog(@"Mail cancelled");
            break;
        case MFMailComposeResultSaved:
            NSLog(@"Mail saved");
            break;
        case MFMailComposeResultSent:
            NSLog(@"Mail sent");
            [self showHomeViewController];
            break;
        case MFMailComposeResultFailed:
            NSLog(@"Mail sent failure: %@", [error localizedDescription]);
            break;
        default:
            break;
    }
    [self dismissViewControllerAnimated:YES completion:Nil];
}

-(NSString *)csv:(NSString *)title withAssets:(NSArray *)assets {
    NSMutableString *csv = [[NSMutableString alloc] init];
    // write header
    [csv appendString:@"Search Values\n"];
    [csv appendString:@"Property name, Values\n"];
    
        [csv appendFormat:@"Status, %@\n",[[NSUserDefaults standardUserDefaults] valueForKey:@"status"]];
        [csv appendFormat:@"Location, %@\n",[[NSUserDefaults standardUserDefaults] valueForKey:@"location"]];
        [csv appendFormat:@"Organisation, %@\n",[[NSUserDefaults standardUserDefaults] valueForKey:@"organisation"]];
        [csv appendFormat:@"Custodian User, %@\n",[[NSUserDefaults standardUserDefaults] valueForKey:@"custodian"]];
        [csv appendFormat:@"Cost Center, %@\n",[[NSUserDefaults standardUserDefaults] valueForKey:@"costcenter"]];
        [csv appendFormat:@"Receive Date, %@\n\n",[[NSUserDefaults standardUserDefaults] valueForKey:@"receiveddate"]];

    [csv appendString:@"Name,FullName,HardwareAssetId,SerialNumber,AssetTag\n"];
   
    for (Asset *asset in assets) {
		[csv appendFormat:@"%@,%@,%@,%@,%@\n", asset.displayName,asset.fullName,asset.hardwareAssetId, asset.serialNumber, asset.tag];
	}
    return csv;
}

-(NSString *)csv:(NSString *)title withBarcodes:(NSArray *)assets {
    NSMutableString *csv = [[NSMutableString alloc] init];
    // write header
    [csv appendString:@"Scanned Barcode\n"];
	for (NSString *barcode in assets) {
		[csv appendFormat:@"\n\"%@\"", barcode];
	}
    return csv;
}

- (void)showHomeViewController {
	[self dismissViewControllerAnimated:YES completion:^{
		[self.navigationController popToRootViewControllerAnimated:YES];
	}];
}

-(void)segmentedControlValueChanged:(id)sender {
	switch (segmentedControl.selectedSegmentIndex) {
		case 0:
			tableViewDataSource = self.notInventoriedAssets;
			break;
		case 1:
			tableViewDataSource = self.inventoriedAssets;
			break;
		case 2:
			tableViewDataSource = self.noMatchAssets;
			break;
		default:
			break;
	}
    self.countLabel.text = [NSString stringWithFormat:@"%lu", (unsigned long)tableViewDataSource.count];
	[self.tableView reloadData];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

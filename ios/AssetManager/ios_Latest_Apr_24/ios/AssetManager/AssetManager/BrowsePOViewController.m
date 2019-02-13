
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
//  PurchasedAssetsListViewController.m
//  AssetManager
//

#import "BrowsePOViewController.h"
#import "PurchaseOrderTableViewCell.h"
#import "UIColor+Cireson.h"

@interface BrowsePOViewController ()

@property (strong, nonatomic) NSArray *purchasedDates;

@end

@implementation BrowsePOViewController

@synthesize tableView, purchasedDates;

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
	self.purchasedDates = [NSArray arrayWithObjects:
						   NSLocalizedString(@"Last 15 Days", Nil),
						   NSLocalizedString(@"Last Month", Nil),
						   NSLocalizedString(@"Last 3 Months", Nil),
						   NSLocalizedString(@"Last 6 Months", Nil),
						   nil];
	self.tableView.backgroundColor = [UIColor backgroundColor];
	self.view.backgroundColor = [UIColor backgroundColor];
    self.tableView.separatorColor = [UIColor separatorColor];
	
	UITapGestureRecognizer * tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTap:)];
	[tap setCancelsTouchesInView:NO];
    [self.view addGestureRecognizer:tap];
	
}

-(void)handleTap:(UIEvent*)event {
    [self.view endEditing:YES];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 10;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *CellIdentifier = @"cellPurchaseOrder";
    
	PurchaseOrderTableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:CellIdentifier];
	
    if(cell == nil){
        cell = [[PurchaseOrderTableViewCell alloc]initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:CellIdentifier];
	}
	
	cell.backgroundColor = [UIColor backgroundColor];
	cell.titleLabel.text = NSLocalizedString(@"Hello", Nil);
	cell.monthLabel.text = NSLocalizedString(@"January", Nil);
	cell.dayLabel.text = NSLocalizedString(@"15", Nil);
	
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	[self.tableView deselectRowAtIndexPath:indexPath animated:YES];
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
	return 80.0f;
}

-(void)backButtonClicked:(id)sender {
	[self dismissViewControllerAnimated:YES completion:nil];
}

-(void)nextButtonClicked:(id)sender {
	UIStoryboard *storyboard = self.storyboard;
	UIViewController *vc = [storyboard instantiateViewControllerWithIdentifier:@"scanner"];
	vc.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
	[self presentViewController:vc animated:YES completion:Nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

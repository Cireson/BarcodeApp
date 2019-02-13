
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
//  StatusViewController.m
//  AssetManager
//

#import "EnumPickerViewController.h"
#import "UIColor+Cireson.h"
#import "RATreeNode.h"
#import "VCRadioButton.h"

@interface EnumPickerViewController ()
{
    NSMutableArray* __userSelectedEnums;
}
-(void)setupTreeView;
-(void)setupCell:(UITableViewCell*)cell enumeration:(CiresonEnumeration*)e treeNodeInfo:(RATreeNodeInfo*)treeNodeInfo;
@end

@implementation EnumPickerViewController

@synthesize treeView, delegate, enumeration, treeViewPlacerHolder, titleLabel, selectedValues, multiSelectionMode;

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
    
    __userSelectedEnums = [[NSMutableArray alloc] init];
    if (self.selectedValues != Nil && self.selectedValues.count != 0) {
        [__userSelectedEnums addObjectsFromArray:self.selectedValues];// initial enums
    }
    self.view.backgroundColor = [UIColor backgroundColor];
    [self setupTreeView];

    [self.treeView reloadData];
}

- (void)setupTreeView{
    CGRect frame = CGRectMake(0, 0, self.treeViewPlacerHolder.frame.size.width, self.treeViewPlacerHolder.frame.size.height);
    RATreeView* tv = [[RATreeView alloc] initWithFrame:frame style:RATreeViewStylePlain];
    tv.delegate = self;
    tv.dataSource = self;
    tv.separatorColor = [UIColor separatorColor];
    tv.backgroundColor = [UIColor clearColor];
    tv.separatorStyle = RATreeViewCellSeparatorStyleSingleLine;
    self.treeView = tv;
    self.treeViewPlacerHolder.backgroundColor = [UIColor clearColor];
    [self.treeViewPlacerHolder addSubview:self.treeView];
    
    self.treeView.allowsMultipleSelection = self.multiSelectionMode;
    self.treeView.allowsSelection = YES;
}

#pragma mark TreeView Delegate methods
- (CGFloat)treeView:(RATreeView *)treeView heightForRowForItem:(id)item treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo {
    return 40;
}

- (NSInteger)treeView:(RATreeView *)treeView indentationLevelForRowForItem:(id)item treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo {
    return 3 * treeNodeInfo.treeDepthLevel;
}

- (BOOL)treeView:(RATreeView *)treeView shouldExpandRowForItem:(id)item treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo {
    return YES;
}

- (BOOL)treeView:(RATreeView *)treeView shouldItemBeExpandedAfterDataReload:(id)item treeDepthLevel:(NSInteger)treeDepthLevel {
    if (item) {
        CiresonEnumeration* current = (CiresonEnumeration*)item;
        if ([CiresonModelBase containsObjectInArray:__userSelectedEnums object:current]) {
            return YES;
        }
        return NO;
    }
    return YES;
}

- (void)treeView:(RATreeView *)tv willDisplayCell:(UITableViewCell *)cell forItem:(id)item treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo {
    
    CiresonEnumeration* e = item;
    [self setupCell:cell enumeration:e treeNodeInfo:treeNodeInfo];
}

- (void)treeView:(RATreeView *)tv didExpandRowForItem:(id)item treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo {
    UITableViewCell* cell = [tv cellForItem:item];
    [self setupCell:cell enumeration:item treeNodeInfo:treeNodeInfo];
}
- (void)treeView:(RATreeView *)tv didCollapseRowForItem:(id)item treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo {
    UITableViewCell* cell = [tv cellForItem:item];
    [self setupCell:cell enumeration:item treeNodeInfo:treeNodeInfo];
}

- (void)treeView:(RATreeView *)tv didDeselectRowForItem:(id)item treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo {
    UITableViewCell *cell = [tv cellForItem:item];
    [self removeSelection:item];
    [self setupCell:cell enumeration:item treeNodeInfo:treeNodeInfo];
}

- (void)treeView:(RATreeView *)tv didSelectRowForItem:(id)item treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo {
    UITableViewCell *cell = [tv cellForItem:item];
    [self addSelection:item];
    [self setupCell:cell enumeration:item treeNodeInfo:treeNodeInfo];
}

-(void)setupCell:(UITableViewCell *)cell enumeration:(CiresonEnumeration *)e treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo {
    if (!self.multiSelectionMode) {
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    else {
        cell.selectionStyle = UITableViewCellSelectionStyleDefault;
    }
    if (e.hasChildren) {
        if (treeNodeInfo.isExpanded) {
            cell.textLabel.text = [NSString stringWithFormat:@"- %@",e.displayName];
            cell.separatorInset = UIEdgeInsetsMake(0, 0, 0, cell.bounds.size.width);
        }
        else {
            cell.textLabel.text = [NSString stringWithFormat:@"+ %@", e.displayName];
            cell.separatorInset = UIEdgeInsetsMake(0, 0, 0, 0);
        }
    }
    else {
        cell.separatorInset = UIEdgeInsetsMake(0, 0, 0, 0);
        cell.textLabel.text = e.displayName;
    }
    
    if (!self.multiSelectionMode) {
        VCRadioButton* radioButton = (VCRadioButton*)cell.accessoryView;
        radioButton.selected = [CiresonModelBase containsObjectInArray:__userSelectedEnums object:e];
    }
    else {
        if ([CiresonModelBase containsObjectInArray:__userSelectedEnums object:e]) {
            [self.treeView selectRowForItem:e animated:YES scrollPosition:RATreeViewScrollPositionNone];
            cell.accessoryType = UITableViewCellAccessoryCheckmark;
            [cell setSelected:YES];
        }
        else {
            [self.treeView deselectRowForItem:e animated:YES];
            cell.accessoryType = UITableViewCellAccessoryNone;
        }
    }
}

-(void)addSelection:(CiresonEnumeration*)e {
    if (!self.multiSelectionMode) {
        [__userSelectedEnums removeAllObjects];
    }
    if (![CiresonModelBase containsObjectInArray:__userSelectedEnums object:e]) {
        [__userSelectedEnums addObject:e];
    }
}

-(void)removeSelection:(CiresonEnumeration*)item{
    if ([CiresonModelBase containsObjectInArray:__userSelectedEnums object:item]) {
        [CiresonModelBase removeObjectWithSameIdInArray:__userSelectedEnums obj:item];
    }
}

#pragma mark TreeView Data Source

- (UITableViewCell *)treeView:(RATreeView *)tv cellForItem:(id)item treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo {
 
    CiresonEnumeration* e = item;
    // NOte: we don't want to reuse cells here.
    UITableViewCell* cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:Nil];
    cell.textLabel.textColor = [UIColor whiteColor];
    cell.textLabel.font = [UIFont systemFontOfSize:15.0];
    cell.backgroundColor = [UIColor backgroundColor];
    
    if (!self.multiSelectionMode) {
        VCRadioButton* btn = [[VCRadioButton alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 40.0f, 40.0f)];
        btn.selectedValue = e;
        btn.groupName = @"SelectStatus";
        __block __weak EnumPickerViewController* __weakSelf = self;
        [btn setSelectedColor:[UIColor colorWithRed:0.2f green:0.2f blue:0.2f alpha:1]];
        [btn setControlColor:[UIColor separatorColor]];
        RadioButtonControlSelectionBlock selectionBlock = ^(VCRadioButton *radioButton){
            [__weakSelf addSelection:(CiresonEnumeration*)radioButton.selectedValue];
        };
        btn.selectionBlock = selectionBlock;
        cell.accessoryView = btn;
    }
    [self setupCell:cell enumeration:enumeration treeNodeInfo:treeNodeInfo];
    
    return cell;
}

- (NSInteger)treeView:(RATreeView *)treeView numberOfChildrenOfItem:(id)item {
    if (item == nil) {
        if (self.enumeration.hasChildren) {
            return self.enumeration.children.count;
        }
        return 0;
    }
    
    CiresonEnumeration* e = (CiresonEnumeration*)item;
    if (e.hasChildren) {
        return e.children.count;
    }
    return 0;
}

- (id)treeView:(RATreeView *)treeView child:(NSInteger)index ofItem:(id)item {
    CiresonEnumeration* data = item;
    if (data == Nil) {
        return [self.enumeration.children objectAtIndex:index];
    }
    else {
        return [data.children objectAtIndex:index];
    }
}

- (UITableViewCellEditingStyle)treeView:(RATreeView *)treeView editingStyleForRowForItem:(id)item treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo {
    return UITableViewCellEditingStyleNone;
}

-(void)didClickDoneButton:(id)sender {
    if (self.delegate) {
        NSArray* selectedItems = Nil;
        if (__userSelectedEnums.count > 0) {
            selectedItems = __userSelectedEnums;
        }
        [self.delegate didFinishSelectingEnums:selectedItems];
    }
    
    [self dismissViewControllerAnimated:YES completion:Nil];
}

-(void)didClickClearButton:(id)sender {
    [__userSelectedEnums removeAllObjects];
    [self.treeView reloadData];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)dealloc {
    [self.treeView removeFromSuperview];
    self.treeView = Nil;
}

@end

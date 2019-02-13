
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
//  MatchingPOTableViewCell.h
//  AssetManager
//

#import <UIKit/UIKit.h>

@interface AssetTableViewCell : UITableViewCell

@property (nonatomic, weak) IBOutlet UILabel *assetNameLabel;
@property (nonatomic, weak) IBOutlet UILabel *assetTypeLabel;
@property (nonatomic, weak) IBOutlet UILabel *currentSerialNo;
@property (nonatomic, weak) IBOutlet UILabel* currentTag;
@property (nonatomic, weak) IBOutlet UIImageView *statusImageView;
@property (strong, nonatomic) IBOutlet UILabel *assetModeLabel;
@end

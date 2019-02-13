
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
//  LongTextViewController.h
//  AssetManager
//

#import <UIKit/UIKit.h>

@protocol LongTextViewControllerDelegate <NSObject>

@required
-(void)didFinishEditingLongText:(NSString *)text withItemMode:(LongTextViewMode)mode;

@end

@interface LongTextViewController : CiresonViewController <UITextViewDelegate>

@property (nonatomic, weak) IBOutlet UITextView *textView;
@property (nonatomic, weak) IBOutlet UILabel *titleLabel;

@property (nonatomic, retain) NSString *editTextValue;
@property (nonatomic, retain) NSString *viewTitle;
@property (nonatomic, assign) LongTextViewMode textItemTypeMode;
@property (assign) id<LongTextViewControllerDelegate> delegate;
@property (assign) NSUInteger maxCharacters;

-(IBAction)didClickDoneButton:(id)sender;
-(IBAction)didClickCancelButton:(id)sender;


@end

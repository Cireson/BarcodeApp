
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
//  UIColor+Cireson.m
//  AssetManager
//

#import "UIColor+Cireson.h"
#import <CoreGraphics/CGBitmapContext.h>
#import <CoreGraphics/CGImage.h>
#import <CoreGraphics/CGContext.h>

@implementation UIImage (Cireson)

+(UIImage*) backgroundImage {
    return [UIImage imageNamed:@"background_orange"];
}

+(UIImage*) backgroundHeaderImage {
	return [UIImage imageNamed:@"background_header"];
}

+(UIImage*) calendarImage {
	return [UIImage imageNamed:@"calendar"];
}

+(UIImage*) deleteImage {
	return [UIImage imageNamed:@"delete_button"];
}

+(UIImage*) checkmarkImage {
	return [UIImage imageNamed:@"checkmark_button"];
}

+(UIImage*) matchImage {
	return [UIImage imageNamed:@"match_button"];
}

+(UIImage*) scanImage {
	return [UIImage imageNamed:@"scan_button"];
}

+(UIImage*) questionMarkImage {
    return [UIImage imageNamed:@"question_mark"];
}

//+(UIImage*) plusImage {
//    return [UIImage imageNamed:@"plus"];
//}

@end

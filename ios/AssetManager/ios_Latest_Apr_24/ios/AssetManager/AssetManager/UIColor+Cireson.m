
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
#import "UIImage+Cireson.h"

@implementation UIColor (Cireson)

+(UIColor*) orangeBackgroundStart {
    return [UIColor colorWithRed:200.0/255.0 green:123.0/255.0 blue:6.0/255.0 alpha:1.0];
}

+(UIColor*) orangeBackgroundEnd {
    return [UIColor colorWithRed:237.0/255.0 green:178.0/255.0 blue:38.0/255.0 alpha:1.0];
}

+(UIColor*) backgroundColor {
    return [UIColor colorWithPatternImage:[UIImage backgroundImage]];
}

+(UIColor*) separatorColor {
    return [UIColor colorWithRed:234.0/255.0 green:174.0/255.0 blue:69.0/255.0 alpha:1.0];
}

+(UIColor *)backgroundHeaderColor {
	return [UIColor colorWithPatternImage:[UIImage backgroundHeaderImage]];
}


@end

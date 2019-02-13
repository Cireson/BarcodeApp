
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
//  Barcode.m
//  AssetManager
//

#import "Barcode.h"

@interface Barcode()

@property (nonatomic, strong) AVMetadataMachineReadableCodeObject *metadataObject;
@property (nonatomic, strong) NSString * barcodeType;
@property (nonatomic, strong) NSString * barcodeData;
@property (nonatomic, strong) UIBezierPath *cornersPath;

@end

@implementation Barcode

+ (Barcode * )processMetadataObject: (AVMetadataMachineReadableCodeObject*)code {
    // 1 create the obj
    Barcode * barcode=[[Barcode alloc]init];
    // 2 store code type and string
    barcode.barcodeType = [NSString stringWithString:code.type];
    barcode.barcodeData = [NSString stringWithString:code.stringValue];
    barcode.metadataObject = code;
    // 3 & 4 Create the path joining code's corners
    CGMutablePathRef cornersPath = CGPathCreateMutable();
    // 5 Make point
    CGPoint point;
    CGPointMakeWithDictionaryRepresentation(
                                            (CFDictionaryRef)code.corners[0], &point);
    // 6 Make path
    CGPathMoveToPoint(cornersPath, nil, point.x, point.y);
    // 7
    for (int i = 1; i < code.corners.count; i++) {
        CGPointMakeWithDictionaryRepresentation(
                                                (CFDictionaryRef)code.corners[i], &point);
        CGPathAddLineToPoint(cornersPath, nil,
                             point.x, point.y);
    }
    // 8 Finish box
    CGPathCloseSubpath(cornersPath);
    // 9 Set path
    barcode.cornersPath =
    [UIBezierPath bezierPathWithCGPath:cornersPath];
    CGPathRelease(cornersPath);
    // 10 Create the path for the code's bounding box
    barcode.boundingBoxPath =
    [UIBezierPath bezierPathWithRect:code.bounds];
    // 11 return
    return barcode;
}
- (NSString *) getBarcodeType{
    return self.barcodeType;
}
- (NSString *) getBarcodeData{
    return self.barcodeData;
}


- (void) printBarcodeData{
    NSLog(@"Barcode of type: %@ and data: %@",self.metadataObject.type, self.barcodeData);
}
@end


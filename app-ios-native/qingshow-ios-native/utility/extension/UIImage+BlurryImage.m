//
//  UIImage+BlurryImage.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/27/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "UIImage+BlurryImage.h"
#import <CoreImage/CoreImage.h>

@implementation UIImage(BlurryImage)
- (UIImage *)blurryImageWithBlurLevel:(CGFloat)blur {
    CIContext *context = [CIContext contextWithOptions:nil];
    CIImage *inputImage = [CIImage imageWithCGImage:self.CGImage];
    CIFilter *filter = [CIFilter filterWithName:@"CIGaussianBlur"
                                  keysAndValues:kCIInputImageKey, inputImage,
                        @"inputRadius", @(blur),
                        nil];
    
    CIImage *outputImage = filter.outputImage;
    
    CGImageRef outImage = [context createCGImage:outputImage
                                        fromRect:[outputImage extent]];
    return [UIImage imageWithCGImage:outImage];
}
@end

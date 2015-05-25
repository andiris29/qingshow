//
//  UIView+ScreenShot.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/27/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "UIView+ScreenShot.h"

@implementation UIView(ScreenShot)

- (UIImage*)makeScreenShot
{
    UIView* view = self;
    CGRect rect =view.frame;
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    [view.layer renderInContext:context];
    UIImage *img = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return img;
}
@end

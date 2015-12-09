//
//  UIView+QSExtension.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "UIView+QSExtension.h"

@implementation UIView(QSExtension)

- (void)configBorderColor:(UIColor*)color
                    width:(CGFloat)borderWidth
             cornerRadius:(CGFloat)cornerRadius {
    self.layer.masksToBounds = YES;
    self.layer.borderColor = color.CGColor;
    self.layer.borderWidth = borderWidth;
    self.layer.cornerRadius = cornerRadius;
}

@end

//
//  UIView+QSExtension.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIView(QSExtension)

- (void)configBorderColor:(UIColor*)color
                    width:(CGFloat)borderWidth
             cornerRadius:(CGFloat)cornerRadius;

@end

//
//  QSRectUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/6/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSRectUtil.h"

@implementation QSRectUtil
+ (CGRect)reducedFrame:(CGRect)innerRect forContainer:(CGRect)containerRect {
    if (CGRectContainsRect(containerRect, innerRect)) {
        return innerRect;
    }
    CGRect newRect = innerRect;
    if (newRect.origin.x < containerRect.origin.x) {
        newRect.origin.x = containerRect.origin.x;
    }
    if (newRect.origin.y < containerRect.origin.y) {
        newRect.origin.y = containerRect.origin.y;
    }
    
    CGFloat right = containerRect.origin.x + containerRect.size.width;
    CGFloat bottom = containerRect.origin.y + containerRect.size.height;
    if (newRect.origin.x + newRect.size.width > right) {
        CGFloat newX = right - newRect.size.width;
        newX = newX <= containerRect.origin.x ? containerRect.origin.x : newX;
        newRect.origin.x = newX;
    }
    if (newRect.origin.y + newRect.size.height > bottom) {
        CGFloat newY = bottom - newRect.size.height;
        newY = newY <= containerRect.origin.y ? containerRect.origin.y : newY;
        newRect.origin.y = newY;
    }
    
    
    if (newRect.origin.x + newRect.size.width > right) {
        CGFloat newWidth = right - newRect.origin.x;
        CGFloat newHeight = newWidth * newRect.size.height / newRect.size.width;
        newRect.size.width = newWidth;
        newRect.size.height = newHeight;
    }
    
    if (newRect.origin.y + newRect.size.height > bottom) {
        CGFloat newHeight = bottom - newRect.origin.y;
        CGFloat newWidth = newHeight * newRect.size.width / newRect.size.height;
        newRect.size.width = newWidth;
        newRect.size.height = newHeight;
    }
    return newRect;
}

@end

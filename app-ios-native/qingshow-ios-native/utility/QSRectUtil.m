//
//  QSRectUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/6/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSRectUtil.h"
#import "NSArray+QSExtension.h"

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

+ (float)getViewUncoveredSquare:(UIView*)view otherViews:(NSArray*)array {
    NSArray* vs = @[[NSValue valueWithCGRect:view.frame]];
    
    NSArray* otherValues = [array mapUsingBlock:^id(UIView* v) {
        return [NSValue valueWithCGRect:v.frame];
    }];

    //切分
    for (NSValue* value in otherValues) {
        vs = [self _rects:vs splitByRect:value];
    }
    
    //求面积和
    float totalSquare = 0.f;
    
    for (NSValue* value in vs) {
        totalSquare += [self getSquare:value.CGRectValue];
    }
    
    
    return totalSquare;
}

+ (NSArray*)_rects:(NSArray*)rects splitByRect:(NSValue*)rect {
    NSMutableArray* retArray = [@[] mutableCopy];
    
    for (NSValue* r in rects) {
        [retArray addObjectsFromArray:[self _rect:r splitByRect:rect]];
    }

    return retArray;
}

+ (NSArray*)_rect:(NSValue*)rectValue1 splitByRect:(NSValue*)rectValue2 {
    if (CGRectContainsRect(rectValue2.CGRectValue, rectValue1.CGRectValue)) {
        return @[];
    }
    if (!CGRectIntersectsRect(rectValue1.CGRectValue, rectValue2.CGRectValue)) {
        return @[rectValue1];
    }
    
    CGRect interRect = CGRectIntersection(rectValue1.CGRectValue, rectValue2.CGRectValue);
    CGRect originRect = rectValue1.CGRectValue;
    
    NSMutableArray* retArray = [@[] mutableCopy];
    
    
    if (originRect.origin.x < interRect.origin.x) {
        //左切
        CGRect newRect = CGRectMake(originRect.origin.x, originRect.origin.y, interRect.origin.x - originRect.origin.x, originRect.size.height);
        [retArray addObject:[NSValue valueWithCGRect:newRect]];
        
        originRect = CGRectMake(interRect.origin.x, originRect.origin.y, originRect.size.width - newRect.size.width, originRect.size.height);
    }
    
    if (originRect.origin.y < interRect.origin.y) {
        //上切
        CGRect newRect = CGRectMake(originRect.origin.x, originRect.origin.y, originRect.size.width, interRect.origin.y - originRect.origin.y);
        [retArray addObject:[NSValue valueWithCGRect:newRect]];
        
        originRect = CGRectMake(originRect.origin.x, interRect.origin.y, originRect.size.width, originRect.size.height - newRect.size.height);
    }
    
    
    if (originRect.origin.x + originRect.size.width > interRect.origin.x + interRect.size.width) {
        //右切
        CGRect newRect = CGRectMake(interRect.origin.x + interRect.size.width, originRect.origin.y, (originRect.origin.x + originRect.size.width) - (interRect.origin.x + interRect.size.width), originRect.size.height);
        [retArray addObject:[NSValue valueWithCGRect:newRect]];
        
        originRect.size.width -= newRect.size.width;

    }
    
    if (originRect.origin.y + originRect.size.height > interRect.origin.y + interRect.size.height) {
        //下切
        CGRect newRect =
        CGRectMake(
                   originRect.origin.x,
                   interRect.origin.y + interRect.size.height,
                   originRect.size.width,
                   (originRect.origin.y + originRect.size.height) - (interRect.origin.y + interRect.size.height)
                   );
        [retArray addObject:[NSValue valueWithCGRect:newRect]];
        originRect.size.height -= newRect.size.height;
    }
    
    return retArray;
}


+ (float)getSquare:(CGRect)rect {
    return rect.size.width * rect.size.height;
}

+ (CGSize)scaleSize:(CGSize)fromSize toFitSize:(CGSize)toSize {
    CGSize retSize = CGSizeZero;
    float scaleX = toSize.width / fromSize.width;
    float scaleY = toSize.height / fromSize.height;
    float s = scaleX < scaleY ? scaleX : scaleY;
    retSize = CGSizeMake(fromSize.width * s, fromSize.height * s);
    return retSize;
}
@end

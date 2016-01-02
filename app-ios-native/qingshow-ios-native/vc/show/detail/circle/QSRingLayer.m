//
//  QSCircleShadoeLayer.m
//  CircleAnimation
//
//  Created by wxy325 on 16/1/2.
//  Copyright © 2016年 BI. All rights reserved.
//

#import "QSRingLayer.h"


@interface QSRingLayer ()
@end

@implementation QSRingLayer
@dynamic innerRadius;
@dynamic outterRadius;
@dynamic circleColor;
+ (BOOL)needsDisplayForKey:(NSString *)key {
    if ([@[@"innerRadius", @"outterRadius"] indexOfObject:key] != NSNotFound) {
        return YES;
    }
    return [super needsDisplayForKey:key];
}


- (void)setValue:(id)value forKeyPath:(NSString *)keyPath {
    [super setValue:value forKeyPath:keyPath];
}

- (instancetype)init {
    self = [super init];
    if (self) {
        self.innerRadius = 10.f;
        self.outterRadius = 50.f;
    }
    return self;
}

- (void)drawInContext:(CGContextRef)ctx {
    CGContextSaveGState(ctx);
    CGRect rect = CGContextGetClipBoundingBox(ctx);
    CGPoint center = CGPointMake(rect.origin.x + rect.size.width / 2, rect.origin.y + rect.size.height / 2);
    
    CGFloat r, g, b, a;
    [self.circleColor getRed:&r green:&g blue:&b alpha:&a];
    
    CGContextSetRGBFillColor(ctx, r, g, b, a);
    CGMutablePathRef path = CGPathCreateMutable();
    CGPathAddArc(path, NULL, center.x, center.y, self.outterRadius, 0, M_PI * 2, true);
    CGPathCloseSubpath(path);
    
    CGPathAddArc(path, NULL, center.x, center.y, self.innerRadius, 0, M_PI * 2, false);
    CGPathCloseSubpath(path);
    CGContextAddPath(ctx, path);
    CGContextDrawPath(ctx, kCGPathFill);
    
    CGContextRestoreGState(ctx);
}
@end

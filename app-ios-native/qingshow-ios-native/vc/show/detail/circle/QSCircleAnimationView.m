//
//  QSCircleAnimationView.m
//  CircleAnimation
//
//  Created by wxy325 on 16/1/2.
//  Copyright © 2016年 BI. All rights reserved.
//

#import "QSCircleAnimationView.h"
#import "QSCircleAnimationLayer.h"
#import <QuartzCore/QuartzCore.h>

@interface QSCircleAnimationView ()

@property (strong, nonatomic) QSCircleAnimationLayer* animationLayer;

@end

@implementation QSCircleAnimationView

- (instancetype)init {
    self = [self initWithFrame:CGRectMake(0, 0, 40, 40)];
    return self;
}
- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.animationLayer = [QSCircleAnimationLayer layer];
        [self.layer addSublayer:self.animationLayer];
        [self.animationLayer beginAnimation];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    CGSize size = self.bounds.size;
    CGPoint center = CGPointMake(size.width / 2, size.height / 2);
    self.animationLayer.position = center;
    self.animationLayer.bounds = self.bounds;
    [self.animationLayer setNeedsDisplay];
    
}

@end

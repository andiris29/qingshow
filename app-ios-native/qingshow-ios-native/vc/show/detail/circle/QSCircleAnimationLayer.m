//
//  QSCircleAnimationLayer.m
//  CircleAnimation
//
//  Created by wxy325 on 16/1/2.
//  Copyright © 2016年 BI. All rights reserved.
//

#import "QSCircleAnimationLayer.h"
#import "QSRingLayer.h"

@interface QSCircleAnimationLayer ()

@property (strong, nonatomic) QSRingLayer* topLayer;
@property (strong, nonatomic) QSRingLayer* shadowLayer;

@end

@implementation QSCircleAnimationLayer
+ (BOOL)needsDisplayForKey:(NSString *)key {
    if ([@[@"state"] indexOfObject:key] != NSNotFound) {
        return YES;
    }
    return [super needsDisplayForKey:key];
}


- (instancetype)init {
    self = [super init];
    if (self) {

        self.shadowLayer = [QSRingLayer layer];
        self.shadowLayer.circleColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:1];
        self.shadowLayer.opacity = 0.4;
        self.shadowLayer.innerRadius = 5.f;
        self.shadowLayer.outterRadius = 5.f;
        [self addSublayer:self.shadowLayer];

        
        self.topLayer = [QSRingLayer layer];
        [self addSublayer:self.topLayer];
        self.topLayer.innerRadius = 0.f;
        self.topLayer.outterRadius = 5.f;
        self.topLayer.circleColor = [UIColor whiteColor];
    }
    return self;
}
- (void)layoutSublayers {
    [super layoutSublayers];
    self.shadowLayer.position = self.position;
    self.shadowLayer.bounds = self.bounds;
    
    self.topLayer.position = self.position;
    self.topLayer.bounds = self.bounds;
}

- (void)beginAnimation {
    CGFloat duration = 2.5f;
//    CAKeyframeAnimation* keyframe0 = [CAKeyframeAnimation animationWithKeyPath:@"innerRadius"];
//    keyframe0.duration = duration;
//    keyframe0.keyTimes = @[@0, @0.05, @0.1, @1.0];
//    keyframe0.values = @[@4, @7, @4, @4];
//    keyframe0.repeatCount = INFINITY;
//    [self.topLayer addAnimation:keyframe0 forKey:@"kf0"];
    
    CAKeyframeAnimation* keyframe1 = [CAKeyframeAnimation animationWithKeyPath:@"outterRadius"];
    keyframe1.duration = duration;
    keyframe1.keyTimes = @[@0, @0.05, @0.1, @1.0];
    keyframe1.values = @[@5, @8, @5, @5.0];
    keyframe1.repeatCount = INFINITY;
    [self.topLayer addAnimation:keyframe1 forKey:@"kf1"];
    
    CAKeyframeAnimation* keyframe2 = [CAKeyframeAnimation animationWithKeyPath:@"outterRadius"];
    keyframe2.duration = duration;
    keyframe2.keyTimes = @[
                           @0, @0.2, @0.3, @0.35, @0.4, @0.5, @0.55, @0.6, @1.0
                           ];
    keyframe2.values = @[
                           @5, @5,   @20,  @5,    @5,   @20,  @5,   @5,   @5];
    keyframe2.repeatCount = INFINITY;
    [self.shadowLayer addAnimation:keyframe2 forKey:@"kf2"];
    
    keyframe2 = [CAKeyframeAnimation animationWithKeyPath:@"opacity"];
    keyframe2.duration = duration;
    keyframe2.keyTimes = @[
                           @0,   @0.2, @0.3, @0.35, @0.4, @0.5, @0.55,  @0.6, @1.0
                           ];
    keyframe2.values = @[
                           @0.4, @0.4, @0.0, @0.0,  @0.4, @0.0, @0,     @0.4, @0.4];
    keyframe2.repeatCount = INFINITY;
    [self.shadowLayer addAnimation:keyframe2 forKey:@"kf3"];
}
- (void)endAnimation {
    [self.topLayer removeAllAnimations];
    [self.shadowLayer removeAllAnimations];
}
@end

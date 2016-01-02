//
//  QSCircleShadoeLayer.h
//  CircleAnimation
//
//  Created by wxy325 on 16/1/2.
//  Copyright © 2016年 BI. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>

@interface QSRingLayer : CALayer

@property (assign, nonatomic) CGFloat innerRadius;
@property (assign, nonatomic) CGFloat outterRadius;
@property (strong, nonatomic) UIColor* circleColor;

@end

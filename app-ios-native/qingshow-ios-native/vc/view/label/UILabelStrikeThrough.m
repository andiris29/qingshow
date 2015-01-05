//
//  UILabelStrikeThrough.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/25/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "UILabelStrikeThrough.h"

@implementation UILabelStrikeThrough

@synthesize isWithStrikeThrough;

- (void)drawRect:(CGRect)rect
{
    if (isWithStrikeThrough)
    {
        CGContextRef c = UIGraphicsGetCurrentContext();
        
        UIColor* color = self.textColor;
        CGFloat r,g,b,a;
        [color getRed:&r green:&g blue:&b alpha:&a];
        CGFloat co[4] = {r,g, b,a};
        CGContextSetStrokeColor(c, co);
        CGContextSetLineWidth(c, 1);
        CGContextBeginPath(c);
        //画直线
        CGFloat halfWayUp = rect.size.height/2 + rect.origin.y;
        CGContextMoveToPoint(c, rect.origin.x, halfWayUp );//开始点
        CGContextAddLineToPoint(c, rect.origin.x + rect.size.width, halfWayUp);//结束点
        //画斜线
//        CGContextMoveToPoint(c, rect.origin.x, rect.origin.y+5 );
//        CGContextAddLineToPoint(c, (rect.origin.x + rect.size.width), rect.origin.y+rect.size.height-5); //斜线
        CGContextStrokePath(c);
    }
    
    [super drawRect:rect];
}


@end

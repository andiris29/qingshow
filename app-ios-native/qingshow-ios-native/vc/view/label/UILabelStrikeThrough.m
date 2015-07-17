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

- (float)getDollarSizeWidth
{
    CGSize conSize = CGSizeMake(INFINITY, INFINITY);
    UIFont* font = self.font;
    NSString* str = @"￥";
    CGSize size;
    if ([str respondsToSelector:@selector(sizeWithAttributes:)]) {
        //Above IOS 7
        NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc]init];
        paragraphStyle.lineBreakMode = NSLineBreakByWordWrapping;
        size = [str boundingRectWithSize:conSize options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName : font, NSParagraphStyleAttributeName : paragraphStyle} context:nil].size;
    } else {
        //Below IOS 7
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
        size = [str sizeWithFont:font constrainedToSize:conSize lineBreakMode:NSLineBreakByWordWrapping];
#pragma clang diagnostic pop
    }
    
    return size.width;
}
- (void)drawRect:(CGRect)rect
{
    if (isWithStrikeThrough)
    {
        float f = 0;
        if (self.isNotStrikeDollor && [self.text hasPrefix:@"￥"]) {
            f = [self getDollarSizeWidth];
        }
        
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
        CGContextMoveToPoint(c, rect.origin.x + f, halfWayUp );//开始点
        CGContextAddLineToPoint(c, rect.origin.x + rect.size.width, halfWayUp);//结束点
        //画斜线
//        CGContextMoveToPoint(c, rect.origin.x, rect.origin.y+5 );
//        CGContextAddLineToPoint(c, (rect.origin.x + rect.size.width), rect.origin.y+rect.size.height-5); //斜线
        CGContextStrokePath(c);
    }
    
    [super drawRect:rect];
}


@end

//
//  QSLayoutUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/18/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSLayoutUtil.h"

@implementation QSLayoutUtil

+ (CGSize)sizeForString:(NSString*)str withMaxWidth:(CGFloat)width height:(CGFloat)height font:(UIFont*)font
{
    CGSize conSize = CGSizeMake(width, height);
    CGSize size;
    if ([str respondsToSelector:@selector(sizeWithAttributes:)]) {
        //Above IOS 7
        NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc]init];
        paragraphStyle.lineBreakMode = NSLineBreakByWordWrapping;
        size = [str boundingRectWithSize:conSize options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName : font, NSParagraphStyleAttributeName : paragraphStyle} context:nil].size;
    } else {
        //Below IOS 7
        size = [str sizeWithFont:font constrainedToSize:conSize lineBreakMode:NSLineBreakByWordWrapping];
    }
    return size;
}

@end

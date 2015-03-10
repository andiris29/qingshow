//
//  QSOrderListHeaderView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSOrderListHeaderView.h"

@implementation QSOrderListHeaderView
- (void)awakeFromNib
{
    //Resize header for different screen size
    CGFloat screenWidth = [UIScreen mainScreen].bounds.size.width;
    CGSize headerSize = self.headerImageView.frame.size;
    CGFloat headerHeight = screenWidth * headerSize.height / headerSize.width;
    CGFloat deltaHeight = headerHeight - headerSize.height;
    headerSize.height = headerHeight;
    headerSize.width = screenWidth;
    
    CGRect frame = self.frame;
    frame.size = CGSizeMake(screenWidth, frame.size.height + deltaHeight);
    self.frame = frame;
    
    frame = self.label1.frame;
    frame.size = CGSizeMake(screenWidth, frame.size.height + deltaHeight);
    self.label1.frame = frame;
    
    frame = self.label2.frame;
    frame.size = CGSizeMake(screenWidth, frame.size.height + deltaHeight);
    self.label2.frame = frame;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end

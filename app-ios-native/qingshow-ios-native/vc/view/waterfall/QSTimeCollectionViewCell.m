//
//  QSTimeCollectionViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/1/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSTimeCollectionViewCell.h"
#import <QuartzCore/QuartzCore.h>
@implementation QSTimeCollectionViewCell

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}
- (void)awakeFromNib
{
    self.layer.cornerRadius = 4;
    self.layer.masksToBounds = YES;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end

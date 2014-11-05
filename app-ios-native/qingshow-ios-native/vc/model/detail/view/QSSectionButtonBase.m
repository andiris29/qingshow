//
//  QSSectionButtonBase.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSSectionButtonBase.h"
#define kQSSectionButtonColorBlack [UIColor colorWithRed:0 green:0 blue:0 alpha:0.6f]
#define kQSSectionButtonColorWhite [UIColor colorWithRed:1.f green:1.f blue:1.f alpha:0.6f]

@implementation QSSectionButtonBase
+ (QSSectionButtonBase*)generateView
{
    return nil;
}

- (void)awakeFromNib
{
    self.selected = false;
}

- (void)setSelected:(BOOL)selected
{
    _selected = selected;
    if (_selected)
    {
        self.backgroundColor = kQSSectionButtonColorWhite;
    }
    else
    {
        self.backgroundColor = kQSSectionButtonColorBlack;
    }
    
}
- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
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

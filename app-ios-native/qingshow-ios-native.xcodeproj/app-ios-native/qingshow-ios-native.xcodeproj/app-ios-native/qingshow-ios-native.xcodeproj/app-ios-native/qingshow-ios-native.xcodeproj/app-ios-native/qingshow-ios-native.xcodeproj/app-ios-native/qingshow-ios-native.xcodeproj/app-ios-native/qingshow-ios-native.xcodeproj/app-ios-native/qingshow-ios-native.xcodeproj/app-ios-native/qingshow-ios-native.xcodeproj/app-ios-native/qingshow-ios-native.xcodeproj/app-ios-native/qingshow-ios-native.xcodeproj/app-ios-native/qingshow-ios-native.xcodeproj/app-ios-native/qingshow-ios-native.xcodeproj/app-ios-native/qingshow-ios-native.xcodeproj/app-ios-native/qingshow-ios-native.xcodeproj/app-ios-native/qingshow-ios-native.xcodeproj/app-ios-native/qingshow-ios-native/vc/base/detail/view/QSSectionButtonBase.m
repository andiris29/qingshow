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
#define kQSSectionButtonColorPurple [UIColor colorWithRed:169.f/255.f green:26.f/255.f blue:78.f/255.f alpha:0.75f]

@interface QSSectionButtonBase ()
@property (strong, nonatomic) UIView* hoverView;
@end

@implementation QSSectionButtonBase
+ (QSSectionButtonBase*)generateView
{
    return nil;
}

- (void)awakeFromNib
{
    self.selected = false;
    self.hoverView = [[UIView alloc] init];
    self.hoverView.backgroundColor = kQSSectionButtonColorPurple;

    [self addSubview:self.hoverView];
    [self sendSubviewToBack:self.hoverView];
}

- (void)setSelected:(BOOL)selected
{
    _selected = selected;
    if (_selected)
    {
        self.hoverView.hidden = NO;
        self.backgroundColor = kQSSectionButtonColorBlack;
    }
    else
    {
        self.hoverView.hidden = YES;
        self.backgroundColor = kQSSectionButtonColorBlack;
    }
    
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    self.hoverView.frame = self.bounds;
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

//
//  QSCanvasImageView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCanvasImageView.h"

@interface QSCanvasImageView ()

@property (strong, nonatomic) UIButton* removeBtn;

@end

@implementation QSCanvasImageView

- (void)setHover:(BOOL)hover {
    _hover = hover;
    self.removeBtn.hidden = !hover;
    if (_hover) {
        self.layer.borderColor = [UIColor colorWithRed:240.f/255.f green:149.f/255.f blue:164.f/255.f alpha:1.f].CGColor;
        self.layer.borderWidth = 1.f;
    } else {
        self.layer.borderColor = [UIColor clearColor].CGColor;
        self.layer.borderWidth = 0.f;
    }
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.imgView = [[UIImageView alloc] init];
        [self addSubview:self.imgView];
        self.imgView.contentMode = UIViewContentModeScaleAspectFill;
        self.imgView.clipsToBounds = YES;
        
        UIImage* removeImg = [UIImage imageNamed:@"s20_canvas_remove"];
        self.removeBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [self.removeBtn setImage:removeImg forState:UIControlStateNormal];
        [self.removeBtn sizeToFit];
        [self addSubview:self.removeBtn];
        self.userInteractionEnabled = YES;
        self.hover = NO;
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    CGSize size = self.bounds.size;
    self.removeBtn.center = CGPointMake(size.width / 2, size.height / 2);
    self.imgView.frame = self.bounds;
}

- (CGSize)sizeThatFits:(CGSize)size {
    return [self.imgView sizeThatFits:size];
}

- (BOOL)judgeIsHitRemoveButton:(CGPoint)p {
    return [self hitTest:p withEvent:nil] == self.removeBtn;
}
@end

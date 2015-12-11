//
//  QSCanvasImageView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCanvasImageView.h"
#import "QSRectUtil.h"

@interface QSCanvasImageView ()

@property (strong, nonatomic) UIButton* removeBtn;
@property (strong, nonatomic) NSTimer* hideRemoveBtnTimer;

@end

@implementation QSCanvasImageView

- (void)hideRemoveBtn {
    [self hideRemoveBtnIn:0.f];
}
- (void)hideRemoveBtnIn:(float)delay {
    if (self.removeBtn.hidden) {
        return;
    }
    if (self.hideRemoveBtnTimer) {
        [self.hideRemoveBtnTimer invalidate];
        self.hideRemoveBtnTimer = nil;
    }
    delay = 1.0f;
    self.hideRemoveBtnTimer = [NSTimer scheduledTimerWithTimeInterval:delay target:self selector:@selector(_hideRemoveBtnWithAnimation) userInfo:nil repeats:NO];
    
}
- (void)_hideRemoveBtnWithAnimation {
    [UIView animateWithDuration:0.5f animations:^{
        self.removeBtn.alpha = 0.f;
    } completion:^(BOOL finished) {
        self.removeBtn.hidden = YES;
    }];
}
- (void)setHover:(BOOL)hover {
    if (hover == _hover) {
        return;
    }
    _hover = hover;
    [self updateHoverColor];
}
- (void)updateHoverColor {
    if (_hover) {
        self.layer.borderColor = [UIColor colorWithRed:40.f/255.f green:45.f/255.f blue:91.f/255.f alpha:1.f].CGColor;
        self.layer.borderWidth = 1.f;
        self.removeBtn.hidden = NO;
        self.removeBtn.alpha = 1.f;
        [self hideRemoveBtnIn:2.f];
    } else {
        self.layer.borderColor = [UIColor clearColor].CGColor;
        self.layer.borderWidth = 0.f;
        self.removeBtn.hidden = YES;
        self.removeBtn.alpha = 0.f;
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
        [self updateHoverColor];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    CGSize size = self.bounds.size;

    [self.removeBtn sizeToFit];
    if (self.removeBtn.bounds.size.width > size.width / 2 || self.removeBtn.bounds.size.height > size.height / 2) {
        CGSize s = [QSRectUtil scaleSize:self.removeBtn.bounds.size toFitSize:CGSizeMake(size.width / 2, size.height / 2)];
        CGRect b = self.removeBtn.bounds;
        b.size = s;
        self.removeBtn.bounds = b;
    }
    self.imgView.frame = self.bounds;
    CGRect rect = self.removeBtn.frame;
    rect.origin = CGPointMake(5, 5);
    self.removeBtn.frame = rect;
}

- (CGSize)sizeThatFits:(CGSize)size {
    return [self.imgView sizeThatFits:size];
}

- (BOOL)judgeIsHitRemoveButton:(CGPoint)p {
    return [self hitTest:p withEvent:nil] == self.removeBtn;
}
@end

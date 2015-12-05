//
//  QSBadgeButton.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/19/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSBadgeButton.h"
#import "UINib+QSExtension.h"


NSString* badgeBtnTypeToIcon(QSBadgeButtonType type) {
    NSArray* array =
    @[
      @"badge_btn_matcher",
      @"badge_btn_recommend",
      @"badge_btn_favor",
      @"badge_btn_following",
      @"badge_btn_follower"
      ];
    return array[type];
}
NSString* badgeBtnTypeToHoverIcon(QSBadgeButtonType type) {
    NSArray* array =
    @[
      @"badge_btn_matcher_hover",
      @"badge_btn_recommend_hover",
      @"badge_btn_favor_hover",
      @"badge_btn_following_hover",
      @"badge_btn_follower_hover"
      ];
    return array[type];
}

NSString* badgeBtnTypeToDotIcon(QSBadgeButtonType type) {
    NSArray* array =
    @[
      @"badge_btn_matcher",
      @"badge_btn_recommend_dot",
      @"badge_btn_favor",
      @"badge_btn_following",
      @"badge_btn_follower"
      ];
    return array[type];
}

NSString* badgeBtnTypeToTitle(QSBadgeButtonType type) {
    NSArray* array =
    @[
      @"搭配",
      @"推荐",
      @"收藏",
      @"关注",
      @"粉丝"
      ];
    return array[type];
}



@interface QSBadgeButton ()

@property (strong, nonatomic) UIImage* normalIcon;
@property (strong, nonatomic) UIImage* hoverIcon;
@property (strong, nonatomic) UIImage* dotIcon;

@end

@implementation QSBadgeButton

+ (instancetype)generateBtnWithType:(QSBadgeButtonType)type {
    QSBadgeButton* btn = [UINib generateViewWithNibName:@"QSBadgeButton"];
    btn.normalIcon = [UIImage imageNamed:badgeBtnTypeToIcon(type)];
    btn.hoverIcon = [UIImage imageNamed:badgeBtnTypeToHoverIcon(type)];
    btn.dotIcon = [UIImage imageNamed:badgeBtnTypeToDotIcon(type)];
    btn.imgView.image = btn.normalIcon;
    btn.label.text = badgeBtnTypeToTitle(type);
    btn.hover = NO;
    btn.type = type;
    return btn;
}
- (void)awakeFromNib {
    UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTap:)];
    [self addGestureRecognizer:ges];
    self.userInteractionEnabled = YES;
}

- (void)setHover:(BOOL)hover {
    _hover = hover;
    if (_hover) {
        //点击后取消红点
        _hasDot = NO;
    }
    [self updateColor];
}
- (void)setHasDot:(BOOL)hasDot {
    _hasDot = hasDot;
    [self updateColor];
}

- (void)updateColor {
    if (_hover) {
        self.imgView.image = self.hoverIcon;
        self.label.textColor = [UIColor colorWithRed:40.f/255.f green:45.f/255.f blue:90.f/255.f alpha:1.f];
    } else {
        if (_hasDot) {
            self.imgView.image = self.dotIcon;
        } else {
            self.imgView.image = self.normalIcon;
        }
        self.label.textColor = [UIColor colorWithRed:112.f/255.f green:112.f/255.f blue:112.f/255.f alpha:1.f];
    }
}

- (void)didTap:(UIGestureRecognizer*)ges {
    [self sendActionsForControlEvents:UIControlEventTouchUpInside];
}



@end

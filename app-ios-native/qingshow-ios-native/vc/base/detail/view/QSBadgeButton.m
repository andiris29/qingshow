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


@end

@implementation QSBadgeButton

+ (instancetype)generateBtnWithType:(QSBadgeButtonType)type {
    QSBadgeButton* btn = [UINib generateViewWithNibName:@"QSBadgeButton"];
    btn.normalIcon = [UIImage imageNamed:badgeBtnTypeToIcon(type)];
    btn.hoverIcon = [UIImage imageNamed:badgeBtnTypeToHoverIcon(type)];
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
    [self updateColor];
}

- (void)updateColor {
    if (_hover) {
        self.imgView.image = self.hoverIcon;
        self.label.textColor = [UIColor colorWithRed:240.f/255.f green:149.f/255.f blue:164.f/255.f alpha:1.f];
    } else {
        self.imgView.image = self.normalIcon;
        self.label.textColor = [UIColor colorWithRed:112.f/255.f green:112.f/255.f blue:112.f/255.f alpha:1.f];
    }
}

- (void)didTap:(UIGestureRecognizer*)ges {
    [self sendActionsForControlEvents:UIControlEventTouchUpInside];
}



@end

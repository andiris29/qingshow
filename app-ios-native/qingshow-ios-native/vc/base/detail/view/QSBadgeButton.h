//
//  QSBadgeButton.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/19/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger, QSBadgeButtonType) {
    QSBadgeButtonTypeMatcher = 0,
    QSBadgeButtonTypeRecommend = 1,
    QSBadgeButtonTypeFavor = 2,
    QSBadgeButtonTypeFollowing = 3,
    QSBadgeButtonTypeFollower = 4
};


@interface QSBadgeButton : UIControl

@property IBOutlet UIImageView* imgView;
@property IBOutlet UILabel* label;
@property (assign, nonatomic) QSBadgeButtonType type;

@property (assign, nonatomic) BOOL hover;

+ (instancetype)generateBtnWithType:(QSBadgeButtonType)type;

@end

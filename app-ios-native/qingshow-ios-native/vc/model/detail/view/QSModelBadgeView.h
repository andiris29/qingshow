//
//  QSModelBadgeView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSSectionButtonGroup.h"

typedef NS_ENUM(NSInteger, QSModelSection) {
    QSModelSectionShows = 0,
    QSModelSectionFollowing = 1,
    QSModelSectionFollower = 2
};

@protocol QSModelBadgeViewDelegate <NSObject>

- (void)changeToSection:(QSModelSection)section;
- (void)followButtonPressed;

@end

@interface QSModelBadgeView : UIView <QSSectionButtonGroupDelegate>

@property (weak, nonatomic) IBOutlet UIImageView *backgroundImageView;
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;

@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabel *roleLabel;
@property (weak, nonatomic) IBOutlet UILabel *statusLabel;

@property (weak, nonatomic) IBOutlet UIView *sectionGroupContainer;

+ (QSModelBadgeView*)generateView;

@end

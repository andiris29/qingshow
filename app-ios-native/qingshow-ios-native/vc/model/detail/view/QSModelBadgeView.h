//
//  QSModelBadgeView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSSectionButtonGroup.h"

@protocol QSModelBadgeViewDelegate <NSObject>

- (void)changeToSection:(int)sectionIndex;
- (void)followButtonPressed;

@end

@interface QSModelBadgeView : UIView <QSSectionButtonGroupDelegate>

@property (weak, nonatomic) IBOutlet UIImageView *backgroundImageView;
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;

@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabel *roleLabel;
@property (weak, nonatomic) IBOutlet UILabel *statusLabel;

@property (weak, nonatomic) IBOutlet UIView *sectionGroupContainer;

@property (weak, nonatomic) NSObject<QSModelBadgeViewDelegate>* delegate;

+ (QSModelBadgeView*)generateView;
- (void)bindWithDict:(NSDictionary*)peopleDict;

@end

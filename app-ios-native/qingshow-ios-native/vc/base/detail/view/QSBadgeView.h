//
//  QSModelBadgeView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSSectionButtonGroup.h"

@protocol QSBadgeViewDelegate <NSObject>

- (void)changeToSection:(int)sectionIndex;
- (void)singleButtonPressed;

@end

@interface QSBadgeView : UIView <QSSectionButtonGroupDelegate>

@property (weak, nonatomic) NSObject<QSBadgeViewDelegate>* delegate;
@property (strong, nonatomic) QSSectionButtonGroup* btnGroup;
@property (assign, nonatomic) QSSectionButtonGroupType type;

#pragma mark - Static
+ (QSBadgeView*)generateView;
+ (QSBadgeView*)generateViewWithType:(QSSectionButtonGroupType)type;

#pragma mark - Binding
- (void)bindWithPeopleDict:(NSDictionary*)peopleDict;

@end


//
//  QSModelBadgeView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSBadgeBtnGroup.h"

@protocol QSBadgeViewDelegate <NSObject>

- (void)changeToSection:(int)sectionIndex;


@end

@interface QSBadgeView : UIView

@property (weak, nonatomic) NSObject<QSBadgeViewDelegate>* delegate;
@property (weak, nonatomic) IBOutlet UIView *btnsContainer;

#pragma mark - Static
+ (QSBadgeView*)generateView;

#pragma mark - Binding
- (void)bindWithPeopleDict:(NSDictionary*)peopleDict;


@end


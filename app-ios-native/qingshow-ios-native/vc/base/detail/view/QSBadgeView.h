//
//  QSModelBadgeView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/5/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSBadgeBtnGroup.h"


@interface QSBadgeView : UIView

@property (weak, nonatomic) IBOutlet UIView *btnsContainer;
@property (weak, nonatomic) IBOutlet UIButton* followBtn;
@property (weak, nonatomic) IBOutlet UIButton* bonusBtn;
@property (strong, nonatomic) QSBadgeBtnGroup* btnGroup;
@property (weak, nonatomic) UIView* touchDelegateView;
@property (weak, nonatomic) IBOutlet UILabel *bonusLabel;

#pragma mark - Static
+ (QSBadgeView*)generateView;

#pragma mark - Binding
- (void)bindWithPeopleDict:(NSDictionary*)peopleDict;


@end


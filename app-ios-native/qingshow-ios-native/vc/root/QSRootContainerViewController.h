//
//  QSRootContainerViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/20/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSAbstractRootViewController.h"
#import "QSIRootContentViewController.h"
#import "QSU20NewBonusViewController.h"

@interface QSRootContainerViewController : QSAbstractRootViewController <UIAlertViewDelegate>
@property (strong, nonatomic) UINavigationController* contentNavVc;
@property (strong, nonatomic) UIViewController<QSIRootContentViewController>* contentVc;

- (void)hideRegisterVc;
- (void)scheduleToShowLoginGuide;

- (void)showNewBonusVcWithId:(NSString*)bonusId type:(QSU20NewBonusViewControllerState)type;
- (void)hideNewBonusVc;
@end

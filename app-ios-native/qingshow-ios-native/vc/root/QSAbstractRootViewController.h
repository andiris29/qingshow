//
//  QSAbstractRootViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSRootMenuView.h"
#import "QSG02WelcomeViewController.h"

@interface QSAbstractRootViewController : UIViewController <QSRootMenuViewDelegate>

@property (strong, nonatomic) UIView* contentContainerView;
@property (strong, nonatomic) UIView* menuContainerView;
@property (strong, nonatomic) UIView* popOverContainerView;

- (void)hideMenu;

@property (assign, nonatomic) BOOL hasFetchUserLogin;
@property (strong, nonatomic) QSRootMenuView* menuView;
- (void)handleCurrentUser;



- (void)didClickMenuBtn;
- (UIViewController*)showRegisterVc;
- (UIViewController*)showDefaultVc;
- (UIViewController*)showGuestVc;
- (UIViewController*)triggerToShowVc:(QSRootMenuItemType)type;

@end

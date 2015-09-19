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

@protocol QSMenuProviderDelegate <NSObject>

- (void)didClickMenuBtn;
- (UIViewController*)showRegisterVc;
- (UIViewController*)showDefaultVc;
- (UIViewController*)triggerToShowVc:(QSRootMenuItemType)type;
- (void)showDotInMenuForType:(QSRootMenuItemType)type;
- (void)hideDotInMenuForType:(QSRootMenuItemType)type;

@end

@interface QSAbstractRootViewController : UIViewController <QSRootMenuViewDelegate, QSG02WelcomeViewControllerDelegate, QSMenuProviderDelegate>

- (void)hideMenu;

@property (assign, nonatomic) BOOL hasFetchUserLogin;
@property (strong, nonatomic) QSRootMenuView* menuView;
- (void)handleCurrentUser;

@end

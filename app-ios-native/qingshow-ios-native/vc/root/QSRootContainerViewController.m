//
//  QSRootContainerViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/20/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSRootContainerViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "QSU01UserDetailViewController.h"
#import "QSU02UserSettingViewController.h"
#import "QSNavigationController.h"
#import "QSUserManager.h"
#import "QSS20MatcherViewController.h"
#import "QSS01MatchShowsViewController.h"

#import "QSU09OrderListViewController.h"
#import "QSU19LoginGuideViewController.h"

#import "QST01ShowTradeViewController.h"
#import "NSDictionary+QSExtension.h"
#import "UIViewController+QSExtension.h"

#import "QSRootContentViewController.h"
#import "QSPnsHandler.h"
#import "QSPeopleUtil.h"

@interface QSRootContainerViewController ()


@property (strong, nonatomic) QSPnsHandler* pnsHandler;
@property (strong, nonatomic) UINavigationController* loginGuideNavVc;
@end

@implementation QSRootContainerViewController

- (instancetype)init {
    self = [super init];
    if (self){
        self.pnsHandler = [[QSPnsHandler alloc] initWithRootVc:self];
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.

    [self.navigationController.navigationBar
     setTitleTextAttributes:@{
                              NSFontAttributeName:NAVNEWFONT,
                              NSForegroundColorAttributeName:[UIColor blackColor]
                              }];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceivePrompToLoginNotification:) name:kShowLoginPrompVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveHidePrompToLoginNotification:) name:kHideLoginPrompVcNotificationName object:nil];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    
    if (![QSUserManager shareUserManager].userInfo) {
        [self.menuView triggerItemTypePressed:QSRootMenuItemMeida];
    }
}
- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    self.contentNavVc.view.frame = self.view.bounds;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}



#pragma mark - QSRootMenuViewDelegate
- (void)rootMenuViewDidTapBlankView
{
    [self hideMenu];
}
- (void)rootMenuItemPressedType:(QSRootMenuItemType)type oldType:(QSRootMenuItemType)oldType
{
    [super rootMenuItemPressedType:type oldType:oldType];
    [self hideMenu];
    
    if (type != QSRootMenuItemMeida && type != QSRootMenuItemShowTrade) {
        NSDictionary* u = [QSUserManager shareUserManager].userInfo;
        if (!u) {
            [self showRegisterVc];
            return;
        } else if ([QSPeopleUtil getPeopleRole:u] == QSPeopleRoleGuest &&
                   type == QSRootMenuItemDiscount) {
            [self showRegisterVc];
            return;
        }
    }
    
    UIViewController<QSIRootContentViewController>* vc = nil;
    switch (type) {
        case QSRootMenuItemMy: {
            QSU01UserDetailViewController* u01Vc = [[QSU01UserDetailViewController alloc] initWithCurrentUser];
            u01Vc.menuProvider = self;
            vc = u01Vc;
            break;
        }
        case QSRootMenuItemMeida: {
            QSS01MatchShowsViewController * matcherShowVc = [[QSS01MatchShowsViewController alloc] init];
            matcherShowVc.menuProvider = self;
            vc = matcherShowVc;
            break;
        }
        case QSRootMenuItemSetting: {
            QSU02UserSettingViewController *settingVc = [[QSU02UserSettingViewController alloc]init];
            settingVc.menuProvider = self;
            vc = settingVc;
            break;
        }
        case QSRootMenuItemMatcher: {
            QSS20MatcherViewController* matcherVc = [[QSS20MatcherViewController alloc] init];
            matcherVc.menuProvider = self;
            vc = matcherVc;
            break;
        }
        case QSRootMenuItemDiscount: {
            QSU09OrderListViewController* orderListVc = [[QSU09OrderListViewController alloc] init];
            orderListVc.menuProvider = self;
            vc = orderListVc;
            break;
        }
        case QSRootMenuItemShowTrade:{
            QST01ShowTradeViewController *t01VC = [[QST01ShowTradeViewController alloc]init];
            t01VC.menuProvider = self;
            vc = t01VC;
            break;
        }
        default:{
            break;
        }
    }
    
    [self showVc:vc];

}

- (void)showVc:(UIViewController<QSIRootContentViewController>*)vc{
    if (!vc) {
        return;
    }
    
    UINavigationController* nav = [[QSNavigationController alloc] initWithRootViewController:vc];
    nav.navigationBar.translucent = NO;
    self.contentVc = vc;
    [self.contentNavVc.view removeFromSuperview];
    [self.contentNavVc willMoveToParentViewController:nil];
    [self.contentNavVc removeFromParentViewController];

    [self.view addSubview:nav.view];
    [self addChildViewController:nav];
    

    [nav didMoveToParentViewController:self];    //Call after transition
    self.contentNavVc = nav;
}
- (UIViewController*)showRegisterVc {
    if (self.loginGuideNavVc) {
        return self.loginGuideNavVc;
    }
    UINavigationController* vc = nil;
    NSDictionary* u = [QSUserManager shareUserManager].userInfo;
    if (!u || [QSPeopleUtil getPeopleRole:u] == QSPeopleRoleGuest) {
#warning TODO animation
        vc = [[UINavigationController alloc] initWithRootViewController: [[QSU19LoginGuideViewController alloc] init]];
        vc.view.backgroundColor = [UIColor clearColor];
        vc.navigationBarHidden = YES;
        vc.view.frame = self.view.bounds;
        [self addChildViewController:vc];
        [self.view addSubview:vc.view];
        self.loginGuideNavVc = vc;
    }
    return vc;
}
- (UIViewController*)showDefaultVc {
    [self.menuView triggerItemTypePressed:QSRootMenuItemMeida];
    return self.contentVc;
}
- (UIViewController*)showGuestVc {
    [self.menuView triggerItemTypePressed:QSRootMenuItemMatcher];
    if ([self.contentVc isKindOfClass:[QSS20MatcherViewController class]]) {
        QSS20MatcherViewController* vc = (QSS20MatcherViewController*)self.contentVc;
        vc.isGuestFirstLoad = YES;
        [vc hideMenuBtn];
    }
    return self.contentVc;
}

- (UIViewController*)triggerToShowVc:(QSRootMenuItemType)type {
    [self.menuView triggerItemTypePressed:type];
    return self.contentVc;
}

#pragma mark - Notification
- (void)didReceivePrompToLoginNotification:(NSNotification*)noti {
    [self showRegisterVc];
}
- (void)didReceiveHidePrompToLoginNotification:(NSNotification*)noti {
#warning TODO animation
    [self.loginGuideNavVc removeFromParentViewController];
    [self.loginGuideNavVc.view removeFromSuperview];
    self.loginGuideNavVc = nil;
}
@end

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
#import "QSUserManager.h"
#import "QSS20MatcherViewController.h"
#import "QSS01MatchShowsViewController.h"
#import "QSNavigationController.h"
#import "QSU09TradeListViewController.h"
#import "QSU19LoginGuideViewController.h"

#import "QST01ShowTradeViewController.h"
#import "NSDictionary+QSExtension.h"
#import "UIViewController+QSExtension.h"

#import "QSRootContentViewController.h"
#import "QSPnsHandler.h"
#import "QSPeopleUtil.h"
#import "QSNotificationHelper.h"

#import "QSU20NewBonusViewController.h"
#import "QSU21NewParticipantBonusViewController.h"

@interface QSRootContainerViewController ()


@property (strong, nonatomic) QSPnsHandler* pnsHandler;
@property (strong, nonatomic) UINavigationController* loginGuideNavVc;

@property (strong, nonatomic) QSU20NewBonusViewController* u20NewBonusVc;
@property (strong, nonatomic) QSU21NewParticipantBonusViewController* u21NewParticipantBonusVc;

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
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceivePrompToLoginNotification:) name:kShowLoginPrompVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveHidePrompToLoginNotification:) name:kHideLoginPrompVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveScheduleToShowLoginGuideNotification:) name:kScheduleToShowLoginGuideNotificationName object:nil];


    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveShowNewBonusVcNoti:) name:kShowNewBonusVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveHideNewBonusVcNoti:) name:kHideNewBonusVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveShowNewParticipantBonusVcNoti:) name:kShowNewParticipantBonusVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveHideNewParticipantBonusVcNoti:) name:kHideNewParticipantBonusVcNotificationName object:nil];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveShowBonusListVcNoti:) name:kShowBonusListVcNotificatinName object:nil];
//    [self didReceiveShowNewParticipantBonusVcNoti:nil];
//    [self didReceiveShowNewBonusVcNoti:nil];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
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
            [self.menuView hoverItemType:oldType];
            return;
        } else if ([QSPeopleUtil getPeopleRole:u] == QSPeopleRoleGuest &&
                   type == QSRootMenuItemDiscount) {
            [self showRegisterVc];
            [self.menuView hoverItemType:oldType];
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
            QSU09TradeListViewController* tradeListVc = [[QSU09TradeListViewController alloc] init];
            tradeListVc.menuProvider = self;
            vc = tradeListVc;
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

    [self.contentContainerView addSubview:nav.view];
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
        vc = [[UINavigationController alloc] initWithRootViewController: [[QSU19LoginGuideViewController alloc] init]];
        [self _showVcInPopoverContainer:vc];
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
    [self _hideVcInPopoverContainer:self.loginGuideNavVc];

    self.loginGuideNavVc = nil;

}
- (void)didReceiveScheduleToShowLoginGuideNotification:(NSNotification*)noti {
    [NSTimer scheduledTimerWithTimeInterval:15.0 target:self selector:@selector(_didFinishScheduleToShowLoginGuide) userInfo:nil repeats:NO];
}
- (void)_didFinishScheduleToShowLoginGuide {
    [self showRegisterVc];
}

- (void)didReceiveShowNewBonusVcNoti:(NSNotification*)noti {
    if (self.u20NewBonusVc) {
        return;
    }
    
    self.u20NewBonusVc = [[QSU20NewBonusViewController alloc] init];
    [self _showVcInPopoverContainer:self.u20NewBonusVc];
}

- (void)didReceiveHideNewBonusVcNoti:(NSNotification*)noti {
    [self _hideVcInPopoverContainer:self.u20NewBonusVc];
    self.u20NewBonusVc = nil;
}

- (void)didReceiveShowNewParticipantBonusVcNoti:(NSNotification*)noti {
    if (self.u21NewParticipantBonusVc) {
        return;
    }
    self.u21NewParticipantBonusVc = [[QSU21NewParticipantBonusViewController alloc] init];
    [self _showVcInPopoverContainer:self.u21NewParticipantBonusVc];
}

- (void)didReceiveHideNewParticipantBonusVcNoti:(NSNotification*)noti {
    [self _hideVcInPopoverContainer:self.u21NewParticipantBonusVc];
    self.u21NewParticipantBonusVc = nil;
}


- (void)didReceiveShowBonusListVcNoti:(NSNotification*)noti {
    UIViewController* vc = [self triggerToShowVc:QSRootMenuItemSetting];
    if ([vc isKindOfClass:[QSU02UserSettingViewController class]]) {
        QSU02UserSettingViewController* u02Vc = (QSU02UserSettingViewController*)vc;
        [u02Vc showBonuesVC];
    }
}


- (void)_showVcInPopoverContainer:(UIViewController*)vc {
#warning TODO animation
    vc.view.frame = self.popOverContainerView.bounds;
    [self addChildViewController:vc];
    [self.popOverContainerView addSubview:vc.view];
    self.popOverContainerView.hidden = NO;
}
- (void)_hideVcInPopoverContainer:(UIViewController*)vc {
#warning TODO animation
    [vc removeFromParentViewController];
    [vc.view removeFromSuperview];
    self.popOverContainerView.hidden = YES;
}
@end

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

#import "QSS12NewTradeExpectableViewController.h"
#import "QSU20NewBonusViewController.h"
#import "QSU21NewParticipantBonusViewController.h"
#import "QSBlock.h"
#import "QSUnreadManager.h"
#import "QSEntityUtil.h"

#define kWelcomePageVersionKey @"kWelcomePageVersionKey"

@interface QSRootContainerViewController () <QSG02WelcomeViewControllerDelegate, QSS12NewTradeNotifyViewControllerDelegate>


@property (strong, nonatomic) QSPnsHandler* pnsHandler;
@property (strong, nonatomic) UINavigationController* loginGuideNavVc;

@property (strong, nonatomic) QSU20NewBonusViewController* u20NewBonusVc;
@property (strong, nonatomic) QSU21NewParticipantBonusViewController* u21NewParticipantBonusVc;
@property (strong, nonatomic) QSS12NewTradeExpectableViewController* s12NotiVc;

@property (strong, nonatomic) QSG02WelcomeViewController* welcomeVc;

@property (assign, nonatomic) BOOL fIsFirstLoad;
@property (strong, nonatomic) NSTimer* showLoginGuideTimer;

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
    self.fIsFirstLoad = YES;
    [self _observeNotifications];

    //For Test
//    [self didReceiveShowNewParticipantBonusVcNoti:nil];
    [self didReceiveShowNewBonusVcNoti:nil];
}
- (void)_observeNotifications {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceivePrompToLoginNotification:) name:kShowLoginPrompVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveHidePrompToLoginNotification:) name:kHideLoginPrompVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveScheduleToShowLoginGuideNotification:) name:kScheduleToShowLoginGuideNotificationName object:nil];


    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveShowNewBonusVcNoti:) name:kShowNewBonusVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveHideNewBonusVcNoti:) name:kHideNewBonusVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveShowNewParticipantBonusVcNoti:) name:kShowNewParticipantBonusVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveHideNewParticipantBonusVcNoti:) name:kHideNewParticipantBonusVcNotificationName object:nil];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveShowBonusListVcNoti:) name:kShowBonusListVcNotificatinName object:nil];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveShowTradeExpectablePriceChangeVcNoti:) name:kShowTradeExpectablePriceChangeVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveHideTradeExpectablePriceChangeVcNoti:) name:kHideTradeExpectablePriceChangeVcNotificationName object:nil];
}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    if (![QSUserManager shareUserManager].userInfo) {
        [self.menuView triggerItemTypePressed:QSRootMenuItemMeida];
    }
    
    if (self.fIsFirstLoad) {
        self.fIsFirstLoad = NO;
        
        NSUserDefaults* userDefault = [NSUserDefaults standardUserDefaults];
        NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
        if (![[userDefault valueForKey:kWelcomePageVersionKey] isEqualToString:version]) {
            
            self.welcomeVc = [[QSG02WelcomeViewController alloc] init];
            self.welcomeVc.delegate = self;
            [self _showVcInPopoverContainer:self.welcomeVc withAnimation:NO];
            
            [userDefault setValue:version forKey:kWelcomePageVersionKey];
            [userDefault synchronize];
        }
    }
    
    if (self.hasFetchUserLogin) {
        [self handleCurrentUser];
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
    if (self.showLoginGuideTimer) {
        [self.showLoginGuideTimer invalidate];
        self.showLoginGuideTimer = nil;
    }
    if (self.loginGuideNavVc) {
        return self.loginGuideNavVc;
    }

    UINavigationController* vc = nil;
    NSDictionary* u = [QSUserManager shareUserManager].userInfo;
    if (!u || [QSPeopleUtil getPeopleRole:u] == QSPeopleRoleGuest) {
        vc = [[UINavigationController alloc] initWithRootViewController: [[QSU19LoginGuideViewController alloc] init]];
        [self _showVcInPopoverContainer:vc withAnimation:YES];
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
    [self _hideVcInPopoverContainer:self.loginGuideNavVc withAnimation:YES];
    self.loginGuideNavVc = nil;
}
- (void)didReceiveScheduleToShowLoginGuideNotification:(NSNotification*)noti {
    self.showLoginGuideTimer = [NSTimer scheduledTimerWithTimeInterval:15.0 target:self selector:@selector(_didFinishScheduleToShowLoginGuide) userInfo:nil repeats:NO];
}
- (void)_didFinishScheduleToShowLoginGuide {
    UIViewController* guideNavVc =  [self showRegisterVc];
    if ([guideNavVc isKindOfClass:[UINavigationController class]]) {
        UINavigationController* navVc = (UINavigationController*)guideNavVc;
        if ([navVc.topViewController isKindOfClass:[QSU19LoginGuideViewController class]]) {
            QSU19LoginGuideViewController* guideVc = (QSU19LoginGuideViewController*)navVc.topViewController;
            guideVc.fShowCloseBtn = NO;
        }
        

    }
}

- (void)didReceiveShowNewBonusVcNoti:(NSNotification*)noti {
    if (self.u20NewBonusVc) {
        return;
    }
    
    self.u20NewBonusVc = [[QSU20NewBonusViewController alloc] init];
    self.u20NewBonusVc.bonusIndex = [noti.userInfo numberValueForKeyPath:@"index"];
    [self _showVcInPopoverContainer:self.u20NewBonusVc withAnimation:YES];
}

- (void)didReceiveHideNewBonusVcNoti:(NSNotification*)noti {
    [self _hideVcInPopoverContainer:self.u20NewBonusVc withAnimation:YES];
    self.u20NewBonusVc = nil;
}

- (void)didReceiveShowNewParticipantBonusVcNoti:(NSNotification*)noti {
    if (self.u21NewParticipantBonusVc) {
        return;
    }
    self.u21NewParticipantBonusVc = [[QSU21NewParticipantBonusViewController alloc] init];
    self.u21NewParticipantBonusVc.bonusIndex = [noti.userInfo numberValueForKeyPath:@"index"];
    [self _showVcInPopoverContainer:self.u21NewParticipantBonusVc withAnimation:YES];
}

- (void)didReceiveHideNewParticipantBonusVcNoti:(NSNotification*)noti {
    [self _hideVcInPopoverContainer:self.u21NewParticipantBonusVc withAnimation:YES];
    self.u21NewParticipantBonusVc = nil;
}


- (void)didReceiveShowBonusListVcNoti:(NSNotification*)noti {
    UIViewController* vc = [self triggerToShowVc:QSRootMenuItemSetting];
    if ([vc isKindOfClass:[QSU02UserSettingViewController class]]) {
        QSU02UserSettingViewController* u02Vc = (QSU02UserSettingViewController*)vc;
        [u02Vc showBonuesVC];
    }
}

- (void)didReceiveShowTradeExpectablePriceChangeVcNoti:(NSNotification*)noti {
    //tradeId or tradeDict
    NSDictionary* tradeDict = [noti.userInfo dictValueForKeyPath:@"tradeDict"];
    if (tradeDict) {
        [self _showS12VcWithTradeDict:tradeDict];
    } else {
        NSString* tradeId = [noti.userInfo stringValueForKeyPath:@"tradeId"];
        [SHARE_NW_ENGINE queryTradeDetail:tradeId onSucceed:^(NSDictionary *dict) {
            [self _showS12VcWithTradeDict:dict];
        } onError:^(NSError *error) {

        }];
    }
}

- (void)_showS12VcWithTradeDict:(NSDictionary*)dict {
    NSString* tradeId = [QSEntityUtil getIdOrEmptyStr:dict];
    [[QSUnreadManager getInstance] clearTradeUnreadId:tradeId];
    self.s12NotiVc = [[QSS12NewTradeExpectableViewController alloc] initWithDict:dict];
    self.s12NotiVc.delelgate = self;
    [self._showVcInPopoverContainer:self.s12NotiVc withAnimation:YES];
}

- (void)didReceiveHideTradeExpectablePriceChangeVcNoti:(NSNotification*)noti {
    [self _hideVcInPopoverContainer:self.s12NotiVc withAnimation:YES];
    self.s12NotiVc = nil;
}

#pragma mark -

- (void)_showVcInPopoverContainer:(UIViewController*)vc withAnimation:(BOOL)fAnimate {
    vc.view.frame = self.popOverContainerView.bounds;
    [self addChildViewController:vc];
    [self.popOverContainerView addSubview:vc.view];
    self.popOverContainerView.hidden = NO;
    
    if (fAnimate) {
        self.popOverContainerView.alpha = 0;
        [UIView animateWithDuration:0.5f animations:^{
            self.popOverContainerView.alpha = 1;
        } completion:^(BOOL finished) {
            self.popOverContainerView.alpha = 1;
        }];
    }
}
- (void)_hideVcInPopoverContainer:(UIViewController*)vc withAnimation:(BOOL)fAnimate{
    VoidBlock hideBlock = ^{
        [vc.view removeFromSuperview];
        [vc removeFromParentViewController];
        [vc.view removeFromSuperview];
        self.popOverContainerView.hidden = YES;
    };
    
    if (fAnimate) {
        [UIView animateWithDuration:0.5f animations:^{
            vc.view.alpha = 0.f;
        } completion:^(BOOL finished) {
            hideBlock();
        }];
    } else {
        hideBlock();
    }
}

#pragma mark - QSG02WelcomeViewControllerDelegate
- (void)dismissWelcomePage:(QSG02WelcomeViewController*)vc
{
    [self _hideVcInPopoverContainer:vc withAnimation:YES];
    self.welcomeVc = nil;
}


#pragma mark - S12NotiVc
- (void)didClickPay:(QSS12NewTradeExpectableViewController*)vc {
    NSDictionary* tradeDict = vc.tradeDict;
    NSNumber* actualPrice = vc.expectablePrice;
    NSDictionary* paramDict = nil;
    if (actualPrice) {
        paramDict = @{@"actualPrice" : vc.expectablePrice};
    }
    [SHARE_PAYMENT_SERVICE sharedForTrade:tradeDict onSucceed:^(NSDictionary* d){
        [self didClickClose:vc];
        QSS11CreateTradeViewController* v = [[QSS11CreateTradeViewController alloc] initWithDict:d];
        v.menuProvider = self.menuProvider;
        [self.navigationController pushViewController:v animated:YES];
    } onError:^(NSError *error) {
        [vc handleError:error];
    }];

}

@end

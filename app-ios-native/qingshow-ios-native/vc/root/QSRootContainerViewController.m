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
#import "QSUserManager.h"
#import "QSS20MatcherViewController.h"
#import "QSS01MatchShowsViewController.h"
#import "QSNavigationController.h"
#import "QSU09TradeListViewController.h"
#import "QSU19LoginGuideViewController.h"
#import "NSDictionary+QSExtension.h"
#import "UIViewController+QSExtension.h"
#import "QSActivityViewController.h"

#import "QSRootContentViewController.h"
#import "QSPnsHandler.h"
#import "QSPeopleUtil.h"
#import "QSRootNotificationHelper.h"

#import "QSU20NewBonusViewController.h"
#import "QSBlock.h"
#import "QSUnreadManager.h"
#import "QSEntityUtil.h"
#import "QSNetworkKit.h"
#import "QSPaymentService.h"
#import "QSU14CreateTradeViewController.h"
#import "QSRootContainerViewController+RootNotification.h"

#define kWelcomePageVersionKey @"kWelcomePageVersionKey"

@interface QSRootContainerViewController () <QSG02WelcomeViewControllerDelegate, QSActivityViewControllerDelegate>


@property (strong, nonatomic) QSPnsHandler* pnsHandler;
@property (strong, nonatomic) UINavigationController* loginGuideNavVc;

@property (strong, nonatomic) QSU20NewBonusViewController* u20NewBonusVc;

@property (strong, nonatomic) QSG02WelcomeViewController* welcomeVc;

@property (assign, nonatomic) BOOL fIsFirstLoad;
@property (strong, nonatomic) NSTimer* showLoginGuideTimer;
@property (strong, nonatomic) QSActivityViewController* activityVc;
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
    [self observeNotifications];
    
//    [self _handleBonusUnread];
    [self _handleSystemConfig];
    [self _registerNoti];
}
- (void)_registerNoti {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(_handleBonusUnread) name:kQSUnreadChangeNotificationName object:nil];
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
    
    if (type != QSRootMenuItemMeida) {
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
            vc = u01Vc;
            break;
        }
        case QSRootMenuItemMeida: {
            QSS01MatchShowsViewController * matcherShowVc = [[QSS01MatchShowsViewController alloc] init];
            vc = matcherShowVc;
            break;
        }
        case QSRootMenuItemMatcher: {
            QSS20MatcherViewController* matcherVc = [[QSS20MatcherViewController alloc] init];
            vc = matcherVc;
            break;
        }
        case QSRootMenuItemDiscount: {
            QSU09TradeListViewController* tradeListVc = [[QSU09TradeListViewController alloc] init];
            vc = tradeListVc;
            break;
        }
        default:{
            break;
        }
    }
    
    [self showVc:vc];

}

- (void)showVc:(UIViewController<QSIRootContentViewController>*)vc {
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
- (void)hideRegisterVc {
    [self _hideVcInPopoverContainer:self.loginGuideNavVc withAnimation:YES];
    self.loginGuideNavVc = nil;
    [self handleCurrentUser];
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
- (void)scheduleToShowLoginGuide {
    self.showLoginGuideTimer = [NSTimer scheduledTimerWithTimeInterval:15.0 target:self selector:@selector(_didFinishScheduleToShowLoginGuide) userInfo:nil repeats:NO];;
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

- (void)showNewBonusVcWithId:(NSString*)bonusId type:(QSU20NewBonusViewControllerState)type {
    [SHARE_NW_ENGINE queryBonusWithIds:@[bonusId]
                             onSucceed:^(NSArray *array, NSDictionary *metadata) {
                                 if (!array.count || self.u20NewBonusVc) {
                                     return;
                                 }
                                 NSDictionary* bonusDict = [array firstObject];
                                 self.u20NewBonusVc = [[QSU20NewBonusViewController alloc] initWithBonus:bonusDict state:type];
                                 CGFloat rate = [UIScreen mainScreen].bounds.size.width / 320.f;
                                 self.u20NewBonusVc.view.transform = CGAffineTransformMakeScale(rate, rate);
                                 [self _showVcInPopoverContainer:self.u20NewBonusVc withAnimation:YES];
                             }
                               onError:nil];

}

- (void)hideNewBonusVc {
    [self _hideVcInPopoverContainer:self.u20NewBonusVc withAnimation:YES];
    self.u20NewBonusVc = nil;
}

- (void)showBonusVc {
    UIViewController* vc = [self triggerToShowVc:QSRootMenuItemMy];
    if ([vc isKindOfClass:[QSU01UserDetailViewController class]]) {
        QSU01UserDetailViewController* u01Vc = (QSU01UserDetailViewController*)vc;
        [u01Vc showBonusVC];
    }
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

#pragma mark - Handle Unread Bonus Notification
- (void)_handleBonusUnread {
    NSArray* unreads = [[QSUnreadManager getInstance] getUnreadOfCommand:@"newBonus"];
    NSDictionary* bonusDict = nil;
    NSNumber* type = nil;
    for (NSDictionary* u in unreads) {
        type = [u numberValueForKeyPath:@"extra.type"];
        if (type.intValue == 0 || type.intValue == 1) {
            bonusDict = u;
            break;
        }
    }
    if (bonusDict) {
        NSString* bonusId = [bonusDict stringValueForKeyPath:@"extra._id"];
        if (type.integerValue == 0) {
            [self showNewBonusVcWithId:bonusId type:QSU20NewBonusViewControllerStateAbout];
        } else {
            [self showNewBonusVcWithId:bonusId type:QSU20NewBonusViewControllerStateParticipant];
        }
    }
}

#pragma mark - System Get Config
- (void)_handleSystemConfig {
    [SHARE_NW_ENGINE systemGetConfigOnSucceed:^(NSDictionary * config) {
        NSString* imgPath = [config stringValueForKeyPath:@"config.event.image"];
        if (imgPath && ![[QSUserManager shareUserManager].configEventImagePath isEqualToString:imgPath] && !self.activityVc) {
            self.activityVc = [[QSActivityViewController alloc] initWithImgPath:imgPath];
            self.activityVc.delegate = self;
            [self _showVcInPopoverContainer:self.activityVc withAnimation:YES];
        }
    } onError:nil];
}

- (void)activityVcShouldDismiss:(QSActivityViewController*)vc {
    [QSUserManager shareUserManager].configEventImagePath = self.activityVc.path;
    [self _hideVcInPopoverContainer:self.activityVc withAnimation:YES];
    self.activityVc = nil;
}

- (void)showLatestS24Vc {
    QSS01MatchShowsViewController* vc = (QSS01MatchShowsViewController*)[self triggerToShowVc:QSRootMenuItemMeida];
    if ([vc respondsToSelector:@selector(showLatestS24Vc)]) {
        [vc showLatestS24Vc];
    }
}
@end

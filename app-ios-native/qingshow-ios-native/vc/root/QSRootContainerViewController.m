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
#import "QSU07RegisterViewController.h"
#import "QSUserManager.h"
#import "QSS20MatcherViewController.h"
#import "QSS01MatchShowsViewController.h"
#import "QSPnsNotificationName.h"
#import "QSEntityUtil.h"
#import "QSS03ShowDetailViewController.h"
#import "QSS04CommentListViewController.h"
#import "QSU09OrderListViewController.h"
#import "NSDictionary+QSExtension.h"
#import "QSBlockAlertView.h"
#import "QSPnsHelper.h"

@interface QSRootContainerViewController ()

@property (strong, nonatomic) UINavigationController* contentNavVc;
@property (strong, nonatomic) UIViewController* contentVc;

@end

@implementation QSRootContainerViewController

- (instancetype)init {
    self = [super init];
    if (self){
        [self registerForPnsNotifications];
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.

    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];

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
    if (oldType != type) {
        UIViewController* vc = nil;
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
            default:{
                break;
            }
        }
        [self showVc:vc];
        
        if (![vc isKindOfClass:[QSS01MatchShowsViewController class]]) {
            [self showRegisterVc];
        }
    } else {
        if ([self.contentNavVc isKindOfClass:[UINavigationController class]]) {
            [((UINavigationController*)self.contentNavVc) popToRootViewControllerAnimated:NO];
        }
    }
    
    
}

- (void)showVc:(UIViewController*)vc{
    if (!vc) {
        return;
    }
    
    UINavigationController* nav = [[QSNavigationController alloc] initWithRootViewController:vc];
    nav.navigationBar.translucent = NO;
    self.contentVc = vc;
    vc = nav;
    [self.contentNavVc.view removeFromSuperview];
    [self.contentNavVc willMoveToParentViewController:nil];
    [self.contentNavVc removeFromParentViewController];

    [self.view addSubview:vc.view];
    [self addChildViewController:vc];
    

    [vc didMoveToParentViewController:self];    //Call after transition
    self.contentNavVc = nav;
}
- (UIViewController*)showRegisterVc {
    UIViewController* vc = nil;
    if (![QSUserManager shareUserManager].userInfo) {
        vc = [[QSU07RegisterViewController alloc] init];
        [self.navigationController pushViewController:vc animated:YES];
    }
    return vc;
}
- (UIViewController*)showDefaultVc {
    [self.menuView triggerItemTypePressed:QSRootMenuItemMeida];
    return self.contentVc;
}
- (UIViewController*)triggerToShowVc:(QSRootMenuItemType)type {
    [self.menuView triggerItemTypePressed:type];
    return self.contentVc;
}
#pragma mark - Pns
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    if ([alertView isKindOfClass:[QSBlockAlertView class]]) {
        QSBlockAlertView* blockAlertView = (QSBlockAlertView*)alertView;
        if (buttonIndex == blockAlertView.cancelButtonIndex) {
            if (blockAlertView.cancelHandler) {
                blockAlertView.cancelHandler();
            }
        } else {
            if (blockAlertView.succeedHandler) {
                blockAlertView.succeedHandler();
            }
        }
    }
}
- (void)handlePnsWithHandler:(VoidBlock)handler
                       title:(NSString*)title
                    userInfo:(NSDictionary*)userInfo {
    if ([QSPnsHelper isFromBackground:userInfo]) {
        //从后台来，直接执行
        if (handler) {
            handler();
        }
    } else {
        //非后台，先显示alertView
        QSBlockAlertView* alertView = [[QSBlockAlertView alloc] initWithTitle:title message:nil delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确定", nil];
        alertView.succeedHandler = handler;
        [alertView show];
    }
}

- (void)registerForPnsNotifications {
    NSNotificationCenter* center = [NSNotificationCenter defaultCenter];
    
    [center addObserver:self selector:@selector(pnsDidReceiveNewShowComment:) name:kPnsNewShowCommentsNotification object:nil];
    [center addObserver:self selector:@selector(pnsDidNewRecommandation:) name:kPnsNewRecommandationNotification object:nil];
    [center addObserver:self selector:@selector(pnsQuestSharingProgress:) name:kPnsQuestSharingProgressNotification object:nil];
    [center addObserver:self selector:@selector(pnsQuestSharingCompleted:) name:kPnsQuestSharingCompleteNotification object:nil];
    [center addObserver:self selector:@selector(pnsTradeInitial:) name:kPnsTradeInitialNotification object:nil];
    [center addObserver:self selector:@selector(pnsTradeShipped:) name:kPnsTradeShippedNotification object:nil];
    [center addObserver:self selector:@selector(pnsItemPriceChanged:) name:kPnsItemPriceChangedNotification object:nil];
}


- (void)pnsDidReceiveNewShowComment:(NSNotification*)noti {
    [self handlePnsWithHandler:^{
        if (self.contentNavVc) {
            NSDictionary* userInfo = noti.userInfo;
            NSString* showId = [QSEntityUtil getStringValue:userInfo keyPath:@"showId"];
            [self.contentNavVc popToRootViewControllerAnimated:NO];
            [self.contentNavVc pushViewController:[[QSS03ShowDetailViewController alloc] initWithShowId:showId] animated:NO];
            [self.contentNavVc pushViewController:[[QSS04CommentListViewController alloc] initWithShowId:showId] animated:NO];
        }
    } title:@"您的搭配有新评论！" userInfo:noti.userInfo];
}

- (void)pnsDidNewRecommandation:(NSNotification*)noti {
    [self handlePnsWithHandler:^{
        if ([QSUserManager shareUserManager].userInfo) {
            [self.menuView triggerItemTypePressed:QSRootMenuItemMy];
            if ([self.contentVc isKindOfClass:[QSU01UserDetailViewController class]]) {
                QSU01UserDetailViewController* myVc = (QSU01UserDetailViewController*)self.contentVc;
                [myVc.badgeView.btnGroup addDotWithType:QSBadgeButtonTypeRecommend];
            }
        }
    } title:@"最新的搭配已经推送给你，美丽怎能忍心被忽略，去看看吧！" userInfo:noti.userInfo];

}

- (void)pnsQuestSharingProgress:(NSNotification*)noti {
    
}

- (void)pnsQuestSharingCompleted:(NSNotification*)noti {
    
}

- (void)pnsTradeInitial:(NSNotification*)noti {
    [self handlePnsWithHandler:^{
        [self.menuView triggerItemTypePressed:QSRootMenuItemDiscount];
    } title:@"你申请的折扣已经成功啦，别让宝贝飞了，快快来付款吧！" userInfo:noti.userInfo];
}

- (void)pnsTradeShipped:(NSNotification*)noti {
    [self handlePnsWithHandler:^{
        [self.menuView triggerItemTypePressed:QSRootMenuItemDiscount];
        if ([self.contentVc isKindOfClass:[QSU09OrderListViewController class]]) {
            QSU09OrderListViewController *u09VC = (QSU09OrderListViewController *)self.contentVc;
            [u09VC changeValueOfSegment:1];
            u09VC.headerView.segmentControl.selectedSegmentIndex = 1;
        }
    } title:@"你购买的宝贝已经向你狂奔而来，等着接收惊喜呦！" userInfo:noti.userInfo];
}
- (void)pnsItemPriceChanged:(NSNotification*)noti {
    [self handlePnsWithHandler:^{
        [self.menuView triggerItemTypePressed:QSRootMenuItemMeida];
        if ([self.contentVc isKindOfClass:[QSS01MatchShowsViewController class]]) {
            QSS01MatchShowsViewController* matchVc = (QSS01MatchShowsViewController*)self.contentVc;
            [matchVc showTradeNotiViewOfTradeId:[noti.userInfo stringValueForKeyPath:@"tradeId"] actualPrice:[noti.userInfo numberValueForKeyPath:@"actualPrice"]];
        }
    } title:@"您申请的折扣有最新信息，不要错过哦！" userInfo:noti.userInfo];
}
@end

//
//  QSPnsHandler.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/9/26.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSPnsHandler.h"
#import "QSPnsHelper.h"
#import "QSPnsNotificationName.h"
#import "NSDictionary+QSExtension.h"

#warning TODO 解除对RootcontainerVC与QSUserManager的依赖
#import "QSRootContainerViewController.h"
#import "QSUserManager.h"

#import "QSBlockAlertView.h"
#import "QSU01UserDetailViewController.h"
#import "QSS03ShowDetailViewController.h"
#import "QSS04CommentListViewController.h"
#import "QSS01MatchShowsViewController.h"
#import "QSU02UserSettingViewController.h"
#import "QSU09TradeListViewController.h"
#import "QSRootNotificationHelper.h"

@interface QSPnsHandler (Private)

@end

@implementation QSPnsHandler

#pragma mark - Life Cycle
- (instancetype)initWithRootVc:(QSRootContainerViewController*)rootVc {
    self = [super init];
    if (self) {
        self.rootVc = rootVc;
        [self registerForPnsNotifications];
    }
    return self;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
- (void)registerForPnsNotifications {
    NSNotificationCenter* center = [NSNotificationCenter defaultCenter];
    
    [center addObserver:self selector:@selector(pnsDidReceiveNewShowComment:) name:kPnsNewShowCommentsNotification object:nil];
    [center addObserver:self selector:@selector(pnsDidNewRecommandation:) name:kPnsNewRecommandationNotification object:nil];
    [center addObserver:self selector:@selector(pnsTradeShipped:) name:kPnsTradeShippedNotification object:nil];
    [center addObserver:self selector:@selector(pnsNewBonus:) name:kPnsNewBonusNotification object:nil];
    [center addObserver:self selector:@selector(pnsBonusWithdrawComplete:) name:kPnsBonusWithdrawCompleteNotification object:nil];
    [center addObserver:self selector:@selector(pnsTradeRefundComplete:) name:kPnsTradeRefundCompleteNotification object:nil];
}

#pragma mark - Private
- (void)handlePnsWithHandler:(VoidBlock)handler
                       title:(NSString*)title
                    userInfo:(NSDictionary*)userInfo {
    [self handleBackgroundPnsWithHandler:handler userInfo:userInfo];
    [self handleForegroundAlertPnsWithHandler:handler title:title userInfo:userInfo];
}
- (void)handleBackgroundPnsWithHandler:(VoidBlock)handler
                              userInfo:(NSDictionary*)userInfo {
    if ([QSPnsHelper isFromBackground:userInfo]) {
        //从后台来，直接执行
        if (handler) {
            handler();
        }
    }
}
- (void)handleForegroundAlertPnsWithHandler:(VoidBlock)handler
                                       title:(NSString*)title
                                    userInfo:(NSDictionary*)userInfo {
    if (![QSPnsHelper isFromBackground:userInfo]) {
        //非后台，先显示alertView
        QSBlockAlertView* alertView = [[QSBlockAlertView alloc] initWithTitle:title message:nil delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确定", nil];
        alertView.succeedHandler = handler;
        [alertView show];
    }
}

#pragma mark - Pns
- (void)pnsDidReceiveNewShowComment:(NSNotification*)noti {
    [self handleBackgroundPnsWithHandler:^{
        UINavigationController* nav = self.rootVc.contentNavVc;
        if (nav) {
            NSDictionary* userInfo = noti.userInfo;
            NSString* showId = [QSEntityUtil getStringValue:userInfo keyPath:@"showId"];
            [nav popToRootViewControllerAnimated:NO];
            //Ignore Href
            [nav pushViewController:[[QSS03ShowDetailViewController alloc] initWithShowId:showId] animated:NO];
            [nav pushViewController:[[QSS04CommentListViewController alloc] initWithShowId:showId] animated:NO];
        }
    } userInfo:noti.userInfo];
}

- (void)pnsDidNewRecommandation:(NSNotification*)noti {
    
    [self handleBackgroundPnsWithHandler:^{
        if ([QSUserManager shareUserManager].userInfo) {
            UIViewController* vc = [self.rootVc triggerToShowVc:QSRootMenuItemMy];
            if ([vc isKindOfClass:[QSU01UserDetailViewController class]]) {
//                QSU01UserDetailViewController* myVc = (QSU01UserDetailViewController*)vc;
//                [myVc.badgeView.btnGroup addDotWithType:QSBadgeButtonTypeRecommend];
            }
        }
    } userInfo:noti.userInfo];

}


- (void)pnsTradeShipped:(NSNotification*)noti {
    [self handlePnsWithHandler:^{
        [self.rootVc triggerToShowVc:QSRootMenuItemDiscount];
    } title:@"你购买的宝贝已经向你狂奔而来，等着接收惊喜呦！" userInfo:noti.userInfo];
}
- (void)pnsTradeRefundComplete:(NSNotification*)noti {
    [self handlePnsWithHandler:^{
        [self.rootVc triggerToShowVc:QSRootMenuItemDiscount];
    } title:@"款项已经退回您的支付账号，请查收。" userInfo:noti.userInfo];
}
- (void)pnsNewBonus:(NSNotification*)noti {
    [self handlePnsWithHandler:^{
        int bonusType = [noti.userInfo numberValueForKeyPath:@"type"].intValue;
        if (bonusType == 0) {
            [QSRootNotificationHelper postShowNewBonusVcNoti:noti.userInfo];
        } else if (bonusType == 2) {
            [QSRootNotificationHelper postShowBonusListVcNotificationName];
        }
        
    } title:@"您有一笔佣金入账啦，立即查看！" userInfo:noti.userInfo];
}

- (void)pnsBonusWithdrawComplete:(NSNotification*)noti {
    [self handlePnsWithHandler:^{
        [QSRootNotificationHelper postShowBonusListVcNotificationName];
    } title:@"您的账户成功提现，请注意查看账户！" userInfo:noti.userInfo];
}


#pragma mark - AlertView Delegate
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

@end

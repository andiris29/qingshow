//
//  UIViewController+Network.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "UIViewController+QSExtension.h"
#import "UIViewController+ShowHud.h"
#import "QSU06LoginViewController.h"
#import "QSError.h"
#import "QSUserLoginAlertHandler.h"
#import "QSS10ItemDetailViewController.h"
#import "QSS25ShowHrefViewController.h"
#import "QSPeopleUtil.h"
#import "QSShowUtil.h"

#import <objc/runtime.h>

#import "QSS03ShowDetailViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "QSBlock.h"
#import "UIView+ScreenShot.h"
#import "QSVersionUpdateAlertHandler.h"

static char alertHandlerKey;
static char versionUpdateHandlerKey;

@interface UIViewController(NetworkPrivate)
@property (nonatomic) QSUserLoginAlertHandler* loginErrorAlertHandler;
@property (nonatomic) QSVersionUpdateAlertHandler* versionUpdateAlertHandler;
@end


@implementation UIViewController(Network)

#pragma mark - Private
//Login Error
- (void)setLoginErrorAlertHandler:(QSUserLoginAlertHandler *)networkErrorAlertHandler
{
    objc_setAssociatedObject(self, &alertHandlerKey, networkErrorAlertHandler, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}
- (QSUserLoginAlertHandler*)loginErrorAlertHandler
{
  return ((QSUserLoginAlertHandler*)objc_getAssociatedObject(self, &alertHandlerKey));
}

//Version
- (void)setVersionUpdateAlertHandler:(QSVersionUpdateAlertHandler *)versionUpdateAlertHandler {
        objc_setAssociatedObject(self, &versionUpdateHandlerKey, versionUpdateAlertHandler, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}
- (QSVersionUpdateAlertHandler*)versionUpdateAlertHandler {
    return ((QSVersionUpdateAlertHandler*)objc_getAssociatedObject(self, &versionUpdateHandlerKey));
}

#pragma mark -

- (void)handleError:(NSError*)error
{
    if ([error isKindOfClass:[QSError class]]) {
        QSError* qsError = (QSError*)error;
        if (qsError.code == 1012 && self.navigationController && !self.loginErrorAlertHandler) {
            UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"请先登陆" message:nil delegate:nil cancelButtonTitle:@"取消" otherButtonTitles:@"确定", nil];
            self.loginErrorAlertHandler = [[QSUserLoginAlertHandler alloc] initWithVc:self alertView:alert];
            [alert show];
        } else if (qsError.code == 2000 && !self.versionUpdateAlertHandler) {
            UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"请更新最新版本" message:@"更多意想不到在等着你哦" delegate:nil cancelButtonTitle:nil otherButtonTitles:@"确定", nil];
            self.versionUpdateAlertHandler = [[QSVersionUpdateAlertHandler alloc] initWithVc:self alertView:alert];
            [alert show];
        }else {
            [self showErrorHudWithError:error];
        }
    } else {
        [self showErrorHudWithText:@"系统错误，请稍后再试"];
    }
}

- (void)hideNaviBackBtnTitle
{
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
}
- (void)disableAutoAdjustScrollViewInset
{
    if ([self respondsToSelector:@selector(setAutomaticallyAdjustsScrollViewInsets:)])
    {
        self.automaticallyAdjustsScrollViewInsets = NO;
    }

}

#pragma mark - Detail
- (UIViewController*)showShowDetailViewController:(NSDictionary*)showDict
{
    NSString* url = [QSShowUtil getHref:showDict];
    UIViewController* vc = nil;

    if (url) {
        vc = [[QSS25ShowHrefViewController alloc] initWithShow:showDict];
    } else {
        vc = [[QSS03ShowDetailViewController alloc] initWithShow:showDict];
    }

    [self.navigationController pushViewController:vc animated:YES];
    return vc;
}


@end

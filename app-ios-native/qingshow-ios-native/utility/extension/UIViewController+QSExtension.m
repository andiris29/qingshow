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
#import "QSUserLoginAlertDelegateObj.h"
//#import "QSU01UserDetailViewController.h"
#import "QSPeopleUtil.h"

#import <objc/runtime.h>

#import "QSS03ShowDetailViewController.h"
#import "QSS10ItemDetailVideoViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "QSBlock.h"
#import "UIView+ScreenShot.h"


static char alertDelegateObjKey;

@interface UIViewController(NetworkPrivate)
@property (nonatomic) QSUserLoginAlertDelegateObj* loginErrorAlertDelegateObj;
@end


@implementation UIViewController(Network)

#pragma mark - Private
- (void)setLoginErrorAlertDelegateObj:(QSUserLoginAlertDelegateObj *)networkErrorAlertDelegateObj
{
    objc_setAssociatedObject(self, &alertDelegateObjKey, networkErrorAlertDelegateObj, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}
- (QSUserLoginAlertDelegateObj*)loginErrorAlertDelegateObj
{
  return ((QSUserLoginAlertDelegateObj*)objc_getAssociatedObject(self, &alertDelegateObjKey));
}


#pragma mark -

- (void)handleError:(NSError*)error
{
    if ([error isKindOfClass:[QSError class]]) {
        QSError* qsError = (QSError*)error;
        if (qsError.code == 1012 && self.navigationController && !self.loginErrorAlertDelegateObj) {
            UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"请先登陆" message:nil delegate:nil cancelButtonTitle:@"取消" otherButtonTitles:@"确定", nil];
            self.loginErrorAlertDelegateObj = [[QSUserLoginAlertDelegateObj alloc] initWithVc:self alertView:alert];
            [alert show];
        } else {
            [self showErrorHudWithError:error];
        }
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
- (void)showShowDetailViewController:(NSDictionary*)showDict
{
    UIViewController* vc = [[QSS03ShowDetailViewController alloc] initWithShow:showDict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)showItemDetailViewController:(NSDictionary*)itemDict
{
    UIViewController* vc = [[QSS10ItemDetailVideoViewController alloc] initWithItem:itemDict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)presentPreviewDetailVc:(NSDictionary*)dict {
    UIViewController* vc = dict[@"vc"];
    UIImageView* imageView = dict[@"imageView"];
    [self presentViewController:vc animated:NO completion:^{
        
        [imageView removeFromSuperview];
    }];

}
@end

//
//  QSUserLoginAlertDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSUserLoginAlertDelegateObj.h"
#import "UIViewController+Network.h"
#import "QSU06LoginViewController.h"

@interface UIViewController(NetworkPrivate)
@property (nonatomic) QSUserLoginAlertDelegateObj* loginErrorAlertDelegateObj;
@end


@implementation QSUserLoginAlertDelegateObj

- (id)initWithVc:(UIViewController*)vc alertView:(UIAlertView*)alertView;
{
    self = [super init];
    if (self) {
        self.vc = vc;
        self.alertView = alertView;
        self.alertView.delegate = self;
    }
    return self;
}
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    UIViewController* vc = self.vc;
    if (buttonIndex != alertView.cancelButtonIndex && vc && vc.navigationController) {
        UIViewController* u06Vc = [[QSU06LoginViewController alloc] initWithShowUserDetailAfterLogin:NO];
        [vc.navigationController pushViewController:u06Vc animated:YES];
    }
    
    self.alertView = nil;
    vc.loginErrorAlertDelegateObj = nil;
}

@end


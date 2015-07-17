//
//  QSUserLoginAlertDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSUserLoginAlertDelegateObj.h"
#import "UIViewController+QSExtension.h"
#import "QSU07RegisterViewController.h"

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
        QSU07RegisterViewController* u07Vc = [[QSU07RegisterViewController alloc] init];
        [vc.navigationController pushViewController:u07Vc animated:YES];
        u07Vc.previousVc = vc;
    }
    
    self.alertView = nil;
    vc.loginErrorAlertDelegateObj = nil;
}

@end


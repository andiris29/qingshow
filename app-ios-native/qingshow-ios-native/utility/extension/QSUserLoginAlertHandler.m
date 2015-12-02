//
//  QSUserLoginAlertDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSUserLoginAlertHandler.h"
#import "QSVersionUpdateAlertHandler.h"
#import "UIViewController+QSExtension.h"
#import "QSRootNotificationHelper.h"
@interface UIViewController(NetworkPrivate)
@property (nonatomic) QSUserLoginAlertHandler* loginErrorAlertHandler;
@end


@implementation QSUserLoginAlertHandler

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
        [QSRootNotificationHelper postShowLoginPrompNoti];
    }

    
    self.alertView = nil;
    vc.loginErrorAlertHandler = nil;
}

@end


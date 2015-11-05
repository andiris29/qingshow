//
//  QSVersionUpdateAlertHandler.m
//  qingshow-ios-native
//
//  Created by wxy325 on 9/2/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSVersionUpdateAlertHandler.h"
#define QINGSHOW_ITUNE_URL @"itms-apps://itunes.apple.com/cn/app/qing-xiu/id946116105?l=en&mt=8"

@interface QSVersionUpdateAlertHandler()
@property (weak, nonatomic) UIViewController* vc;
@end

@interface UIViewController (VersionUpdatePrivate)

@property (nonatomic) QSVersionUpdateAlertHandler* versionUpdateAlertHandler;

@end

@implementation QSVersionUpdateAlertHandler

- (id)initWithVc:(UIViewController*)vc alertView:(UIAlertView*)alertView
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
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:QINGSHOW_ITUNE_URL]];
    UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"请更新最新版本" message:@"更多意想不到在等着你哦" delegate:nil cancelButtonTitle:nil otherButtonTitles:@"确定", nil];
    alert.delegate = self;
    self.alertView = alert;
    [alert show];
}

@end

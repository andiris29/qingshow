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
    self.alertView = nil;
    self.vc.versionUpdateAlertHandler = nil;
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:QINGSHOW_ITUNE_URL]];

}

@end

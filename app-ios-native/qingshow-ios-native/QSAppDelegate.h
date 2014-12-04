//
//  QSAppDelegate.h
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WeiboSDK.h"
#import "WXApi.h"
@interface QSAppDelegate : UIResponder <UIApplicationDelegate, WeiboSDKDelegate, WXApiDelegate>

@property (strong, nonatomic) UIWindow *window;

@end

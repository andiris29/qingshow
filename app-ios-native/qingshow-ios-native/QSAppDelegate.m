//
//  QSAppDelegate.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSAppDelegate.h"
#import "QSNetworkEngine.h"
#import "QSS01RootViewController.h"

#import "QSSharePlatformConst.h"
#import "QSUserManager.h"
#import "MobClick.h"
#import <AlipaySDK/AlipaySDK.h>
#import "QSPaymentConst.h"
//#import "QSS15TopicViewController.h"
#import "QSS15ChosenViewController.h"
#import "QSG02WelcomeViewController.h"
#import "QSNavigationController.h"

@interface QSAppDelegate ()
@property (strong, nonatomic) NSString *wbtoken;
@property (strong, nonatomic) NSString *wbCurrentUserID;
@property (strong, nonatomic) UIImageView* launchImageView;
@end

@implementation QSAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    //Weibo
    [WeiboSDK enableDebugMode:YES];
    [WeiboSDK registerApp:kWeiboAppKeyNum];
    
    //Wechat
    [WXApi registerApp:kWechatAppID];
    
    //umeng
    [MobClick setLogEnabled:NO];
    NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    [MobClick setAppVersion:version];
    [MobClick setEncryptEnabled:YES];
    [MobClick updateOnlineConfig];
    [MobClick startWithAppkey:@"54ceec7cfd98c595030008d5" reportPolicy:BATCH channelId:nil];
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
//    UIViewController* vc = [[QSS01RootViewController alloc] init];
    UIViewController* vc = [[QSS15ChosenViewController alloc] init];
    
//    QSG02WelcomeViewController *welcomeVC = [[QSG02WelcomeViewController alloc]init];
//    self.window.rootViewController = welcomeVC;
    
    UINavigationController* nav = [[QSNavigationController alloc] initWithRootViewController:vc];
    nav.navigationBar.translucent = NO;
    self.window.rootViewController = nav;
    [self.window makeKeyAndVisible];
    
    [self showLaunchImage];
    [self hideLaunchImageAfterDelay:3.f];
    return YES;
}


- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation
{
    NSString* urlStr = [url absoluteString];
    if ([urlStr hasPrefix:kWeiboAppKey]) {
        return [WeiboSDK handleOpenURL:url delegate:self];
    } else if ([urlStr hasPrefix:kWechatAppID]) {
        return [WXApi handleOpenURL:url delegate:self];
    } else if ([urlStr hasPrefix:@"alipay"]) {
        if ([url.host isEqualToString:@"safepay"]) {
            
            [[AlipaySDK defaultService] processOrderWithPaymentResult:url standbyCallback:^(NSDictionary *resultDic) {
                NSNumber* resultStatus = resultDic[@"resultStatus"];
                if (resultStatus.intValue == kAlipayPaymentSuccessCode) {
                    [[NSNotificationCenter defaultCenter] postNotificationName:kPaymentSuccessNotification object:nil];
                } else {
                    [[NSNotificationCenter defaultCenter] postNotificationName:kPaymentFailNotification object:nil];
                }
            }];
//            [[AlipaySDK defaultService] processAuth_V2Result:url
//                                             standbyCallback:^(NSDictionary *resultDic) {
//                                                 NSLog(@"result = %@",resultDic);
//                                                 NSString *resultStr = resultDic[@"result"];
//                                             }];
            
        }
    }
    return YES;

}

- (BOOL)application:(UIApplication *)application handleOpenURL:(NSURL *)url
{
    if ([[url absoluteString] hasPrefix:kWeiboAppKey]) {
        return [WeiboSDK handleOpenURL:url delegate:self ];
    } else if ([[url absoluteString] hasPrefix:kWechatAppID]) {
        return [WXApi handleOpenURL:url delegate:self];
    }
    return YES;
}

#pragma mark - Weibo
- (void)didReceiveWeiboRequest:(WBBaseRequest *)request
{
    
}

- (void)didReceiveWeiboResponse:(WBBaseResponse *)response
{
    if ([response isKindOfClass:WBSendMessageToWeiboResponse.class])
    {
        [[NSNotificationCenter defaultCenter] postNotificationName:kWeiboSendMessageResultNotification object:nil userInfo:@{@"statusCode" : @(response.statusCode)}];
    }
    else if ([response isKindOfClass:WBAuthorizeResponse.class])
    {
        QSUserManager* um = [QSUserManager shareUserManager];
        if (response.statusCode == WeiboSDKResponseStatusCodeSuccess) {
            um.weiboAccessToken = [(WBAuthorizeResponse *)response accessToken];
            um.weiboUserId = [(WBAuthorizeResponse *)response userID];
            [[NSNotificationCenter defaultCenter] postNotificationName:kWeiboAuthorizeResultNotification object:nil userInfo:@{@"statusCode" : @(response.statusCode), @"accessToken" : um.weiboAccessToken, @"userId" : um.weiboUserId}];
        } else {
            [[NSNotificationCenter defaultCenter] postNotificationName:kWeiboAuthorizeResultNotification object:nil userInfo:@{@"statusCode" : @(response.statusCode)}];
        }
        

    }
    else if ([response isKindOfClass:WBPaymentResponse.class])
    {
        
    }
}

#pragma mark - WeChat

#pragma mark - Launch Image
- (void)showLaunchImage
{
    UIScreen* mainScreen = [UIScreen mainScreen];
    NSString* launchImgName = [NSString stringWithFormat:@"launch_%d-%d", (int)mainScreen.bounds.size.width, (int)mainScreen.bounds.size.height];
    UIImage* launchImg = [UIImage imageNamed:launchImgName];
    UIImageView* lauchImgView = [[UIImageView alloc] initWithImage:launchImg];
    lauchImgView.frame = mainScreen.bounds;
    [self.window addSubview:lauchImgView];
    
    self.launchImageView = lauchImgView;
}
- (void)hideLaunchImageAfterDelay:(float)delay
{
    [self performSelector:@selector(hideLaunchImage) withObject:nil afterDelay:delay];
}
- (void)hideLaunchImage
{
    [self hideLaunchImageAnimationWithDuration:0.5f];
}
- (void)hideLaunchImageAnimationWithDuration:(float)duration
{
    [UIView animateWithDuration:duration animations:^{
        self.launchImageView.alpha = 0;
    } completion:^(BOOL finished) {
        [self.launchImageView removeFromSuperview];
        self.launchImageView = nil;
    }];
    
}

-(void) onReq:(BaseReq*)req
{

}

-(void) onResp:(BaseResp*)resp
{
    if ([resp isKindOfClass:[SendAuthResp class]]) {
        //微信登陆
        SendAuthResp* authResp = (SendAuthResp*)resp;
        if (resp.errCode == kWechatPaymentSuccessCode) {
            [[NSNotificationCenter defaultCenter] postNotificationName:kWechatAuthorizeSucceedNotification object:nil userInfo:@{@"code" : authResp.code}];
        } else {
            [[NSNotificationCenter defaultCenter] postNotificationName:kWechatAuthorizeFailNotification object:nil];
        }
    } else if ([resp isKindOfClass:[SendMessageToWXResp class]]) {
        if (resp.errCode == kWechatPaymentSuccessCode) {
            [[NSNotificationCenter defaultCenter] postNotificationName:kShareWechatSuccessNotification object:nil];
        } else {
            [[NSNotificationCenter defaultCenter] postNotificationName:kShareWechatFailNotification object:nil];
        }
    }else {
        //微信支付
        if (resp.errCode == kWechatPaymentSuccessCode) {
            [[NSNotificationCenter defaultCenter] postNotificationName:kPaymentSuccessNotification object:nil];
        } else {
            [[NSNotificationCenter defaultCenter] postNotificationName:kPaymentFailNotification object:nil];
        }
    }
}
@end

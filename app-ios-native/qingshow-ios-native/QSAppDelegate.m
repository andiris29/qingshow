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
#import "QSS12TopicViewController.h"

#import "QSSharePlatformConst.h"
#import "QSUserManager.h"
#import "MobClick.h"
#import <AlipaySDK/AlipaySDK.h>


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
    UIViewController* vc = [[QSS12TopicViewController alloc] init];
    
    UINavigationController* nav = [[UINavigationController alloc] initWithRootViewController:vc];
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
            
            [[AlipaySDK defaultService] processAuth_V2Result:url
                                             standbyCallback:^(NSDictionary *resultDic) {
                                                 NSLog(@"result = %@",resultDic);
                                                 NSString *resultStr = resultDic[@"result"];
                                             }];
            
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
//        NSString *title = NSLocalizedString(@"发送结果", nil);
//        NSString *message = [NSString stringWithFormat:@"%@: %d\n%@: %@\n%@: %@", NSLocalizedString(@"响应状态", nil), (int)response.statusCode, NSLocalizedString(@"响应UserInfo数据", nil), response.userInfo, NSLocalizedString(@"原请求UserInfo数据", nil),response.requestUserInfo];
//        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title
//                                                        message:message
//                                                       delegate:nil
//                                              cancelButtonTitle:NSLocalizedString(@"确定", nil)
//                                              otherButtonTitles:nil];
//        WBSendMessageToWeiboResponse* sendMessageToWeiboResponse = (WBSendMessageToWeiboResponse*)response;
//        NSString* accessToken = [sendMessageToWeiboResponse.authResponse accessToken];
//        if (accessToken)
//        {
//            self.wbtoken = accessToken;
//        }
//        NSString* userID = [sendMessageToWeiboResponse.authResponse userID];
//        if (userID) {
//            self.wbCurrentUserID = userID;
//        }
//        [alert show];
    }
    else if ([response isKindOfClass:WBAuthorizeResponse.class])
    {
        QSUserManager* um = [QSUserManager shareUserManager];
        if (response.statusCode == WeiboSDKResponseStatusCodeSuccess) {
            um.weiboAccessToken = [(WBAuthorizeResponse *)response accessToken];
            um.weiboUserId = [(WBAuthorizeResponse *)response userID];
        }
        [[NSNotificationCenter defaultCenter] postNotificationName:kWeiboAuthorizeResultNotification object:nil userInfo:@{@"statusCode" : @(response.statusCode)}];
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
    
}
@end

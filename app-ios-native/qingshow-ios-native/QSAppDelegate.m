//
//  QSAppDelegate.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSAppDelegate.h"
#import "QSNetworkEngine.h"
#import "QSSharePlatformConst.h"
#import "QSUserManager.h"
#import "QSCategoryManager.h"
#import "MobClick.h"
#import <AlipaySDK/AlipaySDK.h>
#import "QSPaymentConst.h"
#import "QSNetworkKit.h"
#import "QSRootContainerViewController.h"
#import "APService.h"
#import "QSPnsHelper.h"
#import "QSEntityUtil.h"
#import "QSNetworkHelper.h"
#import "QSHookHelper.h"
#import "QSUnreadManager.h"
#import "UIViewController+QSExtension.h"
#import "QSPeopleUtil.h"
#import "QSError.h"
#import "QSRootNotificationHelper.h"

#define kTraceLogFirstLaunch @"kTraceLogFirstLaunch"

@interface QSAppDelegate ()
@property (strong, nonatomic) NSString *wbtoken;
@property (strong, nonatomic) NSString *wbCurrentUserID;
@property (strong, nonatomic) UIImageView* launchImageView;
@end

@implementation QSAppDelegate
#pragma mark - Life Cycle
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{

    [self _configNetwork];

    [QSHookHelper registerHooker];

    [QSUnreadManager getInstance];
    //注册第三方登陆、分享平台
    [self registerSharePlatform];
    
    //umeng
    [self registerMobClick];
    
    //Push Notification
    [self registerPushNotification:launchOptions];
    
    //Start App
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];

    
    [self showLaunchImage];
    [QSNetworkHelper querySystemPathOnSucceed:^{
        [self _RootVcInit];
        QSRootContainerViewController* vc = (QSRootContainerViewController*)self.rootVc;
        
        //标记第一次载入
        VoidBlock guestHandler = ^(){
            [QSUserManager shareUserManager].fShouldShowLoginGuideAfterCreateMatcher = YES;
            [QSCategoryManager getInstance];
            vc.hasFetchUserLogin = YES;
            [vc handleCurrentUser];
            [vc showGuestVc];
            [self hideLaunchImageAfterDelay:0.f];
            //Romote Notification
            if (![QSEntityUtil checkIsNil:launchOptions[UIApplicationLaunchOptionsRemoteNotificationKey]]) {
                NSDictionary* pnsUserInfo = launchOptions[UIApplicationLaunchOptionsRemoteNotificationKey];
                [QSPnsHelper handlePnsData:pnsUserInfo fromBackground:YES];
            }
        };
        VoidBlock guestErrorHandler = ^(){
            [QSCategoryManager getInstance];
            vc.hasFetchUserLogin = YES;
            [vc handleCurrentUser];
            [vc showDefaultVc];
            [self hideLaunchImageAfterDelay:0.f];
            //Romote Notification
            if (![QSEntityUtil checkIsNil:launchOptions[UIApplicationLaunchOptionsRemoteNotificationKey]]) {
                NSDictionary* pnsUserInfo = launchOptions[UIApplicationLaunchOptionsRemoteNotificationKey];
                [QSPnsHelper handlePnsData:pnsUserInfo fromBackground:YES];
            }
        };
        VoidBlock normalUserHandler = ^(){
            [QSUserManager shareUserManager].fShouldShowLoginGuideAfterCreateMatcher = NO;
            [QSCategoryManager getInstance];
            vc.hasFetchUserLogin = YES;
            [vc handleCurrentUser];
            [self hideLaunchImageAfterDelay:0.f];
            [vc showDefaultVc];
            //Romote Notification
            if (![QSEntityUtil checkIsNil:launchOptions[UIApplicationLaunchOptionsRemoteNotificationKey]]) {
                NSDictionary* pnsUserInfo = launchOptions[UIApplicationLaunchOptionsRemoteNotificationKey];
                [QSPnsHelper handlePnsData:pnsUserInfo fromBackground:YES];
            }
        };
        VoidBlock normalUserErrorHandler = ^(){
            [QSCategoryManager getInstance];
            vc.hasFetchUserLogin = YES;
            [vc handleCurrentUser];
            [self hideLaunchImageAfterDelay:0.f];
            [vc showDefaultVc];
            //Romote Notification
            if (![QSEntityUtil checkIsNil:launchOptions[UIApplicationLaunchOptionsRemoteNotificationKey]]) {
                NSDictionary* pnsUserInfo = launchOptions[UIApplicationLaunchOptionsRemoteNotificationKey];
                [QSPnsHelper handlePnsData:pnsUserInfo fromBackground:YES];
            }
        };
        
        //Init Matcher Categories List

        if ([self isFirstLaunch]) {
            //Login as guest
            [SHARE_NW_ENGINE loginAsGuestOnSucceed:^(NSDictionary *data, NSDictionary *metadata) {
                guestHandler();
                
            } onError:^(NSError *error) {
                guestErrorHandler();
            }];
        } else {
            //Normal workflow
            [SHARE_NW_ENGINE getLoginUserOnSucced:^(NSDictionary *data, NSDictionary *metadata) {
                QSPeopleRole r = [QSPeopleUtil getPeopleRole:data];
                if (r == QSPeopleRoleGuest) {
                    [SHARE_NW_ENGINE feedingMatchCreateBy:data page:1 onSucceed:^(NSArray *array, NSDictionary *metadata) {
                        normalUserHandler();
                        [QSRootNotificationHelper postScheduleToShowLoginGuideNoti];
                    } onError:^(NSError *error) {
                        if ([error isKindOfClass:[QSError class]]) {
                            QSError* e = (QSError*)error;
                            if (e.code == 1009) {
                                //PAGE_NOT_EXIST
                                guestHandler();
                            } else {
                                guestErrorHandler();
                            }
                        } else {
                            guestErrorHandler();
                        }
                    }];
                } else {
                    normalUserHandler();
                }
            } onError:^(NSError *error) {
                normalUserErrorHandler();
            }];
        }
        
        [self rememberFirstLaunch];
    } onError:^(NSError *error) {
        [self _RootVcInit];
        [self hideLaunchImageAfterDelay:0.f];
        QSRootContainerViewController* vc = (QSRootContainerViewController*)self.rootVc;
        [vc showDefaultVc];
        [self.rootVc handleError:error];
    }];
    
    return YES;
}

- (void)_RootVcInit {
    [self.launchImageView removeFromSuperview];
    QSRootContainerViewController* vc = [[QSRootContainerViewController alloc] init];
    self.rootVc = vc;
    [self _configRootVc];
    UIImageView* launchImageView = [self generateLaunchImageView];
    [self.window addSubview:launchImageView];
    self.launchImageView = launchImageView;

}
- (void)_configRootVc {
    self.window.rootViewController = self.rootVc;
}

- (void)_configNetwork {
    [NSHTTPCookieStorage sharedHTTPCookieStorage].cookieAcceptPolicy = NSHTTPCookieAcceptPolicyAlways;
    
    //MKNetworkKit Will Handle Cache, So Disable System Cache
    [NSURLCache sharedURLCache].diskCapacity = 0;
    
    //Remove Systme Cache of Old Version
    [[NSURLCache sharedURLCache] removeAllCachedResponses];
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
    [SHARE_NW_ENGINE getLoginUserOnSucced:nil onError:nil];
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    [UIApplication sharedApplication].applicationIconBadgeNumber = 0;
    [APService resetBadge];
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation
{
    NSString* urlStr = [url absoluteString];
    if ([urlStr hasPrefix:kWechatAppID]) {
        return [WXApi handleOpenURL:url delegate:self];
    } else if ([urlStr hasPrefix:@"alipay"]) {
        if ([url.host isEqualToString:@"safepay"]) {
            
            [[AlipaySDK defaultService] processOrderWithPaymentResult:url standbyCallback:^(NSDictionary *resultDic) {
                NSNumber* resultStatus = [QSEntityUtil getNumberValue:resultDic keyPath:@"resultStatus"];
                if (resultStatus.intValue == kAlipayPaymentSuccessCode) {
                    [[NSNotificationCenter defaultCenter] postNotificationName:kPaymentSuccessNotification object:nil];
                } else {
                    [[NSNotificationCenter defaultCenter] postNotificationName:kPaymentFailNotification object:nil];
                }
            }];
        }
    }
    return YES;

}

- (BOOL)application:(UIApplication *)application handleOpenURL:(NSURL *)url
{
    if ([[url absoluteString] hasPrefix:kWechatAppID]) {
        return [WXApi handleOpenURL:url delegate:self];
    }
    return YES;
}

#pragma mark - Share Platform
- (void)registerSharePlatform {
    //Wechat
    [WXApi registerApp:kWechatAppID];
}

#pragma mark - WeChat
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

#pragma mark - Mob Click 友盟
- (void)registerMobClick {
    [MobClick setLogEnabled:NO];
    NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    [MobClick setAppVersion:version];
    [MobClick setEncryptEnabled:YES];
    [MobClick updateOnlineConfig];
    [MobClick startWithAppkey:@"54ceec7cfd98c595030008d5" reportPolicy:BATCH channelId:nil];
}

#pragma mark - Launch Image
- (void)showLaunchImage
{
    self.window.rootViewController = [[UIViewController alloc] init];
    [self.window makeKeyAndVisible];
    UIImageView* lauchImgView = [self generateLaunchImageView];
    [self.window addSubview:lauchImgView];
    self.launchImageView = lauchImgView;
}
- (UIImageView*)generateLaunchImageView {
    UIScreen* mainScreen = [UIScreen mainScreen];
    NSString* launchImgName = [NSString stringWithFormat:@"launch_%d-%d", (int)mainScreen.bounds.size.width, (int)mainScreen.bounds.size.height];
    UIImage* launchImg = [UIImage imageNamed:launchImgName];
    UIImageView* lauchImgView = [[UIImageView alloc] initWithImage:launchImg];
    lauchImgView.frame = mainScreen.bounds;
    return lauchImgView;
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

- (BOOL)isFirstLaunch {
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    return ![userDefaults boolForKey:kTraceLogFirstLaunch];
}
//标记第一次载入软件
- (void)rememberFirstLaunch
{
    if ([self isFirstLaunch]) {
        NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    //[self logTraceFirstLaunch];
        [userDefaults setBool:YES forKey:kTraceLogFirstLaunch];
        [userDefaults synchronize];
    }

}

#pragma mark - Push Notification
- (void)registerPushNotification:(NSDictionary*)launchOptions {

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveJPushRegistrionId:) name:kJPFNetworkDidLoginNotification object:nil];
#if __IPHONE_OS_VERSION_MAX_ALLOWED > __IPHONE_7_1
    if ([[UIDevice currentDevice].systemVersion floatValue] >= 8.0) {
        [APService
         registerForRemoteNotificationTypes:(UIUserNotificationTypeBadge |
                                             UIUserNotificationTypeSound |
                                             UIUserNotificationTypeAlert)
         categories:nil];
    } else {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
        [APService
         registerForRemoteNotificationTypes:(UIRemoteNotificationTypeBadge |
                                             UIRemoteNotificationTypeSound |
                                             UIRemoteNotificationTypeAlert)
#pragma clang diagnostic pop
         
#else
         categories:nil];
        [APService
         registerForRemoteNotificationTypes:(UIRemoteNotificationTypeBadge |
                                             UIRemoteNotificationTypeSound |
                                             UIRemoteNotificationTypeAlert)
#endif

         categories:nil];
    }
    [APService setupWithOption:launchOptions];
}

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)pToken {
    
    [APService registerDeviceToken:pToken];
}

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    NSLog(@"Registfail%@",error);
}


- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void
                        (^)(UIBackgroundFetchResult))completionHandler {

    switch (application.applicationState) {
        case 0:{
            //程序在前台
            [QSPnsHelper handlePnsData:userInfo fromBackground:NO];
            break;
        }
        case 1: {
            //后台收到推送并点击badge进入，处理推送
            [QSPnsHelper handlePnsData:userInfo fromBackground:YES];
            break;
        }
        default: {
            break;
        }
    }
    
    [APService handleRemoteNotification:userInfo];
    if (completionHandler) {
        completionHandler(UIBackgroundFetchResultNewData);
    }

}

#pragma mark - JPush
- (void)didReceiveJPushRegistrionId:(NSNotification*)notification {
    [QSUserManager shareUserManager].JPushRegistrationID = [APService registrationID];
    [SHARE_NW_ENGINE userBindCurrentJpushIdOnSucceed:nil onError:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kJPFNetworkDidLoginNotification object:nil];
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
@end

//
//  QSThirdPartLoginService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/12/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSThirdPartLoginService.h"
#import "QSSharePlatformConst.h"
#import "WeiboSDK.h"
#import "WXApi.h"
#import "QSNetworkKit.h"

@interface QSThirdPartLoginService ()
//Weibo
@property (strong, nonatomic) VoidBlock succeedBlock;
@property (strong, nonatomic) ErrorBlock errorBlock;

@end

@implementation QSThirdPartLoginService
#pragma mark - Life Cycle
- (instancetype)init{
    self = [super init];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveWeiboAuthroizeResult:) name:kWeiboAuthorizeResultNotification object:nil];
        
    }
    return self;
}
- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - Singleton
+ (QSThirdPartLoginService*)getInstance
{
    static QSThirdPartLoginService* s_loginService = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        s_loginService = [[QSThirdPartLoginService alloc] init];
        
    });
    return s_loginService;
}

#pragma mark - Wechat
- (void)loginWithWechatOnSuccees:(VoidBlock)succeedBlock
                         onError:(ErrorBlock)errorBlock
{

}

/*
 
 - (void)sendAuthRequest
 {
 SendAuthReq* req = [[[SendAuthReq alloc] init] autorelease];
 req.scope = @"snsapi_message,snsapi_userinfo,snsapi_friend,snsapi_contact"; // @"post_timeline,sns"
 req.state = @"xxx";
 req.openID = @"0c806938e2413ce73eef92cc3";
 
 [WXApi sendAuthReq:req viewController:self.viewController delegate:self];
 }
 
 */
- (void)didReceiveWechatAuthroizeResult:(NSNotification*)noti
{
    
}

#pragma mark - Weibo
- (void)loginWithWeiboOnSuccees:(VoidBlock)succeedBlock
                        onError:(ErrorBlock)errorBlock
{
    self.succeedBlock = succeedBlock;
    self.errorBlock = errorBlock;
    
    WBAuthorizeRequest *request = [WBAuthorizeRequest request];
    request.redirectURI = kWeiboRedirectURI;
    request.scope = @"all";
    request.userInfo = nil;
    [WeiboSDK sendRequest:request];
}
- (void)didReceiveWeiboAuthroizeResult:(NSNotification*)noti
{
    NSDictionary* userInfo = noti.userInfo;
    NSNumber* statusCode = userInfo[@"statusCode"];
    if (statusCode.intValue == WeiboSDKResponseStatusCodeSuccess) {
        NSString* accessToken = userInfo[@"accessToken"];
        NSString* uid = userInfo[@"userId"];
        [SHARE_NW_ENGINE loginViaWeiboAccessToken:accessToken uid:uid onSucceed:^(NSDictionary *data, NSDictionary *metadata) {
            [self invokeSuccessCallback];
        } onError:^(NSError *error) {
            [self invokeFailCallback:error];
        }];
    } else {
        [self invokeFailCallback:nil];
    }
}
#pragma mark - Private
- (void)invokeSuccessCallback
{
    if (self.succeedBlock) {
        self.succeedBlock();
        self.succeedBlock = nil;
        self.errorBlock = nil;
    }
}

- (void)invokeFailCallback:(NSError*)error
{
    if (self.errorBlock) {
        self.errorBlock(error);
        self.succeedBlock = nil;
        self.errorBlock = nil;
    }
}
@end

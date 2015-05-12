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
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveWechatAuthroizeSuccess:) name:kWechatAuthorizeSucceedNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveWechatAuthroizeFail:) name:kWechatAuthorizeFailNotification object:nil];
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
    SendAuthReq* req = [[SendAuthReq alloc] init];
    req.scope = @"snsapi_message,snsapi_userinfo,snsapi_friend,snsapi_contact"; // @"post_timeline,sns"
    req.state = @(random()).stringValue;
    [WXApi sendReq:req];
}

- (void)didReceiveWechatAuthroizeSuccess:(NSNotification*)noti
{
    NSDictionary* userInfo = noti.userInfo;
    NSString* code = userInfo[@"code"];
    [SHARE_NW_ENGINE loginViaWechatCode:code onSucceed:^(NSDictionary *data, NSDictionary *metadata) {
        [self invokeSuccessCallback];
    } onError:^(NSError *error) {
        [self invokeFailCallback:error];
    }];
}
- (void)didReceiveWechatAuthroizeFail:(NSNotification*)noti
{
    [self invokeFailCallback:nil];
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

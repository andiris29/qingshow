//
//  QSShareService.m
//  qingshow-ios-native
//
//  Created by wxy325 on 8/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSSharePlatformConst.h"
#import "QSShareService.h"
#import <Social/Social.h>
#import "WeiboSDK.h"
#import "WXApi.h"

@interface QSShareService ()

@property (strong, nonatomic) VoidBlock succeedBlock;
@property (strong, nonatomic) ErrorBlock errorBlock;

@end

@implementation QSShareService

#pragma mark - Singleton
+ (QSShareService*)shareService
{
    static QSShareService* s_shareService = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        s_shareService = [[QSShareService alloc] init];
    });
    return s_shareService;
}


#pragma mark - Life Cycle
- (instancetype)init {
    self = [super init];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(invokeShareSuccessCallback:) name:kShareWechatSuccessNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(invokeShareFailCallback:) name:kShareWechatFailNotification object:nil];
    }
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}


#pragma mark - Share
#pragma mark Wechat
- (void)shareWithWechatMoment:(NSString*)title
                         desc:(NSString*)desc
                        image:(UIImage*)image
                          url:(NSString*)url
                    onSucceed:(VoidBlock)succeedBlock
                      onError:(ErrorBlock)errorBlock {
    [self _shareWithWechartScene:WXSceneTimeline title:title desc:desc image:image url:url onSucceed:succeedBlock onError:errorBlock];
}
- (void)shareWithWechatFriend:(NSString*)title
                         desc:(NSString*)desc
                        image:(UIImage*)image
                          url:(NSString*)url
                    onSucceed:(VoidBlock)succeedBlock
                      onError:(ErrorBlock)errorBlock {
    [self _shareWithWechartScene:WXSceneSession title:title desc:desc image:image url:url onSucceed:succeedBlock onError:errorBlock];
}
- (void)_shareWithWechartScene:(int)scene
                         title:(NSString*)title
                          desc:(NSString*)desc
                         image:(UIImage*)image
                           url:(NSString*)url
                     onSucceed:(VoidBlock)succeedBlock
                       onError:(ErrorBlock)errorBlock {
    self.succeedBlock = succeedBlock;
    self.errorBlock = errorBlock;
    
    WXMediaMessage *message = [WXMediaMessage message];
    
    message.title = title;
    message.description = desc;
    [message setThumbImage:image];
    WXWebpageObject *ext = [WXWebpageObject object];
    if (url) {
        ext.webpageUrl = url;
    } else {
        ext.webpageUrl = @"http://121.41.161.239/web-mobile/src/index.html#?entry=S03&_id=";
    }
    message.mediaObject = ext;
    SendMessageToWXReq* req = [[SendMessageToWXReq alloc] init];
    req.bText = NO;
    req.message = message;
    req.scene = scene;
    [WXApi sendReq:req];
}


#pragma mark - Helper
- (void)invokeShareSuccessCallback:(NSNotification*)notification
{
    if (self.succeedBlock) {
        self.succeedBlock();
        self.succeedBlock = nil;
        self.errorBlock = nil;
    }
}

- (void)invokeShareFailCallback:(NSNotification*)notification
{
    if (self.errorBlock) {
        self.errorBlock(nil);
        self.succeedBlock = nil;
        self.errorBlock = nil;
    }
}

@end

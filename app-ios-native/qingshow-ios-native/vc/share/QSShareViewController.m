//
//  QSShareViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/30/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSShareViewController.h"
#import "QSUserManager.h"
#import <Social/Social.h>
#import "WeiboSDK.h"
#import "WXApi.h"
#import "QSSharePlatformConst.h"
#import "UIViewController+ShowHud.h"

@interface QSShareViewController ()

@end

@implementation QSShareViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(weiboAuthorizeNotiHander:) name:kWeiboAuthorizeResultNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(weiboSendMessageNotiHandler:) name:kWeiboSendMessageResultNotification object:nil];

    self.shareContainer.hidden = YES;
    self.sharePanel.hidden = YES;
}
- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark - Share
- (void)showSharePanel
{
    if (self.shareContainer.hidden == NO && self.sharePanel.hidden == NO){
        return;
    }

    self.shareContainer.hidden = NO;
    self.sharePanel.hidden = NO;
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromTop;
    tran.duration = 0.2f;
    [self.sharePanel.layer addAnimation:tran forKey:@"ShowAnimation"];
    
}
- (void)hideSharePanel
{
    if (self.shareContainer.hidden == YES && self.sharePanel.hidden == YES) {
        return;
    }

    self.sharePanel.hidden = YES;
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromBottom;
    tran.duration = 0.2f;
    [self.sharePanel.layer addAnimation:tran forKey:@"ShowAnimation"];
    [self performSelector:@selector(hideContainer) withObject:nil afterDelay:0.2f];
}
- (void)hideContainer
{
    self.shareContainer.hidden = YES;
}


- (IBAction)shareWeiboPressed:(id)sender {
    [self hideSharePanel];
    
    NSString* weiboAccessToken = [QSUserManager shareUserManager].weiboAccessToken;
    WBAuthorizeRequest *request = [WBAuthorizeRequest request];
    request.redirectURI = kWeiboRedirectURI;
    request.scope = @"all";
    request.userInfo = nil;
    
    WBMessageObject *message = [WBMessageObject message];
    WBWebpageObject* webPage = [WBWebpageObject object];
    webPage.objectID = @"qingshow_webpage_id";
    webPage.title = @"倾秀";
    webPage.description = @"qingshow";
    webPage.webpageUrl = @"http://chingshow.com/web-mobile/src/index.html#?entry=S03&_id=";
    webPage.thumbnailData = UIImagePNGRepresentation([UIImage imageNamed:@"share_icon"]);
    
    //    NSData *imageData = UIImagePNGRepresentation([UIImage imageNamed:@"gray_clock"], 0.5);
    
    message.mediaObject = webPage;
    WBSendMessageToWeiboRequest *msgRequest = [WBSendMessageToWeiboRequest requestWithMessage:message authInfo:request access_token:weiboAccessToken];
    message.text = @"倾秀";
    [WeiboSDK sendRequest:msgRequest];
}


- (void)weiboSendMessageNotiHandler:(NSNotification*)notification
{
    if (WeiboSDKResponseStatusCodeSuccess == ((NSNumber*)notification.userInfo[@"statusCode"]).integerValue) {
        [MobClick event:@"shareShow" attributes:@{@"snsName": @"weibo"} counter:1];
        if ([self.delegate respondsToSelector:@selector(didShareWeiboSuccess)]) {
            [self.delegate didShareWeiboSuccess];
        }
    }
}
- (void)weiboAuthorizeNotiHander:(NSNotification*)notification
{
    if (WeiboSDKResponseStatusCodeSuccess == ((NSNumber*)notification.userInfo[@"statusCode"]).integerValue) {
        [self shareWeiboPressed:nil];
    }
}

- (IBAction)shareWechatPressed:(id)sender {
    [MobClick event:@"shareShow" attributes:@{@"snsName": @"weixin"} counter:1];
    WXMediaMessage *message = [WXMediaMessage message];
    message.title = @"qingshow";
    message.description = @"qingshow";
    
    WXWebpageObject *ext = [WXWebpageObject object];
    
    ext.webpageUrl = @"http://chingshow.com/web-mobile/src/index.html#?entry=S03&_id=";
    
    message.mediaObject = ext;
    
    SendMessageToWXReq* req = [[SendMessageToWXReq alloc] init];
    req.bText = NO;
    req.message = message;
    req.scene = WXSceneTimeline;
    
    [WXApi sendReq:req];
    [self hideSharePanel];
}
- (IBAction)shareWechatFriendPressed:(id)sender {
    [MobClick event:@"shareShow" attributes:@{@"snsName": @"weixin"} counter:1];
    WXMediaMessage *message = [WXMediaMessage message];
    message.title = @"qingshow";
    message.description = @"qingshow";
    
    WXWebpageObject *ext = [WXWebpageObject object];
    
    ext.webpageUrl = @"http://chingshow.com/web-mobile/src/index.html#?entry=S03&_id=";
    
    message.mediaObject = ext;
    
    SendMessageToWXReq* req = [[SendMessageToWXReq alloc] init];
    req.bText = NO;
    req.message = message;
    req.scene = WXSceneSession;
    
    [WXApi sendReq:req];
    [self hideSharePanel];
}

- (IBAction)shareCancelPressed:(id)sender {
    [self hideSharePanel];
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self hideSharePanel];
}

@end

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
#import "QSEntityUtil.h"
#import "NSDictionary+QSExtension.h"
#import "QSShareService.h"
#import "MKNetworkKit.h"
#define kShareTitle @"来倾秀玩转搭配，show出你的范儿！"
#define kShareDesc @"随心所欲尽情搭配品牌美衣，淘宝天猫当季服装的折扣中心，最重要的是折扣你说了算"

@interface QSShareViewController ()

@property (strong, nonatomic) NSString* shareUrl;

@end

@implementation QSShareViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
#warning Refactor Share Weibo
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(weiboAuthorizeNotiHander:) name:kWeiboAuthorizeResultNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(weiboSendMessageNotiHandler:) name:kWeiboSendMessageResultNotification object:nil];
    
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
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
- (void)showSharePanelWithUrl:(NSString*)urlStr
{
    self.shareUrl = urlStr;
    if (!self.shareContainer.hidden && !self.sharePanel.hidden){
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
    if (self.shareContainer.hidden && self.sharePanel.hidden) {
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
    webPage.title = kShareTitle;
    webPage.description = kShareDesc;
    if (self.shareUrl) {
        webPage.webpageUrl = self.shareUrl;
    } else {
        webPage.webpageUrl = @"http://121.41.161.239/";
    }

    webPage.thumbnailData = UIImagePNGRepresentation([UIImage imageNamed:@"share_icon"]);
    
    //    NSData *imageData = UIImagePNGRepresentation([UIImage imageNamed:@"gray_clock"], 0.5);
    
    message.mediaObject = webPage;
    WBSendMessageToWeiboRequest *msgRequest = [WBSendMessageToWeiboRequest requestWithMessage:message authInfo:request access_token:weiboAccessToken];
    message.text = @"";
    [WeiboSDK sendRequest:msgRequest];
}


- (void)weiboSendMessageNotiHandler:(NSNotification*)notification
{
    if (WeiboSDKResponseStatusCodeSuccess == [notification.userInfo numberValueForKeyPath:@"statusCode"].integerValue) {
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

//朋友圈
- (IBAction)shareWechatMomentPressed:(id)sender {
    [MobClick event:@"shareShow" attributes:@{@"snsName": @"weixin"} counter:1];

    NSString* sharedUrl = nil;
    if (self.shareUrl) {
        sharedUrl = self.shareUrl;
    } else {
        sharedUrl = @"http://121.41.161.239/web-mobile/src/index.html#?entry=S03&_id=";
    }
    [SHARE_SHARE_SERVICE shareWithWechatMoment:[NSString stringWithFormat:@"【%@】%@", kShareTitle, kShareDesc] desc:nil image:[UIImage imageNamed:@"share_icon"] url:sharedUrl onSucceed:^{
        if ([self.delegate respondsToSelector:@selector(didShareWechatSuccess)]) {
            [self.delegate didShareWechatSuccess];
        }
    } onError:^(NSError *error) {
        
    }];
    
    [self hideSharePanel];
}

//微信好友
- (IBAction)shareWechatFriendPressed:(id)sender {
    [MobClick event:@"shareShow" attributes:@{@"snsName": @"weixin"} counter:1];
    
    NSString* sharedUrl = nil;
    if (self.shareUrl) {
        sharedUrl = self.shareUrl;
    } else {
        sharedUrl = @"http://121.41.161.239/web-mobile/src/index.html#?entry=S03&_id=";
    }
    
    [SHARE_SHARE_SERVICE shareWithWechatFriend:kShareTitle desc:kShareDesc image:[UIImage imageNamed:@"share_icon"] url:sharedUrl onSucceed:^{
        if ([self.delegate respondsToSelector:@selector(didShareWechatSuccess)]) {
            [self.delegate didShareWechatSuccess];
        }
    } onError:^(NSError *error) {
        
    }];
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

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
#import "WXApi.h"
#import "QSSharePlatformConst.h"
#import "UIViewController+ShowHud.h"
#import "QSEntityUtil.h"
#import "NSDictionary+QSExtension.h"
#import "QSShareService.h"
#import "QSNetworkKit.h"

@interface QSShareViewController ()

@property (strong, nonatomic) NSString* shareUrl;
@property (strong, nonatomic) NSString* shareTitle;
@property (strong, nonatomic) NSString* shareDesc;
@property (strong, nonatomic) NSString* shareIconUrl;
@end

@implementation QSShareViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
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
- (void)showSharePanelWithTitle:(NSString*)title desc:(NSString*)desc url:(NSString*)urlStr shareIconUrl:(NSString*)shareIconUrl
{
    self.shareTitle = title;
    self.shareDesc = desc;
    self.shareUrl = urlStr;
    self.shareIconUrl = shareIconUrl;
    
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

//朋友圈
- (IBAction)shareWechatMomentPressed:(id)sender {
    [MobClick event:@"shareShow" attributes:@{@"snsName": @"weixin"} counter:1];

    NSString* sharedUrl = nil;
    if (self.shareUrl) {
        sharedUrl = self.shareUrl;
    } else {
        sharedUrl = @"http://121.41.161.239/web-mobile/src/index.html#?entry=S03&_id=";
    }
    [SHARE_SHARE_SERVICE shareWithWechatMoment:self.shareTitle desc:self.shareDesc imagePath:self.shareIconUrl url:sharedUrl onSucceed:^{
        if ([self.delegate respondsToSelector:@selector(didShareWechatSuccess)]) {
            [self.delegate didShareWechatSuccess];
        }
    } onError:nil];
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
    
    [SHARE_SHARE_SERVICE shareWithWechatFriend:self.shareTitle desc:self.shareDesc imagePath:self.shareIconUrl url:sharedUrl onSucceed:^{
        if ([self.delegate respondsToSelector:@selector(didShareWechatSuccess)]) {
            [self.delegate didShareWechatSuccess];
        }
    } onError:nil];
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

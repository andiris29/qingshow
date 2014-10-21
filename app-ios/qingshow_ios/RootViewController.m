//
//  ViewController.m
//  qingshow_ios
//
//  Created by wxy325 on 10/2/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "RootViewController.h"
#import <ShareSDK/ShareSDK.h>


#define ROOT_URL @"http://121.199.31.4/antSoftware/com.focosee.chingshow/trunk/dev/web/index.html#debug"
#define kErrorDelayTime 5.f

@interface RootViewController ()

@property (strong, nonatomic) NSURL* currentRequestUrl;

@end

@implementation RootViewController

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    self.rootWebView.frame = [UIScreen mainScreen].applicationFrame;    //Adjust for status bar
    NSURL *url = [NSURL fileURLWithPath:[[NSBundle mainBundle] pathForResource:@"index" ofType:@"html" inDirectory:@"/com"]];
    [self loadUrl:url];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UIWebView Delegate
- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
    self.currentRequestUrl = request.URL;
    return YES;
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error{
    //Reload to handle error
    [self performSelector:@selector(loadUrl:) withObject:self.currentRequestUrl afterDelay:kErrorDelayTime];
}

#pragma mark - 
- (void)loadUrl:(NSURL*)url{
    [self.rootWebView loadRequest:[NSURLRequest requestWithURL:url cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:30.f]];
}
#pragma mark - IBAction
- (IBAction)shareButtonPressed:(id)sender {
    //NSString *imagePath = [[NSBundle mainBundle] pathForResource:@"ShareSDK"  ofType:@"jpg"];
    
    //构造分享内容
    id<ISSContent> publishContent = [ShareSDK content:@"分享内容"
                                       defaultContent:@"默认分享内容，没内容时显示"
                                                image:nil
                                                title:@"ShareSDK"
                                                  url:@"http://www.sharesdk.cn"
                                          description:@"这是一条测试信息"
                                            mediaType:SSPublishContentMediaTypeNews];
    
    [ShareSDK showShareActionSheet:nil
                         shareList:nil
                           content:publishContent
                     statusBarTips:YES
                       authOptions:nil
                      shareOptions: nil
                            result:^(ShareType type, SSResponseState state, id<ISSPlatformShareInfo> statusInfo, id<ICMErrorInfo> error, BOOL end) {
                                if (state == SSResponseStateSuccess)
                                {
                                    NSLog(@"分享成功");
                                }
                                else if (state == SSResponseStateFail)
                                {
                                    NSLog(NSLocalizedString(@"TEXT_SHARE_FAI", @"发布失败!error code == %d, error code == %@"), [error errorCode], [error errorDescription]);
                                }
                            }];
}
@end

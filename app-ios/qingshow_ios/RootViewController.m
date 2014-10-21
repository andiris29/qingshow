//
//  ViewController.m
//  qingshow_ios
//
//  Created by wxy325 on 10/2/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "RootViewController.h"

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

}
@end

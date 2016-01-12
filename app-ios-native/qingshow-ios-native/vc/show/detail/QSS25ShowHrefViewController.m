//
//  QSS25ShowHrefViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/29.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSS25ShowHrefViewController.h"
#import "QSShowUtil.h"

@interface QSS25ShowHrefViewController ()

@property (strong, nonatomic) NSDictionary* showDict;

@end

@implementation QSS25ShowHrefViewController

#pragma mark - Init
- (instancetype)initWithShow:(NSDictionary*)showDict {
    self = [super initWithNibName:@"QSS25ShowHrefViewController" bundle:nil];
    if (self) {
        self.showDict = showDict;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"潮流前沿";
    self.webView.delegate = self;
    [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:[QSShowUtil getHref:self.showDict]]]];
    [self.webView setScalesPageToFit:YES];
    
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSFontAttributeName:NAVNEWFONT,
       NSForegroundColorAttributeName:[UIColor blackColor]}];
}
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark - UIWebView Delegate
- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
    if (([request.URL.absoluteString hasPrefix:@"tmall://"] || [request.URL.absoluteString hasPrefix:@"taobao://"])) {
        return NO;
    } else {
        return YES;
    }
    
}
@end

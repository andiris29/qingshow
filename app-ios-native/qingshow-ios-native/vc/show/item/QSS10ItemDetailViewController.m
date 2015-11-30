//
//  QSG01ItemWebViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 1/12/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS10ItemDetailViewController.h"
#import "QSItemUtil.h"
#import "QSEntityUtil.h"
#import "UIViewController+QSExtension.h"
#import "QSTradeUtil.h"
#import "QSUserManager.h"
#import "QSPeopleUtil.h"
#import "QSU11ReceiverEditingViewController.h"
#define PAGE_ID @"G01 - 内嵌浏览器"

@interface QSS10ItemDetailViewController ()

@property (strong, nonatomic) NSDictionary* itemDict;

@end

@implementation QSS10ItemDetailViewController
#pragma mark - Init Method
- (id)initWithItem:(NSDictionary*)item{
    self = [super initWithNibName:@"QSS10ItemDetailViewController" bundle:nil];
    if (self) {
        self.itemDict = item;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    
    [MobClick beginLogPageView:PAGE_ID];
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}

- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
}
- (void)viewDidLoad {
    [super viewDidLoad];

    NSURL* url = [QSItemUtil getShopUrl:self.itemDict];
    self.webView.delegate = self;
    [self.webView loadRequest:[NSURLRequest requestWithURL:url]];
    [self.webView setScalesPageToFit:YES];
    
    [MobClick event:@"viewItemSource" attributes:@{@"itemId": [QSEntityUtil getIdOrEmptyStr:self.itemDict]} counter:1];
    
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSFontAttributeName:NAVNEWFONT,
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    self.title = @"商品详情";
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction
- (IBAction)backBtnPressed:(id)sender {
    self.navigationController.navigationBarHidden = NO;
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)backAction
{
    [self.navigationController popViewControllerAnimated:YES];
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

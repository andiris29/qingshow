//
//  QSG01ItemWebViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 1/12/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSG01ItemWebViewController.h"
#import "QSItemUtil.h"

#define PAGE_ID @"G01 - 内嵌浏览器"

@interface QSG01ItemWebViewController ()

@property (strong, nonatomic) NSDictionary* itemDict;
@property (assign, nonatomic) BOOL fFirst;
@end

@implementation QSG01ItemWebViewController
#pragma mark - Init Method
- (id)initWithItem:(NSDictionary*)item
{
    self = [super initWithNibName:@"QSG01ItemWebViewController" bundle:nil];
    if (self) {
        self.itemDict = item;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [MobClick beginLogPageView:PAGE_ID];
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    UIImageView* titleImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"nav_btn_image_logo"]];
    self.navigationItem.titleView = titleImageView;
    
    NSURL* url = [QSItemUtil getShopUrl:self.itemDict];
    self.webView.delegate = self;
    [self.webView loadRequest:[NSURLRequest requestWithURL:url]];
    self.fFirst = YES;
    
    [MobClick event:@"viewItemSource" attributes:@{@"itemId": self.itemDict[@"_id"]} counter:1];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
    if (self.fFirst && ([request.URL.absoluteString hasPrefix:@"tmall://"] || [request.URL.absoluteString hasPrefix:@"taobao://"])) {
        self.fFirst = NO;
        return NO;
    } else {
        return YES;
    }

}
@end

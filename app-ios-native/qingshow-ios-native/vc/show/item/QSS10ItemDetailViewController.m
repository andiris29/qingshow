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
    // Do any additional setup after loading the view from its nib.
    UIImageView* titleImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"nav_btn_image_logo"]];
    self.navigationItem.titleView = titleImageView;
    NSURL* url = [QSItemUtil getShopUrl:self.itemDict];
    self.webView.delegate = self;
    [self.webView loadRequest:[NSURLRequest requestWithURL:url]];
    [self.webView setScalesPageToFit:YES];
    
    [MobClick event:@"viewItemSource" attributes:@{@"itemId": [QSEntityUtil getIdOrEmptyStr:self.itemDict]} counter:1];
    
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSFontAttributeName:NAVNEWFONT,
       NSForegroundColorAttributeName:[UIColor blackColor]}];

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

/*
- (IBAction)submitBtnPressed:(id)sender {
    if (self.createTradeOp) {
        return;
    }
    if (![self.discountVc checkComplete]) {
        
        [self showErrorHudWithText:[self.discountVc getIncompleteMessage]];
    } else {
        NSDictionary *people = [QSUserManager shareUserManager].userInfo;
        if (people && ([QSPeopleUtil checkMobileExist:people] == NO)) {
            UIAlertView *alert = [[UIAlertView alloc]initWithTitle:nil message:@"请填写收货信息" delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确定", nil];
            [alert show];
        }else{
            self.createTradeOp =
            [SHARE_NW_ENGINE createOrderArray:@[[self.discountVc getResult]] onSucceed:^(NSDictionary *dict) {
                [self showSuccessHudAndPop:@"创建成功"];
                self.createTradeOp = nil;
            } onError:^(NSError *error) {
                [self handleError:error];
                self.createTradeOp = nil;
            }];
            //[self delisthandle];
        }
    }
}
- (void)delisthandle
{
    NSDictionary *newTradeDic = [self.discountVc getResult];
    NSDictionary *itemDic = [QSTradeUtil getItemSnapshot:newTradeDic];
    NSString *itemId = [QSItemUtil getItemId:itemDic];
    NSArray *skuArray = newTradeDic[@"selectedSkuProperties"];
    NSString *key = [QSItemUtil getKeyValueForSkuTableFromeSkuProperties:skuArray];
    [SHARE_NW_ENGINE getItemWithId:itemId onSucceed:^(NSDictionary *dic, NSDictionary *metadata) {
        int count = [QSItemUtil getFirstValueFromSkuTableWithkey:key itemDic:dic];
        if (count < 1) {
            UIAlertView *alert = [[UIAlertView alloc]initWithTitle:nil message:@"您来晚啦，这个规格的已经卖完啦！" delegate:self cancelButtonTitle:@"确认" otherButtonTitles:nil,nil];
            [alert show];
        }
        else{
            self.createTradeOp =
            [SHARE_NW_ENGINE createOrderArray:@[[self.discountVc getResult]] onSucceed:^(NSDictionary *dict) {
                [self showSuccessHudAndPop:@"创建成功"];
                self.createTradeOp = nil;
            } onError:^(NSError *error) {
                [self handleError:error];
                self.createTradeOp = nil;
            }];
        }
    } onError:^(NSError *error) {
        
    }];
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1) {
        self.navigationController.navigationBarHidden = NO;
        UIViewController* vc = [[QSU11ReceiverEditingViewController alloc] initWithDict:nil];
        UIBarButtonItem *backItem = [[UIBarButtonItem alloc]initWithImage:[UIImage imageNamed:@"nav_btn_back"] style:UIBarButtonItemStyleDone target:self action:@selector(backAction)];
        vc.navigationItem.leftBarButtonItem = backItem;
        [self.navigationController pushViewController:vc animated:YES];

    }
}
  */


@end

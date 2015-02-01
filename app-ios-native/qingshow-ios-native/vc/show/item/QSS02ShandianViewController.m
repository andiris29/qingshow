//
//  QSS02ShandianViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 2/1/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS02ShandianViewController.h"
#import "QSG01ItemWebViewController.h"
#import "QSNetworkKit.h"

#define PAGE_ID @"S02"

@interface QSS02ShandianViewController ()

@property (strong, nonatomic) QSItemImageListTableViewProvider* itemProvider;

@end

@implementation QSS02ShandianViewController
#pragma mark - Init
- (instancetype)init
{
    self = [super initWithNibName:@"QSS02ShandianViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self configProvider];
    [self configView];
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [MobClick beginLogPageView:PAGE_ID];
    [self.itemProvider refreshClickedData];
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark -
- (void)configView
{
    self.title = @"闪点推荐";
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    self.navigationController.navigationBarHidden = NO;
    
}
- (void)configProvider
{
    self.itemProvider = [[QSItemImageListTableViewProvider alloc] init];
    [self.itemProvider bindWithTableView:self.tableView];
    self.itemProvider.delegate = self;
    self.itemProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getItemFeedingRandomPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    [self.itemProvider fetchDataOfPage:1];
}
#pragma mark -
- (void)didClickShopBtnOfItem:(NSDictionary *)itemDict
{
    QSG01ItemWebViewController* vc = [[QSG01ItemWebViewController alloc] initWithItem:itemDict];
    [self.navigationController pushViewController:vc animated:YES];
}
@end

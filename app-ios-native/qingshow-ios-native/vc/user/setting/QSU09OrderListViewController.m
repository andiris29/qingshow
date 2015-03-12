//
//  QSU09OrderListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU09OrderListViewController.h"
#import "QSOrderListTableViewProvider.h"
#import "QSNetworkKit.h"
#import "QSOrderListHeaderView.h"
@interface QSU09OrderListViewController ()

@property (strong, nonatomic) QSOrderListTableViewProvider* provider;

@end

@implementation QSU09OrderListViewController

#pragma mark - Init
- (instancetype)init
{
    self = [super initWithNibName:@"QSU09OrderListViewController" bundle:nil];
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
    [self.provider refreshClickedData];
    //TODO MOB
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)configView {
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.title = @"我的订单";
    UIView* headerView = [QSOrderListHeaderView makeView];
    self.tableView.tableHeaderView = headerView;

    self.tableView.backgroundColor = [UIColor colorWithRed:204.f/255.f green:204.f/255.f blue:204.f/255.f alpha:1.f];
    [self.tableView reloadData];
}
- (void)configProvider
{
    self.provider = [[QSOrderListTableViewProvider alloc] init];
    [self.provider bindWithTableView:self.tableView];
    self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        //TODO change network block
        return [SHARE_NW_ENGINE getModelListPage:page onSucceed:succeedBlock onError:errorBlock];
    };

    [self.provider fetchDataOfPage:1];
}
@end

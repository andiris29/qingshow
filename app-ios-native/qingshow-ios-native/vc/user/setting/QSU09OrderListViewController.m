//
//  QSU09OrderListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU09OrderListViewController.h"
#import "QSU12RefundViewController.h"
#import "QSNetworkKit.h"
#import "QSOrderListHeaderView.h"
#import "UIViewController+QSExtension.h"
#import "QSPaymentService.h"
#import "UIViewController+ShowHud.h"

#define PAGE_ID @"U09 - 交易一览"

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
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSFontAttributeName:NAVNEWFONT,
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    self.navigationItem.leftBarButtonItem = backItem;
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.provider refreshClickedData];
    [MobClick beginLogPageView:PAGE_ID];
}
- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (void)willPresentAlertView:(UIAlertView *)alertView
{
    for (UIView *view in alertView.subviews) {
        if ([view isKindOfClass:[UILabel class]]) {
            UILabel *label = (UILabel *)view;
            if ([label.text isEqualToString:alertView.message]) {
                label.font = NEWFONT;
            }
            NSLog(@"label.text = %@",label.text);
            
        }
    }
}

- (void)configView {
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.title = @"我的订单";
    UIView* headerView = [QSOrderListHeaderView makeView];
    self.tableView.tableHeaderView = headerView;

    self.tableView.backgroundColor = [UIColor colorWithRed:204.f/255.f green:204.f/255.f blue:204.f/255.f alpha:1.f];
    [self.tableView reloadData];
    
    [self hideNaviBackBtnTitle];
}
- (void)configProvider
{
    self.provider = [[QSOrderListTableViewProvider alloc] init];
    [self.provider bindWithTableView:self.tableView];
    self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE queryOrderListPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.provider.delegate = self;

    [self.provider fetchDataOfPage:1];
}

#pragma mark - QSOrderListTableViewProviderDelegate
- (void)didClickRefundBtnOfOrder:(NSDictionary*)orderDict
{
    UIViewController* vc = [[QSU12RefundViewController alloc] initWithDict:orderDict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)didClickPayBtnOfOrder:(NSDictionary *)orderDict
{
    __weak QSU09OrderListViewController* weakSelf = self;
    [SHARE_PAYMENT_SERVICE payForTrade:orderDict
                             onSuccess:^{
                                 [weakSelf showTextHud:@"支付成功"];
                             }
                               onError:^(NSError *error) {
                                   [weakSelf showErrorHudWithText:@"支付失败"];
                               }];
}
- (void)didClickExchangeBtnOfOrder:(NSDictionary *)orderDic
{
    __weak QSU09OrderListViewController *weakSelf = self;
    [SHARE_NW_ENGINE changeTrade:orderDic status:7 onSucceed:^{
        [weakSelf showTextHud:@"已申请换货"];
    } onError:nil];
}
- (void)didClickReceiveBtnOfOrder:(NSDictionary *)orderDic
{
    __weak QSU09OrderListViewController *weakSelf = self;
    [SHARE_NW_ENGINE changeTrade:orderDic status:5 onSucceed:^{
        [weakSelf showTextHud:@"收货成功！"];
    } onError:nil];
}


@end

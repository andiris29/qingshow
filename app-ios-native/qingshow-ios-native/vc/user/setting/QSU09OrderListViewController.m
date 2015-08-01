//
//  QSU09OrderListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU09OrderListViewController.h"
#import "QSU12RefundViewController.h"
#import "QSS11CreateTradeViewController.h"
#import "QSNetworkKit.h"

#import "UIViewController+QSExtension.h"
#import "QSPaymentService.h"
#import "UIViewController+ShowHud.h"
#import "QSOrderUtil.h"
#import "QSItemUtil.h"
#import "QSDateUtil.h"
#import "QSTradeUtil.h"
#define PAGE_ID @"U09 - 交易一览"

@interface QSU09OrderListViewController ()

@property (strong, nonatomic) QSOrderListTableViewProvider* provider;
@property (strong,nonatomic) NSDictionary *oderDic;
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
        }
    }
}

- (void)configView {
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.title = @"我的订单";
    QSOrderListHeaderView* headerView = [QSOrderListHeaderView makeView];
    headerView.delegate = self;
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
        
        return [SHARE_NW_ENGINE queryOrderListPage:page inProgress:YES onSucceed:succeedBlock onError:errorBlock];
    };
    self.provider.delegate = self;
    [self.provider fetchDataOfPage:1];
    [self.provider reloadData];
    if (!self.provider.resultArray.count) {
        [self changeValueOfSegment:1];
    }
   
}

#pragma mark - QSOrderListHeaderViewDelegate
- (void)changeValueOfSegment:(NSInteger)value
{
    if (value == 1) {
        self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
            return [SHARE_NW_ENGINE queryOrderListPage:page inProgress:NO onSucceed:succeedBlock onError:errorBlock];
        };
        [self.provider fetchDataOfPage:1];
        [self.provider reloadData];
    }
    else if(value == 0)
    {
        self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
            return [SHARE_NW_ENGINE queryOrderListPage:page inProgress:YES onSucceed:succeedBlock onError:errorBlock];
        };
        [self.provider fetchDataOfPage:1];
        [self.provider reloadData];
    }
}
#pragma mark - QSOrderListTableViewProviderDelegate
- (void)didClickRefundBtnOfOrder:(NSDictionary*)tradeDict
{
    
    QSU12RefundViewController* vc = [[QSU12RefundViewController alloc] initWithDict:orderDict];
    vc.type = 1;
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)didClickPayBtnOfOrder:(NSDictionary *)tradeDict
{
    __weak QSU09OrderListViewController* weakSelf = self;
    [SHARE_PAYMENT_SERVICE payForTrade:tradeDict
                             onSuccess:^{
                                 [weakSelf showTextHud:@"支付成功"];
                             }
                               onError:^(NSError *error) {
                                   [weakSelf showErrorHudWithText:@"支付失败"];
                               }];
}
- (void)didClickExchangeBtnOfOrder:(NSDictionary *)orderDic
{
    NSString *company = [QSTradeUtil getTradeLogisticCompany:orderDic];
    NSString *trackingId = [QSTradeUtil getTradeLogisticId:orderDic];
    NSString *str = [NSString stringWithFormat:@"物流公司：%@\n物流单号：%@",company,trackingId];
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"物流信息" message:str delegate:self cancelButtonTitle:@"确定" otherButtonTitles:nil, nil];
    [alert show];
}
- (void)didClickReceiveBtnOfOrder:(NSDictionary *)orderDic
{
    _oderDic = orderDic;
    NSArray *order = [QSTradeUtil getOrderArray:orderDic];
    NSDictionary *dic = nil;
    if (order.count) {
        dic = [order firstObject];
    }
    NSDictionary* itemDict = [QSOrderUtil getItemSnapshot:dic];
    NSString *title = [QSItemUtil getItemName:itemDict];
    NSDate *date = [NSDate date];
    NSString *dateStr = [NSString stringWithFormat:@"收货时间  %@",[QSDateUtil buildStringFromDate:date]];
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:title message:dateStr delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确认", nil];
    alert.delegate = self;
    [alert show];
    
}
- (void)didClickCancelBtnOfOrder:(NSDictionary *)orderDic
{
    __weak QSU09OrderListViewController *weakSelf = self;
    [SHARE_NW_ENGINE changeTrade:_oderDic status:17 info:nil onSucceed:^{
        [weakSelf showTextHud:@"已取消订单"];
         }onError:nil];
}
#pragma mark - UIAlertViewDelegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1) {
        __weak QSU09OrderListViewController *weakSelf = self;
        [SHARE_NW_ENGINE changeTrade:_oderDic status:5 info:nil onSucceed:^{
            [weakSelf showTextHud:@"收货成功！"];
        } onError:nil];
    }
    else
    {
        [alertView dismissWithClickedButtonIndex:0 animated:YES];
    }
}


@end

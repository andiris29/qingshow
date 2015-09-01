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
#import "WXApi.h"
#import "UIViewController+QSExtension.h"
#import "QSPaymentService.h"
#import "UIViewController+ShowHud.h"
#import "QSItemUtil.h"
#import "QSDateUtil.h"
#import "QSTradeUtil.h"
#import "QSPeopleUtil.h"
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
    [self.provider refreshClickedData];
    [self configProvider];
    [self configView];
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSFontAttributeName:NAVNEWFONT,
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    if ([UIScreen mainScreen].bounds.size.width == 414) {
        self.view.transform = CGAffineTransformMakeScale(1.3, 1.3);
    }
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
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
    _headerView = [QSOrderListHeaderView makeView];
    _headerView.delegate = self;
//    headerView.segmentControl.selectedSegmentIndex = 1;
//    [self changeValueOfSegment:1];
    self.tableView.tableHeaderView = _headerView;
    self.tableView.backgroundColor = [UIColor colorWithRed:204.f/255.f green:204.f/255.f blue:204.f/255.f alpha:1.f];
    
    [self hideNaviBackBtnTitle];
}
- (void)configProvider
{
    self.provider = [[QSOrderListTableViewProvider alloc] init];
    [self.provider bindWithTableView:self.tableView];
    __weak QSU09OrderListViewController *weakSelf = self;
    self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE queryPhase:page phases:@"0" onSucceed:succeedBlock onError:^(NSError *error){
            if (error.code == 1009 && page == 1) {
                weakSelf.headerView.segmentControl.selectedSegmentIndex = 1;
                [weakSelf changeValueOfSegment:1];
            }else(errorBlock(error));
            
        }];
    };
    self.provider.delegate = self;
    [self.provider fetchDataOfPage:1];
    [self.provider reloadData];
}

#pragma mark - QSOrderListHeaderViewDelegate
- (void)changeValueOfSegment:(NSInteger)value
{
    if (value == 1) {
        self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
            return [SHARE_NW_ENGINE queryPhase:page phases:@"1,2" onSucceed:succeedBlock onError:errorBlock];
        };
        [self.provider fetchDataOfPage:1];
        [self.provider reloadData];
    }
    else if(value == 0)
    {
        self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
            return [SHARE_NW_ENGINE queryPhase:page phases:@"0" onSucceed:succeedBlock onError:errorBlock];
        };
        [self.provider fetchDataOfPage:1];
        [self.provider reloadData];
    }
}
#pragma mark - QSOrderListTableViewProviderDelegate
- (void)didClickOrder:(NSDictionary *)orderDict {
    

}
- (void)didClickRefundBtnOfOrder:(NSDictionary*)tradeDict
{
    
    QSU12RefundViewController* vc = [[QSU12RefundViewController alloc] initWithDict:tradeDict];
    vc.type = 1;
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    vc.navigationItem.leftBarButtonItem = backItem;
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)didClickPayBtnOfOrder:(NSDictionary *)tradeDict
{
    [SHARE_PAYMENT_SERVICE sharedForTrade:tradeDict onSucceed:^{
        QSS11CreateTradeViewController* vc = [[QSS11CreateTradeViewController alloc] initWithDict:tradeDict];
        vc.menuProvider = self.menuProvider;
        [self.navigationController pushViewController:vc animated:YES];
    } onError:^(NSError *error) {
        [self handleError:error];
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
- (void)didClickReceiveBtnOfOrder:(NSDictionary *)tradeDict
{
    _oderDic = tradeDict;
    NSDictionary *dic = tradeDict;

    NSDictionary* itemDict = [QSTradeUtil getItemSnapshot:dic];
    NSString *title = [QSItemUtil getItemName:itemDict];
    NSDate *date = [NSDate date];
    NSString *dateStr = [NSString stringWithFormat:@"收货时间  %@",[QSDateUtil buildStringFromDate:date]];
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:title message:dateStr delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确认", nil];
    alert.delegate = self;
    alert.tag = 101;
    [alert show];
    
}
- (void)didClickCancelBtnOfOrder:(NSDictionary *)orderDic
{
    _oderDic = orderDic;
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:nil message:@"确认取消订单？" delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确认", nil];
    alert.delegate = self;
    alert.tag = 102;
    [alert show];
}
#pragma mark - UIAlertViewDelegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (alertView.tag == 101) {
        if (buttonIndex == 1) {
            __weak QSU09OrderListViewController *weakSelf = self;
            [SHARE_NW_ENGINE changeTrade:_oderDic status:5 info:nil onSucceed:^(NSDictionary* tradeDict){
                [weakSelf showTextHud:@"收货成功！"];
            } onError:nil];
        }
        else
        {
            [alertView dismissWithClickedButtonIndex:0 animated:YES];
        }

    }
    else if(alertView.tag == 102){
        if (buttonIndex == 1) {
            __weak QSU09OrderListViewController *weakSelf = self;
            [SHARE_NW_ENGINE changeTrade:_oderDic status:18 info:nil onSucceed:^(NSDictionary* dict){
                if ([QSTradeUtil getStatus:_oderDic].intValue == 0) {
                    [weakSelf showTextHud:@"已取消订单"];
                }else{
                    [weakSelf showTextHud:@"已取消订单" afterCustomDelay:2.f];
                }
                
                [weakSelf.provider reloadData];
            }onError:nil];

        }
        else
        {
            [alertView dismissWithClickedButtonIndex:0 animated:YES];
        }
    }
}


@end
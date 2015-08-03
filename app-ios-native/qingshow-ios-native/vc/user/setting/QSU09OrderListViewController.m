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
#import "QSOrderUtil.h"
#import "QSItemUtil.h"
#import "QSDateUtil.h"
#import "QSTradeUtil.h"
#import "QSPeopleUtil.h"
#define PAGE_ID @"U09 - 交易一览"
#define kShareTitle @"时尚宠儿的归属地"
#define kShareDesc @"美丽乐分享，潮流资讯早知道"
@interface QSU09OrderListViewController ()

@property (strong, nonatomic) QSOrderListTableViewProvider* provider;
@property (strong,nonatomic) NSDictionary *oderDic;
@property (strong,nonatomic) QSOrderListHeaderView *headerView;
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
        return [SHARE_NW_ENGINE queryOrderListPage:page inProgress:@"true" onSucceed:succeedBlock onError:^(NSError *error){
            if (error.code == 1009) {
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
            return [SHARE_NW_ENGINE queryOrderListPage:page onSucceed:succeedBlock onError:errorBlock];
        };
        [self.provider fetchDataOfPage:1];
        [self.provider reloadData];
    }
    else if(value == 0)
    {
        self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
            return [SHARE_NW_ENGINE queryOrderListPage:page inProgress:@"true" onSucceed:succeedBlock onError:errorBlock];
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
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)didClickPayBtnOfOrder:(NSDictionary *)tradeDict shouldShare:(BOOL)shouldShare
{
    
    if (shouldShare) {
        NSDictionary *peopleDic = [QSTradeUtil getPeopleDic:tradeDict];
        NSString *peopleId = [QSPeopleUtil getPeopleId:peopleDic];
        NSString *orderId = [QSTradeUtil getOrderId:tradeDict];
        [MobClick event:@"shareShow" attributes:@{@"snsName": @"weixin"} counter:1];
        WXMediaMessage *message = [WXMediaMessage message];
        
        message.title = [NSString stringWithFormat:@"【%@】%@", kShareTitle, kShareDesc];
        //    message.description = kShareDesc;
        [message setThumbImage:[UIImage imageNamed:@"share_icon"]];
        WXWebpageObject *ext = [WXWebpageObject object];

        ext.webpageUrl = [NSString stringWithFormat:@"http://chingshow.com/app-web?entry=shareTrade&_id={%@}&initiatorRef={%@}",orderId,peopleId];
        
        message.mediaObject = ext;
        
        SendMessageToWXReq* req = [[SendMessageToWXReq alloc] init];
        req.bText = NO;
        req.message = message;
        req.scene = WXSceneTimeline;
        
        [WXApi sendReq:req];

    }
    QSS11CreateTradeViewController* vc = [[QSS11CreateTradeViewController alloc] initWithDict:tradeDict];
    [self.navigationController pushViewController:vc animated:YES];
//    __weak QSU09OrderListViewController* weakSelf = self;
//    [SHARE_PAYMENT_SERVICE payForTrade:tradeDict
//                             onSuccess:^{
//                                 [weakSelf showTextHud:@"支付成功"];
//                             }
//                               onError:^(NSError *error) {
//                                   [weakSelf showErrorHudWithText:@"支付失败"];
//                               }];
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
    [SHARE_NW_ENGINE changeTrade:orderDic status:18 info:nil onSucceed:^{
        [weakSelf showTextHud:@"已取消订单"];
        [weakSelf.provider reloadData];
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

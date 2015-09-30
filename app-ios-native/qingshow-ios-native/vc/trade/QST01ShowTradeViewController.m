//
//  QST01ShowTradeViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/9/6.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QST01ShowTradeViewController.h"
#import "QSNetworkKit.h"
#import "QSNetworkEngine+TradeService.h"
#import "QSNetworkEngine+ShowService.h"
#import "QSG01ItemWebViewController.h"
#import "QSU01UserDetailViewController.h"
#import "QSItemUtil.h"
#import "QSTradeUtil.h"
@interface QST01ShowTradeViewController ()

@property (strong,nonatomic)QST01ShowTradeProvider *provider;
@end

@implementation QST01ShowTradeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self configUI];
    [self configProvider];
}

- (void)configUI
{
    self.title = @"潮人晒单";
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSFontAttributeName:NAVNEWFONT,
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    self.backToTopBtn.hidden = YES;

}
- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
}
- (void)configProvider
{
    self.provider = [[QST01ShowTradeProvider alloc]init];
    self.provider.delegate = self;
    [self.provider bindWithTableView:self.tableView];
    self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE tradeQueryHighted:page OnSecceed:succeedBlock onError:errorBlock];
    };
    [self.provider fetchDataOfPage:1];
    [self.provider reloadData];
}
- (void)didTapTradeCell:(NSDictionary *)tradeDict
{
    __weak QST01ShowTradeViewController *weakSelf = self;
    NSDictionary* itemDict = [QSTradeUtil getItemDic:tradeDict];
    NSDictionary* peopleDict = [QSTradeUtil getPeopleDic:tradeDict];
    QSG01ItemWebViewController *vc = [[QSG01ItemWebViewController alloc] initWithItem:itemDic peopleId:[QSEntityUtil getIdOrEmptyStr:peopleDict]];
    if ([QSItemUtil getDelist:itemDic] == YES) {
        vc.isDisCountBtnHidden = YES;
    }
    [weakSelf.navigationController pushViewController:vc animated:YES];
}
- (void)didTapHeaderInT01Cell:(NSDictionary *)peopleDic
{
    QSU01UserDetailViewController *vc = [[QSU01UserDetailViewController alloc]initWithPeople:peopleDic];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if (self.tableView.contentOffset.y != 0) {
        _backToTopBtn.hidden = NO;
    }
    else
    {
        _backToTopBtn.hidden = YES;
    }
}
- (IBAction)backToTopBtnPressed:(id)sender {
    CGPoint p = [self.tableView contentOffset];
    p.y = 0;
    [self.tableView setContentOffset:p animated:YES];
    _backToTopBtn.hidden = YES;
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

@end

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

}
- (void)viewWillAppear:(BOOL)animated{
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
- (void)didTapTradeCell:(NSString *)ItemId
{
    __weak QST01ShowTradeViewController *weakSelf = self;
    [SHARE_NW_ENGINE getItemWithId:ItemId onSucceed:^(NSArray *array, NSDictionary *metadata) {
        if (array.count) {
            NSDictionary *ItemDic = [array firstObject];
            QSG01ItemWebViewController *vc = [[QSG01ItemWebViewController alloc]initWithItem:ItemDic];
            if ([QSItemUtil getDelist:ItemDic] == YES) {
                 vc.isDisCountBtnHidden = YES;
            }
            [weakSelf.navigationController pushViewController:vc animated:YES];
        }
    } onError:^(NSError *error) {
        
    }];
}
- (void)didTapHeaderInT01Cell:(NSDictionary *)peopleDic
{
    QSU01UserDetailViewController *vc = [[QSU01UserDetailViewController alloc]initWithPeople:peopleDic];
    [self.navigationController pushViewController:vc animated:YES];
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

//
//  QSS17ViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS17ViewController.h"
#import "QSNetworkKit.h"
#import "QSShowUtil.h"
#import "QSS18TopShowOneDayViewController.h"
#import "UIViewController+ShowHud.h"


#define PAGE_ID @"美搭榜单"
#define SS17CellId @"SS17TableViewCellId"

@interface QSS17ViewController ()<QSS17ProviderDelegate>

@property(nonatomic,strong)QSS17TavleViewProvider *provider;
@end



@implementation QSS17ViewController

- (instancetype)init
{
    if (self = [super initWithNibName:@"QSS17ViewController" bundle:nil]) {
        
    }
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    //[self getNetWorkData];
    [self configProvider];
    self.topShowTableView.separatorColor = [UIColor grayColor];
    self.topShowTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.navigationItem.title = @"美搭榜单";

}
- (void)configProvider
{
    self.provider = [[QSS17TavleViewProvider alloc] init];
    self.provider.delegate = self;
    [self.provider bindWithTableView:self.topShowTableView];
    self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return  [SHARE_NW_ENGINE getTestShowsOnSucceed:^(NSArray *array, NSDictionary *metadata) {
            succeedBlock(array,metadata);
        } onError:errorBlock];
    };
    [self.provider fetchDataOfPage:1];
    [self.provider reloadData];

}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [MobClick beginLogPageView:PAGE_ID];
}
//////获取网络数据
//- (void)getNetWorkData
//{
//    static dispatch_once_t onceToken;
//    dispatch_once(&onceToken, ^{
//        [SHARE_NW_ENGINE getTestShowsOnSucceed:^(NSArray *array, NSDictionary *metadata) {
//            _dataArray = (NSMutableArray *)array;
//        } onError:^(NSError *error) {
//            [self showErrorHudWithError:error];
//        }];
//    });
//    
//}
#pragma mark - QSS17ProviderDelegate - 点击跳转的方法

- (void)didClickedDate:(NSDate*)date ofProvider:(QSS17TavleViewProvider*)provider
{
    QSS18TopShowOneDayViewController* vc = [[QSS18TopShowOneDayViewController alloc] initWithDate:date];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)handleNetworkError:(NSError *)error
{
    [self showErrorHudWithError:error];
}

@end





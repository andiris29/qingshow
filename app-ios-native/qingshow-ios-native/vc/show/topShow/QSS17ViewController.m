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
#import "QSS17TavleViewProvider.h"

#define PAGE_ID @"美搭榜单"
#define SS17CellId @"SS17TableViewCellId"

@interface QSS17ViewController ()<QSS17ProviderDelegate>
@property(nonatomic,strong)QSS17TavleViewProvider *delegateObj;
@property(nonatomic,strong)NSMutableArray *dataArray;

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
  
    self.delegateObj = [[QSS17TavleViewProvider alloc]initWithArray:self.dataArray];
    //self.delegateObj.dataArray = self.dataArray;
    self.delegateObj.delegate = self;
    [self.delegateObj bindWithTableView:self.topShowTableView];
      __weak QSS17ViewController* weakSelf = self;
    self.delegateObj.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return  [SHARE_NW_ENGINE getTestShowsOnSucceed:^(NSArray *array, NSDictionary *metadata) {
            //weakSelf.dataArray = (NSMutableArray *)array;
            succeedBlock(array,metadata);

            //NSLog(@"weakdataArray = %@",weakSelf.dataArray);
            
        } onError:^(NSError *error) {
            [weakSelf showErrorHudWithError:error];
        }];
        
    };
    [self.delegateObj fetchDataOfPage:1];
    [self.delegateObj reloadData];

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

- (void)tableViewCellDidClicked:(NSInteger)row
{
    NSDate* date =[QSShowUtil getRecommendDate: _dataArray[row*2]];
    QSS18TopShowOneDayViewController* vc = [[QSS18TopShowOneDayViewController alloc] initWithDate:date];
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





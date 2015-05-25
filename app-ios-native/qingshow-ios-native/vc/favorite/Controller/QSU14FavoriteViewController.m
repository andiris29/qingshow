//
//  QSU14FavoriteViewController.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//  我的收藏

#import "QSU14FavoriteViewController.h"
#import "QSU14DisplayCell.h"
#import "QSNetworkKit.h"
#import "QSShowUtil.h"
#import "QSFavoInfo.h"
#import "Common.h"
#import "UIViewController+ShowHud.h"
#import "QSU14FavoTableVIewProvider.h"

@interface QSU14FavoriteViewController ()

@property (weak, nonatomic) IBOutlet UITableView *tableView;

//tableView 数据源
@property (nonatomic, strong)NSMutableArray *dataArray;

@property (nonatomic, strong)QSU14FavoTableVIewProvider *favoProvider;

@end

@implementation QSU14FavoriteViewController


- (instancetype)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    if (self = [super initWithNibName:@"QSU14FavoriteViewController" bundle:nil]) {
        _dataArray = [NSMutableArray array];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self setProvider];
    //获取数据源
//    [self loadFavotiteData];
    
}

#pragma mark -- provider
- (void)setProvider
{
//    _favoProvider = [[QSU14FavoTableVIewProvider alloc] init];
//    
    
                self.favoProvider = [[QSU14FavoTableVIewProvider alloc] init];
        [self.favoProvider bindWithTableView:self.tableView];
        __weak QSU14FavoriteViewController* weakSelf = self;

        self.favoProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
            return [SHARE_NW_ENGINE getTestShowsOnSucceed:^(NSArray *array, NSDictionary *metadata) {
                succeedBlock(array,metadata);
            } onError:^(NSError *error) {
                
            }];
        };
        [self.favoProvider fetchDataOfPage:1];
    [self.favoProvider reloadData];

}



#pragma mark --获取网络数据
- (void)loadFavotiteData
{
        [SHARE_NW_ENGINE getTestShowsOnSucceed:^(NSArray *array, NSDictionary *metadata) {
//            NSLog(@"array = %@,dic = %@",array,metadata);
            //数据转模型
            NSArray *favoInfoArray = [QSFavoInfo favoInfoWithDicArray:array];
            
            //模型传递
            self.favoProvider.dataArray = [NSMutableArray arrayWithArray:favoInfoArray];
            
            [self.tableView reloadData];
        } onError:^(NSError *error) {
            
            [self showErrorHudWithError:error];
        }];
}

@end

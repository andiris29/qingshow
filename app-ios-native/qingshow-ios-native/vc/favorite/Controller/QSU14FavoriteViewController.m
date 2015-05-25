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
#import "QSTableViewBasicProvider.h"

@interface QSU14FavoriteViewController ()

@property (weak, nonatomic) IBOutlet UITableView *tableView;

//tableView 数据源
@property (nonatomic,strong)NSMutableArray *dataArray;

@end

@implementation QSU14FavoriteViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    QSTableViewBasicProvider *tableView = [[QSTableViewBasicProvider alloc] initWithCellNib:[UINib nibWithNibName:@"QSU14DisplayCell" bundle:[NSBundle mainBundle]] identifier:@"displayCell"];
    
    [tableView bindWithTableView:self.tableView];
    
    //获取数据源
    [self loadFavotiteData];
    
}

#pragma mark --获取网络数据
- (void)loadFavotiteData
{
        [SHARE_NW_ENGINE getTestShowsOnSucceed:^(NSArray *array, NSDictionary *metadata) {
//            NSLog(@"array = %@,dic = %@",array,metadata);
            //数据转模型
            NSArray *favoInfoArray = [QSFavoInfo favoInfoWithDicArray:array];
            
            //模型传递
            _dataArray = [NSMutableArray arrayWithArray:favoInfoArray];
            
            [self.tableView reloadData];
        } onError:^(NSError *error) {
#warning 改成 [self showErrorHudWithError:err];
            
            NSLog(@"TopShow Page  NetWorlk Error!");
        }];
}

#pragma mark //UITableViewDataSource
#warning Move to Provider
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _dataArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSU14DisplayCell *cell = [self.tableView dequeueReusableCellWithIdentifier:@"displayCell"];
    if (cell == nil) {
        cell = [[QSU14DisplayCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"displayCell"];
    }
    QSFavoInfo *favoInfo = _dataArray[indexPath.row];
    [cell setValueForSubViewsWith:favoInfo];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
#warning 这里改成根据屏幕宽度来计算cell高度
    if (iPhone6Plus) {
        return 240;
    }else if (iPhone6){
        return 225;
    }else{
        return 215;
    }
}
#pragma mark --UITableViewDelegate
@end

//
//  QSS17ViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS17ViewController.h"
#import "QSNetworkKit.h"
#import "QSS17TopShowCell.h"
#import "QSShowUtil.h"

#define PAGE_ID @"美搭榜单"
#define SS17CellId @"SS17TableViewCellId"
@interface QSS17ViewController ()<UITableViewDelegate,UITableViewDataSource>
{
    NSMutableArray *_dataArray;
}

@end

@implementation QSS17ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.view.backgroundColor = [UIColor clearColor];
    self.topShowTableView.dataSource = self;
    self.topShowTableView.delegate = self;
    _dataArray =  [[NSMutableArray alloc]initWithCapacity:0];
    [self transformNavigationItem];
    [self getNetWorkData];
  
}
//优化显示
- (void)transformNavigationItem
{
    
    self.navigationItem.title = @"美搭榜单";
    
}

//获取网络数据
- (void)getNetWorkData
{
    [SHARE_NW_ENGINE getTestShowsOnSucceed:^(NSArray *array, NSDictionary *metadata) {
        NSLog(@"array = %@,dic = %@",array,metadata);
        [_dataArray addObjectsFromArray:array];
        [self.topShowTableView reloadData];
    } onError:^(NSError *error) {
        NSLog(@"TopShow Page  NetWorlk Error!");
    }];

}

#pragma mark -UITableViewDataSource

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 180.f;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (_dataArray.count) {
        return _dataArray.count;
    }
    else
    {
        return 1;
    }
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSS17TopShowCell *cell = [tableView dequeueReusableCellWithIdentifier:SS17CellId ];
    if (cell == nil) {
        cell = [[[NSBundle mainBundle]loadNibNamed:@"QSS17TopShowCell" owner:nil options:nil]lastObject];
    }
    [cell bindWithDataArray:_dataArray];
    return cell;
}
#pragma mark - UITableViewDelegate - 点击跳转的方法
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSLog(@"跳转美搭榜单第二页");
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




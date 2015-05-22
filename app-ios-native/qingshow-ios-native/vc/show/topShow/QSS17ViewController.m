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
    self.topShowTableView.separatorColor = [UIColor grayColor];
    self.topShowTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _dataArray =  [[NSMutableArray alloc]init];
    self.navigationItem.title = @"";
#warning add leftNavigationButton
    [self getNetWorkData];
    
#warning expected reload moreData
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [MobClick beginLogPageView:PAGE_ID];
}

//获取网络数据
- (void)getNetWorkData
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [SHARE_NW_ENGINE getTestShowsOnSucceed:^(NSArray *array, NSDictionary *metadata) {
            [_dataArray addObject:array];
            [self.topShowTableView reloadData];
        } onError:^(NSError *error) {
            NSLog(@"TopShow Page  NetWorlk Error!");
        }];
    });
    
}
- (CGFloat)getHeight:(NSDictionary *)dic
{
    CGFloat height = 180;
    if (dic.count) {
        height = [QSShowUtil getCoverMetaDataHeight:dic];
    }
    return height;
}

#pragma mark -UITableViewDataSource

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (_dataArray.count) {
        return  [self getHeight:[_dataArray firstObject][0]];
    }
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
    cell.userInteractionEnabled = YES;
   
    //查看网络返回的数据
    //NSLog(@"datadic = %@",[_dataArray firstObject][0]);
    
    [cell bindWithDataDic:[_dataArray firstObject][indexPath.row*2] andAnotherDic:nil];
    
    return cell;
}
#pragma mark - UITableViewDelegate - 点击跳转的方法
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    NSLog(@"date = %@",[QSShowUtil getRecommendDate: [_dataArray firstObject][indexPath.row*2]]);
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




//
//  QSU16BonusListViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/8/31.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU16BonusListViewController.h"
#import "QSU15BonusListTableViewCell.h"
#import "QSPeopleUtil.h"
#import "QSBonusUtil.h"
#import "QSNetworkEngine+ShowService.h"
#import "QSNetworkKit.h"
#import "QSS10ItemDetailViewController.h"
#import "UIViewController+QSExtension.h"

#define QSU16CELLID @"QSU16TableViewCellId"
@interface QSU16BonusListViewController ()

@property (strong,nonatomic)NSArray *listArray;

@property (strong, nonatomic) UITableView* tableView;
@end

@implementation QSU16BonusListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = @"佣金明细";
    UITableView *tableView = [[UITableView alloc]initWithFrame:self.view.bounds style:UITableViewStylePlain];
    self.tableView = tableView;
    tableView.delegate = self;
    tableView.dataSource = self;
    tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:tableView];
    
    [SHARE_NW_ENGINE queryOwnedBonusOnSucceed:^(NSArray *array, NSDictionary *metadata) {
        self.listArray = array;
        [self.tableView reloadData];
    } onError:^(NSError *error) {
        [self handleError:error];
    }];
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 60.f;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _listArray.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSU15BonusListTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:QSU16CELLID];
    if (cell == nil) {
        cell = [[[NSBundle mainBundle]loadNibNamed:@"QSU15BonusListTableViewCell" owner:self options:nil]lastObject];
    }
    [cell bindWithDict:_listArray[_listArray.count - indexPath.row -1]];

    return cell;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary *dic = _listArray[_listArray.count - 1 - indexPath.row];
    NSString *itemId = [QSBonusUtil getItemRef:dic];
    __weak QSU16BonusListViewController *weakSelf = self;
    [SHARE_NW_ENGINE getItemWithId:itemId onSucceed:^(NSDictionary *itemDic, NSDictionary *metadata) {
        if (itemDic) {
            QSS10ItemDetailViewController *vc = [[QSS10ItemDetailViewController alloc] initWithItem:itemDic];
            [weakSelf.navigationController pushViewController:vc animated:YES];
        }
    } onError:^(NSError *error) {
        
    }];
    
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

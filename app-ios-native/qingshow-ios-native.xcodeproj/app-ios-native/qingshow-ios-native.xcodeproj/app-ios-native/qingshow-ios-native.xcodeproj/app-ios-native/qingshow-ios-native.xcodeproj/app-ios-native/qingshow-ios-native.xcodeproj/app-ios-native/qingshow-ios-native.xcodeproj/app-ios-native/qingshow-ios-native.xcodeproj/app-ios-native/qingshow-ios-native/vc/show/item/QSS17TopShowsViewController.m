//
//  QSS17TopShowsViewController.m
//  qingshow-ios-native
//
//  Created by ching show on 15/5/4.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS17TopShowsViewController.h"
#import "QSS17TopShow_1Cell.h"
#import "QSS17TopShow_2Cell.h"
#import "QSS17TopShow_3Cell.h"
#import "QSS17TopShow_4Cell.h"
#import "QSS17TopShow_5Cell.h"
#import "QSS03ShowDetailViewController.h"
#import "QSNetworkKit.h"
//#import "QSNetworkEngine+QSHotService.h"

static NSString *TopShow_1Indentifier = @"TopShow_1Cell";
static NSString *TopShow_2Indentifier = @"TopShow_2Cell";
static NSString *TopShow_3Indentifier = @"TopShow_3Cell";
static NSString *TopShow_4Indentifier = @"TopShow_4Cell";
static NSString *TopShow_5Indentifier = @"TopShow_5Cell";
@interface QSS17TopShowsViewController ()<UITableViewDataSource, UITableViewDelegate>

@end

@implementation QSS17TopShowsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"美搭榜单";
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.showsVerticalScrollIndicator = NO;
//    [SHARE_NW_ENGINE hotFeedingByTopShows:nil OnSucceed:^(NSArray *array, NSDictionary *metadata) {
//        
//    } onError:^(NSError *error) {
//        
//    }];
//    [SHARE_NW_ENGINE hotFeedingByTopShows:nil OnSucceed:^(NSArray *array, NSDictionary *metadata) {
//    
//    } onError:^(NSError *error) {
//    
//    }];

    
    [self registerCell];
}

//注册Cell
- (void)registerCell
{
  [self.tableView registerNib:[UINib nibWithNibName:@"QSS17TopShow_1Cell" bundle:nil] forCellReuseIdentifier:TopShow_1Indentifier];
  [self.tableView registerNib:[UINib nibWithNibName:@"QSS17TopShow_2Cell" bundle:nil] forCellReuseIdentifier:TopShow_2Indentifier];
  [self.tableView registerNib:[UINib nibWithNibName:@"QSS17TopShow_3Cell" bundle:nil] forCellReuseIdentifier:TopShow_3Indentifier];
  [self.tableView registerNib:[UINib nibWithNibName:@"QSS17TopShow_4Cell" bundle:nil] forCellReuseIdentifier:TopShow_4Indentifier];
  [self.tableView registerNib:[UINib nibWithNibName:@"QSS17TopShow_5Cell" bundle:nil] forCellReuseIdentifier:TopShow_5Indentifier];
    
}

#pragma mark - UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 5;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        QSS17TopShow_1Cell *cell = [tableView dequeueReusableCellWithIdentifier:TopShow_1Indentifier forIndexPath:indexPath];
//        if (indexPath.row == 0) {
//            cell.dayLabel.hidden = NO;
//            cell.lineView.hidden = NO;
//            cell.YAndMLabel.hidden = NO;
//        } else {
//            cell.dayLabel.hidden = YES;
//            cell.lineView.hidden = YES;
//            cell.YAndMLabel.hidden = YES;
//        }
        return cell;
    } else if(indexPath.row == 1){
        QSS17TopShow_2Cell *cell = [tableView dequeueReusableCellWithIdentifier:TopShow_2Indentifier forIndexPath:indexPath];
        return cell;
    } else if (indexPath.row == 2){
        QSS17TopShow_3Cell *cell = [tableView dequeueReusableCellWithIdentifier:TopShow_3Indentifier forIndexPath:indexPath];
        return cell;
    } else if (indexPath.row == 3){
        QSS17TopShow_4Cell *cell = [tableView dequeueReusableCellWithIdentifier:TopShow_4Indentifier forIndexPath:indexPath];
        return cell;
    } else {
        QSS17TopShow_5Cell *cell = [tableView dequeueReusableCellWithIdentifier:TopShow_5Indentifier forIndexPath:indexPath];
        return cell;
    }
    
}


#pragma mark - UITableViewDelegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [UIScreen mainScreen].bounds.size.height - 130;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
//    QSS03ShowDetailViewController *showVC = [[QSS03ShowDetailViewController alloc] init];
//    [self.navigationController pushViewController:showVC animated:YES];
}



@end

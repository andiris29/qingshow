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
#import "QSHotUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSDateUtil.h"
#import "QSS03ShowDetailViewController.h"
#define PAGE_ID @"S17"

@interface QSS17TopShowsViewController ()<UITableViewDataSource, UITableViewDelegate>

@property (nonatomic, strong)NSMutableArray *coverArray;
@property (nonatomic, strong)NSMutableArray *likeArray;
@property (nonatomic, strong)NSDictionary *topShows;
@property (nonatomic, strong)NSMutableArray *timeArray;

@end

@implementation QSS17TopShowsViewController
- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"美搭榜单";
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.showsVerticalScrollIndicator = NO;
    [self registerCell];
    
    __block QSS17TopShowsViewController *blockSelf = self;
    [SHARE_NW_ENGINE hotFeedingByOnSucceed:^(NSArray *array, NSDictionary *metadata) {
        self.coverArray = [NSMutableArray arrayWithCapacity:0];
        self.likeArray = [NSMutableArray arrayWithCapacity:0];
        self.timeArray = [NSMutableArray arrayWithCapacity:0];
        for (NSDictionary *dic in array) {
            self.topShows = dic;
            NSURL *image = [QSHotUtil getHotCoverUrl:dic];
            NSString *like = [QSHotUtil getHotNumLike:dic];
            NSDate *date = [QSHotUtil getHotCreateDate:dic];
            [self.coverArray addObject:image];
            [self.likeArray addObject:like];
            [self.timeArray addObject:date];
        }
        [blockSelf.tableView reloadData];
    } onError:^(NSError *error) {
        if (error) {
        }
    }];
    
    
}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden =NO;
    [MobClick beginLogPageView:PAGE_ID];
}


- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}



//注册Cell
- (void)registerCell
{
  [self.tableView registerNib:[UINib nibWithNibName:@"QSS17TopShow_1Cell" bundle:nil] forCellReuseIdentifier:TopShowS_1Indentifier];
  [self.tableView registerNib:[UINib nibWithNibName:@"QSS17TopShow_2Cell" bundle:nil] forCellReuseIdentifier:TopShowS_2Indentifier];
  [self.tableView registerNib:[UINib nibWithNibName:@"QSS17TopShow_3Cell" bundle:nil] forCellReuseIdentifier:TopShowS_3Indentifier];
  [self.tableView registerNib:[UINib nibWithNibName:@"QSS17TopShow_4Cell" bundle:nil] forCellReuseIdentifier:TopShowS_4Indentifier];
  [self.tableView registerNib:[UINib nibWithNibName:@"QSS17TopShow_5Cell" bundle:nil] forCellReuseIdentifier:TopShowS_5Indentifier];
    
}

#pragma mark - UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 5;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        QSS17TopShow_1Cell *cell = [tableView dequeueReusableCellWithIdentifier:TopShowS_1Indentifier forIndexPath:indexPath];
        cell.dayLabel.text = [QSDateUtil getDayDesc:self.timeArray[indexPath.row]];
        cell.YAndMLabel.text = [NSString stringWithFormat:@"%@  %@", [QSDateUtil getMonthDesc:self.timeArray[indexPath.row]], [QSDateUtil getYearDesc:self.timeArray[indexPath.row]]];
        [cell.backImage setImageFromURL:self.coverArray[indexPath.row] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"]];
        cell.likeButton.titleLabel.text = self.likeArray[indexPath.row];
        return cell;
    } else if(indexPath.row == 1){
        QSS17TopShow_2Cell *cell = [tableView dequeueReusableCellWithIdentifier:TopShowS_2Indentifier forIndexPath:indexPath];
        [cell.backImage setImageFromURL:self.coverArray[indexPath.row] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"]];
        cell.likeButton.titleLabel.text = self.likeArray[indexPath.row];

        return cell;
    } else if (indexPath.row == 2){
        QSS17TopShow_3Cell *cell = [tableView dequeueReusableCellWithIdentifier:TopShowS_3Indentifier forIndexPath:indexPath];
        [cell.backImage setImageFromURL:self.coverArray[indexPath.row] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"]];
        cell.likeButton.titleLabel.text = self.likeArray[indexPath.row];

        return cell;
    } else if (indexPath.row == 3){
        QSS17TopShow_4Cell *cell = [tableView dequeueReusableCellWithIdentifier:TopShowS_4Indentifier forIndexPath:indexPath];
        [cell.backImage setImageFromURL:self.coverArray[indexPath.row] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"]];
        cell.likeButton.titleLabel.text = self.likeArray[indexPath.row];

        return cell;
    } else {
        QSS17TopShow_5Cell *cell = [tableView dequeueReusableCellWithIdentifier:TopShowS_5Indentifier forIndexPath:indexPath];
        [cell.backImage setImageFromURL:self.coverArray[indexPath.row] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"]];
        cell.likeButton.titleLabel.text = self.likeArray[indexPath.row];

        return cell;
    }
    return nil;
}


#pragma mark - UITableViewDelegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [UIScreen mainScreen].bounds.size.height/ 5*4 ;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:NO];
    QSS03ShowDetailViewController *showVC = [[QSS03ShowDetailViewController alloc] initWithShow:self.topShows];
    [self.navigationController pushViewController:showVC animated:YES];
}



@end

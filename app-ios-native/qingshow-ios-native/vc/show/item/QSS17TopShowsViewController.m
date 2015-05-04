//
//  QSS17TopShowsViewController.m
//  qingshow-ios-native
//
//  Created by ching show on 15/5/4.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS17TopShowsViewController.h"
#import "QSS17TopShow_1Cell.h"

static NSString *TopShow_1Indentifier = @"TopShow_1Cell";

@interface QSS17TopShowsViewController ()<UITableViewDataSource, UITableViewDelegate>

@end

@implementation QSS17TopShowsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.tableFooterView = [UIView new];
//    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.tableView registerNib:[UINib nibWithNibName:@"QSS17TopShow_1Cell" bundle:nil] forCellReuseIdentifier:TopShow_1Indentifier];
}

#pragma mark - UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 5;
}



- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
//    QSS17TopShows_1Cell *cell = [tableView dequeueReusableCellWithIdentifier:TopShows_1Indentifier];
    QSS17TopShow_1Cell *cell = [tableView dequeueReusableCellWithIdentifier:TopShow_1Indentifier forIndexPath:indexPath];
    return cell;
}

#pragma mark - UITableViewDelegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [UIScreen mainScreen].bounds.size.height - 130;
}



@end

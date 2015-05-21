//
//  QSU14FavoriteViewController.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//  我的收藏

#import "QSU14FavoriteViewController.h"
#import "QSU14DisplayCell.h"

@interface QSU14FavoriteViewController ()
@property (weak, nonatomic) IBOutlet UITableView *tableView;

@end

@implementation QSU14FavoriteViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    //设置tableView dataDataSources/delegate
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    
    //注册cell
    [self.tableView registerNib:[UINib nibWithNibName:@"QSU14DisplayCell" bundle:[NSBundle mainBundle]] forCellReuseIdentifier:@"displayCell"];
    
}

#pragma mark //UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 10;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSU14DisplayCell *cell = [self.tableView dequeueReusableCellWithIdentifier:@"displayCell"];
    if (cell == nil) {
        cell = [[QSU14DisplayCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"displayCell"];
    }
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 215;
}
#pragma mark //UITableViewDelegate
@end

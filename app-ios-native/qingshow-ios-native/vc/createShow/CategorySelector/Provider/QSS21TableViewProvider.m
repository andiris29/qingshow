//
//  QSS21TableViewProvider.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS21TableViewProvider.h"
#import "QSS21TableViewCell.h"

#define selectorCellID @"QSS21SelectorCell"

@implementation QSS21TableViewProvider
@dynamic delegate;

- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSS21TableViewCell" bundle:nil] forCellReuseIdentifier:selectorCellID];
}

#pragma mark -- UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 5;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSS21TableViewCell *cell = [self.view dequeueReusableCellWithIdentifier:selectorCellID];
    if (cell == nil) {
        cell = [[QSS21TableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:selectorCellID];
    }
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [UIScreen mainScreen].bounds.size.width/80 * 37;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return [UIScreen mainScreen].bounds.size.width/160 * 37;
}
#pragma mark -- UITableViewDelegate
@end

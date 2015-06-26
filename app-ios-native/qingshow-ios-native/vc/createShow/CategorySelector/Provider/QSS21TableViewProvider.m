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

@interface QSS21TableViewProvider ()

@property (strong ,nonatomic) NSMutableArray *cellArray;
@end

@implementation QSS21TableViewProvider
@dynamic delegate;

- (void)registerCell
{
}

- (NSMutableArray *)cellArray
{
    if (_cellArray == nil) {
        _cellArray = [NSMutableArray array];
        for (int i = 0; i < self.dataArray.count; i ++) {
            QSS21TableViewCell *cell = [[[NSBundle mainBundle]loadNibNamed:@"QSS21TableViewCell" owner:nil options:nil]lastObject];
            [_cellArray addObject:cell];
        }
    }
    return _cellArray;
}

#pragma mark -- UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.dataArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    QSS21TableViewCell *cell = (QSS21TableViewCell *)self.cellArray[indexPath.row];
    
    NSDictionary *cellDic = self.dataArray[indexPath.row];
    
    NSDictionary *dic = [self getSelectedDicCompareWithCellDic:cellDic];
    [cell setSubViewsWith:cellDic andSelectedDic:dic];
    
    if ([UIScreen mainScreen].bounds.size.width == 414) {
        cell.contentView.transform  = CGAffineTransformMakeScale(1.3, 1.3);
    }
    //在这里将更新后的cell放在array里
    //[self.cellArray replaceObjectAtIndex:cell withObject:indexPath.row];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [UIScreen mainScreen].bounds.size.width/80 * 37;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return [UIScreen mainScreen].bounds.size.width/16;
}

#pragma mark -- 获取记录的category
- (NSDictionary *)getSelectedDicCompareWithCellDic:(NSDictionary *)cellDic
{
    for (NSDictionary *dic in self.selectedArray) {
        if ([dic[@"parentRef"] isEqualToString:cellDic[@"_id"]]) {
            return dic;
        }
    }
    return nil;
}

#pragma mark -- 获取选择categorys
- (NSMutableArray *)getResultArray
{
    NSMutableArray *resultArray = [NSMutableArray array];
    for (int i = 0 ; i < self.dataArray.count; i ++) {
        QSS21TableViewCell *cell = (QSS21TableViewCell *)self.cellArray[i];
        if (cell.recordDic == nil) {
            continue;
        }
        [resultArray addObject:cell.recordDic];
    }
    return resultArray;
}
@end

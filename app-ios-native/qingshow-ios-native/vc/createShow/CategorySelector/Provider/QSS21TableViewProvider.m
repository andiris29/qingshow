//
//  QSS21TableViewProvider.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS21TableViewProvider.h"
#import "QSCategoryUtil.h"
#import "NSArray+QSExtension.h"
#import "QSEntityUtil.h"
#import "QSCategoryManager.h"

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
            QSCategoryTableViewCell *cell = [[[NSBundle mainBundle]loadNibNamed:@"QSCategoryTableViewCell" owner:nil options:nil]lastObject];
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
    
    QSCategoryTableViewCell *cell = (QSCategoryTableViewCell *)self.cellArray[indexPath.row];
    cell.delegate = self;
    NSDictionary *cellDic = self.dataArray[indexPath.row];
    
    [cell setLastCellWith:cellDic andSelectedArray:self.selectedArray];
    CGFloat rate = [UIScreen mainScreen].bounds.size.width / 320.0;
    cell.contentView.transform  = CGAffineTransformMakeScale(rate, rate);
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CGFloat rate = [UIScreen mainScreen].bounds.size.width / 320.0;
    return [UIScreen mainScreen].bounds.size.width /80 * 37;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return [UIScreen mainScreen].bounds.size.width/16;
}

#pragma mark -- 获取记录的category
- (NSDictionary *)getSelectedDicCompareWithCellDic:(NSDictionary *)cellDic
{
    for (NSDictionary *dic in self.selectedArray) {
        if ([[QSEntityUtil getStringValue:dic keyPath:@"parentRef"] isEqualToString:[QSEntityUtil getStringValue:cellDic keyPath:@"_id"]]) {
            return dic;
        }
    }
    return nil;
}

#pragma mark -- 获取选择categorys
- (NSMutableArray *)getResultArray
{
    return self.selectedArray;
}

- (void)didSelectItem:(NSDictionary*)categoryDict ofCell:(QSCategoryTableViewCell*)cell {
    BOOL f = YES;
    
    for (int i = 0; i < self.selectedArray.count; i ++) {
        NSDictionary *dic = self.selectedArray[i];
        if (dic == categoryDict) {
            [self.selectedArray removeObject:dic];
            i --;
            f = NO;
        }
    }
    
    NSString* parentId = [QSEntityUtil getIdOrEmptyStr:self.modelParentDict];
    NSMutableArray* dictToRemove = [NSMutableArray array];
    
    if ([parentId isEqualToString:[QSCategoryUtil getParentId:categoryDict]]) {
        for (NSDictionary* d in self.selectedArray) {
            if ([parentId isEqualToString:[QSCategoryUtil getParentId:d]]) {
                [dictToRemove addObject:d];
            }
        }
    }
    [self.selectedArray removeObjectsInArray:dictToRemove];
    
    if (f && categoryDict) {
        [self.selectedArray addObject:categoryDict];
    }
    
    [self.view reloadData];
}
@end

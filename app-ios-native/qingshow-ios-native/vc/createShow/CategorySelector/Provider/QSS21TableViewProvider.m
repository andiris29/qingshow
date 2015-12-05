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
    
    //NSDictionary *dic = [self getSelectedDicCompareWithCellDic:cellDic];
    //if (cell == [self.cellArray lastObject]) {
        [cell setLastCellWith:cellDic andSelectedArray:self.selectedArray];
//    }
//    else
//    {
//    [cell setSubViewsWith:cellDic andSelectedDic:dic];
//    }
    CGFloat rate = [UIScreen mainScreen].bounds.size.width / 320.0;
    cell.contentView.transform  = CGAffineTransformMakeScale(rate, rate);
    //在这里将更新后的cell放在array里
    //[self.cellArray replaceObjectAtIndex:cell withObject:indexPath.row];
    
    
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

- (void)didSelectItem:(NSDictionary*)itemDict ofCell:(QSCategoryTableViewCell*)cell {
     BOOL f = YES;
//    if (cell == [self.cellArray lastObject]) {
        for (int i = 0; i < self.selectedArray.count; i ++) {
             NSDictionary *dic = self.selectedArray[i];
                        if (dic == itemDict) {
                            [self.selectedArray removeObject:dic];
                            i --;
                            f = NO;
                        }
        }
        if (f && itemDict) {
            [self.selectedArray addObject:itemDict];
        }
        
//    }
//    else
//    {
    
//        for (int i = 0; i < self.selectedArray.count; i++) {
//            NSDictionary* dict = self.selectedArray[i];
//            if ([[QSCategoryUtil getParentId:dict] isEqualToString:[QSCategoryUtil getParentId:itemDict]] ) {
//                [self.selectedArray removeObject:dict];
//                i--;
//                if (dict == itemDict  ) {
//                    f = NO;
//                }
//                
//                
//            }
//            
//        }
//        if (f && itemDict) {
//            [self.selectedArray addObject:itemDict];
//        }
    
//    }
    
    
    [self.view reloadData];
    
}
@end

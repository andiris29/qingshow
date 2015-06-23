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
//记录indexpath
@property (strong ,nonatomic) NSMutableArray *indexPathArray;
@property (strong ,nonatomic) NSMutableArray *cellArray;
@end

@implementation QSS21TableViewProvider
@dynamic delegate;

- (void)registerCell
{
//    [self.view registerNib:[UINib nibWithNibName:@"QSS21TableViewCell" bundle:nil] forCellReuseIdentifier:selectorCellID];
}
- (instancetype)init
{
    if (self == [super init]) {
        NSMutableArray *arr = [NSMutableArray array];
        for (int i = 0; i < 5; i ++) {
            QSS21TableViewCell *cell = [[[NSBundle mainBundle]loadNibNamed:@"QSS21TableViewCell" owner:nil options:nil]lastObject];
            [arr addObject:cell];
        }
        self.cellArray = arr;
    }
    return self;
}

//懒加载
- (NSMutableArray *)indexPathArray
{
    if (_indexPathArray == nil) {
        _indexPathArray = [NSMutableArray array];
    }
    return _indexPathArray;
}
#pragma mark -- UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 5;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
#warning TODO cell复用带来的问题 不能记录UI状态
//    QSS21TableViewCell *cell = [self.view dequeueReusableCellWithIdentifier:selectorCellID];
//    if (cell == nil) {
//        cell = [[QSS21TableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:selectorCellID];
//    }
    
    
    QSS21TableViewCell *cell = (QSS21TableViewCell *)self.cellArray[indexPath.row];
    //记录indexpath
    [self.indexPathArray addObject:indexPath];
    
//    NSDictionary *cellDic = self.dataArray[indexPath.row];
    
    [cell setSubViewsWith:nil];
    
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

- (NSMutableArray *)getResultArray
{
    NSMutableArray *resultArray = [NSMutableArray array];
    for (int i = 0 ; i < 5; i ++) {
        NSIndexPath *indexPath = self.indexPathArray[i];
        QSS21TableViewCell *cell = (QSS21TableViewCell *)[self.view cellForRowAtIndexPath:indexPath];
        [resultArray addObject:cell.recordDic];
    }
    return resultArray;
}
@end

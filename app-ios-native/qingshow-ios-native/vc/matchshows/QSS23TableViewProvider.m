//
//  QSS23TableViewProvider.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/12.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS23TableViewProvider.h"
#import "QSCategoryTableViewCell.h"
@interface QSS23TableViewProvider()


@end

@implementation QSS23TableViewProvider

@dynamic delegate;

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSCategoryTableViewCell *cell = self.cellArray[indexPath.row];
    cell.delegate = self;
    NSDictionary *categoryDict = self.dataArray[indexPath.row];
    [cell bindCategoryForSearch:categoryDict];
    if ([UIScreen mainScreen].bounds.size.width == 414) {
        cell.contentView.transform  = CGAffineTransformMakeScale(1.3, 1.3);
    }
    return cell;
}


- (void)didSelectItem:(NSDictionary*)categoryDict ofCell:(QSCategoryTableViewCell*)cell {
    if ([self.delegate respondsToSelector:@selector(provider:didSelectCategory:)]) {
        [self.delegate provider:self didSelectCategory:categoryDict];
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
#warning TODO Work Around
    return 1.f;
}
@end

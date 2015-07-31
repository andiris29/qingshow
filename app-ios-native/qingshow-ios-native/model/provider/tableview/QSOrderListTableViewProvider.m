//
//  QSOrderListTableViewProvider.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/13/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSOrderListTableViewProvider.h"


@implementation QSOrderListTableViewProvider
@dynamic delegate;
#pragma mark - Override
- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSOrderListTableViewCell" bundle:nil] forCellReuseIdentifier:QSOrderListTableViewCellIdentifier];
}


#pragma mark - UITableView DataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSOrderListTableViewCell* cell = (QSOrderListTableViewCell*)[tableView dequeueReusableCellWithIdentifier:QSOrderListTableViewCellIdentifier forIndexPath:indexPath];
    cell.delegate = self;
    [cell bindWithDict:[self orderForIndexPath:indexPath]];
    return cell;
}

- (void)refreshData:(NSDictionary*)dict
{
//    NSIndexPath* indexPath = [NSIndexPath indexPathForRow:[self.resultArray indexOfObject:dict] inSection:0];
//    QSModelListTableViewCell* cell = (QSModelListTableViewCell*)[self.view cellForRowAtIndexPath:indexPath];
//    [cell bindWithPeople:dict];
}

#pragma mark - UITableView Delegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return QSOrderListTableViewCellHeight;
}

#pragma mark - QSOrderListTableViewCellDelegate
- (void)didClickRefundBtnForCell:(QSOrderListTableViewCell*)cell
{
    if ([self.delegate respondsToSelector:@selector(didClickRefundBtnOfOrder:)]) {
        [self.delegate didClickRefundBtnOfOrder:[self orderForCell:cell]];
    }
}

- (void)didClickCancelBtnForCell:(QSOrderListTableViewCell *)cell
{
    if ([self.delegate respondsToSelector:@selector(didClickCancelBtnForCell:)]) {
        [self.delegate didClickCancelBtnOfOrder:[self orderForCell:cell]];
    }
}
- (void)didClickPayBtnForCell:(QSOrderListTableViewCell *)cell
{
    if ([self.delegate respondsToSelector:@selector(didClickPayBtnOfOrder:)]) {
        [self.delegate didClickPayBtnOfOrder:[self orderForCell:cell]];
    }
}
- (void)didClickReceiveBtnForCell:(QSOrderListTableViewCell *)cell
{
    if ([self.delegate respondsToSelector:@selector(didClickReceiveBtnOfOrder:)]) {
        [self.delegate didClickReceiveBtnOfOrder:[self orderForCell:cell]];
    }
}
- (void)didClickExchangeBtnForCell:(QSOrderListTableViewCell *)cell
{
    if ([self.delegate respondsToSelector:@selector(didClickExchangeBtnOfOrder:)]) {
        [self.delegate didClickExchangeBtnOfOrder:[self orderForCell:cell]];
    }
}
#pragma mark - Private
- (NSDictionary*)orderForCell:(UITableViewCell*)cell
{
    NSIndexPath* indexPath = [self.view indexPathForCell:cell];
    return [self orderForIndexPath:indexPath];
}

- (NSDictionary*)orderForIndexPath:(NSIndexPath*)indexPath
{
    NSInteger row = indexPath.row;
    if (row < self.resultArray.count) {
        return self.resultArray[row];
    } else {
        return nil;
    }
}
@end

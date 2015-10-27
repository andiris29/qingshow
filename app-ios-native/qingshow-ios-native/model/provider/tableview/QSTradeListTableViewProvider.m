//
//  QSTradeListTableViewProvider.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/13/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSTradeListTableViewProvider.h"
#import "QSTradeUtil.h"

@implementation QSTradeListTableViewProvider
@dynamic delegate;
#pragma mark - Override
- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSTradeListTableViewCell" bundle:nil] forCellReuseIdentifier:QSTradeListTableViewCellIdentifier];
    [self.view registerNib:[UINib nibWithNibName:@"QSTradeListTableViewCellComplete" bundle:nil] forCellReuseIdentifier:QSTradeListTableViewCellCompleteIdentifier];
}


#pragma mark - UITableView DataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSTradeListTableViewCell* cell = nil;
    if (self.cellType == QSTradeListTableViewCellNormal) {
        cell = (QSTradeListTableViewCell*)[tableView dequeueReusableCellWithIdentifier:QSTradeListTableViewCellIdentifier forIndexPath:indexPath];
    } else {
        cell = (QSTradeListTableViewCell*)[tableView dequeueReusableCellWithIdentifier:QSTradeListTableViewCellCompleteIdentifier forIndexPath:indexPath];
    }
    cell.cellType = self.cellType;
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
    return QSTradeListTableViewCellHeight;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if ([self.delegate respondsToSelector:@selector(didClickOrder:)]) {
        [self.delegate didClickOrder:[self orderForIndexPath:indexPath]];
    }
}
#pragma mark - QSTradeListTableViewCellDelegate
- (void)didClickRefundBtnForCell:(QSTradeListTableViewCell*)cell
{
    if ([self.delegate respondsToSelector:@selector(didClickRefundBtnOfOrder:)]) {
        [self.delegate didClickRefundBtnOfOrder:[self orderForCell:cell]];
    }
}

- (void)didClickCancelBtnForCell:(QSTradeListTableViewCell *)cell
{
        [self.delegate didClickCancelBtnOfOrder:[self orderForCell:cell]];
}
- (void)didClickPayBtnForCell:(QSTradeListTableViewCell *)cell
{
    if ([self.delegate respondsToSelector:@selector(didClickPayBtnOfOrder:)]) {
        [self.delegate didClickPayBtnOfOrder:[self orderForCell:cell]];
    }
}
- (void)didClickReceiveBtnForCell:(QSTradeListTableViewCell *)cell
{
    if ([self.delegate respondsToSelector:@selector(didClickReceiveBtnOfOrder:)]) {
        [self.delegate didClickReceiveBtnOfOrder:[self orderForCell:cell]];
    }
}
- (void)didClickExchangeBtnForCell:(QSTradeListTableViewCell *)cell
{
    
}
- (void)didClickLogisticForCell:(QSTradeListTableViewCell *)cell
{
    if ([self.delegate respondsToSelector:@selector(didClickExchangeBtnOfOrder:)]) {
        [self.delegate didClickExchangeBtnOfOrder:[self orderForCell:cell]];
    }
}
- (void)didClickExpectablePriceBtnForCell:(QSTradeListTableViewCell *)cell {
    if ([self.delegate respondsToSelector:@selector(didClickExpectablePriceBtnOfOrder:)]) {
        [self.delegate didClickExpectablePriceBtnOfOrder:[self orderForCell:cell]];
    }
}
- (void)didClickToWebPageForCell:(QSTradeListTableViewCell *)cell
{
    [self.delegate didClickToWebPage:[self orderForCell:cell]];
}
#pragma mark - Private
- (NSDictionary*)orderForCell:(UITableViewCell*)cell
{
    
    NSIndexPath* indexPath = [self.view indexPathForRowAtPoint:cell.center];
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
   
    return self.resultArray[row];
}
@end

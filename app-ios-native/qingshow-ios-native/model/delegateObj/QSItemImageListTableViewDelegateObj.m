//
//  QSItemListTableViewDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 1/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSItemImageListTableViewDelegateObj.h"
#import "QSItemImageTableViewCell.h"
@implementation QSItemImageListTableViewDelegateObj
- (void)registerCell
{
    [self.tableView registerNib:[UINib nibWithNibName:@"QSItemImageTableViewCell" bundle:nil] forCellReuseIdentifier:@"QSItemImageTableViewCell"];
}
#pragma mark - Table View
- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSItemImageTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"QSItemImageTableViewCell" forIndexPath:indexPath];
    NSDictionary* dict = self.resultArray[indexPath.row];
    [cell bindWithItem:dict];
//    [cell bindWithComment:dict];
//    cell.delegate = self;
    if (self.type == QSItemImageListTableViewDelegateObjTypeDiscount) {
        cell.saleLabel.hidden = NO;
    } else {
        cell.saleLabel.hidden = YES;
    }
    cell.delegate = self;
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [QSItemImageTableViewCell getHeightWithItem:self.resultArray[indexPath.row]];
}
- (void)didClickShopBtn:(UITableViewCell *)cell
{
    NSIndexPath* i = [self.tableView indexPathForCell:cell];
    NSDictionary* itemDict = self.resultArray[i.row];
    if ([self.delegate respondsToSelector:@selector(didClickShopBtnOfItem:)]) {
        [self.delegate didClickShopBtnOfItem:itemDict];
    }

}
@end

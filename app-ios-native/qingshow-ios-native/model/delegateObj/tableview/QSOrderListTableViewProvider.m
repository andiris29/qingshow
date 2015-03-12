//
//  QSOrderListTableViewProvider.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/13/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSOrderListTableViewProvider.h"
#import "QSOrderListTableViewCell.h"

@implementation QSOrderListTableViewProvider

#pragma mark - Override
- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSOrderListTableViewCell" bundle:nil] forCellReuseIdentifier:QSOrderListTableViewCellIdentifier];
}


#pragma mark - UITableView DataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSOrderListTableViewCell* cell = (QSOrderListTableViewCell*)[tableView dequeueReusableCellWithIdentifier:QSOrderListTableViewCellIdentifier forIndexPath:indexPath];
//    cell.delegate = self;
//    NSDictionary* dict = self.resultArray[indexPath.row];
//    [cell bindWithPeople:dict];
//    cell.followBtn.hidden = self.type == QSModelListTableViewDelegateObjTypeHideFollow;
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

@end

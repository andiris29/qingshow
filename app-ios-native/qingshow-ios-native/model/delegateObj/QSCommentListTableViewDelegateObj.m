//
//  QSCommentListTableViewDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSCommentListTableViewDelegateObj.h"
#import "QSCommentUtil.h"

@implementation QSCommentListTableViewDelegateObj

- (void)registerCell
{
    [self.tableView registerNib:[UINib nibWithNibName:@"QSCommentTableViewCell" bundle:nil] forCellReuseIdentifier:@"QSCommentTableViewCell"];
}

#pragma mark - Table View
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSCommentTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"QSCommentTableViewCell" forIndexPath:indexPath];
    NSDictionary* dict = self.resultArray[indexPath.row];
    [cell bindWithComment:dict];
    cell.delegate = self;
    return cell;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 62.f;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if ([self.delegate respondsToSelector:@selector(didClickComment:atIndex:)]) {
            NSDictionary* dict = self.resultArray[indexPath.row];
        [self.delegate didClickComment:dict atIndex:(int)indexPath.row];
    }
}

- (void)didTapIcon:(QSCommentTableViewCell*)cell
{
    NSIndexPath* indexPath = [self.tableView indexPathForCell:cell];
    if ([self.delegate respondsToSelector:@selector(didClickPeople:)]) {
        NSDictionary* dict = self.resultArray[indexPath.row];
        [self.delegate didClickPeople:[QSCommentUtil getPeople:dict]];
    }
}

@end

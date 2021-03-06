//
//  QSCommentListTableViewDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSCommentListTableViewProvider.h"
#import "QSCommentUtil.h"

@implementation QSCommentListTableViewProvider
@dynamic delegate;

- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSCommentTableViewCell" bundle:nil] forCellReuseIdentifier:@"QSCommentTableViewCell"];
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
    NSDictionary* commentDict = self.resultArray[indexPath.row];
    return [QSCommentTableViewCell getCellSize:commentDict].height;
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

}

@end

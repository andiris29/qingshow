//
//  QSTopicTableViewProvider.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSTopicTableViewProvider.h"
#import "QSTopicTableViewCell.h"

@implementation QSTopicTableViewProvider
#pragma mark - Override
- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSTopicTableViewCell" bundle:nil] forCellReuseIdentifier:TOPIC_TALBE_VIEW_CELL_IDENTIFIER];
}

#pragma mark - Table View
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSTopicTableViewCell* cell = (QSTopicTableViewCell*)[tableView dequeueReusableCellWithIdentifier:TOPIC_TALBE_VIEW_CELL_IDENTIFIER forIndexPath:indexPath];
//    cell.delegate = self;
    [cell bindWithDict:[self topicForIndexPath:indexPath]];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 283.f;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary* dict = [self topicForIndexPath:indexPath];
    if ([self.delegate respondsToSelector:@selector(didClickTopic:)]) {
        [self.delegate didClickTopic:dict];
    }
}
#pragma mark - Private
- (NSDictionary*)topicForCell:(UITableViewCell*)cell
{
    return [self topicForIndexPath:[self.view indexPathForCell:cell]];
}

- (NSDictionary*)topicForIndexPath:(NSIndexPath*)indexPath
{
    NSInteger row = indexPath.row;
    if (row < self.resultArray.count) {
        return self.resultArray[row];
    } else {
        return nil;
    }
}


@end

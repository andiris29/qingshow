//
//  QSTableViewDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/7/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSModelListTableViewDelegateObj.h"
#import "MKNetworkOperation.h"

@interface QSModelListTableViewDelegateObj ()


@end

@implementation QSModelListTableViewDelegateObj


#pragma mark - Override

- (void)registerCell
{
    [self.tableView registerNib:[UINib nibWithNibName:@"QSModelListTableViewCell" bundle:nil] forCellReuseIdentifier:@"QSModelListTableViewCell"];
}
#pragma mark - UITableView DataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSModelListTableViewCell* cell = (QSModelListTableViewCell*)[tableView dequeueReusableCellWithIdentifier:@"QSModelListTableViewCell" forIndexPath:indexPath];
    cell.delegate = self;
    NSDictionary* dict = self.resultArray[indexPath.row];
    [cell bindWithPeople:dict];
    return cell;
}


#pragma mark - UITableView Delegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 62.f;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if ([self.delegate respondsToSelector:@selector(clickModel:)]) {
        [self.delegate clickModel:self.resultArray[indexPath.row]];
    }
}
#pragma mark - QSModelListTableViewCellDelegate
- (void)favorBtnPressed:(QSModelListTableViewCell *)cell
{
    NSIndexPath* indexPath = [self.tableView indexPathForCell:cell];
    if ([self.delegate respondsToSelector:@selector(followBtnPressed:)]) {
        [self.delegate followBtnPressed:self.resultArray[indexPath.row]];
    }
}

@end

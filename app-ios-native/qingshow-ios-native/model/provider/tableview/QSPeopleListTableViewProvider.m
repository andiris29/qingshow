//
//  QSTableViewDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/7/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSPeopleListTableViewProvider.h"
#import "MKNetworkOperation.h"

@interface QSPeopleListTableViewProvider ()


@end

@implementation QSPeopleListTableViewProvider
@dynamic delegate;

#pragma mark - Override

- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSPeopleListTableViewCell" bundle:nil] forCellReuseIdentifier:@"QSPeopleListTableViewCell"];
}
#pragma mark - UITableView DataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSPeopleListTableViewCell* cell = (QSPeopleListTableViewCell*)[tableView dequeueReusableCellWithIdentifier:@"QSPeopleListTableViewCell" forIndexPath:indexPath];
    cell.delegate = self;
    NSDictionary* dict = self.resultArray[indexPath.row];
    [cell bindWithPeople:dict];
//    cell.followBtn.hidden = self.type == QSModelListTableViewDelegateObjTypeHideFollow;
    return cell;
}
- (void)refreshData:(NSDictionary*)dict
{
    NSIndexPath* indexPath = [NSIndexPath indexPathForRow:[self.resultArray indexOfObject:dict] inSection:0];
    QSPeopleListTableViewCell* cell = (QSPeopleListTableViewCell*)[self.view cellForRowAtIndexPath:indexPath];
    [cell bindWithPeople:dict];
}

#pragma mark - UITableView Delegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 62.f;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSDictionary* modelDict = self.resultArray[indexPath.row];
    self.clickedData = modelDict;
    if ([self.delegate respondsToSelector:@selector(clickModel:)]) {
        [self.delegate clickModel:modelDict];
    }
}
#pragma mark - QSModelListTableViewCellDelegate
- (void)favorBtnPressed:(QSPeopleListTableViewCell *)cell
{
    NSIndexPath* indexPath = [self.view indexPathForCell:cell];
    if ([self.delegate respondsToSelector:@selector(followBtnPressed:)]) {
        [self.delegate followBtnPressed:self.resultArray[indexPath.row]];
    }
}

- (void)refreshClickedData
{
    if (self.clickedData) {
        NSIndexPath* indexPath = [NSIndexPath indexPathForRow:[self.resultArray indexOfObject:self.clickedData] inSection:0];
        if (self.filterBlock) {
            if (!self.filterBlock(self.clickedData)) {
                [self.resultArray removeObject:self.clickedData];
                [self.view reloadData];
                return;
            }
        }
        QSPeopleListTableViewCell* cell = (QSPeopleListTableViewCell*)[self.view cellForRowAtIndexPath:indexPath];
        [cell bindWithPeople:self.clickedData];
        self.clickedData = nil;
    }
}

@end

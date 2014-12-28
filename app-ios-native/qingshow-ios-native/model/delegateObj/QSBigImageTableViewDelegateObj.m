//
//  QSShowTableViewDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/14/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSBigImageTableViewDelegateObj.h"

@implementation QSBigImageTableViewDelegateObj

#pragma mark - Override

- (void)registerCell
{
    [self.tableView registerNib:[UINib nibWithNibName:@"QSBigImageTableViewCell" bundle:nil] forCellReuseIdentifier:@"QSBigImageTableViewCell"];
    [self.tableView registerNib:[UINib nibWithNibName:@"QSBigImageFashionTableViewCell" bundle:nil] forCellReuseIdentifier:@"QSBigImageFashionTableViewCell"];
}
#pragma mark - UITableView DataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{

    QSBigImageTableViewCell* cell = nil;
    if (self.type == QSBigImageTableViewCellTypeFashion) {
        cell = (QSBigImageTableViewCell*)[tableView dequeueReusableCellWithIdentifier:@"QSBigImageFashionTableViewCell" forIndexPath:indexPath];
    } else {
        cell = (QSBigImageTableViewCell*)[tableView dequeueReusableCellWithIdentifier:@"QSBigImageTableViewCell" forIndexPath:indexPath];
    }

    cell.type = self.type;
    cell.delegate = self;
    NSDictionary* dict = self.resultArray[indexPath.row];
    [cell bindWithDict:dict];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}


#pragma mark - UITableView Delegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary* dict = self.resultArray[indexPath.row];
    if (self.type == QSBigImageTableViewCellTypeFashion) {
        return [QSBigImageTableViewCell getHeightWithPreview:dict];
    } else {
        return [QSBigImageTableViewCell getHeighWithShow:dict];
    }
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if ([self.delegate respondsToSelector:@selector(didClickCell:ofData:)]) {
        UITableViewCell* cell = [tableView cellForRowAtIndexPath:indexPath];
        NSDictionary* data = self.resultArray[indexPath.row];
        self.clickedData = data;
        [self.delegate didClickCell:cell ofData:data];
    }
}
- (void)refreshClickedData
{
    if (self.clickedData) {
        NSUInteger row = [self.resultArray indexOfObject:self.clickedData];
        NSIndexPath* indexPath = [NSIndexPath indexPathForRow:row inSection:0];
        QSBigImageTableViewCell* cell = (QSBigImageTableViewCell*)[self.tableView cellForRowAtIndexPath:indexPath];
        [cell bindWithDict:self.clickedData];
        self.clickedData = nil;
    }
}

#pragma mark - QSBigImageTableViewCellDelegate
- (void)clickCommentBtn:(QSBigImageTableViewCell*)cell
{
    NSIndexPath* indexPath = [self.tableView indexPathForCell:cell];
    NSDictionary* dict = self.resultArray[indexPath.row];
    if ([self.delegate respondsToSelector:@selector(clickCommentOfDict:)]) {
        [self.delegate clickCommentOfDict:dict];
    }
}
- (void)clickLikeBtn:(QSBigImageTableViewCell*)cell
{
    NSIndexPath* indexPath = [self.tableView indexPathForCell:cell];
    NSDictionary* dict = self.resultArray[indexPath.row];
    if ([self.delegate respondsToSelector:@selector(clickLikeOfDict:)]) {
        [self.delegate clickLikeOfDict:dict];
    }
}
- (void)clickShareBtn:(QSBigImageTableViewCell*)cell
{
    NSIndexPath* indexPath = [self.tableView indexPathForCell:cell];
    NSDictionary* dict = self.resultArray[indexPath.row];
    if ([self.delegate respondsToSelector:@selector(clickShareOfDict:)]) {
        [self.delegate clickShareOfDict:dict];
    }
}
- (void)rebindData:(NSDictionary*)dict
{
    NSIndexPath* indexPath = [NSIndexPath indexPathForRow:[self.resultArray indexOfObject:dict] inSection:0];
    QSBigImageTableViewCell* cell = (QSBigImageTableViewCell*)[self.tableView cellForRowAtIndexPath:indexPath];
    [cell bindWithDict:dict];
}
@end

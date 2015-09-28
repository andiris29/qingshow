//
//  QSShowTableViewDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/14/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSBigImageTableViewProvider.h"
#import "QSDateUtil.h"

@implementation QSBigImageTableViewProvider
@dynamic delegate;

#pragma mark - Override

- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSBigImageTableViewCell" bundle:nil] forCellReuseIdentifier:@"QSBigImageTableViewCell"];
    [self.view registerNib:[UINib nibWithNibName:@"QSBigImageFashionTableViewCell" bundle:nil] forCellReuseIdentifier:@"QSBigImageFashionTableViewCell"];
}
#pragma mark - UITableView DataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary* dict = self.resultArray[indexPath.row];
    QSBigImageTableViewCell* cell = nil;
    cell = (QSBigImageTableViewCell*)[tableView dequeueReusableCellWithIdentifier:@"QSBigImageTableViewCell" forIndexPath:indexPath];
    

    cell.type = self.type;
    cell.delegate = self;
    

    BOOL fShowDate = NO;
    if (indexPath.row == 0) {
        fShowDate = YES;
    }
    cell.dateContainer.hidden = !fShowDate;
    [cell bindWithDict:dict];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}


#pragma mark - UITableView Delegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary* dict = self.resultArray[indexPath.row];
    return [QSBigImageTableViewCell getHeightWithShow:dict];
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if ([self.delegate respondsToSelector:@selector(didClickCell:ofData:type:)]) {
        UITableViewCell* cell = [tableView cellForRowAtIndexPath:indexPath];
        NSDictionary* data = self.resultArray[indexPath.row];
        self.clickedData = data;
        [self.delegate didClickCell:cell ofData:data type:self.type];
    }
}
- (void)refreshClickedData
{
    if (self.clickedData) {
        NSUInteger row = [self.resultArray indexOfObject:self.clickedData];
        NSIndexPath* indexPath = [NSIndexPath indexPathForRow:row inSection:0];
        QSBigImageTableViewCell* cell = (QSBigImageTableViewCell*)[self.view cellForRowAtIndexPath:indexPath];
        [cell bindWithDict:self.clickedData];
        self.clickedData = nil;
    }
}

#pragma mark - QSBigImageTableViewCellDelegate
- (void)rebindData:(NSDictionary*)dict
{
    NSIndexPath* indexPath = [NSIndexPath indexPathForRow:[self.resultArray indexOfObject:dict] inSection:0];
    QSBigImageTableViewCell* cell = (QSBigImageTableViewCell*)[self.view cellForRowAtIndexPath:indexPath];
    [cell bindWithDict:dict];
}
- (void)clickDetailBtn:(QSBigImageTableViewCell *)cell
{
    NSIndexPath* indexPath = [self.view indexPathForRowAtPoint:cell.center];;
    NSDictionary* dict = self.resultArray[indexPath.row];
    if ([self.delegate respondsToSelector:@selector(clickDetailOfDict:type:)]) {
        [self.delegate clickDetailOfDict:dict type:self.type];
    }
}
- (void)clickLikeBtn:(QSBigImageTableViewCell *)cell
{
    NSIndexPath* indexPath = [self.view indexPathForRowAtPoint:cell.center];
    NSDictionary* dict = self.resultArray[indexPath.row];
    if ([self.delegate respondsToSelector:@selector(clickLikeOfDict:type:)]) {
        [self.delegate clickLikeOfDict:dict type:self.type];
    }
}
@end

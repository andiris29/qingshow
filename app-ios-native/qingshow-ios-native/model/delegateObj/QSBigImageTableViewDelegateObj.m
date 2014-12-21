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
    NSDictionary* dict = self.resultArray[indexPath.row];
    [cell bindWithDict:dict];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}


#pragma mark - UITableView Delegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary* dict = self.resultArray[indexPath.row];
    return [QSBigImageTableViewCell getHeighWithShow:dict];
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if ([self.delegate respondsToSelector:@selector(didClickCell:ofData:)]) {
        UITableViewCell* cell = [tableView cellForRowAtIndexPath:indexPath];
        NSDictionary* data = self.resultArray[indexPath.row];
        [self.delegate didClickCell:cell ofData:data];
    }
}

@end

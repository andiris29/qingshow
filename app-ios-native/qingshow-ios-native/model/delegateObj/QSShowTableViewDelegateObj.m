//
//  QSShowTableViewDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/14/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSShowTableViewDelegateObj.h"

@implementation QSShowTableViewDelegateObj

#pragma mark - Override

- (void)registerCell
{
    [self.tableView registerNib:[UINib nibWithNibName:@"QSShowTableViewCell" bundle:nil] forCellReuseIdentifier:@"QSShowTableViewCell"];
}
#pragma mark - UITableView DataSource
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSShowTableViewCell* cell = (QSShowTableViewCell*)[tableView dequeueReusableCellWithIdentifier:@"QSShowTableViewCell" forIndexPath:indexPath];
    cell.type = self.type;
    NSDictionary* dict = self.resultArray[indexPath.row];
    [cell bindWithShow:dict];
    return cell;
}


#pragma mark - UITableView Delegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary* dict = self.resultArray[indexPath.row];
    return [QSShowTableViewCell getHeighWithShow:dict];
}

@end

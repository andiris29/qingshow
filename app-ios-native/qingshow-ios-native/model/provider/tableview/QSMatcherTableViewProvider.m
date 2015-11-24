//
//  QSMatcherTableViewProvider.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSMatcherTableViewProvider.h"
#import "QSMatcherTableViewCell.h"
@implementation QSMatcherTableViewProvider
- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSMatcherTableViewCell" bundle:nil] forCellReuseIdentifier:QSMatcherTableViewCellId];
}

#pragma mark - Table View
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSMatcherTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:QSMatcherTableViewCellId forIndexPath:indexPath];
    NSDictionary* dict = self.resultArray[indexPath.row];
    [cell bindWithDict:dict];
//    cell.delegate = self;
    return cell;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return QSMatcherTableViewCellHeight;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
}
@end

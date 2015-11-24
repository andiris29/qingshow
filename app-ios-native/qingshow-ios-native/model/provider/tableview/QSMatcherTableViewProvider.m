//
//  QSMatcherTableViewProvider.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSMatcherTableViewProvider.h"
#import "QSMatcherTableViewCell.h"
#import "NSDictionary+QSExtension.h"
#import "QSDateUtil.h"

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
    NSDictionary* dict = self.resultArray[indexPath.row];
    NSNumber* hour = [dict numberValueForKeyPath:@"hour"];
    NSDate* date = [dict dateValueForKeyPath:@"date"];
    NSString* dateStr = [QSDateUtil buildDateStringFromDate:date];
    if (!dateStr) {
        return;
    }
    NSString* fullStr = [NSString stringWithFormat:@"%@ %d:00:00", dateStr, hour.intValue];
    NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    [dateFormatter setTimeZone:[NSTimeZone timeZoneForSecondsFromGMT:0]];
    NSDate* retDate = [dateFormatter dateFromString:fullStr];
    if ([self.delegate respondsToSelector:@selector(provider:didClickDate:)]) {
        [self.delegate provider:self didClickDate:retDate];
    }
    
}
@end

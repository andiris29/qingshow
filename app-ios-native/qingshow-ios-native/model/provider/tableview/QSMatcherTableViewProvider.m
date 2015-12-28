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
#define w ([UIScreen mainScreen].bounds.size.width)
#define h ([UIScreen mainScreen].bounds.size.height)

@interface QSMatcherTableViewProvider() <QSMatcherTableViewCellDelegate>

@end

@implementation QSMatcherTableViewProvider
@dynamic delegate;

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
    cell.delegate = self;
    cell.contentView.transform = CGAffineTransformMakeScale(w / 320, w / 320);
    return cell;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary* dict = self.resultArray[indexPath.row];

    NSString* stickyUrl = [dict stringValueForKeyPath:@"data.stickyShow.stickyCover"];
    if (stickyUrl) {
        return (QSMatcherTableViewCellHeight + QSMatcherTableViewCellStickyImageHeight) * w / 320;
    } else {
        return QSMatcherTableViewCellHeight * w / 320;
    }
    

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
    
    NSDate* retDate = [date dateByAddingTimeInterval:hour.intValue * 60 * 60];
    if ([self.delegate respondsToSelector:@selector(provider:didClickDate:)]) {
        [self.delegate provider:self didClickDate:retDate];
    }
    
}

- (void)cell:(QSMatcherTableViewCell*)cell didClickUser:(NSDictionary*)dict {
    if ([self.delegate respondsToSelector:@selector(provider:didClickPeople:)]) {
        [self.delegate provider:self didClickPeople:dict];
    }
}
@end

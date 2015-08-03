//
//  QSCommentUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSEntityUtil.h"
#import "QSCommentUtil.h"
#import "QSDateUtil.h"
#import "NSDate+QSExtension.h"

@implementation QSCommentUtil
+ (NSString*)getContent:(NSDictionary*)commentDict
{
    if ([QSEntityUtil checkIsNil:commentDict]) {
        return nil;
    }
    return commentDict[@"comment"];
}
+ (NSDictionary*)getPeople:(NSDictionary*)commentDict
{
    if ([QSEntityUtil checkIsNil:commentDict]) {
        return nil;
    }
    id a = commentDict[@"authorRef"];
    if ([a isKindOfClass:[NSDictionary class]]) {
        return a;
    }
    return nil;
}
+ (NSDictionary*)getShow:(NSDictionary*)commentDict
{
    if ([QSEntityUtil checkIsNil:commentDict]) {
        return nil;
    }
    return commentDict[@"showRef"];
}

+ (NSString*)getFormatedDateString:(NSDictionary*)commentDict
{
    if ([QSEntityUtil checkIsNil:commentDict]) {
        return nil;
    }
    NSString* dateStr =  [QSEntityUtil getStringValue:commentDict keyPath:@"create"];
    NSDate* date = [QSDateUtil buildDateFromResponseString:dateStr];
    
    if ([date isToday]) {
        return [QSDateUtil getTime:date];
    }else if ([date isYesterday]){
        NSString *timeStr = [NSString stringWithFormat:@"昨天 %@",[QSDateUtil getTime:date]];
        return timeStr;
    }else {
        NSString *currentDateStr = [QSDateUtil getMonthAndDate:date];
        return currentDateStr;
    }
}

@end

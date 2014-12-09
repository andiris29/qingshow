//
//  QSCommentUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSCommentUtil.h"
#import "QSDateUtil.h"

@implementation QSCommentUtil
+ (NSString*)getContent:(NSDictionary*)commentDict
{
    return commentDict[@"comment"];
}
+ (NSDictionary*)getPeople:(NSDictionary*)commentDict
{
    id a = commentDict[@"authorRef"];
    if ([a isKindOfClass:[NSDictionary class]]) {
        return a;
    }
    return nil;
}
+ (NSDictionary*)getShow:(NSDictionary*)commentDict
{
    return commentDict[@"showRef"];
}

+ (NSString*)getFormatedDateString:(NSDictionary*)commentDict
{
    NSString* dateStr = commentDict[@"create"];
    NSDate* date = [QSDateUtil buildDateFromResponseString:dateStr];
    NSString* currentDateStr = [QSDateUtil buildStringFromDate:date];
    return currentDateStr;
}
@end

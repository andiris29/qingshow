//
//  QSChosenUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/6/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSChosenUtil.h"
#import "QSCommonUtil.h"
#import "QSDateUtil.h"
@implementation QSChosenUtil

+ (QSChosenRefType)getChosenRefType:(NSDictionary*)chosen
{
    if (![QSCommonUtil checkIsDict:chosen]) {
        return QSChosenRefTypeUnknown;
    }
    NSString* refCollection = chosen[@"refCollection"];
    if ([refCollection isEqualToString:@"shows"]) {
        return QSChosenRefTypeShow;
    } else if ([refCollection isEqualToString:@"items"]) {
        return QSChosenRefTypeItem;
    } else if ([refCollection isEqualToString:@"previews"]) {
        return QSChosenRefTypePreview;
    } else {
        return QSChosenRefTypeUnknown;
    }

}

+ (NSDictionary*)getRef:(NSDictionary*)chosen
{
    if (![QSCommonUtil checkIsDict:chosen]) {
        return nil;
    }
    return chosen[@"ref"];
}

+ (NSDate*)getChosenDate:(NSDictionary*)chosen
{
    if (![QSCommonUtil checkIsDict:chosen]) {
        return nil;
    }
    NSString* dateStr = chosen[@"date"];
    return [QSDateUtil buildDateFromResponseString:dateStr];
}
@end

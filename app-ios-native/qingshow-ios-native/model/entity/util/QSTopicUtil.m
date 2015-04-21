//
//  QSTopicUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/31/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCommonUtil.h"
#import "QSTopicUtil.h"

@implementation QSTopicUtil
+ (NSString*)getTitle:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"title"];
}

+ (NSString*)getSubTitle:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"subtitle"];
}

+ (NSURL*)getCoverUrl:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    return [NSURL URLWithString:dict[@"cover"]];
}

+ (NSURL*)getHorizontalCoverUrl:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    return [NSURL URLWithString:dict[@"horizontalCover"]];
}
+ (NSString*)getShowNumberDesc:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    NSArray* showArray = dict[@"showRefs"];
    if (![QSCommonUtil checkIsArray:showArray]) {
        return nil;
    }
    return [NSString stringWithFormat:@"|%ld款搭配|", showArray.count];
}
@end

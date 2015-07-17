//
//  QSPromotionUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/25/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSPromotionUtil.h"
#import "QSCommonUtil.h"

@implementation QSPromotionUtil


+ (NSString*)getHint:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"hint"];
}
+ (NSString*)getDescription:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"description"];
}
+ (NSNumber*)getCriteria:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"criteria"];
}
+ (BOOL)getIsEnabled:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return NO;
    }
    NSNumber* n = dict[@"enabled"];
    if ([QSCommonUtil checkIsNil:n]) {
        return NO;
    } else {
        return n.boolValue;
    }
}
@end

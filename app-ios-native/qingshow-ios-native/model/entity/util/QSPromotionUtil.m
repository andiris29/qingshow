//
//  QSPromotionUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/25/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSPromotionUtil.h"
#import "QSEntityUtil.h"

@implementation QSPromotionUtil


+ (NSString*)getHint:(NSDictionary*)dict
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"hint"];
}
+ (NSString*)getDescription:(NSDictionary*)dict
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"description"];
}
+ (NSNumber*)getCriteria:(NSDictionary*)dict
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"criteria"];
}
+ (BOOL)getIsEnabled:(NSDictionary*)dict
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return NO;
    }
    NSNumber* n = dict[@"enabled"];
    if ([QSEntityUtil checkIsNil:n]) {
        return NO;
    } else {
        return n.boolValue;
    }
}
@end

//
//  QSOrderUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/22/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSOrderUtil.h"
#import "QSCommonUtil.h"
@implementation QSOrderUtil
+ (NSDictionary*)getItemSnapshot:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"itemSnapshot"];
}
+ (NSString*)getPriceDesc:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    NSNumber* num = dict[@"price"];
    return [NSString stringWithFormat:@"%.2f", num.doubleValue];
}
+ (NSString*)getQuantityDesc:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    NSNumber* num = dict[@"quantity"];
    return num.stringValue;
}
+ (NSString*)getSkuId:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    NSString* num = dict[@"selectedItemSkuId"];

    return num;
}
+ (NSString*)getReceiverUuid:(NSDictionary*)dict;
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"selectedPeopleReceiverUuid"];
}
@end

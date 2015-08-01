//
//  QSOrderUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/22/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSOrderUtil.h"
#import "NSDictionary+QSExtension.h"

@implementation QSOrderUtil
+ (NSDictionary*)getItemSnapshot:(NSDictionary*)dict
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"itemSnapshot"];
}
+ (NSArray*)getSkuProperties:(NSDictionary*)dict {
    return [dict arrayValueForKeyPath:@"selectedSkuProperties"];
}
+ (NSString*)getExpectedPriceDesc:(NSDictionary*)dict {
    NSNumber* price = [self getExpectedPrice:dict];
    return [NSString stringWithFormat:@"%.2f", price.doubleValue];
}

+ (NSNumber*)getExpectedPrice:(NSDictionary*)dict {
    return [dict numberValueForKeyPath:@"expectedPrice"];
}

+ (NSString*)getActualPriceDesc:(NSDictionary*)dict {
    NSNumber* price = [self getActualPrice:dict];
    return [NSString stringWithFormat:@"%.2f", price.doubleValue];
}

+ (NSNumber*)getActualPrice:(NSDictionary*)dict {
    return [dict numberValueForKeyPath:@"actualPrice"];
}

+ (NSString*)getReceiverUuid:(NSDictionary*)dict;
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return nil;
    }
    return [dict stringValueForKeyPath:@"selectedPeopleReceiverUuid"];
}

+ (NSNumber*)getQuantity:(NSDictionary*)dict {
    return [dict numberValueForKeyPath:@"quantity"];
}
+ (NSString*)getQuantityDesc:(NSDictionary*)dict {
    NSNumber* quantity = [self getQuantity:dict];
    return quantity.stringValue;
}
@end

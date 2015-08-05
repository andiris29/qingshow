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
+ (NSString *)getSizeText:(NSDictionary *)dict
{
    NSArray *array = [self getSkuProperties:dict];
    if (!array.count) {
        return nil;
    }
    NSString *str = [array firstObject];
    if ([str containsString:@"尺码"]) {
        return str;
    }
    return [NSString stringWithFormat:@"尺码%@",str];
}
+ (NSString *)getColorText:(NSDictionary *)dict
{
    NSArray *array = [self getSkuProperties:dict];
    if (!array.count) {
        return nil;
    }
    NSString *str = [array lastObject];
    if ([str containsString:@"颜色"]) {
        return str;
    }
    return [NSString stringWithFormat:@"颜色：%@",str];

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
+ (NSNumber*)getTotalFee:(NSDictionary*)dict {
    NSNumber* price = [self getActualPrice:dict];
    if (!price) {
        price = [self getExpectedPrice:dict];
    }
    NSNumber* quantity = [self getQuantity:dict];
    return @(price.doubleValue * quantity.intValue);
}
@end

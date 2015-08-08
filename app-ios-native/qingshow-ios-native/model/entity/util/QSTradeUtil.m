//
//  QSTradeUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/22/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSTradeUtil.h"
#import "QSEntityUtil.h"
#import "QSDateUtil.h"
#import "QSTradeStatus.h"
#import "NSDictionary+QSExtension.h"

@implementation QSTradeUtil


+ (NSDictionary *)getPeopleDic:(NSDictionary *)dict
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"peopleSnapshot"];
}

+ (NSString*)getCreateDateDesc:(NSDictionary*)dict
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return nil;
    }

    NSString* resDateStr = [dict stringValueForKeyPath:@"create"];
    NSDate* date = [QSDateUtil buildDateFromResponseString:resDateStr];
    return [QSDateUtil buildStringFromDate:date];
}
+ (NSString *)getDayDesc:(NSDictionary *)dict
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return nil;
    }
    NSString *resDateStr = [dict stringValueForKeyPath:@"create"];
    NSDate *date  = [QSDateUtil buildDateFromResponseString:resDateStr];
    return [QSDateUtil buildDayStringFromDate:date];
}
+ (NSNumber*)getStatus:(NSDictionary*)dict
{
    return [dict numberValueForKeyPath:@"status"];
}
+ (NSString*)getStatusDesc:(NSDictionary*)dict
{
    NSNumber* status = [self getStatus:dict];
    return QSTradeStatusToDesc(status.integerValue);
}

+ (NSString*)getWechatPrepayId:(NSDictionary*)dict
{
    return [dict stringValueForKeyPath:@"pay.weixin.prepayid"];
}

+ (NSString*)getTotalFeeDesc:(NSDictionary*)dict {
    NSNumber* num = [QSEntityUtil getNumberValue:dict keyPath:@"totalFee"];;
    if (num) {
        return num.stringValue;
    } else {
        return @"";
    }
}
+ (NSString*)getTradeLogisticCompany:(NSDictionary*)dict
{
    return [dict stringValueForKeyPath:@"logistic.company"];
}
+ (NSString*)getTradeLogisticId:(NSDictionary*)dict
{
    return [dict stringValueForKeyPath:@"logistic.trackingId"];
}
+ (BOOL)getTraddSharedByCurrentUser:(NSDictionary*)dict
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return NO;
    }
    NSNumber *n =  [QSEntityUtil getNumberValue:dict keyPath:@"__context.sharedByCurrentUser"];
    if ([QSEntityUtil checkIsNil:n]) {
        return NO;
    }
    return n.boolValue;
}

#pragma mark - Order

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
    if ([str hasSuffix:@":"]) {
        str = [str substringToIndex:str.length-1];
    }
    if ([str containsString:@"尺码"]) {
        return str;
    }else{
        return [NSString stringWithFormat:@"尺码%@",str];
    }
}
+ (NSString *)getColorText:(NSDictionary *)dict
{
    NSArray *array = [self getSkuProperties:dict];
    if (!array.count) {
        return nil;
    }
    NSString *str = [array lastObject];
    if ([str hasSuffix:@":"]) {
        str = [str substringToIndex:str.length-1];
    }
    if ([str containsString:@"颜色"]) {
        return str;
    }else{
        return [NSString stringWithFormat:@"颜色%@",str];
    }
    
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

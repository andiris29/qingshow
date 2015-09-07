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
#import "QSItemUtil.h"
#import "QSTradeStatus.h"
#import "NSDictionary+QSExtension.h"

@implementation QSTradeUtil

+ (NSString *)getTradeId:(NSDictionary *)dict
{
    return [QSEntityUtil getStringValue:dict keyPath:@"_id"];
}

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

+ (BOOL)getShouldShare:(NSDictionary*)dict {
    BOOL shareToPay = [self getShareToPay:dict];
    if (shareToPay) {
        BOOL hasShared = [self getTradeSharedByCurrentUser:dict];
        return !hasShared;
    }
    return NO;
}

+ (BOOL)getShareToPay:(NSDictionary*)dict {
    NSNumber* n = [dict numberValueForKeyPath:@"shareToPay"];
    if (n) {
        return n.boolValue;
    }
    return NO;
}

+ (BOOL)getTradeSharedByCurrentUser:(NSDictionary*)dict
{
    NSNumber* n = [dict numberValueForKeyPath:@"__context.sharedByCurrentUser"];
    if (n) {
        return n.boolValue;
    }
    return NO;
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
//    NSArray *array = [self getSkuProperties:dict];
//    if (!array.count) {
//        return nil;
//    }
//    NSString *str = nil;
//    if (array.count == 1) {
//        str = array[0];
//        NSArray *strArray = [str componentsSeparatedByString:str];
// 
//    }
//    else {
//        str = [array objectAtIndex:array.count-2];
//    }
//    if ([str hasSuffix:@":"]) {
//        str = [str substringToIndex:str.length-1];
//    }
//    if ([str containsString:@"尺码"]) {
//        return str;
//    }else{
//        return [NSString stringWithFormat:@"规格%@",str];
//    }
    NSArray *array = [self getSkuProperties:dict];
    NSMutableString *arrayStr = [[NSMutableString alloc]init];;
    for (int i = 0; i < array.count; i++) {
        NSString *str = (NSString *)array[i];
        [arrayStr appendString:str];
        [arrayStr appendString:@" "];
    }
    NSArray *s = [arrayStr componentsSeparatedByString:@":"];
    NSMutableString *resultStr = [@"" mutableCopy];
    for (int i = 0; i < s.count; i ++) {
        NSString *string = (NSString *)s[i];
        [resultStr appendString:string];
        [resultStr appendString:@" "];
    }

    if ([resultStr rangeOfString:@"颜色"].location != NSNotFound) {
        NSRange range = [resultStr rangeOfString:@"颜色"];
        [resultStr deleteCharactersInRange:range];
    }
    if ([resultStr rangeOfString:@"尺码"].location != NSNotFound) {
        NSRange range = [resultStr rangeOfString:@"尺码"];
        [resultStr deleteCharactersInRange:range];
    }
    if (resultStr.length) {
        return [NSString stringWithFormat:@"规格:%@",resultStr];
    }
    else
    {
        return nil;
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
    
    if ([str rangeOfString:@"颜色"].location != NSNotFound) {
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
+ (NSNumber*)getItemExpectablePrice:(NSDictionary*)dict {
    NSNumber* n = [dict numberValueForKeyPath:@"__context.item.expectablePrice"];
    if (!n) {
        NSString* s = [dict stringValueForKeyPath:@"__context.item.expectablePrice"];
        if (s) {
            n = @(s.doubleValue);
        }

    }
    return n;
}

+ (NSString*)calculateDiscountDescWithPrice:(NSNumber*)targetPrice trade:(NSDictionary*)trade {
    
    NSNumber* price = [QSItemUtil getPromoPrice:[self getItemSnapshot:trade]];
    int disCount = targetPrice.doubleValue * 100 / price.doubleValue;
    if (disCount < 10) {
        disCount = 10;
    }else if(disCount > 90)
    {
        disCount = 90;
    }else
    {
        if (disCount%10 > 5) {
            disCount = (disCount/10+1)*10;
        }
    }
    disCount = disCount/10;
    return [NSString stringWithFormat:@"%d折", disCount];
}
@end

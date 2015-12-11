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

+ (NSString*)getWechatPrepayId:(NSDictionary*)dict
{
    return [dict stringValueForKeyPath:@"pay.weixin.prepayid"];
}


+ (NSString*)getTradeLogisticCompany:(NSDictionary*)dict
{
    return [dict stringValueForKeyPath:@"logistic.company"];
}
+ (NSString*)getTradeLogisticId:(NSDictionary*)dict
{
    return [dict stringValueForKeyPath:@"logistic.trackingId"];
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

+ (NSString *)getItemId:(NSDictionary *)dict
{
    return [QSEntityUtil getStringValue:dict keyPath:@"itemRef"];
}
+ (NSDictionary *)getItemDic:(NSDictionary *)dict
{
    return [QSEntityUtil getDictValue:dict keyPath:@"itemRef"];
}
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

+ (NSString *)getPropertiesFullDesc:(NSDictionary *)dict {
    NSArray *array = [self getSkuProperties:dict];
    NSMutableString *arrayStr = [[NSMutableString alloc]init];;
    for (int i = 0; i < array.count; i++) {
        if (i != 0) {
            [arrayStr appendString:@"\n"];
        }
        NSString *str = (NSString *)array[i];
        [arrayStr appendString:str];
    }
    NSArray *s = [arrayStr componentsSeparatedByString:@":"];
    NSMutableString *resultStr = [@"" mutableCopy];
    for (int i = 0; i < s.count; i ++) {
        if (i != 0) {
            [resultStr appendString:@" "];
        }
        NSString *string = (NSString *)s[i];
        [resultStr appendString:string];
    }
    return resultStr;
}
+ (NSString *)getPropertiesDesc:(NSDictionary *)dict
{
    NSMutableString *resultStr = [[self getPropertiesFullDesc:dict] mutableCopy];
    

    if ([resultStr rangeOfString:@"颜色"].location != NSNotFound) {
        NSRange range = [resultStr rangeOfString:@"颜色"];
        [resultStr deleteCharactersInRange:range];
    }
    if ([resultStr rangeOfString:@"尺码"].location != NSNotFound) {
        NSRange range = [resultStr rangeOfString:@"尺码"];
        [resultStr deleteCharactersInRange:range];
    }
    if ([resultStr rangeOfString:@"尺寸"].location != NSNotFound) {
        NSRange range = [resultStr rangeOfString:@"尺寸"];
        [resultStr deleteCharactersInRange:range];
    }
    if (resultStr.length) {
        return [NSString stringWithFormat:@"规格: %@",resultStr];
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

+ (NSString*)getTotalFeeDesc:(NSDictionary*)dict {
    NSNumber* num = [QSEntityUtil getNumberValue:dict keyPath:@"totalFee"];
    if (num) {
        return [NSString stringWithFormat:@"%.2f", num.doubleValue];
    } else {
        return @"";
    }
}

+ (NSNumber*)getTotalFee:(NSDictionary*)dict {
    return [QSEntityUtil getNumberValue:dict keyPath:@"totalFee"];;
}

+ (NSNumber*)getPrice:(NSDictionary*)dict {
    NSNumber* totalFee = [self getTotalFee:dict];
    NSNumber* quantity = [self getQuantity:dict];
    return [NSNumber numberWithDouble:totalFee.doubleValue / quantity.doubleValue];
}
+ (NSString*)getPriceDesc:(NSDictionary*)dict {
    NSNumber* num = [self getPrice:dict];
    if (num) {
        return [NSString stringWithFormat:@"%.2f", num.doubleValue];
    } else {
        return @"";
    }
}

+ (NSString*)calculateDiscountDescWithPrice:(NSNumber*)targetPrice trade:(NSDictionary*)trade {
    NSNumber* price = [QSItemUtil getPromoPrice:[self getItemSnapshot:trade]];
    int disCount = (int)(targetPrice.doubleValue * 10 / price.doubleValue + 0.5);
    disCount = disCount < 1 ? 1 : disCount;
    disCount = disCount > 9 ? 9 : disCount;
    return [NSString stringWithFormat:@"%d折", disCount];
}

+ (NSString*)getPromoterId:(NSDictionary*)dict {
    return [dict stringValueForKeyPath:@"promoterRef"];
}

@end

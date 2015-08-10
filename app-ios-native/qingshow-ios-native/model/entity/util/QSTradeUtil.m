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

+ (NSString *)getOrderId:(NSDictionary *)dict
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"_id"];
}
+ (NSArray*)getOrderArray:(NSDictionary*)dict
{
    return [dict arrayValueForKeyPath:@"orders"];
}
+ (NSDictionary*)getFirstOrder:(NSDictionary*)dict {
    NSArray* orders = [self getOrderArray:dict];
    if (orders && orders.count) {
        return orders[0];
    }
    return nil;
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
@end

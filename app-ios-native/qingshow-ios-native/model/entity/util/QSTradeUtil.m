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

@implementation QSTradeUtil
+ (NSArray*)getOrderArray:(NSDictionary*)dict
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"orders"];
}
+ (NSString*)getCreateDateDesc:(NSDictionary*)dict
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return nil;
    }
    NSString* resDateStr = dict[@"create"];
    NSDate* date = [QSDateUtil buildDateFromResponseString:resDateStr];
    return [QSDateUtil buildStringFromDate:date];
}
+ (NSNumber*)getStatus:(NSDictionary*)dict
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"status"];
}
+ (NSString*)getStatusDesc:(NSDictionary*)dict
{
    NSNumber* status = [self getStatus:dict];
    return QSTradeStatusToDesc(status.integerValue);
}

+ (NSString*)getWechatPrepayId:(NSDictionary*)dict
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"pay"][@"weixin"][@"prepayid"];
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
    if (![QSEntityUtil checkIsDict:dict]) {
        return nil;
    }
    NSString *str = dict[@"logistic"][@"company"];
    if ([QSEntityUtil checkIsNil:str]) {
        return nil;
    }
    return str;
}
+ (NSString*)getTradeLogisticId:(NSDictionary*)dict
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return nil;
    }
    NSString *str = dict[@"logistic"][@"trackingId"];
    if ([QSEntityUtil checkIsNil:str]) {
        return nil;
    }
    return str;
}
+ (BOOL)getTraddSharedByCurrentUser:(NSDictionary*)dict
{
    if (![QSEntityUtil checkIsDict:dict]) {
        return NO;
    }
    NSNumber *n =  [QSEntityUtil getNumberValue:dict keyPath:@"tradeContext.sharedByCurrentUser"];
    if ([QSEntityUtil checkIsNil:n]) {
        return NO;
    }
    return n.boolValue;
}
@end

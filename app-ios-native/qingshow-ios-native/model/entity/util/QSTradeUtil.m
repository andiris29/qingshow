//
//  QSTradeUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/22/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSTradeUtil.h"
#import "QSCommonUtil.h"
#import "QSDateUtil.h"
#import "QSTradeStatus.h"

@implementation QSTradeUtil
+ (NSArray*)getOrderArray:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"orders"];
}
+ (NSString*)getCreateDateDesc:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    NSString* resDateStr = dict[@"create"];
    NSDate* date = [QSDateUtil buildDateFromResponseString:resDateStr];
    return [QSDateUtil buildStringFromDate:date];
}
+ (NSNumber*)getStatus:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
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
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"pay"][@"weixin"][@"prepayid"];
}

+ (NSString*)getTotalFeeDesc:(NSDictionary*)dict {
    NSNumber* num = [QSCommonUtil getNumberValue:dict key:@"totalFee"];;
    if (num) {
        return num.stringValue;
    } else {
        return @"";
    }
}
@end

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
    NSArray* statusStrArray = @[
                                @"等待买家付款",
                                @"等待倾秀代购",
                                @"等待卖家发货",
                                @"卖家已发货",
                                @"买家已签收",
                                @"交易成功",
                                @"申请退货中",
                                @"退货中",
                                @"退款中",
                                @"退款成功",
                                @"退款失败"
                               ];
    return statusStrArray[status.integerValue];
}
@end

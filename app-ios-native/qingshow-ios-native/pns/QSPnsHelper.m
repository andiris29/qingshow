//
//  QSPnsHelper.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/19/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSPnsHelper.h"
#import "QSEntityUtil.h"
#import "QSPnsNotificationName.h"
#import "NSDictionary+QSExtension.h"
#import "QSUnreadManager.h"

#define kPnsCommandNewShowComments @"newShowComments"
#define kPnsCommandNewRecommandations @"newRecommandations"
#define kPnsCommandItemExpectablePriceUpdated @"itemExpectablePriceUpdated"
#define kPnsCommandTradeInitialized @"tradeInitialized"
#define kPnsCommandTradeShipped @"tradeShipped"
#define kPnsCommandNewBonus @"newBonus"
#define kPnsCommandBonusWithdrawComplete @"bonusWithdrawComplete"
#define kPnsTradeRefundComplete @"tradeRefundComplete"


@implementation QSPnsHelper
+ (BOOL)isFromBackground:(NSDictionary*)userInfo {
    NSNumber* bg = [userInfo numberValueForKeyPath:@"fromBackground"];
    return bg.boolValue;
}

+ (void)handlePnsData:(NSDictionary*)userInfo fromBackground:(BOOL)fFromBackground {
    NSNotificationCenter* center = [NSNotificationCenter defaultCenter];
    [[QSUnreadManager getInstance] addUnread:userInfo];
    
    NSString* command = [QSEntityUtil getStringValue:userInfo keyPath:@"command"];
    
    NSMutableDictionary* userInfoDict = [@{} mutableCopy];
    userInfoDict[@"fromBackground"] = @(fFromBackground);
    
    if ([command isEqualToString:kPnsCommandNewShowComments]) {
        NSString* showId = [QSEntityUtil getStringValue:userInfo keyPath:@"_id"];
        if (showId) {
            userInfoDict[@"showId"] = showId;
        }
        
        //新评论
        [center postNotificationName:kPnsNewShowCommentsNotification object:nil userInfo:userInfoDict];
        
    } else if ([command isEqualToString:kPnsCommandNewRecommandations]) {
        //新推荐
        [center postNotificationName:kPnsNewRecommandationNotification object:nil userInfo:userInfoDict];
        
    } else if ([command isEqualToString:kPnsCommandTradeInitialized]) {
        //折扣申请成功
        [center postNotificationName:kPnsTradeInitialNotification object:nil userInfo:userInfoDict];
    } else if ([command isEqualToString:kPnsCommandTradeShipped]) {
        //订单发货
        [center postNotificationName:kPnsTradeShippedNotification object:nil userInfo:userInfoDict];
    } else if ([command isEqualToString:kPnsCommandItemExpectablePriceUpdated]) {
        //折扣有新信息
        NSString* tradeId = [QSEntityUtil getStringValue:userInfo keyPath:@"_id"];
        if (tradeId) {
            userInfoDict[@"tradeId"] = tradeId;
        }
        [center postNotificationName:kPnsItemExpectablePriceUpdatedNotification object:nil userInfo:userInfoDict];
    } else if ([command isEqualToString:kPnsCommandNewBonus]) {
        [center postNotificationName:kPnsNewBonusNotification object:nil];
    } else if ([command isEqualToString:kPnsCommandBonusWithdrawComplete]) {
        [center postNotificationName:kPnsBonusWithdrawCompleteNotification object:nil];
    } else if ([command isEqual:kPnsTradeRefundComplete]) {
        [center postNotificationName:kPnsTradeRefundCompleteNotification object:nil];
    }
}
@end

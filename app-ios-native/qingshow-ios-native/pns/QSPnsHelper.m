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

#define kPnsCommandNewShowComments @"newShowComments"
#define kPnsCommandNewRecommandations @"newRecommandations"
#define kPnsCommandQuestSharingProgress @"questSharingProgress"
#define kPnsCommandQuestSharingComplete @"questSharingComplete"
#define kPnsCommandTradeInitialized @"tradeInitialized"
#define kPnsCommandTradeShipped @"tradeShipped"
#define kPnsCommandItemExpectablePriceUpdated @"itemExpectablePriceUpdated"

@implementation QSPnsHelper
+ (BOOL)isFromBackground:(NSDictionary*)userInfo {
    NSNumber* bg = [userInfo numberValueForKeyPath:@"fromBackground"];
    return bg.boolValue;
}

+ (void)handlePnsData:(NSDictionary*)userInfo fromBackground:(BOOL)fFromBackground {
    NSNotificationCenter* center = [NSNotificationCenter defaultCenter];
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
        
    } else if ([command isEqualToString:kPnsCommandQuestSharingProgress]) {
        //搭配活动进行中
        [center postNotificationName:kPnsQuestSharingProgressNotification object:nil userInfo:userInfoDict];
        
    } else if ([command isEqualToString:kPnsCommandQuestSharingComplete]) {
        //搭配活动完成
        [center postNotificationName:kPnsQuestSharingCompleteNotification object:nil userInfo:userInfoDict];
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
    }
}
@end

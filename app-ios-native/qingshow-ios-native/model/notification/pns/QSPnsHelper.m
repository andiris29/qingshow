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
#define kPnsCommandTradeShipped @"tradeShipped"
#define kPnsCommandNewBonus @"newBonus"
#define kPnsCommandNewParticipantBonus @"newParticipantBonus"
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
        
    } else if ([command isEqualToString:kPnsCommandTradeShipped]) {
        //订单发货
        [center postNotificationName:kPnsTradeShippedNotification object:nil userInfo:userInfoDict];
    } else if ([command isEqualToString:kPnsCommandNewBonus]) {
        NSNumber* index = [userInfo numberValueForKeyPath:@"_id"];
        if (index) {
            userInfoDict[@"_id"] = index;
        }
        [center postNotificationName:kPnsNewBonusNotification object:userInfoDict];
    } else if ([command isEqualToString:kPnsCommandNewParticipantBonus]) {
        NSNumber* index = [userInfo numberValueForKeyPath:@"_id"];
        if (index) {
            userInfoDict[@"_id"] = index;
        }
        [center postNotificationName:kPnsNewParticipantBonusNotification object:userInfoDict];
    } else if ([command isEqualToString:kPnsCommandBonusWithdrawComplete]) {
        [center postNotificationName:kPnsBonusWithdrawCompleteNotification object:userInfoDict];
    } else if ([command isEqual:kPnsTradeRefundComplete]) {
        [center postNotificationName:kPnsTradeRefundCompleteNotification object:userInfoDict];
    }
}
@end

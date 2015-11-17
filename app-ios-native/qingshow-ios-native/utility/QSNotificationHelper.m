//
//  QSNotificationHelper.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/4.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSNotificationHelper.h"

@implementation QSNotificationHelper
+ (void)postScheduleToShowLoginGuideNoti {
    [[NSNotificationCenter defaultCenter] postNotificationName:kScheduleToShowLoginGuideNotificationName object:nil];
}

+ (void)postShowNewBonusVcNoti:(NSDictionary*)userInfo {
    [[NSNotificationCenter defaultCenter] postNotificationName:kShowNewBonusVcNotificationName object:nil userInfo:userInfo];
}

+ (void)postHideNewBonusVcNoti {
    [[NSNotificationCenter defaultCenter] postNotificationName:kHideNewBonusVcNotificationName object:nil];
}

+ (void)postShowNewParticipantBonusVcNoti:(NSDictionary*)userInfo {
    [[NSNotificationCenter defaultCenter] postNotificationName:kShowNewParticipantBonusVcNotificationName object:nil userInfo:userInfo];
}

+ (void)postHideNewParticipantBonusVcNoti {
    [[NSNotificationCenter defaultCenter] postNotificationName:kHideNewParticipantBonusVcNotificationName object:nil];
}

+ (void)postShowBonusListVcNotificationName {
    [[NSNotificationCenter defaultCenter] postNotificationName:kShowBonusListVcNotificatinName object:nil];
}

+ (void)postShowTradeExpectablePriceChangeVcNotiWithTradeDict:(NSDictionary*)tradeDict {
    [[NSNotificationCenter defaultCenter] postNotificationName:kShowTradeExpectablePriceChangeVcNotificationName object:nil userInfo:@{@"tradeDict" : tradeDict}];
}
+ (void)postShowTradeExpectablePriceChangeVcNoti:(NSDictionary*)userInfo {
    [[NSNotificationCenter defaultCenter] postNotificationName:kShowTradeExpectablePriceChangeVcNotificationName object:nil userInfo:userInfo];
}
+ (void)postHideTradeExpectablePriceChangeVcNoti {
    [[NSNotificationCenter defaultCenter] postNotificationName:kHideTradeExpectablePriceChangeVcNotificationName object:nil];
}


@end

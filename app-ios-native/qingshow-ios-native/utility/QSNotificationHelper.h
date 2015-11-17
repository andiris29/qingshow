//
//  QSNotificationHelper.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/4.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

#define kScheduleToShowLoginGuideNotificationName @"kScheduleToShowLoginGuideNotificationName"

#define kShowNewBonusVcNotificationName @"kShowNewBonusVcNotificationName"
#define kHideNewBonusVcNotificationName @"kHideNewBonusVcNotificationName"
#define kShowNewParticipantBonusVcNotificationName @"kShowNewParticipantBonusVcNotificationName"
#define kHideNewParticipantBonusVcNotificationName @"kHideNewParticipantBonusVcNotificationName"

#define kShowBonusListVcNotificatinName @"kShowBonusListVcNotificatinName"

#define kShowTradeExpectablePriceChangeVcNotificationName @"kShowTradeExpectablePriceChangeVcNotificationName"
#define kHideTradeExpectablePriceChangeVcNotificationName @"kHideTradeExpectablePriceChangeVcNotificationName"

@interface QSNotificationHelper : NSObject

+ (void)postScheduleToShowLoginGuideNoti;

+ (void)postShowNewBonusVcNoti:(NSDictionary*)userInfo;
+ (void)postHideNewBonusVcNoti;
+ (void)postShowNewParticipantBonusVcNoti:(NSDictionary*)userInfo;
+ (void)postHideNewParticipantBonusVcNoti;

+ (void)postShowBonusListVcNotificationName;

+ (void)postShowTradeExpectablePriceChangeVcNotiWithTradeDict:(NSDictionary*)tradeDict;
+ (void)postShowTradeExpectablePriceChangeVcNoti:(NSDictionary*)userInfo;
+ (void)postHideTradeExpectablePriceChangeVcNoti;
@end

//
//  QSRootNotificationHelper.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/2.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSRootNotificationName.h"
#import "QSRootMenuItemType.h"

@interface QSRootNotificationHelper : NSObject


+ (void)postScheduleToShowLoginGuideNoti;

+ (void)postShowNewBonusVcNoti:(NSDictionary*)userInfo;
+ (void)postHideNewBonusVcNoti;
+ (void)postShowNewParticipantBonusVcNoti:(NSDictionary*)userInfo;
+ (void)postHideNewParticipantBonusVcNoti;

+ (void)postShowBonusListVcNotificationName;

+ (void)postShowLoginPrompNoti;
+ (void)postHideLoginPrompNoti;

+ (void)postShowRootMenuNoti;
+ (void)postShowRootContentTypeNoti:(QSRootMenuItemType)type;
+ (void)postShowLatestS24VcNoti;
@end

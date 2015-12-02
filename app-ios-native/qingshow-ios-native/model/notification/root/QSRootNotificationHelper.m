//
//  QSRootNotificationHelper.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/2.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSRootNotificationHelper.h"

@implementation QSRootNotificationHelper

+ (void)postShowLoginPrompNoti {
    [[NSNotificationCenter defaultCenter] postNotificationName:kShowLoginPrompVcNotificationName object:nil];
}
+ (void)postHideLoginPrompNoti {
    [[NSNotificationCenter defaultCenter] postNotificationName:kHideLoginPrompVcNotificationName object:nil];
}

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

+ (void)postShowRootMenuNoti {
    [[NSNotificationCenter defaultCenter] postNotificationName:kRootShowMenuNotificationName object:nil];
}

+ (void)postShowRootContentTypeNoti:(QSRootMenuItemType)type {
    [[NSNotificationCenter defaultCenter] postNotificationName:kShowRootContentTypeNotificationName object:nil userInfo:@{@"type" : @(type)}];
}
@end

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

+ (void)postShowNewBonusVcNoti {
    [[NSNotificationCenter defaultCenter] postNotificationName:kShowNewBonusVcNotificationName object:nil];
}

+ (void)postHideNewBonusVcNoti {
    [[NSNotificationCenter defaultCenter] postNotificationName:kHideNewBonusVcNotificationName object:nil];
}

+ (void)postShowNewParticipantBonusVcNoti {
    [[NSNotificationCenter defaultCenter] postNotificationName:kShowNewParticipantBonusVcNotificationName object:nil];
}

+ (void)postHideNewParticipantBonusVcNoti {
    [[NSNotificationCenter defaultCenter] postNotificationName:kHideNewParticipantBonusVcNotificationName object:nil];
}

+ (void)postShowBonusListVcNotificationName {
    [[NSNotificationCenter defaultCenter] postNotificationName:kShowBonusListVcNotificatinName object:nil];
}
@end

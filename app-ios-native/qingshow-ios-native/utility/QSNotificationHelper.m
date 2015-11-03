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
@end

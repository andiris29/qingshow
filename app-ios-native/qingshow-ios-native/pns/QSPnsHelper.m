//
//  QSPnsHelper.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/19/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSPnsHelper.h"
#import "QSCommonUtil.h"
#import "QSPnsNotificationName.h"

#define kPnsCommandNewShowComments @"newShowComments"
#define kPnsCommandNewRecommandations @"newRecommandations"
#define kPnsCommandQuestSharingProgress @"questSharingProgress"
#define kPnsCommandQuestSharingComplete @"questSharingComplete"

@implementation QSPnsHelper
+ (void)handlePnsData:(NSDictionary*)userInfo {
    NSNotificationCenter* center = [NSNotificationCenter defaultCenter];

    
    NSString* command = [QSCommonUtil getStringValue:userInfo key:@"command"];
    if ([command isEqualToString:kPnsCommandNewShowComments]) {
        NSString* showId = [QSCommonUtil getStringValue:userInfo key:@"id"];
        NSDictionary* notiInfoDict = nil;
        if (showId) {
            notiInfoDict = @{@"showId" : showId};
        }
        
        //新评论
        [center postNotificationName:kPnsNewShowCommentsNotification object:nil userInfo:notiInfoDict];
        
    } else if ([command isEqualToString:kPnsCommandNewRecommandations]) {
        //新推荐
        [center postNotificationName:kPnsNewRecommandationNotification object:nil userInfo:nil];
        
    } else if ([command isEqualToString:kPnsCommandQuestSharingProgress]) {
        //搭配活动进行中
        [center postNotificationName:kPnsQuestSharingProgressNotification object:nil userInfo:nil];
        
    } else if ([command isEqualToString:kPnsCommandQuestSharingComplete]) {
        //搭配活动完成
        [center postNotificationName:kPnsQuestSharingCompleteNotification object:nil userInfo:nil];
    }
}
@end

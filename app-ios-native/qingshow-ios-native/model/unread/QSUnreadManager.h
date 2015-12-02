//
//  QSUnreadManager.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/9/26.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSRootMenuItemType.h"

#define kQSUnreadChangeNotificationName @"kQSUnreadChangeNotificationName"

typedef NS_ENUM(NSUInteger, QSUnreadTradeType) {
    QSUnreadTradeTypeTradeInitialized = 1,
    QSUnreadTradeTypeTradeShipped = 2
};

@interface QSUnreadManager : NSObject

+ (QSUnreadManager*)getInstance;

// Update
- (void)updateUnreadState:(NSArray*)unreadArray;
- (void)addUnread:(NSDictionary*)unreadDict;
- (void)removeUnread:(NSDictionary*)unreadDict;
- (NSArray*)getUnreadOfCommand:(NSString*)command;
- (NSArray*)getUnreadOfCommands:(NSArray*)commands;

//Menu Dot
- (BOOL)shouldShowDotAtMenu;
- (BOOL)shouldShowDotAtMenuItem:(QSRootMenuItemType)type;

//Recommand Dot
- (BOOL)shouldShowRecommandUnread;
- (void)clearRecommandUnread;
//Trade Dot
- (BOOL)shouldShowTradeUnreadOfType:(QSUnreadTradeType)type id:(NSString*)tradeId;
- (BOOL)shouldShowTradeUnreadOfType:(QSUnreadTradeType)type;
- (void)clearTradeUnreadOfType:(QSUnreadTradeType)type id:(NSString*)tradeId;
- (void)clearTradeUnreadId:(NSString*)tradeId;
//Bonu Dot
- (BOOL)shouldShowBonuUnread;
- (void)clearBonuUnread;
@end

//
//  QSUnreadManager.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/9/26.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSUnreadManager.h"
#import "NSDictionary+QSExtension.h"
#import "NSArray+QSExtension.h"
#import "QSNetworkKit.h"
#import "QSPeopleUtil.h"

#define _typeToStr(type) @(type).stringValue

NSString* unreadTradeTypeToCommand(QSUnreadTradeType type) {
    NSArray* commandArray = @[
                              @"itemExpectablePriceUpdated",
                              @"tradeInitialized",
                              @"tradeShipped"
                              ];
    if (type < commandArray.count) {
        return commandArray[type];
    } else {
        return @"";
    }
}

@interface QSUnreadManager ()
@property (strong, nonatomic) NSDictionary* typeToDotCommands;
@property (strong, nonatomic) NSArray* menuDotCommands;
@property (strong, nonatomic) NSArray* bonuDotCommands;

/*
 {
    create : Date
    extra : {
        command : ...,
        * : ...
    }
 }
 
 */

@property (strong, nonatomic) NSMutableArray* unreadNotis;


- (BOOL)_shouldIgnoreUnread:(NSDictionary*)dict;
@end

@implementation QSUnreadManager

- (void)didReceiveUserInfoUpdateNoti:(NSNotification*)noti {
    NSDictionary* userInfo = noti.userInfo;
    [self updateUnreadState:[QSPeopleUtil getUnreadNotifications:userInfo]];
    
}
- (instancetype)init {
    self = [super init];
    if (self) {
        self.typeToDotCommands = @{
            _typeToStr(QSRootMenuItemMy) : @[@"newRecommandations"],
            _typeToStr(QSRootMenuItemDiscount) : @[@"itemExpectablePriceUpdated", @"tradeInitialized", @"tradeShipped"]
        };
        
        NSMutableArray* commands = [@[] mutableCopy];
        for (NSArray* a in [self.typeToDotCommands allValues]) {
            [commands addObjectsFromArray:a];
        }
        self.menuDotCommands = commands;
        self.bonuDotCommands = @[@"newBonus", @"bonusWithdrawComplete"];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveUserInfoUpdateNoti:) name:kUserInfoUpdateNotification object:nil];
    }
    return self;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
+ (QSUnreadManager*)getInstance {
    static QSUnreadManager* s_mgr = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        s_mgr = [[QSUnreadManager alloc] init];
    });

    return s_mgr;
}

//根据user/get的unread结果更新未读状态
- (void)updateUnreadState:(NSArray*)unreadArray {
    self.unreadNotis = [[unreadArray filteredArrayUsingBlock:^BOOL(NSDictionary* dict) {
        return ![self _shouldIgnoreUnread:dict];
    }] mutableCopy];
    [self _postUnreadChangeNotification];
}

- (void)_addUnreadWithoutNotification:(NSDictionary*)unreadDict {
    if (![self _shouldIgnoreUnread:unreadDict]) {
        [self.unreadNotis addObject:@{
                                      @"extra" : unreadDict
                                      }];
    }

}

- (void)addUnread:(NSDictionary*)unreadDict {
    [self _addUnreadWithoutNotification:unreadDict];
    [self _postUnreadChangeNotification];
}

- (void)removeUnread:(NSDictionary*)unreadDict {
    [SHARE_NW_ENGINE userReadNotification:[unreadDict dictValueForKeyPath:@"extra"] onSucceed:nil onError:nil];
    [self.unreadNotis removeObject:unreadDict];
    [self _postUnreadChangeNotification];
}

- (NSArray*)getUnreadOfCommand:(NSString*)command {
    return [self getUnreadOfCommands:@[command]];
}

- (NSArray*)getUnreadOfCommands:(NSArray*)commands {
    return [self.unreadNotis filteredArrayUsingBlock:^BOOL(NSDictionary* dict) {
        return [commands indexOfObject:[dict stringValueForKeyPath:@"extra.command"]] != NSNotFound;
    }];
}

#pragma mark - Dot
#pragma mark Menu Dot
- (BOOL)shouldShowDotAtMenu {
    return [self _hasNotiOfCommands:self.menuDotCommands];
}

- (BOOL)shouldShowDotAtMenuItem:(QSRootMenuItemType)type {
    NSArray* v = [self.typeToDotCommands arrayValueForKeyPath:_typeToStr(type)];
    return [self _hasNotiOfCommands:v];
}

#pragma mark Recommand Dot
- (BOOL)shouldShowRecommandUnread {
    return [self _hasNotiOfCommands:@[@"newRecommandations"]];
}

- (void)clearRecommandUnread {
    [self _clearUnreadOfCommand:@[@"newRecommandations"]];
    [self _postUnreadChangeNotification];
}

#pragma mark Trade Dot
- (BOOL)shouldShowTradeUnreadOfType:(QSUnreadTradeType)type {
    NSString* command = unreadTradeTypeToCommand(type);
    NSArray* notis = [self getUnreadOfCommand:command];
    return notis.count != 0;
}
- (BOOL)shouldShowTradeUnreadOfType:(QSUnreadTradeType)type id:(NSString*)tradeId {
    NSString* command = unreadTradeTypeToCommand(type);
    NSArray* notis = [self getUnreadOfCommand:command];
    for (NSDictionary* noti in notis) {
        if ([[noti stringValueForKeyPath:@"extra._id"] isEqualToString:tradeId]) {
            return YES;
        }
    }
    return NO;
}

- (void)clearTradeUnreadId:(NSString*)tradeId {
    NSArray* commandArray = @[
                              @"itemExpectablePriceUpdated",
                              @"tradeInitialized",
                              @"tradeShipped"
                              ];
    NSArray* notis = [self getUnreadOfCommands:commandArray];
    NSMutableArray* removeArray = [@[] mutableCopy];
    for (NSDictionary* noti in notis) {
        if ([[noti stringValueForKeyPath:@"extra._id"] isEqualToString:tradeId]) {
            [removeArray addObject:noti];
        }
    }
    for (NSDictionary* noti in removeArray) {
        [self removeUnread:noti];
    }
    [self _postUnreadChangeNotification];
}

- (void)clearTradeUnreadOfType:(QSUnreadTradeType)type id:(NSString*)tradeId {
    NSString* command = unreadTradeTypeToCommand(type);
    NSArray* notis = [self getUnreadOfCommand:command];
    NSMutableArray* removeArray = [@[] mutableCopy];
    for (NSDictionary* noti in notis) {
        if ([[noti stringValueForKeyPath:@"extra._id"] isEqualToString:tradeId]) {
            [removeArray addObject:noti];
        }
    }
    for (NSDictionary* noti in removeArray) {
        [self removeUnread:noti];
    }
    [self _postUnreadChangeNotification];
}

#pragma mark Bonu Dot
- (BOOL)shouldShowBonuUnread {
    return [self _hasNotiOfCommands:self.bonuDotCommands];
}

- (void)clearBonuUnread {
    [self _clearUnreadOfCommand:self.bonuDotCommands];
    [self _postUnreadChangeNotification];
}

#pragma mark - Private
- (BOOL)_hasNotiOfCommands:(NSArray*)commands {
    if (!commands || !commands.count) {
        return NO;
    }
    for (NSDictionary* c in self.unreadNotis) {
        NSString* command = [c stringValueForKeyPath:@"extra.command"];
        if ([commands indexOfObject:command] != NSNotFound) {
            return YES;
        }
    }
    return NO;
}

- (void)_clearUnreadOfCommand:(NSArray*)commands {
    NSArray* notis = [self getUnreadOfCommands:commands];
    for (NSDictionary* noti in notis) {
        [self removeUnread:noti];
    }
}

- (BOOL)_shouldIgnoreUnread:(NSDictionary*)dict {
    NSArray* commandIgnoreUnread = @[@"newShowComments"];
    NSString* command = [dict stringValueForKeyPath:@"extra.command"];
    if ([commandIgnoreUnread indexOfObject:command] != NSNotFound) {
        return YES;
    }
    return NO;
}

- (void)_postUnreadChangeNotification {
    [[NSNotificationCenter defaultCenter] postNotificationName:kQSUnreadChangeNotificationName object:nil];
}
@end

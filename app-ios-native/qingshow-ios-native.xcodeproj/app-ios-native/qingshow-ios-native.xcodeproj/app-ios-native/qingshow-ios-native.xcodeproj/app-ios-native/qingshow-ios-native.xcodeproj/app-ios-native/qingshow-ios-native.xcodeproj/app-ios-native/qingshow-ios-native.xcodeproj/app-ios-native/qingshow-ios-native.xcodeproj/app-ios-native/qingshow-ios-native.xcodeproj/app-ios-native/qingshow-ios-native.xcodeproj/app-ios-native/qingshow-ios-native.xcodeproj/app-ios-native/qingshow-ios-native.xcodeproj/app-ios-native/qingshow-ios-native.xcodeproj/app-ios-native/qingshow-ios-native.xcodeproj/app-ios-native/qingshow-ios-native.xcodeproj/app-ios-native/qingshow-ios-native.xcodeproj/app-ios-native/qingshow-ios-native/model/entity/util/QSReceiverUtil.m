//
//  QSReceiverUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSReceiverUtil.h"
#import "QSCommonUtil.h"
@implementation QSReceiverUtil

+ (NSString*)getUuid:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    return dict[@"uuid"];
}

+ (NSString*)getName:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return @"";
    }
    return dict[@"name"];
}

+ (NSString*)getPhone:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return @"";
    }
    return dict[@"phone"];
}

+ (NSString*)getProvince:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return @"";
    }
    return dict[@"province"];
}

+ (NSString*)getAddress:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return @"";
    }
    return dict[@"address"];
}

+ (BOOL)getIsDefault:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return NO;
    }
    NSNumber* f = dict[@"isDefault"];
    return f.boolValue;
}
+ (void)setReceiver:(NSDictionary*)receiver isDefault:(BOOL)isDefault
{
    if (![QSCommonUtil checkIsDict:receiver]) {
        return;
    }
    
    if ([receiver isKindOfClass:[NSMutableDictionary class]]) {
        [(NSMutableDictionary*)receiver setValue:@(isDefault) forKey:@"isDefault"];
    }
}

+ (NSDictionary*)getDefaultReceiver:(NSArray*)receivers
{
    for (NSDictionary* dict in receivers) {
        if ([self getIsDefault:dict]) {
            return dict;
        }
    }
    if (receivers.count) {
        return receivers[0];
    }
    
    return nil;
}

+ (void)setDefaultReceiver:(NSDictionary*)receiver ofReceivers:(NSArray*)receiverList
{
    for (NSDictionary* dict in receiverList) {
        BOOL isDefault = [[self getUuid:dict] isEqualToString:[self getUuid:receiver]];
        [self setReceiver:dict isDefault:isDefault];
    }
}
@end

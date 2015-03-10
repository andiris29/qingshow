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

+ (NSString*)getDefault:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return @"";
    }
    return dict[@"default"];
}
@end

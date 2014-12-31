//
//  QSBrandUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/30/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSBrandUtil.h"
#import "QSCommonUtil.h"

@implementation QSBrandUtil

+ (NSString*)getBrandName:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    }
    if ([dict isKindOfClass:[NSDictionary class]]) {
        return dict[@"name"];
    }
    return @"";

}
+ (NSURL*)getBrandLogoUrl:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    }
    NSString* s = dict[@"logo"];
    return [NSURL URLWithString:s];
}

+ (NSString*)getBrandTypeDesc:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    }
    NSNumber* type = dict[@"type"];
    return @[@"brand", @"studio"][type.intValue];
}

+ (NSURL*)getBrandBgUrl:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    }
    NSString* path = dict[@"background"];
    return [NSURL URLWithString:path];
}


+ (NSURL*)getBrandCoverUrl:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    }
    NSString* path = dict[@"cover"];
    return [NSURL URLWithString:path];
}

+ (NSString*)getBrandShopPhone:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return @"";
    }
    
    NSDictionary* shopDict = dict[@"shopInfo"];
    if ([QSCommonUtil checkIsNil:shopDict]) {
        return @"";
    }
    return shopDict[@"phone"];
}
+ (NSString*)getBrandShopAddress:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return @"";
    }
    
    NSDictionary* shopDict = dict[@"shopInfo"];
    if ([QSCommonUtil checkIsNil:shopDict]) {
        return @"";
    }
    return shopDict[@"address"];
}

+ (BOOL)getHasFollowBrand:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return NO;
    }
    
    NSDictionary* context = dict[@"__context"];
    if (context) {
        NSNumber* f = context[@"followedByCurrentUser"];
        if (f) {
            return f.boolValue;
        }
    }
    return NO;
}
+ (void)setHasFollow:(BOOL)f brand:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return;
    }
    
    if (![dict isKindOfClass:[NSMutableDictionary class]]) {
        return;
    }
    NSMutableDictionary* mutableDict = (NSMutableDictionary*)dict;
    NSMutableDictionary* context = [mutableDict[@"__context"] mutableCopy];
    if (!context) {
        context = [@{} mutableCopy];
    }
    
    context[@"followedByCurrentUser"] = @(f);
    mutableDict[@"__context"] = context;
    
}
@end

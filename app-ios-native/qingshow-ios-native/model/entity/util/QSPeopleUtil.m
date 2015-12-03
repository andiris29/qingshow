//
//  QSModelUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSEntityUtil.h"
#import "QSPeopleUtil.h"
#import "NSNumber+QSExtension.h"
#import "NSDictionary+QSExtension.h"

@implementation QSPeopleUtil

+ (NSString*)buildModelStatusString:(NSDictionary*)modelDict
{
    NSMutableString* statusString = [@"" mutableCopy];
    NSNumber* height = [modelDict numberValueForKeyPath:@"height"];
    NSNumber* weight = [modelDict numberValueForKeyPath:@"weight"];
    if (height) {
        [statusString appendFormat:@"%@cm", height];
    }
    if (weight) {
        if (statusString.length) {
            [statusString appendFormat:@"/"];
        }
        [statusString appendFormat:@"%@kg", weight];
    }
    return statusString;
}

+ (NSString *)getPeopleId:(NSDictionary *)peopleDict
{
    if(![QSEntityUtil checkIsDict:peopleDict]){
        return nil;
    }
    return [QSEntityUtil getStringValue:peopleDict keyPath:@"_id"];
}
+ (NSString*)getNickname:(NSDictionary*)peopleDict
{
    NSString* name = [peopleDict stringValueForKeyPath:@"nickname"];
    if (!name || !name.length) {
        name = @"倾秀用户";
    }
    return name;
}

+ (NSURL*)getHeadIconUrl:(NSDictionary *)peopleDict type:(QSImageNameType)type {
    NSString* path = [peopleDict stringValueForKeyPath:@"portrait"];
    if (path && path.length) {
        return [NSURL URLWithString:[QSImageNameUtil appendImageName:path type:type]];
    } else {
        return [[NSBundle mainBundle] URLForResource:@"user_head_default@2x" withExtension:@"jpg"];
    }
    
    return nil;
}

+ (NSURL*)getHeadIconUrl:(NSDictionary*)peopleDict
{
    return [self getHeadIconUrl:peopleDict type:QSImageNameTypeOrigin];
}

+ (NSURL*)getBackgroundUrl:(NSDictionary*)peopleDict
{
    NSString* path = [peopleDict stringValueForKeyPath:@"background"];
    if (path && path.length) {
        return [NSURL URLWithString:path];
    } else {
        return [[NSBundle mainBundle] URLForResource:@"user_bg_default" withExtension:@"png"];
    }
}

+ (NSString*)getDetailDesc:(NSDictionary*)peopleDict
{
    if ([QSEntityUtil checkIsNil:peopleDict]) {
        return nil;
    }
    
    NSString* statusStr = [self buildModelStatusString:peopleDict];
    NSMutableString* m = [[NSMutableString alloc] initWithString:@""];
    if (m.length && statusStr.length) {
        [m appendString:@","];
    }
    [m appendString:statusStr];
    return m;
}


+ (NSString*)getNumberShowsDescription:(NSDictionary*)modelDict
{
    NSNumber* f = [modelDict numberValueForKeyPath:@"__context.numShows"];
    if (f) {
        return f.kmbtStringValue;
    }
    return @"0";
}

+ (NSString*)getNumberLiketoCreateShows:(NSDictionary*)peopleDict
{
    if ([QSEntityUtil checkIsNil:peopleDict]) {
        return nil;
    }
    
    NSDictionary* context = [peopleDict dictValueForKeyPath:@"__context"];
    if (context) {
        NSNumber* f = [context numberValueForKeyPath:@"numLikeToCreateShows"];
        if (f) {
            return f.kmbtStringValue;
        }
    }
    return @"0";
}

+ (NSString*)getNumberCreateShows:(NSDictionary*)peopleDict
{
    if ([QSEntityUtil checkIsNil:peopleDict]) {
        return nil;
    }
    
    NSDictionary* context =  [peopleDict dictValueForKeyPath:@"__context"];
    if (context) {
        NSNumber* f = [context numberValueForKeyPath:@"numCreateShows"];
        if (f) {
            return f.kmbtStringValue;
        }
    }
    return @"0";
}

+ (BOOL)getPeopleIsFollowed:(NSDictionary*)dict
{
    NSNumber* f = [dict numberValueForKeyPath:@"__context.followedByCurrentUser"];
    if (f) {
        return f.boolValue;
    }
    return NO;
}

+ (void)setPeople:(NSDictionary*)dict isFollowed:(BOOL)isFollowed
{
    if ([QSEntityUtil checkIsNil:dict]) {
        return;
    }
    
    if (![dict isKindOfClass:[NSMutableDictionary class]]) {
        return;
    }
    NSMutableDictionary* mutableDict = (NSMutableDictionary*)dict;
    NSMutableDictionary* context = [[QSEntityUtil getDictValue:mutableDict keyPath:@"__context"]  mutableCopy];
    if (!context) {
        context = [@{} mutableCopy];
    }

    context[@"followedByCurrentUser"] = @(isFollowed);
    mutableDict[@"__context"] = context;

}
+ (BOOL)isPeople:(NSDictionary*)l equalToPeople:(NSDictionary*)r
{
    if ([QSEntityUtil checkIsNil:l] && [QSEntityUtil checkIsNil:r]) {
        return YES;
    }
    if ([QSEntityUtil checkIsNil:l] || [QSEntityUtil checkIsNil:r]) {
        return NO;
    }
    
    if (l == r) {
        return YES;
    }
    
    NSString* lId = [QSEntityUtil getIdOrEmptyStr:l];
    NSString* rId = [QSEntityUtil getIdOrEmptyStr:r];
    return [lId isEqualToString:rId];
}

+ (NSString *)getHeight:(NSDictionary *)peopleDict
{
    NSNumber* n = [peopleDict numberValueForKeyPath:@"height"];
    if (n) {
        return n.stringValue;
    }
    return @"";
}

+ (NSString *)getWeight:(NSDictionary *)peopleDict {
    NSNumber* n = [peopleDict numberValueForKeyPath:@"weight"];
    if (n) {
        return n.stringValue;
    }
    return @"";
}


+ (NSArray*)getReceiverList:(NSDictionary*)dict
{
    return [dict arrayValueForKeyPath:@"receivers"];
}

+ (BOOL)hasPersonalizeData:(NSDictionary*)dict
{
    NSArray* necessaryKeys = @[@"age", @"height",@"weight", @"bodyType", @"dressStyle", @"expectations"];
    NSArray* keys = [dict allKeys];
    for (NSString* k in necessaryKeys) {
        if (![keys containsObject:k]) {
            return NO;
        }
    }
    return YES;
}


+ (NSString*)getBodyTypeDesc:(NSDictionary*)dict{
    NSArray* array = @[@"A型",@"H型",@"V型",@"X型"];
    NSNumber* bodyType = [dict numberValueForKeyPath:@"bodyType"];
    if (!bodyType) {
        return nil;
    }
    int type = bodyType.intValue;
    if (type < array.count) {
        return array[type];
    } else {
        return nil;
    }
}

+ (NSString*)getDressStyleDesc:(NSDictionary*)dict {
    NSArray* array = @[@"日韩系", @"欧美系"];
    
    NSNumber* dressStyle = [dict numberValueForKeyPath:@"dressStyle"];

    if ([QSEntityUtil checkIsNil:dressStyle]) {
        return nil;
    }
    int style = dressStyle.intValue;
    if (style < array.count) {
        return array[style];
    } else {
        return nil;
    }
}

+ (NSArray*)getExpectations:(NSDictionary*)dict {
    return [dict arrayValueForKeyPath:@"expectations"];
}

+ (NSString*)getExpectationsDesc:(NSDictionary*)dict {
    NSArray* array = @[@"显瘦", @"显高", @"显身材", @"遮臀部", @"遮肚腩", @"遮手臂"];
    NSArray* expectations = [self getExpectations:dict];
    if (!expectations) {
        return nil;
    }
    NSMutableString* str = [@"" mutableCopy];
    for (NSNumber* n in expectations) {
        int v = n.intValue;
        if (v < array.count) {
            [str appendFormat:@"%@ ",array[v]];
        }
    }
    return str;
}
+ (NSString*)getAge:(NSDictionary*)dict
{
    return [dict numberValueForKeyPath:@"age"].stringValue;
}

+ (NSString*)getShoulder:(NSDictionary*)dict
{
    return [dict numberValueForKeyPath:@"measureInfo.shoulder"].stringValue;

}
+ (NSString*)getBust:(NSDictionary*)dict
{
    return [dict numberValueForKeyPath:@"measureInfo.bust"].stringValue;
}
+ (NSString*)getWaist:(NSDictionary*)dict
{
    return [dict numberValueForKeyPath:@"measureInfo.waist"].stringValue;
}
+ (NSString*)getHips:(NSDictionary*)dict
{
    return [dict numberValueForKeyPath:@"measureInfo.hips"].stringValue;
}
+ (NSString*)getShoeSize:(NSDictionary*)dict
{
    return [dict numberValueForKeyPath:@"measureInfo.shoeSize"].stringValue;
}

+ (QSPeopleRole)getPeopleRole:(NSDictionary*)dict {
    NSNumber* r = [dict numberValueForKeyPath:@"role"];
    if (r && r.intValue == 0) {
        return QSPeopleRoleGuest;
    } else {
        return QSPeopleRoleUser;
    }
}

#pragma mark - Unread
+ (NSArray*)getUnreadNotifications:(NSDictionary*)peopleDict {
    return [peopleDict arrayValueForKeyPath:@"unreadNotifications"];
}


+ (NSString*)getAlipayId:(NSDictionary *)dict
{
    return [QSEntityUtil getStringValue:dict keyPath:@"alipayId"];
}

+ (BOOL)hasBindWechat:(NSDictionary*)dict {
    return [self getWechatLoginId:dict] != nil;
}

+ (NSString *)getWechatLoginId:(NSDictionary *)dict
{
    return [QSEntityUtil getStringValue:dict keyPath:@"userInfo.weixin.openid"];
}

+ (NSString *)getNameAndPswLoginId:(NSDictionary *)dict
{
    return [QSEntityUtil getStringValue:dict keyPath:@"userInfo.id"];
}


+ (BOOL)checkMobileExist:(NSDictionary *)dict
{
    if (![QSEntityUtil getStringValue:dict keyPath:@"mobile"]) {
        return NO;
    }
    return YES;
}

+ (BOOL)isTalent:(NSDictionary*)dict {
    NSNumber* f = [dict numberValueForKeyPath:@"talent"];
    if (f) {
        return f.boolValue;
    } else {
        return NO;
    }
}

+ (NSNumber*)getTotalBonus:(NSDictionary*)dict {
    NSDictionary* bonusDict = [dict dictValueForKeyPath:@"__context.bonusAmountByStatus"];
    if (bonusDict) {
        return @([dict numberValueForKeyPath:@"__context.bonusAmountByStatus.0"].floatValue + [dict numberValueForKeyPath:@"__context.bonusAmountByStatus.1"].floatValue + [dict numberValueForKeyPath:@"__context.bonusAmountByStatus.2"].floatValue);

    } else {
        return nil;
    }
}

+ (NSNumber*)getAvailableBonus:(NSDictionary*)dict {
    NSDictionary* bonusDict = [dict dictValueForKeyPath:@"__context.bonusAmountByStatus"];
    if (bonusDict) {
        return @([dict numberValueForKeyPath:@"__context.bonusAmountByStatus.0"].floatValue + [dict numberValueForKeyPath:@"__context.bonusAmountByStatus.1"].floatValue);
        
    } else {
        return nil;
    }
}
@end

//
//  QSModelUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSCommonUtil.h"
#import "QSPeopleUtil.h"
#import "NSNumber+QSExtension.h"


@implementation QSPeopleUtil

+ (NSString*)buildModelStatusString:(NSDictionary*)modelDict
{
    if ([QSCommonUtil checkIsNil:modelDict]) {
        return nil;
    }
    
    NSNumber* height = modelDict[@"height"];
    if (modelDict[@"height"] == [NSNull null]) {
        height = nil;
    }
    NSNumber* weight = modelDict[@"weight"];
    if (modelDict[@"weight"] == [NSNull null]) {
        weight = nil;
    }
    NSMutableString* statusString = [@"" mutableCopy];
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
+ (NSString*)getGenderDesc:(NSDictionary*)modelDict
{
    if ([QSCommonUtil checkIsNil:modelDict]) {
        return nil;
    }
    
    NSNumber* gender = modelDict[@"gender"];
    if (gender) {
        if (gender.intValue == 0) {
            return @"男";
        } else if (gender.intValue == 1) {
            return @"女";
        }
    }
    return @"";
}


+ (NSString*)getNickname:(NSDictionary*)peopleDict
{
    if ([QSCommonUtil checkIsNil:peopleDict]) {
        return nil;
    }
    
    NSString* name = peopleDict[@"nickname"];
    if (!name || !name.length) {
        name = @"倾秀用户";
    }
    return name;
}

+ (NSURL*)getHeadIconUrl:(NSDictionary *)peopleDict type:(QSImageNameType)type {
    if ([QSCommonUtil checkIsNil:peopleDict]) {
        return nil;
    }
    
    NSString* path = peopleDict[@"portrait"];
    if (![QSCommonUtil checkIsNil:path]) {
        return [NSURL URLWithString:[QSImageNameUtil appendImageName:path type:type]];
    } else {
        return [[NSBundle mainBundle] URLForResource:@"user_head_default" withExtension:@"png"];
    }
    
    return nil;
}

+ (NSURL*)getHeadIconUrl:(NSDictionary*)peopleDict
{
    return [self getHeadIconUrl:peopleDict type:QSImageNameTypeOrigin];
}

+ (NSURL*)getBackgroundUrl:(NSDictionary*)peopleDict
{
    if ([QSCommonUtil checkIsNil:peopleDict]) {
        return nil;
    }
    
    NSString* path = peopleDict[@"background"];
    if (![QSCommonUtil checkIsNil:path]) {
        return [NSURL URLWithString:path];
    } else {
        return [[NSBundle mainBundle] URLForResource:@"user_bg_default" withExtension:@"png"];
    }
    
    return nil;
}

+ (NSString*)getDetailDesc:(NSDictionary*)peopleDict
{
    if ([QSCommonUtil checkIsNil:peopleDict]) {
        return nil;
    }
    
    NSString* statusStr = [self buildModelStatusString:peopleDict];
    NSString* genderStr = [self getGenderDesc:peopleDict];
    NSMutableString* m = [[NSMutableString alloc] initWithString:@""];
    if (genderStr.length) {
        [m appendString:genderStr];
    }
    if (m.length && statusStr.length) {
        [m appendString:@","];
    }
    [m appendString:statusStr];
    return m;
}

+ (NSString*)getProvinceDesc:(NSDictionary*)peopleDict
{
    if ([QSCommonUtil checkIsNil:peopleDict]) {
        return @"";
    }
    NSArray* provinceArray = @[@"安徽", @"北京", @"重庆", @"福建", @"甘肃", @"广东", @"广西", @"贵州", @"海南", @"河北", @"黑龙江", @"河南", @"湖北", @"湖南", @"江苏", @"江西", @"吉林", @"辽宁", @"内蒙古", @"宁夏", @"青海", @"陕西", @"山东", @"上海", @"山西", @"四川", @"台湾", @"天津", @"新疆", @"西藏", @"云南", @"浙江"];
    
    NSNumber* provinceNum = peopleDict[@"province"];
    if ([QSCommonUtil checkIsNil:provinceNum] || provinceNum.intValue >= provinceArray.count) {
        return @"";
    }
    return provinceArray[provinceNum.intValue];
}

+ (NSString*)getNumberFollowersDescription:(NSDictionary*)modelDict
{
    if ([QSCommonUtil checkIsNil:modelDict]) {
        return nil;
    }
    
    NSDictionary* context = modelDict[@"__context"];
    if (context) {
        NSNumber* f = context[@"numFollowers"];
        if (f) {
            return f.kmbtStringValue;
        }
    }
    return @"0";
}

+ (void)addNumFollower:(long long)num forPeople:(NSDictionary*)peopleDict
{
    if ([QSCommonUtil checkIsNil:peopleDict] && ![peopleDict isKindOfClass:[NSMutableDictionary class]]) {
        return;
    }
    NSMutableDictionary* p = (NSMutableDictionary*)peopleDict;
    NSMutableDictionary* context = [peopleDict[@"__context"] mutableCopy];
    if (context) {
        NSNumber* f = context[@"numFollowers"];
        context[@"numFollowers"] = @(f.longLongValue + num);
        p[@"__context"] = context;
    }
    
}

+ (NSString*)getNumberShowsDescription:(NSDictionary*)modelDict
{
    if ([QSCommonUtil checkIsNil:modelDict]) {
        return nil;
    }
    
    NSDictionary* context = modelDict[@"__context"];
    if (context) {
        NSNumber* f = context[@"numShows"];
        if (f) {
            return f.kmbtStringValue;
        }
    }
    return @"0";
}

+ (NSString*)getNumberFollowBrands:(NSDictionary*)peopleDict
{
    if ([QSCommonUtil checkIsNil:peopleDict]) {
        return nil;
    }
    
    NSDictionary* context = peopleDict[@"__context"];
    if (context) {
        NSNumber* f = context[@"numLikeToCreateShows"];
        if (f) {
            return f.kmbtStringValue;
        }
    }
    return @"0";
}

+ (NSString*)getNumberFollowPeoples:(NSDictionary*)peopleDict
{
    if ([QSCommonUtil checkIsNil:peopleDict]) {
        return nil;
    }
    
    NSDictionary* context = peopleDict[@"__context"];
    if (context) {
        NSNumber* f = context[@"numCreateShows"];
        if (f) {
            return f.kmbtStringValue;
        }
    }
    return @"0";
}

+ (BOOL)getPeopleIsFollowed:(NSDictionary*)dict
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
+ (void)setPeople:(NSDictionary*)dict isFollowed:(BOOL)isFollowed
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

    context[@"followedByCurrentUser"] = @(isFollowed);
    mutableDict[@"__context"] = context;

}
+ (BOOL)isPeople:(NSDictionary*)l equalToPeople:(NSDictionary*)r
{
    if ([QSCommonUtil checkIsNil:l] && [QSCommonUtil checkIsNil:r]) {
        return YES;
    }
    if ([QSCommonUtil checkIsNil:l] || [QSCommonUtil checkIsNil:r]) {
        return NO;
    }
    
    if (l == r) {
        return YES;
    }
    NSString* lId = l[@"_id"];
    NSString* rId = r[@"_id"];
    return [lId isEqualToString:rId];
}

+ (NSString *)getHeight:(NSDictionary *)peopleDict
{
    if (peopleDict[@"height"] != [NSNull null]) {
        return [(NSNumber *)peopleDict[@"height"] stringValue];
    }
    return @"";
}

+ (NSString *)getWeight:(NSDictionary *)peopleDict {
    if (peopleDict[@"weight"] != [NSNull null]) {
        return [(NSNumber *)peopleDict[@"weight"] stringValue];
    }
    return @"";
}


+ (NSArray*)getReceiverList:(NSDictionary*)dict
{
    if ([QSCommonUtil checkIsNil:dict]) {
        return nil;
    }
    return dict[@"receivers"];
}

+ (BOOL)hasPersonalizeData:(NSDictionary*)dict
{
    NSArray* keys = [dict allKeys];
    return
    [keys containsObject:@"age"] &&
    [keys containsObject:@"height"] &&
    [keys containsObject:@"weight"] &&
    [keys containsObject:@"bodyType"] &&
    [keys containsObject:@"dressStyle"] &&
    [keys containsObject:@"expectations"];
}

+ (NSString*)getBodyTypeDesc:(NSDictionary*)dict{
    NSArray* array = @[@"A型",@"H型",@"V型",@"X型"];
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }

    NSNumber* bodyType = dict[@"bodyType"];
    if ([QSCommonUtil checkIsNil:bodyType]) {
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
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    NSNumber* dressStyle = dict[@"dressStyle"];
    if ([QSCommonUtil checkIsNil:dressStyle]) {
        return nil;
    }
    if ([QSCommonUtil checkIsNil:dressStyle]) {
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
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    NSArray* expectations = dict[@"expectations"];
    if ([QSCommonUtil checkIsNil:expectations]) {
        return nil;
    } else {
        return expectations;
    }
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
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    NSNumber* age = dict[@"age"];
    if ([QSCommonUtil checkIsNil:age]) {
        return nil;
    } else {
        return age.stringValue;
    }
}

+ (NSString*)getShoulder:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    NSNumber* s = [dict valueForKeyPath:@"measureInfo.shoulder"];
    if ([QSCommonUtil checkIsNil:s]) {
        return nil;
    }
    return s.stringValue;
}
+ (NSString*)getBust:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    NSNumber* s = [dict valueForKeyPath:@"measureInfo.bust"];
    if ([QSCommonUtil checkIsNil:s]) {
        return nil;
    }
    return s.stringValue;
}
+ (NSString*)getWaist:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    NSNumber* s = [dict valueForKeyPath:@"measureInfo.waist"];
    if ([QSCommonUtil checkIsNil:s]) {
        return nil;
    }
    return s.stringValue;
}
+ (NSString*)getHips:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    NSNumber* s = [dict valueForKeyPath:@"measureInfo.hips"];
    if ([QSCommonUtil checkIsNil:s]) {
        return nil;
    }
    return s.stringValue;
}
+ (NSString*)getShoeSize:(NSDictionary*)dict
{
    if (![QSCommonUtil checkIsDict:dict]) {
        return nil;
    }
    NSNumber* s = [dict valueForKeyPath:@"measureInfo.shoeSize"];
    if ([QSCommonUtil checkIsNil:s]) {
        return nil;
    }
    return s.stringValue;
}
@end

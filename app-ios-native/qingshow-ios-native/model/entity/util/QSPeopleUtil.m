//
//  QSModelUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSPeopleUtil.h"

@implementation QSPeopleUtil

+ (NSString*)buildModelStatusString:(NSDictionary*)modelDict
{
    NSNumber* height = modelDict[@"height"];
    NSNumber* weight = modelDict[@"weight"];
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
    NSNumber* gender = modelDict[@"gender"];
    if (gender) {
        if (gender.intValue == 0) {
            return @"男性";
        } else if (gender.intValue == 1) {
            return @"女性";
        }
    }
    return @"";
}

+ (NSString*)buildNumLikeString:(NSDictionary*)peopleDict
{
    NSDictionary* modelInfo = peopleDict[@"modelInfo"];
    if (modelInfo) {
        NSNumber* numLike = modelInfo[@"numLikes"];
        if (numLike) {
            return [numLike stringValue];
        }
    }
    return @"0";
}
+ (NSString*)getName:(NSDictionary*)peopleDict
{
    NSString* name = peopleDict[@"name"];
    if (!name || !name.length) {
        name = @"未命名";
    }
    return name;
}
+ (NSURL*)getHeadIconUrl:(NSDictionary*)peopleDict
{
    NSString* path = peopleDict[@"portrait"];
    if (path) {
        return [NSURL URLWithString:path];
    }
    
    return nil;
}
+ (NSString*)getDetailDesc:(NSDictionary*)peopleDict
{
    NSString* statusStr = [QSPeopleUtil buildModelStatusString:peopleDict];
    NSString* genderStr = [QSPeopleUtil getGenderDesc:peopleDict];
    NSMutableString* m = [[NSMutableString alloc] initWithString:genderStr];
    if (genderStr.length) {
        [m appendString:genderStr];
    }
    if (m.length && statusStr.length) {
        [m appendString:@","];
    }
    [m appendString:statusStr];
    return m;
}
+ (NSString*)getStatus:(NSDictionary*)modelDict
{
    if (modelDict && modelDict[@"modelInfo"] && modelDict[@"modelInfo"][@"status"]) {
        return modelDict[@"modelInfo"][@"status"];
    }
    return nil;
}
+ (NSString*)getRolesDescription:(NSDictionary*)modelDict
{
    NSArray* roles = modelDict[@"roles"];
    for (NSNumber* r in roles) {
        if (r.intValue == 1) {
            return @"模特";
        }
    }
    return @"";
}

+ (NSString*)getNumberFollowingsDescription:(NSDictionary*)modelDict
{
    NSNumber* n = modelDict[@"$numFollowed"];
    return n.stringValue;
}
+ (NSString*)getNumberFollowersDescription:(NSDictionary*)modelDict
{
    NSNumber* n = modelDict[@"$numFollowers"];
    return n.stringValue;
}
+ (NSString*)getNumberShowsDescription:(NSDictionary*)modelDict
{
    NSNumber* n = modelDict[@"$numShows"];
    return n.stringValue;
}
+ (NSString*)getNumberFavorsDescription:(NSDictionary*)modelDict
{
    NSNumber* n = modelDict[@"$numFavors"];
    return n.stringValue;
}

+ (NSString*)getNumberRecommendationsDescription:(NSDictionary*)modelDict
{
    NSNumber* n = modelDict[@"$numRecommendation"];
    return n.stringValue;
}

+ (BOOL)getPeopleIsFollowed:(NSDictionary*)dict
{
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
    if (l == r) {
        return YES;
    }
    NSString* lId = l[@"_id"];
    NSString* rId = r[@"_id"];
    return [lId isEqualToString:rId];
}
@end

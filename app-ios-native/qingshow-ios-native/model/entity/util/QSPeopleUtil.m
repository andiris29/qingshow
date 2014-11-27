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
        [statusString appendFormat:@"%@cm ", height];
    }
    if (weight) {
        [statusString appendFormat:@"%@kg ", weight];
    }
    return statusString;
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

@end

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
    return peopleDict[@"name"];
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
@end

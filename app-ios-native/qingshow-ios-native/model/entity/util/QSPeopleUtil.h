//
//  QSModelUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSPeopleUtil : NSObject

+ (NSString*)buildModelStatusString:(NSDictionary*)peopleDict;
+ (NSString*)buildNumLikeString:(NSDictionary*)peopleDict;
+ (NSString*)getName:(NSDictionary*)peopleDict;
+ (NSURL*)getHeadIconUrl:(NSDictionary*)peopleDict;
+ (NSString*)getStatus:(NSDictionary*)modelDict;
+ (NSString*)getRolesDescription:(NSDictionary*)modelDict;


+ (NSString*)getNumberFollowingsDescription:(NSDictionary*)modelDict;
+ (NSString*)getNumberFollowersDescription:(NSDictionary*)modelDict;
+ (NSString*)getNumberShowsDescription:(NSDictionary*)modelDict;
+ (NSString*)getNumberFavorsDescription:(NSDictionary*)modelDict;
+ (NSString*)getNumberRecommendationsDescription:(NSDictionary*)modelDict;

+ (BOOL)getPeopleIsFollowed:(NSDictionary*)dict;
+ (void)setPeople:(NSDictionary*)peopleDict isFollowed:(BOOL)isFollowed;

@end

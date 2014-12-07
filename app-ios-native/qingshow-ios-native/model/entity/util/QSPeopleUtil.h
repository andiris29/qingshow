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
+ (NSString*)getGenderDesc:(NSDictionary*)modelDict;
+ (NSString*)buildNumLikeString:(NSDictionary*)peopleDict;
+ (NSString*)getName:(NSDictionary*)peopleDict;
+ (NSURL*)getHeadIconUrl:(NSDictionary*)peopleDict;
+ (NSString*)getDetailDesc:(NSDictionary*)peopleDict;
+ (NSString*)getStatus:(NSDictionary*)modelDict;
+ (NSString*)getRolesDescription:(NSDictionary*)modelDict;
+ (BOOL)checkPeopleIsModel:(NSDictionary*)peopleDict;

+ (NSString*)getNumberFollowersDescription:(NSDictionary*)modelDict;
+ (NSString*)getNumberShowsDescription:(NSDictionary*)modelDict;

+ (BOOL)getPeopleIsFollowed:(NSDictionary*)dict;
+ (void)setPeople:(NSDictionary*)peopleDict isFollowed:(BOOL)isFollowed;

+ (BOOL)isPeople:(NSDictionary*)l equalToPeople:(NSDictionary*)r;
@end

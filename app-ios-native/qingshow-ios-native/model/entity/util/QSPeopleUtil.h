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
+ (NSURL*)getBackgroundUrl:(NSDictionary*)peopleDict;
+ (NSString*)getDetailDesc:(NSDictionary*)peopleDict;
+ (NSString*)getStatus:(NSDictionary*)modelDict;
+ (NSString*)getRolesDescription:(NSDictionary*)modelDict;
+ (NSString*)getJobDesc:(NSDictionary*)peopleDict;
+ (NSString*)getClothingSizeDesc:(NSDictionary*)peopleDict;
+ (NSString*)getProvinceDesc:(NSDictionary*)peopleDict;
+ (NSString*)getHeight:(NSDictionary*)peopleDict;
+ (NSString*)getWeight:(NSDictionary*)peopleDict;
+ (NSString*)getShoeSizeDesc:(NSDictionary*)peopleDict;

+ (BOOL)checkPeopleIsModel:(NSDictionary*)peopleDict;

+ (NSString*)getNumberFollowersDescription:(NSDictionary*)modelDict;
+ (void)addNumFollower:(long long)num forPeople:(NSDictionary*)peopleDict;
+ (NSString*)getNumberShowsDescription:(NSDictionary*)modelDict;

+ (BOOL)getPeopleIsFollowed:(NSDictionary*)dict;
+ (void)setPeople:(NSDictionary*)peopleDict isFollowed:(BOOL)isFollowed;

+ (BOOL)isPeople:(NSDictionary*)l equalToPeople:(NSDictionary*)r;
@end

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
+ (NSString*)getNickname:(NSDictionary*)peopleDict;
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
+ (NSString*)getHairTypeDesc:(NSDictionary*)peopleDict;

+ (BOOL)checkPeopleIsModel:(NSDictionary*)peopleDict;

+ (NSString*)getNumberFollowersDescription:(NSDictionary*)modelDict;
+ (void)addNumFollower:(long long)num forPeople:(NSDictionary*)peopleDict;
+ (NSString*)getNumberShowsDescription:(NSDictionary*)modelDict;

+ (NSString*)getNumberFollowBrands:(NSDictionary*)peopleDict;
+ (NSString*)getNumberFollowPeoples:(NSDictionary*)peopleDict;


+ (BOOL)getPeopleIsFollowed:(NSDictionary*)dict;
+ (void)setPeople:(NSDictionary*)peopleDict isFollowed:(BOOL)isFollowed;

+ (BOOL)isPeople:(NSDictionary*)l equalToPeople:(NSDictionary*)r;

+ (NSArray*)getReceiverList:(NSDictionary*)dict;

+ (BOOL)hasPersonalizeData:(NSDictionary*)dict;
@end

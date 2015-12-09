//
//  QSModelUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSImageNameUtil.h"

typedef NS_ENUM(NSInteger, QSPeopleRole) {
    QSPeopleRoleGuest = 0,
    QSPeopleRoleUser = 1
};

@interface QSPeopleUtil : NSObject

+ (NSString *)getPeopleId:(NSDictionary *)peopleDict;
+ (NSString*)getNickname:(NSDictionary*)peopleDict;
+ (NSURL*)getHeadIconUrl:(NSDictionary*)peopleDict;
+ (NSURL*)getHeadIconUrl:(NSDictionary *)peopleDict type:(QSImageNameType)type;
+ (NSURL*)getBackgroundUrl:(NSDictionary*)peopleDict;
+ (NSString*)getDetailDesc:(NSDictionary*)peopleDict;
+ (NSString*)getHeight:(NSDictionary*)peopleDict;
+ (NSString*)getWeight:(NSDictionary*)peopleDict;


+ (NSString*)getNumberShowsDescription:(NSDictionary*)modelDict;
+ (NSString*)getNumberCreateShows:(NSDictionary*)peopleDict;


+ (NSString*)getNumberLiketoCreateShows:(NSDictionary*)peopleDict;

+ (BOOL)getPeopleIsFollowed:(NSDictionary*)dict;
+ (void)setPeople:(NSDictionary*)peopleDict isFollowed:(BOOL)isFollowed;

+ (BOOL)isPeople:(NSDictionary*)l equalToPeople:(NSDictionary*)r;

+ (NSArray*)getReceiverList:(NSDictionary*)dict;
+ (BOOL)hasPersonalizeData:(NSDictionary*)dict;

+ (NSString*)getAge:(NSDictionary*)dict;
+ (NSString*)getBodyTypeDesc:(NSDictionary*)dict;
+ (NSString*)getDressStyleDesc:(NSDictionary*)dict;
+ (NSArray*)getExpectations:(NSDictionary*)dict;
+ (NSString*)getExpectationsDesc:(NSDictionary*)dict;

+ (NSString*)getShoulder:(NSDictionary*)dict;
+ (NSString*)getBust:(NSDictionary*)dict;
+ (NSString*)getWaist:(NSDictionary*)dict;
+ (NSString*)getHips:(NSDictionary*)dict;
+ (NSString*)getShoeSize:(NSDictionary*)dict;
+ (QSPeopleRole)getPeopleRole:(NSDictionary*)dict;

#pragma mark - Unread
+ (NSArray*)getUnreadNotifications:(NSDictionary*)peopleDict;

#pragma mark - logintype
+ (BOOL)hasBindWechat:(NSDictionary*)dict;
+ (NSString *)getNameAndPswLoginId:(NSDictionary *)dict;
#pragma mark - code
+ (BOOL)checkMobileExist:(NSDictionary *)dict;
+ (BOOL)isTalent:(NSDictionary*)dict;

#pragma mark - Bonus
+ (NSNumber*)getTotalBonus:(NSDictionary*)dict;
+ (NSNumber*)getAvailableBonus:(NSDictionary*)dict;
+ (NSNumber*)getRank:(NSDictionary*)dict;
+ (UIImage*)rankImgView:(NSDictionary*)dict;
@end

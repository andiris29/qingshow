//
//  QSBonusUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/4.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSBonusUtil : NSObject

+ (NSNumber*)getMoney:(NSDictionary*)dict;
+ (NSArray*)getParticipantsIds:(NSDictionary*)dict;
+ (NSNumber*)getStatus:(NSDictionary *)dict;
+ (NSString*)getNote:(NSDictionary *)dict;
+ (NSString*)getCreate:(NSDictionary *)dict;
+ (NSString*)getIcon:(NSDictionary*)dict;
+ (NSString*)getTradeItemId:(NSDictionary*)dict;
+ (NSNumber*)getType:(NSDictionary*)dict;
@end

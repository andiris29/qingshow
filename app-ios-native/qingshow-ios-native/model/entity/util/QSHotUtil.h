//
//  QSHotUtil.h
//  qingshow-ios-native
//
//  Created by Han Hugh on 15/5/12.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSHotUtil : NSObject

+ (NSURL *)getHotCoverUrl:(NSDictionary *)topShows;
+ (NSString *)getHotNumLike:(NSDictionary *)topShows;
+ (NSDate *)getHotCreateDate:(NSDictionary *)topShows;
+ (NSDate *)getHotUpDate:(NSDictionary *)topShows;


@end

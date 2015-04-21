//
//  QSTopicUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/31/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSTopicUtil : NSObject

+ (NSString*)getTitle:(NSDictionary*)dict;
+ (NSString*)getSubTitle:(NSDictionary*)dict;
+ (NSURL*)getCoverUrl:(NSDictionary*)dict;
+ (NSURL*)getHorizontalCoverUrl:(NSDictionary*)dict;
+ (NSString*)getShowNumberDesc:(NSDictionary*)dict;
@end

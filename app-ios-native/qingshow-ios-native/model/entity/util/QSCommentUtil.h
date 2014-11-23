//
//  QSCommentUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSCommentUtil : NSObject

+ (NSString*)getContent:(NSDictionary*)commentDict;
+ (NSDictionary*)getPeople:(NSDictionary*)commentDict;
+ (NSDictionary*)getShow:(NSDictionary*)commentDict;
+ (NSString*)getFormatedDateString:(NSDictionary*)commentDict;



@end

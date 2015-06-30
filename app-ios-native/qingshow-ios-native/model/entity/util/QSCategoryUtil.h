//
//  QSCategoryUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSCategoryUtil : NSObject

+ (NSArray*)getChildren:(NSDictionary*)categoryDict;
+ (NSString*)getParentId:(NSDictionary*)categoryDict;

+ (BOOL)getMatchEnabled:(NSDictionary*)categoryDict;
+ (BOOL)getDefaultOnCanvas:(NSDictionary*)categoryDict;
+ (NSNumber*)getMathchInfoRow:(NSDictionary*)categoryDict;
+ (NSNumber*)getMatchInfoColumn:(NSDictionary*)categoryDict;

@end

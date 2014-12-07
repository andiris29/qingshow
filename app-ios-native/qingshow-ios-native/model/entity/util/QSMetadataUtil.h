//
//  QSMetadataUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/7/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSMetadataUtil : NSObject

+ (NSString*)getNumberPageDesc:(NSDictionary*)matedataDict;
+ (NSString*)getNumberTotalDesc:(NSDictionary*)matedataDict;

@end

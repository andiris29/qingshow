//
//  QSPnsHelper.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/19/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSPnsHelper : NSObject

+ (BOOL)isFromBackground:(NSDictionary*)userInfo;
+ (void)handlePnsData:(NSDictionary*)userInfo fromBackground:(BOOL)fFromBackground;

@end

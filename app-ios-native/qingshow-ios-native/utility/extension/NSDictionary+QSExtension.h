//
//  NSDictionary+QSExtension.h
//  qingshow-ios-native
//
//  Created by wxy325 on 1/19/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSDictionary(QSExtension)
- (NSMutableDictionary*)deepMutableCopy;

- (NSString*)stringValueForKeyPath:(NSString*)keypath;
- (NSDictionary*)dictValueForKeyPath:(NSString*)keypath;
- (NSNumber*)numberValueForKeyPath:(NSString*)keypath;
- (NSArray*)arrayValueForKeyPath:(NSString*)keypath;
- (NSDate*)dateValueForKeyPath:(NSString*)keyPath;
@end

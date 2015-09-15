//
//  QSHookDictionary.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/9/12.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
//Hook Method for NSDictionay, filter nil value before init
@interface NSDictionary (QSHookDictionary)

+ (instancetype)hookDictionaryWithObjects:(const id [])objects forKeys:(const id <NSCopying> [])keys count:(NSUInteger)cnt;
- (instancetype)initHookWithObjects:(const id [])objects forKeys:(const id<NSCopying> [])keys count:(NSUInteger)cnt ;
@end

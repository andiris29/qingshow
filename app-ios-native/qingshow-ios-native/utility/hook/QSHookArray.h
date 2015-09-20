//
//  QSHookArray.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/9/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSArray(Hook)
+ (instancetype)hookArrayWithObjects:(const id [])objects count:(NSUInteger)cnt;
- (instancetype)initHookWithObjects:(const id [])objects count:(NSUInteger)cnt;
@end

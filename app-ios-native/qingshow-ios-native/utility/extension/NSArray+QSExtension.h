//
//  NSArray+QSExtension.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/30/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSBlock.h"
@interface NSArray(QSExtension)

- (NSMutableArray*)deepMutableCopy;
- (NSArray*)mapUsingBlock:(IdBlock)block;
- (NSArray*)filteredArrayUsingBlock:(FilterBlock)block;

@end

//
//  QSExtension.h
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/6/1.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSDate (QSExtension)

- (BOOL)isToday;
- (BOOL)isYesterday;

/**
 *  获得与当前时间的差距
 */
- (NSDateComponents *)deltaWithNow;
@end

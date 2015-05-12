//
//  QSLayoutUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/18/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSLayoutUtil : NSObject

+ (CGSize)sizeForString:(NSString*)str withMaxWidth:(CGFloat)width height:(CGFloat)height font:(UIFont*)font;

@end

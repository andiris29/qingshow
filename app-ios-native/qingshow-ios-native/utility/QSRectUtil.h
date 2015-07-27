//
//  QSRectUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/6/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSRectUtil : NSObject
+ (CGRect)reducedFrame:(CGRect)innerRect forContainer:(CGRect)containerRect;

+ (float)getViewUncoveredSquare:(UIView*)view otherViews:(NSArray*)array;

+ (float)getSquare:(CGRect)rect;

+ (CGSize)scaleSize:(CGSize)fromSize toFitSize:(CGSize)toSize;
@end

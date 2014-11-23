//
//  QSShowUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSShowUtil : NSObject

+ (NSArray*)getShowVideoPreviewUrlArray:(NSDictionary*)dict;
+ (NSArray*)getItemsImageUrlArrayFromShow:(NSDictionary*)dict;
@end

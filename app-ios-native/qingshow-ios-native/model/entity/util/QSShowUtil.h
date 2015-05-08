//
//  QSShowUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSShowUtil : NSObject
+ (NSURL*)getCoverUrl:(NSDictionary*)dict;
+ (NSURL*)getHoriCoverUrl:(NSDictionary*)dict;
+ (NSArray*)getShowVideoPreviewUrlArray:(NSDictionary*)dict;
+ (NSArray*)getItemsImageUrlArrayFromShow:(NSDictionary*)dict;
+ (NSDictionary*)getItemFromShow:(NSDictionary*)showDict AtIndex:(int)index;
+ (NSArray*)getItems:(NSDictionary*)showDict;
+ (NSDictionary*)getCoverMetadata:(NSDictionary*)showDict;
//+ (float)getCoverHeight:(NSDictionary*)showDict;
//+ (float)getCoverWidth:(NSDictionary*)showDict;

+ (NSString*)getNumberCommentsDescription:(NSDictionary*)showDict;
+ (void)addNumberComment:(long long)num forShow:(NSDictionary*)showDict;
+ (NSString*)getNumberLikeDescription:(NSDictionary*)showDict;
+ (NSString*)getNumberItemDescription:(NSDictionary*)showDict;
+ (BOOL)getIsLike:(NSDictionary*)showDict;
+ (void)setIsLike:(BOOL)isLike show:(NSDictionary*)showDict;
+ (void)addNumberLike:(long long)num forShow:(NSDictionary*)showDict;
+ (NSDate*)getRecommendDate:(NSDictionary*)showDict;
@end

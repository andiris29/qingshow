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

+ (NSString*)getShowDesc:(NSDictionary*)showDict;
+ (NSArray*)getShowVideoPreviewUrlArray:(NSDictionary*)dict;
+ (NSDictionary*)getItemFromShow:(NSDictionary*)showDict AtIndex:(int)index;
+ (NSArray*)getItems:(NSDictionary*)showDict;
+ (NSDictionary*)getCoverMetadata:(NSDictionary*)showDict;
+ (NSString*)getNumberCommentsDescription:(NSDictionary*)showDict;
+ (void)addNumberComment:(long long)num forShow:(NSDictionary*)showDict;
+ (NSString*)getNumberLikeDescription:(NSDictionary*)showDict;
+ (NSString*)getNumberItemDescription:(NSDictionary*)showDict;
+ (BOOL)getIsLike:(NSDictionary*)showDict;
+ (void)setIsLike:(BOOL)isLike show:(NSDictionary*)showDict;
+ (void)addNumberLike:(long long)num forShow:(NSDictionary*)showDict;
+ (NSDate*)getRecommendDate:(NSDictionary*)showDict;
+ (NSString*)getRecommentDesc:(NSDictionary*)showDict;
+ (NSDictionary*)getBrand:(NSDictionary*)showDict;
@end

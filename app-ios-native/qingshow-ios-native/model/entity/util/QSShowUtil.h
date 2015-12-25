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
+ (NSURL*)getCoverBackgroundUrl:(NSDictionary*)dict;
+ (NSURL*)getCoverForegroundUrl:(NSDictionary*)dict;
+ (NSString *)getShowId:(NSDictionary *)dict;
+ (NSURL*)getFormatterCoverUrl:(NSDictionary *)dict;
+ (NSURL*)getFormatterCoVerForegroundUrl:(NSDictionary*)dict;

+ (NSURL*)getHoriCoverUrl:(NSDictionary*)dict;
+ (NSString *)getRecommendGroup:(NSDictionary *)dict;
+ (NSString *)getUserId:(NSDictionary *)dict;
+ (NSString*)getShowDesc:(NSDictionary*)showDict;
+ (NSArray*)getShowVideoPreviewUrlArray:(NSDictionary*)dict;
+ (NSDictionary*)getItemFromShow:(NSDictionary*)showDict AtIndex:(int)index;
+ (NSArray*)getItems:(NSDictionary*)showDict;
+ (NSDictionary*)getPeopleFromShow:(NSDictionary*)showDict;
+ (NSString*)getNumberCommentsDescription:(NSDictionary*)showDict;
+ (void)addNumberComment:(long long)num forShow:(NSDictionary*)showDict;
+ (NSString*)getNumberLikeDescription:(NSDictionary*)showDict;
+ (NSString*)getNumberItemDescription:(NSDictionary*)showDict;
+ (BOOL)getIsLike:(NSDictionary*)showDict;
+ (void)setIsLike:(BOOL)isLike show:(NSDictionary*)showDict;
+ (void)addNumberLike:(long long)num forShow:(NSDictionary*)showDict;
+ (NSDate*)getRecommendDate:(NSDictionary*)showDict;
+ (NSString*)getRecommentDesc:(NSDictionary*)showDict;
+ (NSString*)getVideoPath:(NSDictionary*)showDict;

+ (NSDate*)getCreatedDate:(NSDictionary*)showDict;
+ (NSString*)getNumberViewDesc:(NSDictionary*)showDict;
//新增获取图片高度的方法
+ (CGFloat)getCoverMetaDataHeight:(NSDictionary *)dic;
+ (NSDictionary*)getPromotionRef:(NSDictionary*)showDict;
+ (void)addNumberView:(long long)num forShow:(NSDictionary*)showDict;
+ (NSArray*)getAllItemArray:(NSDictionary*)showDict;
+ (NSArray*)getItemRects:(NSDictionary*)showDict;
+ (BOOL)getItemReductionEnabled:(NSDictionary*)showDict;
@end

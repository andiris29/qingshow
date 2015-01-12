//
//  QSPreviewUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/28/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSPreviewUtil : NSObject

+ (NSURL*)getCoverUrl:(NSDictionary*)previewDict;
+ (NSDictionary*)getCoverMetadata:(NSDictionary*)previewDict;
+ (NSString*)getNumLikeDesc:(NSDictionary*)previewDict;
+ (NSString*)getBrandDesc:(NSDictionary*)previewDict;
+ (NSString*)getNameDesc:(NSDictionary*)previewDesc;
+ (NSString*)getPriceDesc:(NSDictionary*)previewDict;
+ (NSString*)getCreateDesc:(NSDictionary*)previewDict;
+ (NSString*)getNumCommentDesc:(NSDictionary*)previewDict;
+ (NSArray*)getImagesUrl:(NSDictionary*)previewDict;
+ (BOOL)getIsLike:(NSDictionary*)previewDict;
+ (void)setIsLike:(BOOL)isLike preview:(NSDictionary*)previewDict;
+ (void)addNumberLike:(long long)num forShow:(NSDictionary*)previewDict;
+ (void)addNumberComment:(long long)num forPreview:(NSDictionary*)previewDict;
//+ (NSArray*)getCoverAndImagesUrl:(NSDictionary*)previewDict;
- (NSString*)getImagesDesc:(NSDictionary*)previewDict atIndex:(int)index;
@end

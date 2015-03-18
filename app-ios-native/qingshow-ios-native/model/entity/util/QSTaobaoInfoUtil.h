//
//  QSTaobaoInfoUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/18/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSTaobaoInfoUtil : NSObject

+ (BOOL)hasSizeSku:(NSDictionary*)taobaoInfo;
+ (BOOL)hasColorSku:(NSDictionary*)taobaoInfo;
+ (BOOL)hasPropertiesThumbnail:(NSDictionary*)taobaoInfo;

+ (NSArray*)getSizeSkus:(NSDictionary*)taobaoInfo;
+ (NSArray*)getColorSkus:(NSDictionary*)taobaoInfo;

+ (NSURL*)getThumbnailUrlOfProperty:(NSString*)property taobaoInfo:(NSDictionary*)taobaoInfo;
+ (NSString*)getNameOfProperty:(NSString*)property taobaoInfo:(NSDictionary*)taobaoInfo;

+ (NSString*)getPriceOfSize:(NSString*)sizeSku color:(NSString*)colorSku taobaoInfo:(NSDictionary*)taobaoInfo;
+ (NSString*)getPromoPriceOfSize:(NSString*)sizeSku color:(NSString*)colorSku taobaoInfo:(NSDictionary*)taobaoInfo;
+ (BOOL)getIsAvaliableOfSize:(NSString*)sizeSku color:(NSString*)colorSku taobaoInfo:(NSDictionary*)taobaoInfo;

@end

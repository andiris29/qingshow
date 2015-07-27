//
//  QSTaobaoInfoUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/18/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSTaobaoInfoUtil : NSObject

+ (NSString*)getNameOfProperty:(NSString*)property taobaoInfo:(NSDictionary*)taobaoInfo;

+ (NSArray*)getSkusArray:(NSDictionary*)taobaoInfo;
+ (NSDictionary*)findSkusWithSkuId:(NSString*)skuId taobaoInfo:(NSDictionary*)taobaoInfo;
+ (NSNumber*)getPriceOfSkuId:(NSString*)skuId taobaoInfo:(NSDictionary*)taobaoInfo quantity:(NSNumber*)quantity;
+ (NSNumber*)getPromoPriceOfSkuId:(NSString*)skuId taobaoInfo:(NSDictionary*)taobaoInfo quantity:(NSNumber*)quantity;
+ (NSString*)getColorPropertyName:(NSDictionary*)taobaoInfo sku:(NSString*)skuId;
@end

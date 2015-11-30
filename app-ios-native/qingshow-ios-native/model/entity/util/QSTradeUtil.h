//
//  QSTradeUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/22/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSTradeUtil : NSObject
+ (NSString *)getTradeId:(NSDictionary *)dict;
+ (NSDictionary *)getPeopleDic:(NSDictionary *)dict;
+ (NSString*)getCreateDateDesc:(NSDictionary*)dict;
+ (NSString *)getDayDesc:(NSDictionary *)dict;
+ (NSNumber*)getStatus:(NSDictionary*)dict;
+ (NSString*)getWechatPrepayId:(NSDictionary*)dict;
+ (NSString*)getTotalFeeDesc:(NSDictionary*)dict;

+ (NSString*)getTradeLogisticCompany:(NSDictionary*)dict;
+ (NSString*)getTradeLogisticId:(NSDictionary*)dict;
+ (NSNumber*)getPrice:(NSDictionary*)dict;
+ (NSNumber*)getPriceDesc:(NSDictionary*)dict;

#pragma mark - Order
+ (NSDictionary *)getItemDic:(NSDictionary *)dict;
+ (NSString *)getItemId:(NSDictionary *)dict;
+ (NSDictionary*)getItemSnapshot:(NSDictionary*)dict;
+ (NSArray*)getSkuProperties:(NSDictionary*)dict;
+ (NSString *)getPropertiesDesc:(NSDictionary *)dict;
+ (NSString *)getPropertiesFullDesc:(NSDictionary *)dict;
+ (NSString *)getColorText:(NSDictionary *)dict;
+ (NSNumber*)getExpectedPrice:(NSDictionary*)dict;
+ (NSString*)getExpectedPriceDesc:(NSDictionary*)dict;
+ (NSNumber*)getQuantity:(NSDictionary*)dict;
+ (NSString*)getQuantityDesc:(NSDictionary*)dict;
+ (NSNumber*)getTotalFee:(NSDictionary*)dict;
+ (NSString*)getReceiverUuid:(NSDictionary*)dict;
+ (NSString*)calculateDiscountDescWithPrice:(NSNumber*)price trade:(NSDictionary*)trade;
+ (NSString*)getPromoterId:(NSDictionary*)dict;
@end

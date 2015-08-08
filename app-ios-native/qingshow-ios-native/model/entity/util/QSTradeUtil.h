//
//  QSTradeUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/22/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSTradeUtil : NSObject
+ (NSDictionary *)getPeopleDic:(NSDictionary *)dict;
+ (NSString*)getCreateDateDesc:(NSDictionary*)dict;
+ (NSString *)getDayDesc:(NSDictionary *)dict;
+ (NSNumber*)getStatus:(NSDictionary*)dict;
+ (NSString*)getStatusDesc:(NSDictionary*)dict;
+ (NSString*)getWechatPrepayId:(NSDictionary*)dict;
+ (NSString*)getTotalFeeDesc:(NSDictionary*)dict;

+ (NSString*)getTradeLogisticCompany:(NSDictionary*)dict;
+ (NSString*)getTradeLogisticId:(NSDictionary*)dict;
+ (BOOL)getTraddSharedByCurrentUser:(NSDictionary*)dict;


#pragma mark - Order

+ (NSDictionary*)getItemSnapshot:(NSDictionary*)dict;
+ (NSArray*)getSkuProperties:(NSDictionary*)dict;
+ (NSString *)getSizeText:(NSDictionary *)dict;
+ (NSString *)getColorText:(NSDictionary *)dict;
+ (NSNumber*)getExpectedPrice:(NSDictionary*)dict;
+ (NSString*)getExpectedPriceDesc:(NSDictionary*)dict;
+ (NSNumber*)getActualPrice:(NSDictionary*)dict;
+ (NSString*)getActualPriceDesc:(NSDictionary*)dict;
+ (NSNumber*)getQuantity:(NSDictionary*)dict;
+ (NSString*)getQuantityDesc:(NSDictionary*)dict;
+ (NSNumber*)getTotalFee:(NSDictionary*)dict;
+ (NSString*)getReceiverUuid:(NSDictionary*)dict;
@end

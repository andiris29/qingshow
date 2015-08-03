//
//  QSTradeUtil.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/22/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSTradeUtil : NSObject
+ (NSArray*)getOrderArray:(NSDictionary*)dict;
+ (NSString*)getCreateDateDesc:(NSDictionary*)dict;
+ (NSNumber*)getStatus:(NSDictionary*)dict;
+ (NSString*)getStatusDesc:(NSDictionary*)dict;
+ (NSString*)getWechatPrepayId:(NSDictionary*)dict;
+ (NSString*)getTotalFeeDesc:(NSDictionary*)dict;

+ (NSString*)getTradeLogisticCompany:(NSDictionary*)dict;
+ (NSString*)getTradeLogisticId:(NSDictionary*)dict;
+ (BOOL)getTraddSharedByCurrentUser:(NSDictionary*)dict;
@end

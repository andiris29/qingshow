//
//  QSShareUtil.h
//  qingshow-ios-native
//
//  Created by mhy on 15/10/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface QSShareUtil : NSObject

+ (NSString *)getShareIcon:(NSDictionary *)shareDic;
+ (NSString *)getShareTitle:(NSDictionary *)shareDic;
+ (NSString *)getShareDesc:(NSDictionary *)shareDic;
+ (NSString *)getshareUrl:(NSDictionary *)shareDic;

@end

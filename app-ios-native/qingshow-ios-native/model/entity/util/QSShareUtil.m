//
//  QSShareUtil.m
//  qingshow-ios-native
//
//  Created by mhy on 15/10/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSShareUtil.h"
#import "QSEntityUtil.h"

@implementation QSShareUtil

+ (NSString *)getShareIcon:(NSDictionary *)shareDic
{
    if (!shareDic) {
        return nil;
    }
    return [QSEntityUtil getStringValue:shareDic keyPath:@"icon"];
}
+ (NSString *)getShareTitle:(NSDictionary *)shareDic
{
    if (!shareDic) {
        return nil;
    }
    return [QSEntityUtil getStringValue:shareDic keyPath:@"title"];
}
+ (NSString *)getShareDesc:(NSDictionary *)shareDic
{
    if (!shareDic) {
        return nil;
    }
    return [QSEntityUtil getStringValue:shareDic keyPath:@"description"];
}
+ (NSString *)getshareUrl:(NSDictionary *)shareDic
{
    if (!shareDic) {
        return nil;
    }
    return [QSEntityUtil getStringValue:shareDic keyPath:@"url"];
}
@end

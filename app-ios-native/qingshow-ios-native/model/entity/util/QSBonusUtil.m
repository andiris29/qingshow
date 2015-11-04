//
//  QSBonusUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/4.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSBonusUtil.h"
#import "NSDictionary+QSExtension.h"
@implementation QSBonusUtil

+ (NSString*)getItemRef:(NSDictionary*)dict {
    return [dict stringValueForKeyPath:@"trigger.itemRef"];
}
+ (NSString*)getTradeRef:(NSDictionary*)dict {
    return [dict stringValueForKeyPath:@"trigger.tradeRef"];
}
+ (NSNumber*)getMoney:(NSDictionary*)dict {
    return [dict numberValueForKeyPath:@"money"];
}
+ (NSArray*)getParticipantsIds:(NSDictionary*)dict {
    return [dict arrayValueForKeyPath:@"participants"];
}

@end

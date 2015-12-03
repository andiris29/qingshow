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
    return [dict numberValueForKeyPath:@"amount"];
}
+ (NSArray*)getParticipantsIds:(NSDictionary*)dict {
    return [dict arrayValueForKeyPath:@"participants"];
}

+ (NSNumber*)getStatus:(NSDictionary *)dict {
    return [QSEntityUtil getNumberValue:dict keyPath:@"status"];
}
+ (NSString*)getNote:(NSDictionary *)dict {
    return [QSEntityUtil getStringValue:dict keyPath:@"description"];
}
+ (NSString*)getCreate:(NSDictionary *)dict {
    return [QSEntityUtil getStringValue:dict keyPath:@"create"];
}

+ (NSString*)getIcon:(NSDictionary*)dict {
    return [QSEntityUtil getStringValue:dict keyPath:@"icon"];
}



@end

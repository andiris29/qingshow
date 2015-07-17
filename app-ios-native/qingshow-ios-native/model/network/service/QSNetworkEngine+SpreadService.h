//
//  QSNetworkEngine+TraceService.h
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine.h"
#define kGlobalFirstUpdateNotification @"kGlobalFirstUpdateNotification"


@interface QSNetworkEngine(SpreadService)

- (MKNetworkOperation*)logTraceWithParametes:(NSDictionary *)parametes
                                   onSucceed:(BoolBlock)succeedBlock
                                     onError:(ErrorBlock)errorBlock;
@end

//
//  QSNetworkEngine+TraceService.h
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine.h"

@interface QSNetworkEngine(TraceService)

- (MKNetworkOperation*)logTraceWithParametes:(NSDictionary *)parametes
                                   onSucceed:(EntitySuccessBlock)succeedBlock
                                     onError:(ErrorBlock)errorBlock;
@end

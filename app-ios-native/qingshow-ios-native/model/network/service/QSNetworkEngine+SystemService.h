//
//  QSNetworkEngine+SystemService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/9/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine.h"
#import "QSBlock.h"

@interface QSNetworkEngine(SystemService)
- (MKNetworkOperation*)systemLogLevel:(NSString*)level
                              message:(NSString*)message
                                stack:(NSString*)stack
                                extra:(NSString*)extra
                            onSuccess:(VoidBlock)successBlock
                              onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)systemGetConfigOnSucceed:(DicBlock)succeedBlock
                                        onError:(ErrorBlock)errorBlock;
@end

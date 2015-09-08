//
//  QSNetworkEngine+ItemService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 9/7/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine.h"

@interface QSNetworkEngine(ItemService)

- (MKNetworkOperation*)itemSync:(NSString*)itemId
                      onSucceed:(EntitySuccessBlock)succeedBlock
                        onError:(ErrorBlock)errorBlock;

@end

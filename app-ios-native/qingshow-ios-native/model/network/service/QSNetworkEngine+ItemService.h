//
//  QSNetworkEngine+ItemService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 5/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine.h"

@interface QSNetworkEngine(ItemService)

#pragma mark Like
- (MKNetworkOperation*)handleItemLike:(NSDictionary*)itemDict
                            onSucceed:(BoolBlock)succeedBlock
                              onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)likeItem:(NSDictionary*)itemDict
                      onSucceed:(VoidBlock)succeedBlock
                        onError:(ErrorBlock)errorBlock;
- (MKNetworkOperation*)unlikeItem:(NSDictionary*)showDict
                        onSucceed:(VoidBlock)succeedBlock
                          onError:(ErrorBlock)errorBlock;


@end

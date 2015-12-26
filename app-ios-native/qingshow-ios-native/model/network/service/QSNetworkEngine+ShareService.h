//
//  QSNetworkEngine+ShareService.h
//  qingshow-ios-native
//
//  Created by mhy on 15/10/13.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine.h"

@interface QSNetworkEngine (ShareService)

- (MKNetworkOperation *)shareCreateShow:(NSString *)showId
                              onSucceed:(DicBlock)succeedBlock
                                onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation *)shareCreateBonus:(NSString *)peopleId
                              onSucceed:(DicBlock)succeedBlock
                                onError:(ErrorBlock)errorBlock;
@end

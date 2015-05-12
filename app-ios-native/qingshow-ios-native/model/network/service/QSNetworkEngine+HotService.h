//
//  QSNetworkEngine+HotService.h
//  qingshow-ios-native
//
//  Created by Han Hugh on 15/5/12.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSNetworkEngine.h"

@interface QSNetworkEngine (HotService)

- (MKNetworkOperation *)hotFeedingByOnSucceed:(ArraySuccessBlock)succeedBlock onError:(ErrorBlock)errorBlock;

@end

//
//  QSNetworkEngine+BonusService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/1.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSNetworkEngine.h"

@interface QSNetworkEngine(BonusService)

- (MKNetworkOperation*)queryBonusWithIds:(NSArray*)bonusIds
                               onSucceed:(ArraySuccessBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)queryOwnedBonusPage:(int)page
                                 onSucceed:(ArraySuccessBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock;

@end

//
//  QSNetworkEngine+TradeService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/19/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSNetworkEngine.h"

@interface QSNetworkEngine(TradeService)

- (MKNetworkOperation*)createTradeTotalFee:(long)totalFee
                                  quantity:(int)quantity
                                     price:(long)price
                                      item:(NSDictionary*)item
                                       sku:(NSString*)sku
                              receiverUuid:(NSString*)uuid
                                 onSucceed:(VoidBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)createTradeTotalFee:(long)totalFee
                                orderArray:(NSArray*)orderArray
                                 onSucceed:(VoidBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock;

- (MKNetworkOperation*)queryTradeCreatedBy:(NSString*)peopleId
                                      page:(int)page
                                 onSucceed:(ArraySuccessBlock)succeedBlock
                                   onError:(ErrorBlock)errorBlock;

@end

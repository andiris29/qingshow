//
//  QSPaymentService.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/26/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSBlock.h"

#define SHARE_PAYMENT_SERVICE [QSPaymentService shareService]

@interface QSPaymentService : NSObject

+ (void)configPaymentHost:(NSString*)paymentHost;

+ (QSPaymentService*)shareService;
- (NSString*)getPaymentHost;
- (void)sharedForTrade:(NSDictionary*)tradeDict
             onSucceed:(DicBlock)succeedBlock
               onError:(ErrorBlock)errorBlock;

- (void)payForTrade:(NSDictionary *)tradeDict
          onSuccess:(VoidBlock)succeedBlock
            onError:(ErrorBlock)errorBlock;

- (void)payWithAliPayTrade:(NSDictionary*)tradeDict
               productName:(NSString*)productName;
- (void)payWithWechatPrepayId:(NSString*)prepayId
                  productName:(NSString*)productName;
@end

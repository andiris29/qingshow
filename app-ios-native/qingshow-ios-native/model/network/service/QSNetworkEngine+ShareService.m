//
//  QSNetworkEngine+ShareService.m
//  qingshow-ios-native
//
//  Created by mhy on 15/10/13.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSNetworkEngine+ShareService.h"
#import "MKNetworkEngine+QSExtension.h"
#import "NSDictionary+QSExtension.h"
#import "QSEntityUtil.h"

#define PATH_SHARE_CREATE_SHOW @"share/createShow"
#define PATH_SHARE_CREATE_TRADE @"share/createTrade"
#define PATH_SHARE_CREATE_BONUS @"share/createBonus"

@implementation QSNetworkEngine (ShareService)


- (MKNetworkOperation *)shareCreateShow:(NSString *)showId
                              onSucceed:(StringBlock)succeedBlock
                                onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_SHARE_CREATE_SHOW method:@"POST" paramers:@{@"_id":showId} onSucceeded:^(MKNetworkOperation *completedOperation) {
        
        if (succeedBlock) {
            NSDictionary *retDict = completedOperation.responseJSON;
            NSDictionary *shareDict = [QSEntityUtil getDictValue:retDict keyPath:@"data.sharedObject"];
            NSString *shareId = [QSEntityUtil getStringValue:shareDict keyPath:@"_id"];
            succeedBlock(shareId);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
   
}

- (MKNetworkOperation *)shareCreateTrade:(NSString *)tradeId
                               onSucceed:(StringBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_SHARE_CREATE_TRADE method:@"POST" paramers:@{@"_id":tradeId} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            NSDictionary *retDict = completedOperation.responseJSON;
            NSDictionary *shareDict = [QSEntityUtil getDictValue:retDict keyPath:@"data.sharedObject"];
            NSString *shareId = [QSEntityUtil getStringValue:shareDict keyPath:@"_id"];
            succeedBlock(shareId);

        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];

}

- (MKNetworkOperation *)shareCreateBonus:(NSString *)peopleId
                               onSucceed:(StringBlock)succeedBlock
                                 onError:(ErrorBlock)errorBlock
{
    return [self startOperationWithPath:PATH_SHARE_CREATE_BONUS method:@"POST" paramers:@{@"_id":peopleId} onSucceeded:^(MKNetworkOperation *completedOperation) {
        if (succeedBlock) {
            NSDictionary *retDict = completedOperation.responseJSON;
            NSDictionary *shareDict = [QSEntityUtil getDictValue:retDict keyPath:@"data.sharedObject"];
            NSString *shareId = [QSEntityUtil getStringValue:shareDict keyPath:@"_id"];
            succeedBlock(shareId);
        }
    } onError:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];

}
@end

//
//  QSNetworkHelper.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/9/14.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSNetworkHelper.h"
#import "MKNetworkEngine.h"

#import "NSDictionary+QSExtension.h"

#import "QSNetworkEngine.h"
#import "QSShareService.h"
#import "QSPaymentService.h"

#define PATH_SYSTEM_GET_SERVER @"http://chingshow.com/services/system/get"


@implementation QSNetworkHelper


+ (MKNetworkOperation*)querySystemPathOnSucceed:(VoidBlock)succeedBlock onError:(ErrorBlock)errorBlock {
    NSDictionary* paramDict = @{@"client" : @"ios"};
    NSMutableDictionary* p = [paramDict mutableCopy];
    NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    p[@"version"] = version;
    MKNetworkEngine* engine = [[MKNetworkEngine alloc] init];
    MKNetworkOperation* op = [[MKNetworkOperation alloc] initWithURLString:PATH_SYSTEM_GET_SERVER params:p httpMethod:@"GET"];
    [op setHeader:@"version" withValue:version];
    [op addCompletionHandler:^(MKNetworkOperation *completedOperation) {
        NSDictionary* dict = completedOperation.responseJSON;
        dict = [dict dictValueForKeyPath:@"data.deployment"];
        [QSNetworkEngine hostInit:[dict stringValueForKeyPath:@"appServiceRoot"]];
        [QSShareService configShareHost:[dict stringValueForKeyPath:@"appWebRoot"]];
        [QSPaymentService configPaymentHost:[dict stringValueForKeyPath:@"paymentServiceRoot"]];
        
        if (succeedBlock) {
            succeedBlock();
        }
    } errorHandler:^(MKNetworkOperation *completedOperation, NSError *error) {
        if (errorBlock) {
            errorBlock(error);
        }
    }];
    [engine enqueueOperation:op];
    return op;
}

@end

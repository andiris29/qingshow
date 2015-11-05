//
//  QSNetworkHelper.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/9/14.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSNetworkHelper.h"
#import "MKNetworkEngine.h"
#import "QSNetworkOperation.h"

#import "NSDictionary+QSExtension.h"

#import "QSNetworkEngine.h"
#import "QSShareService.h"
#import "QSPaymentService.h"
#import "UIDevice-Hardware.h"

#define PATH_SYSTEM_GET_SERVER @"http://chingshow.com/services/system/get"


@implementation QSNetworkHelper


+ (MKNetworkOperation*)querySystemPathOnSucceed:(VoidBlock)succeedBlock onError:(ErrorBlock)errorBlock {
    NSDictionary* paramDict = @{@"client" : @"ios"};
    NSMutableDictionary* p = [paramDict mutableCopy];

#warning TODO Workaround for old server version
    NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    p[@"version"] = version;
    
    MKNetworkEngine* engine = [[MKNetworkEngine alloc] init];
    MKNetworkOperation* op = [[QSNetworkOperation alloc] initWithURLString:PATH_SYSTEM_GET_SERVER params:p httpMethod:@"GET"];
    NSDictionary* headers = [self generateHeader];
    for (NSString* key in headers) {
        [op setHeader:key withValue:headers[key]];
    }

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
+ (NSDictionary*)generateHeader {
    
    NSMutableDictionary* p = [@{} mutableCopy];
    NSDictionary* infoDict = [[NSBundle mainBundle] infoDictionary];

    p[@"qs-version"] = [infoDict objectForKey:@"CFBundleShortVersionString"];
    p[@"qs-version-code"] = [infoDict objectForKey:@"CFBundleVersion"];
    p[@"qs-type"] = @"app-ios";
    UIDevice* device = [UIDevice currentDevice];
    p[@"qs-os-type"] = @"ios";
    p[@"qs-device-model"] = [device modelName];
    p[@"qs-os-version"] = [device systemVersion];
    NSUUID* uuid = [device identifierForVendor];
    p[@"qs-device-uid"] = uuid.UUIDString;
    return p;
}
@end

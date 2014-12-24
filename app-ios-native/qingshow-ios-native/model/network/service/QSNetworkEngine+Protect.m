//
//  QSNetworkEngine+Protect.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSNetworkEngine+Protect.h"

@implementation QSNetworkEngine (Protect)

- (MKNetworkOperation*)startOperationWithPath:(NSString*)path
                                       method:(NSString*)method
                                     paramers:(NSDictionary*)paramDict
                                  onSucceeded:(OperationSucceedBlock)succeedBlock
                                      onError:(OperationErrorBlock)errorBlock
{
    MKNetworkOperation* op = nil;
    NSMutableDictionary* p = [paramDict mutableCopy];
    // Move to global const
    p[@"version"] = @"1.0.0";
    op = [self operationWithPath:path params:p httpMethod:method ];
    [op addCompletionHandler:succeedBlock errorHandler:errorBlock];
    [self enqueueOperation:op];
    return op;
}

- (MKNetworkOperation *)startOperationWithPath:(NSString *)path
                                        method:(NSString *)method
                                      paramers:(NSDictionary *)paramDict
                                       fileKey:(NSString *)fileKey
                                         image:(NSData *)image
                                   onSucceeded:(OperationSucceedBlock)succeedBlock
                                       onError:(OperationErrorBlock)errorBlock {
    NSMutableDictionary* p = [paramDict mutableCopy];
    p[@"version"] = @"1.0.0";
    MKNetworkOperation *op = nil;
    op = [self operationWithPath:path params:p httpMethod:method];
    [op addData:image forKey:fileKey];
    //    [op setFreezable:YES];
    [op addCompletionHandler:succeedBlock errorHandler:errorBlock];
    [self enqueueOperation:op];
    return op;
}

@end

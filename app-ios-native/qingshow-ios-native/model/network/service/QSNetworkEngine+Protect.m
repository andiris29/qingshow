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
    NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    p[@"version"] = version;
    op = [self operationWithPath:path params:p httpMethod:method ];
    [op addCompletionHandler:succeedBlock errorHandler:errorBlock];
    op.postDataEncoding = MKNKPostDataEncodingTypeJSON;
    [self enqueueOperation:op];
    return op;
}
- (MKNetworkOperation*)startOperationWithPathNoVersion:(NSString*)path
                                       method:(NSString*)method
                                     paramers:(NSDictionary*)paramDict
                                  onSucceeded:(OperationSucceedBlock)succeedBlock
                                      onError:(OperationErrorBlock)errorBlock
{
    MKNetworkOperation* op = nil;
    NSMutableDictionary* p = [paramDict mutableCopy];
   // NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    //p[@"version"] = version;
    op = [self operationWithPath:path params:p httpMethod:method ];
    [op addCompletionHandler:succeedBlock errorHandler:errorBlock];
    op.postDataEncoding = MKNKPostDataEncodingTypeJSON;
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
    NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    p[@"version"] = version;
    MKNetworkOperation *op = nil;
    op = [self operationWithPath:path params:p httpMethod:method];
    [op addData:image forKey:fileKey];
    //    [op setFreezable:YES];
    [op addCompletionHandler:succeedBlock errorHandler:errorBlock];
    [self enqueueOperation:op];
    return op;
}

- (MKNetworkOperation *)startOperationWithPath:(NSString *)path
                                        method:(NSString *)method
                                      paramers:(NSDictionary *)paramDict
                                       fileKey:(NSString *)fileKey
                                      fileName:(NSString*)fileName
                                         image:(NSData *)image
                                   onSucceeded:(OperationSucceedBlock)succeedBlock
                                       onError:(OperationErrorBlock)errorBlock {
    NSMutableDictionary* p = [paramDict mutableCopy];
    NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    p[@"version"] = version;
    MKNetworkOperation *op = nil;
    op = [self operationWithPath:path params:p httpMethod:method];
    [op addData:image forKey:fileKey mimeType:@"application/octet-stream" fileName:fileName];
    [op addCompletionHandler:succeedBlock errorHandler:errorBlock];
    [self enqueueOperation:op];
    return op;
}
@end

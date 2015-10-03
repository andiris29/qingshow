//
//  QSNetworkEngine+Protect.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "MKNetworkEngine+QSExtension.h"
#import "QSNetworkHelper.h"

@implementation MKNetworkEngine (QSExtension)

- (MKNetworkOperation*)startOperationWithPath:(NSString*)path
                                       method:(NSString*)method
                                     paramers:(NSDictionary*)paramDict
                                  onSucceeded:(OperationSucceedBlock)succeedBlock
                                      onError:(OperationErrorBlock)errorBlock
{
    MKNetworkOperation* op = nil;
    NSMutableDictionary* p = [paramDict mutableCopy];
    op = [self operationWithPath:path params:p httpMethod:method ];
    NSDictionary* headers = [QSNetworkHelper generateHeader];
    for (NSString* key in headers) {
        [op setHeader:key withValue:headers[key]];
    }
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
    MKNetworkOperation *op = nil;
    op = [self operationWithPath:path params:p httpMethod:method];
    [op addData:image forKey:fileKey];
    NSDictionary* headers = [QSNetworkHelper generateHeader];
    for (NSString* key in headers) {
        [op setHeader:key withValue:headers[key]];
    }
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
    MKNetworkOperation *op = nil;
    op = [self operationWithPath:path params:p httpMethod:method];
    NSDictionary* headers = [QSNetworkHelper generateHeader];
    for (NSString* key in headers) {
        [op setHeader:key withValue:headers[key]];
    }
    [op addData:image forKey:fileKey mimeType:@"application/octet-stream" fileName:fileName];
    [op addCompletionHandler:succeedBlock errorHandler:errorBlock];
    [self enqueueOperation:op];
    return op;
}
@end

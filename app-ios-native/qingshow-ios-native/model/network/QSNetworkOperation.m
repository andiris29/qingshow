//
//  QSNetworkOperation.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/9/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSNetworkOperation.h"
#import "NSDictionary+QSExtension.h"
@implementation QSNetworkOperation
- (QSError*)checkCustomerError
{
    if (![self.responseJSON isKindOfClass:[NSDictionary class]]) {
        return nil;
    }
    NSDictionary* metaData = [self.responseJSON dictValueForKeyPath:@"metadata"];
    if (metaData) {
        NSDictionary* errorDict = [metaData dictValueForKeyPath:@"error"];
        if (errorDict) {
            self.qsError = [[QSError alloc] initWithDomain:kQSErrorDomain code:kQSErrorCodeNumber userInfo:errorDict];
            self.qsError.qsErrorCode = [errorDict stringValueForKeyPath:@"code"];
            self.qsError.qsErrorMessage = [errorDict stringValueForKeyPath:@"message"];
            return self.qsError;
        }
    }
    return nil;
}
- (void)operationSucceeded
{
    if ([self checkCustomerError]) {
        [super operationFailedWithError:self.qsError];
    }
    else {
        [super operationSucceeded];
    }

}

- (void)operationFailedWithError:(NSError *)error
{
    [self checkCustomerError];
    [super operationFailedWithError:error];
}


@end

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
        
        NSNumber* errorCode = [metaData numberValueForKeyPath:@"error"];;
        if (errorCode) {
            self.qsError = [[QSError alloc] initWithDomain:kQSErrorDomain code:errorCode.intValue userInfo:metaData];
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

//
//  QSNetworkOperation.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/9/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSNetworkOperation.h"
#import "NSDictionary+QSExtension.h"
#import "NSArray+QSExtension.h"

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

- (id)responseJSON {
    id ret = [super responseJSON];
    if ([ret isKindOfClass:[NSArray class]]) {
        return [((NSArray*)ret) deepMutableCopy];
    } else if ([ret isKindOfClass:[NSDictionary class]]) {
        return [((NSDictionary*)ret) deepMutableCopy];
    } else {
        return ret;
    }
}
@end

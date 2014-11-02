//
//  QSBlock.h
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#ifndef qingshow_ios_native_QSBlock_h
#define qingshow_ios_native_QSBlock_h

@class NSError;
@class MKNetworkOperation;


typedef void (^VoidBlock)(void);
typedef void (^ErrorBlock) (NSError* error);
typedef void (^OperationSucceedBlock)(MKNetworkOperation *completedOperation);
typedef void (^OperationErrorBlock)(MKNetworkOperation *completedOperation, NSError *error);

typedef void (^FeedingSuccessBlock)(NSArray* showArray, NSDictionary* metadata);

#endif

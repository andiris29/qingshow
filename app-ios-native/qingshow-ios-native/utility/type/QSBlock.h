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
typedef void (^ImgBlock) (UIImage* image);
typedef void (^DicBlock) (NSDictionary*);
typedef void (^StringBlock) (NSString*);
typedef void (^BoolBlock)(BOOL);
typedef BOOL (^FilterBlock)(id);
typedef void (^ErrorBlock) (NSError* error);
typedef void (^OperationSucceedBlock)(MKNetworkOperation *completedOperation);
typedef void (^OperationErrorBlock)(MKNetworkOperation *completedOperation, NSError *error);

typedef void (^ArraySuccessBlock)(NSArray* array, NSDictionary* metadata);
typedef void (^ArrayAndDictSuccessBlock)(NSArray* array, NSDictionary* context, NSDictionary* metadata);
typedef void (^EntitySuccessBlock)(NSDictionary *data, NSDictionary *metadata);
typedef MKNetworkOperation* (^ArrayNetworkBlock)(ArraySuccessBlock, ErrorBlock, int);
typedef NSArray* (^ArrayBlock)(id);
typedef void (^InputArrayBlock)(NSArray* arrayBlock);
typedef id(^IdBlock)(id);

#endif

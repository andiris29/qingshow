//
//  QSMobileVerifyModel.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/15.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <Foundation/Foundation.h>

@class QSMobileVerifyManager;

@protocol QSMobileVerifyDelegate <NSObject>

@optional
- (void)mobileVerifyManager:(QSMobileVerifyManager*)mgr remainTimeUpdate:(int)time;
- (void)mobileVerifyManagerTimerFinish:(QSMobileVerifyManager*)mgr;

@end


@interface QSMobileVerifyManager : NSObject

@property (readonly) int currentTime;

+ (QSMobileVerifyManager*)getInstance;

- (BOOL)canGetVerifyCode;
- (void)startTimer;

- (void)addDelegate:(NSObject<QSMobileVerifyDelegate>*)delegate;
- (void)removeDelegate:(NSObject<QSMobileVerifyDelegate>*)delegate;

@end

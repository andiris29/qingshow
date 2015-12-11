//
//  QSMobileVerifyModel.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/15.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSMobileVerifyManager.h"
#define MAX_COUNT_DONW_TIME 60
@interface QSMobileVerifyManager ()

@property (strong, nonatomic) NSTimer* timer;
@property (assign, nonatomic) int internalCurrentTime;


@property (strong, nonatomic) NSMutableArray* delegateArray;
@end

@implementation QSMobileVerifyManager
#pragma mark - Singleton
+ (QSMobileVerifyManager*)getInstance {
    static QSMobileVerifyManager* s_manager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        s_manager = [[QSMobileVerifyManager alloc] init];
    });
    return s_manager;
}

#pragma mark - Init
- (instancetype)init {
    self = [super init];
    if (self) {
        self.delegateArray = [@[] mutableCopy];
    }
    return self;
}

#pragma mark - 
- (BOOL)canGetVerifyCode {
    return self.timer == nil;
}

- (void)startTimer {
    if (self.timer) {
        return;
    }
    
    self.internalCurrentTime = MAX_COUNT_DONW_TIME + 1;
    self.timer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(_timerUpdateHandler) userInfo:nil repeats:YES];
    [self _timerUpdateHandler];
}
- (void)_timerUpdateHandler {
    self.internalCurrentTime -= 1;
    if (self.internalCurrentTime > 0) {
        [self _notifyRemainTimeUpdate:self.internalCurrentTime];
    } else {
        [self.timer invalidate];
        self.timer = nil;
        [self _notifyTimerFinish];
    }
}

#pragma mark - Getter and Setter
@synthesize internalCurrentTime = _internalCurrentTime;
- (int)currentTime {
    return self.internalCurrentTime;
}


#pragma mark - Delegate
- (void)addDelegate:(NSObject<QSMobileVerifyDelegate>*)delegate {
    NSValue* v = [NSValue valueWithNonretainedObject:delegate];
    [self.delegateArray addObject:v];
}
- (void)removeDelegate:(NSObject<QSMobileVerifyDelegate>*)delegate {
    NSValue* targetValue = nil;
    for (NSValue* v in self.delegateArray) {
        if (v.nonretainedObjectValue == delegate) {
            targetValue = v;
        }
    }
    if (targetValue) {
        [self.delegateArray removeObject:targetValue];
    }
}

#pragma mark Notify
- (void)_notifyRemainTimeUpdate:(int)remainTime {
    for (NSValue* v in self.delegateArray) {
        if ([v.nonretainedObjectValue conformsToProtocol:@protocol(QSMobileVerifyDelegate)]) {
            NSObject<QSMobileVerifyDelegate>* delegate = v.nonretainedObjectValue;
            if ([delegate respondsToSelector:@selector(mobileVerifyManager:remainTimeUpdate:)]) {
                [delegate mobileVerifyManager:self remainTimeUpdate:remainTime];
            }
        }
    }
}

- (void)_notifyTimerFinish {
    for (NSValue* v in self.delegateArray) {
        if ([v.nonretainedObjectValue conformsToProtocol:@protocol(QSMobileVerifyDelegate)]) {
            NSObject<QSMobileVerifyDelegate>* delegate = v.nonretainedObjectValue;
            if ([delegate respondsToSelector:@selector(mobileVerifyManagerTimerFinish:)]) {
                [delegate mobileVerifyManagerTimerFinish:self];
            }
        }
    }
}

@end

//
//  QSRandomUtil.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/1/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSRandomUtil.h"
#import <time.h>

@implementation QSRandomUtil

+ (void)initialize {
    [super initialize];
     srandom((unsigned int) time((time_t *)NULL));
}

+ (int)randomRangeFrom:(int)from to:(int)to {
    if (to < from) {
        return 0;
    }
    return from + arc4random() % (to - from);
}
@end

//
//  QSBigImageDateView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/6/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSBigImageDateView.h"

@implementation QSBigImageDateView
+ (instancetype)makeView
{
    return [[UINib nibWithNibName:@"QSBigImageDateView" bundle:nil] instantiateWithOwner:self options:nil][0];
}

- (void)bindWithDate:(NSDate*)date
{
#warning TODO
}
@end

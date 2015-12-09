//
//  QSCreateTradeReceiverInfoTitleCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeReceiverInfoTitleCell.h"
#import <QuartzCore/QuartzCore.h>

@implementation QSCreateTradeReceiverInfoTitleCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    self.locationBtn.layer.masksToBounds = YES;
    self.locationBtn.layer.borderWidth = 1.f;
    self.locationBtn.layer.borderColor = [UIColor colorWithRed:40.f/255.f green:45.f/255.f blue:91.f/255.f alpha:1.f].CGColor;
    self.locationBtn.layer.cornerRadius = 4.f;
}

@end

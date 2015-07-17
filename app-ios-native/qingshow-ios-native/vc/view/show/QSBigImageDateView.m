//
//  QSBigImageDateView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/6/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSBigImageDateView.h"
#import "QSDateUtil.h"

@implementation QSBigImageDateView
+ (instancetype)makeView
{
    return [[UINib nibWithNibName:@"QSBigImageDateView" bundle:nil] instantiateWithOwner:self options:nil][0];
}

- (void)bindWithDate:(NSDate*)date
{
    if (!date) {
        self.hidden = YES;
        return;
    } else {
        self.hidden = NO;
    }
    self.yearLabel.text = [QSDateUtil getYearDesc:date];
    self.monthLabel.text = [QSDateUtil getMonthDesc:date];
    self.dayLabel.text = [QSDateUtil getDayDesc:date];
    self.weekLabel.text = [QSDateUtil getWeekdayDesc:date];
}
@end

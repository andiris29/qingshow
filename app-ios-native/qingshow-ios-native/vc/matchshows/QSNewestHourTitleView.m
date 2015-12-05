//
//  QSNewestHourHeaderView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/25.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSNewestHourTitleView.h"
#import "UINib+QSExtension.h"
#import "QSDateUtil.h"

@implementation QSNewestHourTitleView

+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QSNewestHourTitleView"];
}

- (void)bindWithDate:(NSDate*)date showTime:(BOOL)fShowTime {
    if (fShowTime) {
        self.timeLabel.text = [QSDateUtil buildDotStringFromDate:date];
    } else {
        self.timeLabel.text = [QSDateUtil buildDayStringFromDate:date];
    }

    
}
@end

//
//  QSTimeCollectionViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/1/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSTimeCollectionViewCell.h"
#import "QSDateUtil.h"
#import <QuartzCore/QuartzCore.h>
@implementation QSTimeCollectionViewCell

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}
- (void)awakeFromNib
{
    self.layer.cornerRadius = 4;
    self.layer.masksToBounds = YES;
}
- (void)bindWithMetadata:(NSDictionary*)metaData
{
    if (!metaData) {
        return;
    }
    
    NSString* timeStr = metaData[@"refreshTime"];
    if (!timeStr || ![timeStr isKindOfClass:[NSString class]] || !timeStr.length) {
        return;
    }
    NSDate* date = [QSDateUtil buildDateFromResponseString:timeStr];
    self.timeLabel.text = [NSString stringWithFormat:@"%@更新",[QSDateUtil getTime:date]];
    self.weekdayLabel.text = [QSDateUtil getWeek:date];
    self.ymdLabel.text = [QSDateUtil getMYD:date];
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end

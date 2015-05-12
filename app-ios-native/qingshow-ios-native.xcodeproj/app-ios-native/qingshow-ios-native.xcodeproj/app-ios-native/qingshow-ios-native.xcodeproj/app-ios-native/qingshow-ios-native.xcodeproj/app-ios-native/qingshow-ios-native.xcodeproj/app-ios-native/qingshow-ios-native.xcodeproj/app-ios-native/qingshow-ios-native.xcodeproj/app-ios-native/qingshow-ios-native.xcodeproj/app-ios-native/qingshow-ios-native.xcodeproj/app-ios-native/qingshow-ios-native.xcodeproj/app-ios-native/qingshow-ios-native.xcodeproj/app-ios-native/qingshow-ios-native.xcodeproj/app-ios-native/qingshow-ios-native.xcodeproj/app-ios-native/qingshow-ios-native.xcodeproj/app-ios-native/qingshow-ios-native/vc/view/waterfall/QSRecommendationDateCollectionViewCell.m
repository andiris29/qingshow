//
//  QSRecommendationDateCollectionViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/9/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSRecommendationDateCollectionViewCell.h"
#import "QSDateUtil.h"
@implementation QSRecommendationDateCollectionViewCell

- (void)awakeFromNib {
    // Initialization code
}
- (void)bindWithDate:(QSRecommendationDateCellModel*)model;
{
    NSDate* date = model.date;
    
    self.dayLabel.text = [QSDateUtil getDayDesc:date];
    self.monthLabel.text = [QSDateUtil getMonthDesc:date];
    self.yearLabel.text = [QSDateUtil getYearDesc:date];
    self.weekdayLabel.text = [QSDateUtil getWeekdayDesc:date];
    self.descLabel.text = model.desc;
}
@end

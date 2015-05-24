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
    float rate = ([UIScreen mainScreen].bounds.size.width - 4) / 2.f /158.f;
    self.contentView.transform = CGAffineTransformMakeScale(rate, rate);
}
- (void)bindWithDate:(QSRecommendationDateCellModel*)model;
{
    NSDate* date = model.date;
    
    self.dayLabel.text = [QSDateUtil getDayDesc:date];
    self.monthLabel.text = [QSDateUtil getMonthDesc:date];
    self.yearLabel.text = [QSDateUtil getYearDesc:date];
    self.weekdayLabel.text = [QSDateUtil getWeekdayDesc:date];
    self.descLabel.text = model.desc;
    
    NSArray* backgroundColorArray = @[[NSNull null],
                                      [UIColor colorWithRed:113.f/255.f green:218.f/255.f blue:190.f/255.f alpha:1.f],//71dabe
                                      [UIColor colorWithRed:240.f/255.f green:149.f/255.f blue:164.f/255.f alpha:1.f],//f095a4
                                      [UIColor colorWithRed:120.f/255.f green:194.f/255.f blue:224.f/255.f alpha:1.f],//80c2e0
                                      [UIColor colorWithRed:160.f/255.f green:197.f/255.f blue:142.f/255.f alpha:1.f],//a0c58e
                                      [UIColor colorWithRed:212.f/255.f green:175.f/255.f blue:96.f/255.f alpha:1.f],//d4af60
                                      [UIColor colorWithRed:137.f/255.f green:140.f/255.f blue:194.f/255.f alpha:1.f],//898cc2
                                      [UIColor colorWithRed:214.f/255.f green:149.f/255.f blue:187.f/255.f alpha:1.f]//d695bb
                                      ];
    int index = [QSDateUtil  getWeekdayIndex:date];
    if (index > 0 && index < backgroundColorArray.count) {
        UIColor* c = backgroundColorArray[index];
        self.backgroundColor = c;
    }
    /*
     周一  71dabe
     周二  f095a4
     周三  80c2e0
     周四  a0c58e
     周五  d4af60
     周六  898cc2
     周日  d695bb
     */
}
@end

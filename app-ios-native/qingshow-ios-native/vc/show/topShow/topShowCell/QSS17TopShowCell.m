//
//  QSS17TopShowCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/21.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS17TopShowCell.h"
#import "QSShowUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSDateUtil.h"
#import "QSImageNameUtil.h"

@implementation QSS17TopShowCell

- (void)awakeFromNib {
    // Initialization code
    self.autoresizingMask = NO;
   
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}



- (void)bindWithDataDic:(NSDictionary *)fDic andAnotherDic:(NSDictionary *)sDic
{
    
    [self.leftImgView setImageFromURL:[QSImageNameUtil appendImageNameUrl:[QSShowUtil getCoverUrl:fDic] type:QSImageNameTypeS]];
    [self.rightImgView setImageFromURL:[QSImageNameUtil appendImageNameUrl:[QSShowUtil getCoverUrl:sDic] type:QSImageNameTypeS]];
    self.colorView.backgroundColor = [UIColor grayColor];
    self.favoNumLabel.text = [QSShowUtil getNumberCommentsDescription:fDic];
    NSDate *date = [QSShowUtil getRecommendDate:fDic];
    NSString *day = [QSDateUtil getDayDesc:date];
    NSString *month = [QSDateUtil getMonthDesc:date];
    self.dayLabel.text = [NSString stringWithFormat:@"%@ %@",day,month];
    self.yearLabel.text = [QSDateUtil getYearDesc:date];
    NSString *str = [QSDateUtil getWeek:date];
    NSLog(@"str = %@",str);
    
    if ([str isEqualToString:@"星期一MON"]) {
        self.weekLabel.text = @"MON.";
    }
    else if([str isEqualToString:@"星期二TUE"])
    {
        self.weekLabel.text = @"TUE.";
    }
    else if([str isEqualToString:@"星期三WED"])
    {
        self.weekLabel.text = @"WED.";
    }
    else if([str isEqualToString:@"星期四THU"])
    {
        self.weekLabel.text = @"THUR.";
    }
    else if([str isEqualToString:@"星期五FRI"])
    {
        self.weekLabel.text = @"FRI.";
    }
    else if([str isEqualToString:@"星期六SAT"])
    {
        self.weekLabel.text = @"SAT.";
    }
    else if([str isEqualToString:@"星期日SUN"])
    {
        self.weekLabel.text = @"SUN.";
    }
    
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
        self.colorView.backgroundColor = c;
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
//- (IBAction)favoBtnPressed:(UIButton *)sender {
//    if (_isFavoBtnPressed == NO) {
//        
//        [sender setImage:[UIImage imageNamed:@"s03_like_btn_hover.png"] forState:UIControlStateNormal];
//        _isFavoBtnPressed = YES;
//    }
//    else if(_isFavoBtnPressed == YES)
//    {
//        
//        [sender setImage:[UIImage imageNamed:@"s03_like_btn_full.png"] forState:UIControlStateNormal];
//        _isFavoBtnPressed = NO;
//    }
//}
@end

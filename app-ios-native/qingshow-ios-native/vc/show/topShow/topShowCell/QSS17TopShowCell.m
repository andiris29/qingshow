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
    [self.leftImgView setImageFromURL:[QSShowUtil getCoverUrl:fDic]];
    [self.rightImgView setImageFromURL:[QSShowUtil getCoverUrl:sDic]];
    self.favoNumLabel.text = [QSShowUtil getNumberCommentsDescription:fDic];
    NSDate *date = [QSShowUtil getRecommendDate:fDic];
    NSString *day = [QSDateUtil getDayDesc:date];
    NSString *month = [QSDateUtil getMonthDesc:date];
    self.dayLabel.text = [NSString stringWithFormat:@"%@ %@",day,month];
    self.yearLabel.text = [QSDateUtil getYearDesc:date];
    self.weekLabel.text = [QSDateUtil getWeekdayDesc:date];
    
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

//
//  QSU15BonusListTableViewCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/8/31.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU15BonusListTableViewCell.h"
#import "QSPeopleUtil.h"
#import "QSBonusUtil.h"
#import "QSDateUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
@implementation QSU15BonusListTableViewCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)bindWithDict:(NSDictionary *)dict
{
    self.nameLabel.text = [QSBonusUtil getNote:dict];
    NSString *dateStr = [QSBonusUtil getCreate:dict];
    NSDate *date = [QSDateUtil buildDateFromResponseString:dateStr];
    self.dateLabel.text = [QSDateUtil buildStringFromDate:date];
    NSNumber* money = [QSBonusUtil getMoney:dict];
    double m = 0.0;
    if (money) {
        m = money.doubleValue;
    }
    self.priceLabel.text = [NSString stringWithFormat:@"￥%.2f",m];
    [self.headerImgView setImageFromURL:[NSURL URLWithString:[QSBonusUtil getIcon:dict]]];
}
- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end

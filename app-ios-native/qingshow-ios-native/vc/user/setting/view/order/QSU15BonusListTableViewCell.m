//
//  QSU15BonusListTableViewCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/8/31.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU15BonusListTableViewCell.h"
#import "QSPeopleUtil.h"
#import "QSDateUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
@implementation QSU15BonusListTableViewCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)bindWithDict:(NSDictionary *)dict
{
    self.nameLabel.text = [QSPeopleUtil getNoteFromBonusDict:dict];
    NSString *dateStr = [QSPeopleUtil getCreateFromBonusDict:dict];
    NSDate *date = [QSDateUtil buildDateFromResponseString:dateStr];
    self.dateLabel.text = [QSDateUtil buildStringFromDate:date];
    self.priceLabel.text = [NSString stringWithFormat:@"￥%@",[QSPeopleUtil getMoneyFromBonusDict:dict]];
    [self.headerImgView setImageFromURL:[NSURL URLWithString:[QSPeopleUtil getIconFromBonusDict:dict]]];
}
- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end

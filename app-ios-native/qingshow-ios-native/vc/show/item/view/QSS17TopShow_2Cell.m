//
//  QSS17TopShow_2Cell.m
//  qingshow-ios-native
//
//  Created by ching show on 15/5/4.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS17TopShow_2Cell.h"

@implementation QSS17TopShow_2Cell

- (void)awakeFromNib {
    // Initialization code
     self.backgroundColor = [UIColor colorWithRed:223.f / 255.f green:223.f / 255.f blue:223.f / 255.f alpha:1.0];
    self.backImage.transform = CGAffineTransformMakeRotation(-M_1_PI / 4);
    self.likeButton.transform = CGAffineTransformMakeRotation(-M_1_PI / 2);
    self.backView.backgroundColor = [UIColor colorWithRed:227.f / 255.f green:195.f / 255.f blue:208.f / 255.f alpha:1.0];
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end

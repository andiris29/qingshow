//
//  QSS17TopShow_5Cell.m
//  qingshow-ios-native
//
//  Created by Han Hugh on 15/5/14.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS17TopShow__5Cell.h"

@implementation QSS17TopShow__5Cell

- (void)awakeFromNib {
    // Initialization code
    self.backgroundColor = [UIColor colorWithRed:223.f / 255.f green:223.f / 255.f blue:223.f / 255.f alpha:1.0];
    self.backImage.transform = CGAffineTransformMakeRotation(M_1_PI / 4);
    self.likeButton.transform = CGAffineTransformMakeRotation(-M_1_PI / 2);
    self.backView.backgroundColor = [UIColor colorWithRed:194.f / 255.f green:222.f / 255.f blue:211.f / 255.f alpha:1.0];
    self.selectionStyle = UITableViewCellSelectionStyleNone;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end

//
//  QSS17TopShow_1Cell.m
//  qingshow-ios-native
//
//  Created by Han Hugh on 15/5/14.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS17TopShow__1Cell.h"

@implementation QSS17TopShow__1Cell

- (void)awakeFromNib {
    // Initialization code
    self.backImage.transform = CGAffineTransformMakeRotation(M_1_PI / 4);
    self.likeButton.transform = CGAffineTransformMakeRotation(-M_1_PI / 2);
    self.backView.backgroundColor = [UIColor colorWithRed:187.f / 255.f green:217.f / 255.f blue:218.f / 255.f alpha:1.0];
    self.backgroundColor = [UIColor colorWithRed:223.f / 255.f green:223.f / 255.f blue:223.f / 255.f alpha:1.0];
    self.selectionStyle = UITableViewCellSelectionStyleNone;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end

//
//  QSS17TopShow_1Cell.m
//  qingshow-ios-native
//
//  Created by ching show on 15/5/4.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS17TopShow_1Cell.h"

@implementation QSS17TopShow_1Cell

- (void)awakeFromNib {
    self.backImage.transform = CGAffineTransformMakeRotation(M_1_PI / 4);
    self.likeButton.transform = CGAffineTransformMakeRotation(-M_1_PI / 4);
    self.backView.backgroundColor = [UIColor colorWithRed:187.f / 255.f green:217.f / 255.f blue:218.f / 255.f alpha:1.0];
    self.backgroundColor = [UIColor colorWithRed:223.f / 255.f green:223.f / 255.f blue:223.f / 255.f alpha:1.0];
    self.selectionStyle = UITableViewCellSelectionStyleNone;    
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end

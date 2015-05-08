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
    self.bacImage.transform = CGAffineTransformMakeRotation(M_1_PI / 4);
    self.samLabel.transform = CGAffineTransformMakeRotation(-M_1_PI / 4);
    
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end

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
    
    self.bigImage.transform = CGAffineTransformMakeRotation(-M_1_PI / 4);
    //self.samLabel.transform = CGAffineTransformMakeRotation(-M_1_PI / 2);
//    self.samLabel.layer.transform = CGAffineTransformMakeRotation(-M_1_PI / 4);
    self.samLabel.layer.transform = CATransform3DMakeAffineTransform(CGAffineTransformMakeRotation(-M_1_PI / 4));
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end

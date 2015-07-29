//
//  QSAbstractDiscountTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSAbstractDiscountTableViewCell.h"

@implementation QSAbstractDiscountTableViewCell
+ (instancetype)generateCell {
    return nil;
}
- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (CGFloat)getHeight:(NSDictionary*)itemDict {
    return 44.f;
}
- (void)bindWithData:(NSDictionary*)itemDict {
    
}

@end

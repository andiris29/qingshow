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
    
    CGFloat width = DISCOUNT_CELL_WIDTH;
    CGRect frame = self.frame;
    frame.size.width = width;
    self.frame = frame;
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    self.backgroundColor = [UIColor clearColor];
    self.contentView.backgroundColor = [UIColor clearColor];
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

- (void)layoutSubviews {
    [super layoutSubviews];
}

@end

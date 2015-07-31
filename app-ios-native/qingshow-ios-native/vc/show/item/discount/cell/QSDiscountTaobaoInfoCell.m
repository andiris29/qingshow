//
//  QSDiscountTaobaoInfoCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSDiscountTaobaoInfoCell.h"
#import "UINib+QSExtension.h"
@implementation QSDiscountTaobaoInfoCell

- (void)awakeFromNib {
    // Initialization code
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

+ (instancetype)generateCell {
    return [UINib generateViewWithNibName:@"QSDiscountTaobaoInfoCell"];
}

- (CGFloat)getHeight:(NSDictionary*)itemDict {
    return 76.f;
}
@end

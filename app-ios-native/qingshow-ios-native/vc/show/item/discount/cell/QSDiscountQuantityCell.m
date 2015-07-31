//
//  QSDiscountQuantityCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSDiscountQuantityCell.h"
#import "UINib+QSExtension.h"
@implementation QSDiscountQuantityCell

- (void)awakeFromNib {
    // Initialization code
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

+ (instancetype)generateCell {
    return [UINib generateViewWithNibName:@"QSDiscountQuantityCell"];
}

- (IBAction)increaseBtnPressed:(id)sender {
    
}

- (IBAction)decreaseBtnPressed:(id)sender {
    
}

- (CGFloat)getHeight:(NSDictionary*)itemDict {
    return 52.f;
}

@end

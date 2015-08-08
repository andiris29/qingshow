//
//  QS11OrderInfoCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/8/7.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QS11OrderInfoCell.h"
#import "UINib+QSExtension.h"
@implementation QS11OrderInfoCell
+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QS11OrderInfoCell"];
}

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)bindWithDict:(NSDictionary*)tradeDict {
    
}
@end

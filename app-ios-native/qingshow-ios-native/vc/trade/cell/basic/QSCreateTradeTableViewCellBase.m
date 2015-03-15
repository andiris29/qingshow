//
//  QSCreateTradeTableViewCellBase.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeTableViewCellBase.h"

@implementation QSCreateTradeTableViewCellBase

- (void)awakeFromNib {
    // Initialization code
    [super awakeFromNib];
    
    if ([self respondsToSelector:@selector(setSeparatorInset:)]) {
        self.separatorInset = UIEdgeInsetsZero;
    }
    if ([self respondsToSelector:@selector(setLayoutMargins:)]) {
        self.layoutMargins = UIEdgeInsetsZero;
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)bindWithDict:(NSDictionary*)dict
{
}

- (CGFloat)getHeightWithDict:(NSDictionary*)dict
{
    return 0;
}
- (id)getInputData
{
    return nil;
}
@end

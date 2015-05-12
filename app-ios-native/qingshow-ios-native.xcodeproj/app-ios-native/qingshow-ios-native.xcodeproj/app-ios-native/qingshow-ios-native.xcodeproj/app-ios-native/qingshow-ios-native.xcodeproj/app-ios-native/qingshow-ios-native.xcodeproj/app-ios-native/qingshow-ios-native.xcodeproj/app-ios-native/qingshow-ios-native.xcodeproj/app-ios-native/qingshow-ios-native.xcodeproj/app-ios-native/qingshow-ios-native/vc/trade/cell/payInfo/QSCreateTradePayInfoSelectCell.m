//
//  QSCreateTradePayInfoSelectCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradePayInfoSelectCell.h"

@implementation QSCreateTradePayInfoSelectCell
- (void)awakeFromNib
{
    _isSelect = NO;
}
- (void)setIsSelect:(BOOL)isSelect
{
    _isSelect = isSelect;
    self.selectedBtn.highlighted = _isSelect;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end

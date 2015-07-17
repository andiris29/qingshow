//
//  QSCreateTradeItemInfoColorCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/28/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeItemInfoColorCell.h"
#import "QSItemUtil.h"

@implementation QSCreateTradeItemInfoColorCell

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/
- (void)bindWithDict:(NSDictionary*)dict
{
    self.colorLabel.text = [QSItemUtil getItemColorDesc:dict];
}
@end

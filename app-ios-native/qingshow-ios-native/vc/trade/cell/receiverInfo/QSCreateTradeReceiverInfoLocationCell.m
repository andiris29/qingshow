//
//  QSCreateTradeReceiverInfoLocationCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeReceiverInfoLocationCell.h"

@implementation QSCreateTradeReceiverInfoLocationCell
- (void)awakeFromNib
{
    [super awakeFromNib];
//    self.selectionStyle = UITableViewCellSelectionStyleDefault;
}
- (id)getInputData
{
    if (self.label.text.length) {
        return self.label.text;
    } else {
        return nil;
    }
}
@end

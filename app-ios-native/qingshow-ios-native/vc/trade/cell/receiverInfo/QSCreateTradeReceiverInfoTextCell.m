//
//  QSCreateTradeReceiverInfoTextCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeReceiverInfoTextCell.h"

@implementation QSCreateTradeReceiverInfoTextCell

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/
- (void)hideKeyboard
{
    [self.textField resignFirstResponder];
}
- (id)getInputData
{
    if (self.textField.text.length) {
        return self.textField.text;
    } else {
        return nil;
    }
}
@end

//
//  QSCreateTradeShoeSizeCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/28/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeShoeSizeCell.h"

@implementation QSCreateTradeShoeSizeCell

- (void)awakeFromNib {
    self.textField.delegate = self;
}
- (void)hideKeyboard {
    [self.textField resignFirstResponder];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [self hideKeyboard];
    return YES;
}
@end

//
//  QSCreateTradePayInfoSelectCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradePayInfoSelectCell.h"
#import "UIView+QSExtension.h"

@interface QSCreateTradePayInfoSelectCell ()

@end

@implementation QSCreateTradePayInfoSelectCell
- (void)setPaymentType:(QSCreateTradePaymentType)paymentType {
    _paymentType = paymentType;
    if (_paymentType == QSCreateTradePaymentTypeWechat) {
        [self _setSelect:self.wechatBtn];
    } else {
        [self _setUnselect:self.wechatBtn];
    }
    if (_paymentType == QSCreateTradePaymentTypeAlipay) {
        [self _setSelect:self.alipayBtn];
    } else {
        [self _setUnselect:self.alipayBtn];
    }
}

- (void)_setUnselect:(UIButton*)btn {
    [btn configBorderColor:[UIColor colorWithRed:149.f/255.f green:149.f/255.f blue:149.f/255.f alpha:1.f] width:1 cornerRadius:4];
}
- (void)_setSelect:(UIButton*)btn {
    [btn configBorderColor:[UIColor colorWithRed:40.f/255.f green:45.f/255.f blue:91.f/255.f alpha:1.f] width:2 cornerRadius:4];
}

- (void)awakeFromNib
{
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    self.paymentType = QSCreateTradePaymentTypeNone;
}

- (CGFloat)getHeightWithDict:(NSDictionary*)dict {
    return 100.f;
}

- (IBAction)wechatBtnPressed:(id)sender {
    self.paymentType = QSCreateTradePaymentTypeWechat;
}
- (IBAction)alipayBtnPressed:(id)sender {
    self.paymentType = QSCreateTradePaymentTypeAlipay;
}
@end

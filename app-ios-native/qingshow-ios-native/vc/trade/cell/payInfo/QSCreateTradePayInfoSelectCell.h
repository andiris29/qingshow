//
//  QSCreateTradePayInfoSelectCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeTableViewCellBase.h"

typedef NS_ENUM(NSUInteger, QSCreateTradePaymentType)
{
    QSCreateTradePaymentTypeNone,
    QSCreateTradePaymentTypeWechat,
    QSCreateTradePaymentTypeAlipay
};

@interface QSCreateTradePayInfoSelectCell : QSCreateTradeTableViewCellBase

@property (weak, nonatomic) IBOutlet UIButton* wechatBtn;
@property (weak, nonatomic) IBOutlet UIButton* alipayBtn;

@property (assign, nonatomic) QSCreateTradePaymentType paymentType;

- (IBAction)wechatBtnPressed:(id)sender;
- (IBAction)alipayBtnPressed:(id)sender;
@end

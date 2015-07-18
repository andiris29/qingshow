//
//  QSOrderListTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSOrderListTableViewCell.h"
#import <QuartzCore/QuartzCore.h>
#import "QSTradeUtil.h"
#import "QSOrderUtil.h"
#import "QSItemUtil.h"
#import "QSTaobaoInfoUtil.h"
#import "QSCommonUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSNetworkKit.h"
#import "QSTradeStatus.h"

@interface QSOrderListTableViewCell ()<UIAlertViewDelegate>

@property (strong, nonatomic) NSDictionary* tradeDict;

@property (assign, nonatomic) float skuLabelBaseY;
@end

@implementation QSOrderListTableViewCell

#pragma mark - Init Config
- (void)awakeFromNib {
    // Initialization code
    [self configBtn:self.submitButton];
    [self configBtn:self.returnButton];
    [self configBtn:self.exchangeButton];
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    self.skuLabelBaseY = self.sizeTextLabel.frame.origin.y+30;
}
- (void)configBtn:(UIButton*)btn
{
    UIColor* color = btn.titleLabel.textColor;
    btn.layer.borderColor = color.CGColor;
    btn.layer.borderWidth = 1.f;
    btn.layer.cornerRadius = 4.f;
    btn.layer.masksToBounds = YES;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}


#pragma mark - Binding
- (void)bindWithDict:(NSDictionary*)tradeDict
{

    self.tradeDict = tradeDict;
    NSArray* orderList = [QSTradeUtil getOrderArray:tradeDict];
    NSDictionary* orderDict = nil;
    if (orderList.count) {
        orderDict = orderList[0];
    }
    NSDictionary* itemDict = [QSOrderUtil getItemSnapshot:orderDict];
    
    self.stateLabel.text = [QSTradeUtil getStatusDesc:tradeDict];
    self.titleLabel.text = [QSItemUtil getItemName:itemDict];
    [self.itemImgView setImageFromURL:[QSItemUtil getFirstImagesUrl:itemDict]];
    
    self.priceLabel.text = [QSOrderUtil getPriceDesc:orderDict];
    self.quantityLabel.text = [QSOrderUtil getQuantityDesc:orderDict];


    float height = self.skuLabelBaseY;

    
    for (UIView* view in @[self.quantityLabel, self.quantityTextLabel, self.priceLabel, self.priceTextLabel]) {
        [self updateView:view y:height];
    }
    
    NSNumber* status = [QSTradeUtil getStatus:tradeDict];
    
    QSTradeStatus s = status.integerValue;

    switch (s) {
        case QSTradeStatusUnpaid:
        {
            self.submitButton.hidden = NO;
            self.returnButton.hidden = YES;
            self.exchangeButton.hidden = YES;
            [self.submitButton setTitle:@"付款" forState:UIControlStateNormal];
            break;
        }
        case QSTradeStatusFahuo:
        case QSTradeStatusHuanhuoFachu: {
            self.submitButton.hidden = NO;
            self.exchangeButton.hidden = NO;
            self.returnButton.hidden = NO;
            [self.submitButton setTitle:@"确认付款" forState:UIControlStateNormal];
            break;
        }
        default: {
            self.submitButton.hidden = YES;
            self.exchangeButton.hidden = YES;
            self.returnButton.hidden = YES;
            break;
        }
    }
}
- (void)updateView:(UIView*)view y:(float)y
{
    CGRect rect = view.frame;
    rect.origin.y = y;
    view.frame = rect;
}

#pragma mark - IBAction
- (IBAction)refundBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickRefundBtnForCell:)]) {
        [self.delegate didClickRefundBtnForCell:self];
    }
}
- (IBAction)submitBtnPressed:(id)sender
{
    int status = [QSTradeUtil getStatus:self.tradeDict].intValue;
    if (status == 0) {
        [self payBtnPressed];
    } else if (status > 0 && status < 5) {
        if ([self.delegate respondsToSelector:@selector(didClickReceiveBtnForCell:)]) {
            [self.delegate didClickReceiveBtnForCell:self];
        }
    }
}

- (IBAction)returnBtnPressed:(id)sender {
   [self refundBtnPressed:sender];

}

- (IBAction)exchangeBtnPressed:(id)sender {
    
    if ([self.delegate respondsToSelector:@selector(didClickExchangeBtnForCell:)]) {
        [self.delegate didClickExchangeBtnForCell:self];
    }
}

- (void)payBtnPressed
{
    if ([self.delegate respondsToSelector:@selector(didClickPayBtnForCell:)]) {
        [self.delegate didClickPayBtnForCell:self];
    }
}

@end

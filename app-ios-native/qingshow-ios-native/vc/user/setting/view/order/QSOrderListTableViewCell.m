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
#import "QSEntityUtil.h"
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
    self.saleImgView.hidden = YES;
    self.selectionStyle = UITableViewCellSelectionStyleNone;
//    self.skuLabelBaseY = self.sizeTextLabel.frame.origin.y+30;
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

- (void)layoutSubviews
{
    [super layoutSubviews];
    if (self.type == 0) {
        self.quantityLabel.frame = CGRectMake(112, 76, 190, 14);;
        self.priceLabel.frame = CGRectMake(112, 98, 190, 14);
    }else{
        self.quantityLabel.frame = CGRectMake(112, 120, 190, 14);
        self.priceLabel.frame = CGRectMake(112, 142, 190, 14);
    }
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
    [self.itemImgView setImageFromURL:[QSItemUtil getThumbnail:itemDict]];
    self.sizeLabel.text = [QSOrderUtil getSizeText:orderDict];
    self.colorLabel.text = [QSOrderUtil getColorText:orderDict];
    if ([QSOrderUtil getActualPrice:orderDict]) {
        self.priceLabel.text = [NSString stringWithFormat:@"折后价格：￥%@",[QSOrderUtil getActualPriceDesc:orderDict]];
    } else {
        self.priceLabel.text = [NSString stringWithFormat:@"折后价格：￥%@",[QSOrderUtil getExpectedPriceDesc:orderDict]];
    }
    self.quantityLabel.text = [NSString stringWithFormat:@"数量：%@",[QSOrderUtil getQuantityDesc:orderDict]];

    NSNumber* status = [QSTradeUtil getStatus:tradeDict];
    QSTradeStatus s = status.integerValue;
    BOOL shouldShare = [QSTradeUtil getTraddSharedByCurrentUser:tradeDict];
    switch (s) {
        case 0:
        case 2:
        {
            self.submitButton.hidden = NO;
            self.returnButton.hidden = YES;
            self.exchangeButton.hidden = YES;
            self.saleImgView.hidden = YES;
            [self.submitButton setTitle:@"取消订单" forState:UIControlStateNormal];
            break;
        }
        case 1:{
            self.submitButton.hidden = NO;
            self.exchangeButton.hidden = YES;
            self.returnButton.hidden = NO;
            self.saleImgView.hidden = NO;
            [self.returnButton setTitle:@"取消订单" forState:UIControlStateNormal];
            if (!shouldShare) {
                [self.submitButton setTitle:@"立即付款" forState:UIControlStateNormal];
            }
            else{
            [self.submitButton setTitle:@"分享并付款" forState:UIControlStateNormal];
            }
            break;
        }
        case 3: {
            self.submitButton.hidden = NO;
            self.exchangeButton.hidden = NO;
            self.returnButton.hidden = NO;
            self.saleImgView.hidden = YES;
            [self.submitButton setTitle:@"确认收货" forState:UIControlStateNormal];
            break;
        }
         default: {
            self.submitButton.hidden = YES;
            self.exchangeButton.hidden = YES;
            self.returnButton.hidden = YES;
             self.saleImgView.hidden = YES;
            break;
        }
    }
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
    if (status == 1) {
        
        BOOL shouldShare = [QSTradeUtil getTraddSharedByCurrentUser:self.tradeDict];
        [self payBtnPressed:shouldShare];
        
    } else if (status  == 3) {
        if ([self.delegate respondsToSelector:@selector(didClickReceiveBtnForCell:)]) {
            [self.delegate didClickReceiveBtnForCell:self];
        }
    }
    else if(status == 2 || status == 0)
    {
        [self.delegate didClickCancelBtnForCell:self];
    }
}

- (IBAction)returnBtnPressed:(id)sender {
    int status = [QSTradeUtil getStatus:self.tradeDict].intValue;
    if ( status == 1 ) {
            [self.delegate didClickCancelBtnForCell:self];
    }
    else if(status == 3)
    {
        [self refundBtnPressed:sender];
    }
}

- (IBAction)exchangeBtnPressed:(id)sender {
    
    if ([self.delegate respondsToSelector:@selector(didClickExchangeBtnForCell:)]) {
        [self.delegate didClickExchangeBtnForCell:self];
    }
}

- (void)payBtnPressed:(BOOL)shoudShare
{
    if ([self.delegate respondsToSelector:@selector(didClickPayBtnForCell: ShouldShare:)]) {
        [self.delegate didClickPayBtnForCell:self ShouldShare:shoudShare];
    }
}

@end

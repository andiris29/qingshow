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

@implementation QSOrderListTableViewCell

#pragma mark - Init Config
- (void)awakeFromNib {
    // Initialization code
    [self configBtn:self.refundButton];
    [self configBtn:self.logisticButton];
    [self configBtn:self.submitButton];
    self.selectionStyle = UITableViewCellSelectionStyleNone;
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
    NSArray* orderList = [QSTradeUtil getOrderArray:tradeDict];
    NSDictionary* orderDict = nil;
    if (orderList.count) {
        orderDict = orderList[0];
    }
    NSDictionary* itemDict = [QSOrderUtil getItemSnapshot:orderDict];
    NSDictionary* taobaoInfo = [QSItemUtil getTaobaoInfo:itemDict];
    
//    self.orderIdLabel.text = [QSCommonUtil getIdOrEmptyStr:tradeDict];
    self.stateLabel.text = [QSTradeUtil getStatusDesc:tradeDict];
    self.titleLabel.text = [QSItemUtil getItemName:itemDict];
    [self.itemImgView setImageFromURL:[QSItemUtil getFirstImagesUrl:itemDict]];
    
    self.priceLabel.text = [QSOrderUtil getPriceDesc:orderDict];
    self.quantityLabel.text = [QSOrderUtil getQuantityDesc:orderDict];

    NSString* sku = [QSOrderUtil getSkuId:orderDict];
    NSDictionary* skuDict = [QSTaobaoInfoUtil findSkusWithSkuId:sku taobaoInfo:taobaoInfo];
    
    self.sizeLabel.text = [QSTaobaoInfoUtil getSizeOfSku:skuDict];
    self.colorLabel.text = [QSTaobaoInfoUtil getColorOfSku:skuDict];
    
}
#pragma mark - Getter And Setter
- (void)setType:(QSOrderListTableViewCellType)type
{
    _type = type;
    BOOL fHiddenLabel = NO;
    switch (type) {
        case QSOrderListTableViewCellTypeComplete:
        {
            self.stateLabel.text = @"交易完成";
            fHiddenLabel = NO;

            break;
        }
        case QSOrderListTableViewCellTypeWaiting:
        {
            self.stateLabel.text = @"待收货";
            fHiddenLabel = YES;
            break;
        }
        default:
        {
            break;
        }
    }
    self.dateEndLabel.hidden = fHiddenLabel;
    self.dateEndTextLabel.hidden = fHiddenLabel;
    self.dateStartLabel.hidden = fHiddenLabel;
    self.dateStartTextLabel.hidden = fHiddenLabel;
    self.submitButton.hidden = !fHiddenLabel;
    self.refundButton.hidden = !fHiddenLabel;
    self.logisticButton.hidden = !fHiddenLabel;
}

#pragma mark - IBAction
- (IBAction)refundBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickRefundBtnForCell:)]) {
        [self.delegate didClickRefundBtnForCell:self];
    }
}
- (IBAction)logisticBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickLogisticBtnForCell:)]) {
        [self.delegate didClickLogisticBtnForCell:self];
    }
}
- (IBAction)submitBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickSubmitBtnForCell:)]) {
        [self.delegate didClickSubmitBtnForCell:self];
    }
}
@end

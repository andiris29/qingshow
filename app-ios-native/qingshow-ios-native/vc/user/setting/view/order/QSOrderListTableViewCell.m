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
#import "QSItemUtil.h"
#import "QSEntityUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSNetworkKit.h"
#import "QSTradeStatus.h"

@interface QSOrderListTableViewCell ()<UIAlertViewDelegate>

@property (strong, nonatomic) NSDictionary* tradeDict;
@property (assign, nonatomic) float skuLabelBaseY;
@property (assign, nonatomic) float actualPrice;
@end

@implementation QSOrderListTableViewCell

#pragma mark - Init Config
- (void)awakeFromNib {
    // Initialization code
    [self configBtn:self.submitButton];
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
        self.quantityLabel.frame = CGRectMake(112, 120, 190, 14);
        self.exDiscountLabel.frame = CGRectMake(112, 142, 190, 14);
        self.priceLabel.frame = CGRectMake(112, 164, 190, 14);
    }else{
        self.quantityLabel.frame = CGRectMake(112, 142, 190, 14);
        self.priceLabel.frame = CGRectMake(112, 186, 190, 14);
        self.exDiscountLabel.frame = CGRectMake(112, 164, 190, 14);
    }
}

#pragma mark - Binding
- (void)bindWithDict:(NSDictionary*)tradeDict
{
    [self configBtn:self.submitButton];
    [self.submitButton setImage:nil forState:UIControlStateNormal];
    [self.submitButton setTitle:nil forState:UIControlStateNormal];
    
    self.tradeDict = tradeDict;
    
    NSDictionary* itemDict = [QSTradeUtil getItemSnapshot:tradeDict];
    self.stateLabel.text = [QSTradeUtil getStatusDesc:tradeDict];
    self.titleLabel.text = [QSItemUtil getItemName:itemDict];
    [self.itemImgView setImageFromURL:[QSItemUtil getThumbnail:itemDict]];
    
    NSString *oldPrice = [NSString stringWithFormat:@"原价：￥%@",[QSItemUtil getPriceDesc:itemDict]];
    NSMutableAttributedString *attri = [[NSMutableAttributedString alloc] initWithString:oldPrice];
    [attri addAttribute:NSStrikethroughStyleAttributeName value:@(NSUnderlinePatternSolid | NSUnderlineStyleSingle) range:NSMakeRange(0, oldPrice.length)];
    [attri addAttribute:NSStrikethroughColorAttributeName value:[UIColor colorWithWhite:0.800 alpha:1.000] range:NSMakeRange(0, oldPrice.length)];
    [self.originPriceLabel setAttributedText:attri];
    
 
    self.nowPriceLabel.text = [NSString stringWithFormat:@"现价：￥%@",[QSItemUtil getPromoPriceDesc:itemDict]];
    self.sizeLabel.text = [QSTradeUtil getSizeText:tradeDict];
    self.colorLabel.text = [QSTradeUtil getColorText:tradeDict];
    if ([QSTradeUtil getActualPrice:tradeDict]) {
        self.priceLabel.text = [NSString stringWithFormat:@"期望价格：￥%@",[QSTradeUtil getActualPriceDesc:tradeDict]];
         _actualPrice = [QSTradeUtil getActualPriceDesc:tradeDict].floatValue;
    } else {
        self.priceLabel.text = [NSString stringWithFormat:@"期望价格：￥%@",[QSTradeUtil getExpectedPriceDesc:tradeDict]];
        _actualPrice = [QSTradeUtil getExpectedPriceDesc:tradeDict].floatValue;
    }
    self.quantityLabel.text = [NSString stringWithFormat:@"数量：%@",[QSTradeUtil getQuantityDesc:tradeDict]];

    NSNumber* status = [QSTradeUtil getStatus:tradeDict];
    QSTradeStatus s = status.integerValue;
//    if (s == 0) {
//           NSLog(@"%@",itemDict[@"_id"]);
//    }
    if (s == 0 || s == 1) {
         self.dateLabel.text = [NSString stringWithFormat:@"申请日期: %@",[QSTradeUtil getDayDesc:tradeDict]];
    }else
    {
        self.dateLabel.text = [NSString stringWithFormat:@"付款日期：%@",[QSTradeUtil getDayDesc:tradeDict]];
    }
    NSNumber* price = [QSItemUtil getPromoPrice:[QSTradeUtil getItemSnapshot:tradeDict]];
    int disCount = _actualPrice * 100 / price.doubleValue;
    if (disCount < 10) {
        disCount = 10;
    }else if(disCount > 90)
    {
        disCount = 90;
    }else
    {
        if (disCount%10 > 5) {
            disCount = (disCount/10+1)*10;
        }
    }
    disCount = disCount/10;
    self.exDiscountLabel.text = [NSString stringWithFormat:@"期望折扣：%d折", disCount];
    BOOL shouldShare = [QSTradeUtil getShouldShare:tradeDict];
    switch (s) {
        case 0:
        {
            self.submitButton.hidden = NO;
            self.stateLabel.hidden = YES;
            self.exchangeButton.hidden = YES;
            self.saleImgView.hidden = YES;
            [self.submitButton setTitle:@"取消申请" forState:UIControlStateNormal];
            break;
        }
        case 2:
        {
            self.submitButton.hidden = YES;
            self.exchangeButton.hidden = YES;
            self.saleImgView.hidden = YES;
            self.stateLabel.hidden = NO;
            //[self.submitButton setTitle:@"取消订单" forState:UIControlStateNormal];
            break;
        }
        case 1:{
            self.submitButton.hidden = NO;
            self.exchangeButton.hidden = YES;
            self.stateLabel.hidden = YES;
            self.saleImgView.hidden = YES;
            if (!shouldShare) {
                [self.submitButton setTitle:@"立即付款" forState:UIControlStateNormal];
            }
            else{
                [self.submitButton setTitle:nil forState:UIControlStateNormal];
                self.submitButton.layer.borderWidth = 0.f;
                [self.submitButton setImage:[UIImage imageNamed:@"order_list_share_pay.png"] forState:UIControlStateNormal]; 
            }
            break;
        }
        case 3: {
            self.submitButton.hidden = NO;
            self.exchangeButton.hidden = NO;
            self.saleImgView.hidden = YES;
            self.stateLabel.hidden = YES;
            [self.submitButton setImage:nil forState:UIControlStateNormal];
            [self configBtn:self.submitButton];
            [self.submitButton setTitle:@"申请退货" forState:UIControlStateNormal];
            break;
        }
         default: {
            self.submitButton.hidden = YES;
            self.exchangeButton.hidden = YES;
             self.saleImgView.hidden = YES;
             self.stateLabel.hidden = NO;
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
        [self payBtnPressed];
    } else if (status  == 3) {
        if ([self.delegate respondsToSelector:@selector(didClickRefundBtnForCell:)]) {
            [self.delegate didClickRefundBtnForCell:self];
        }
    }
    else if(status == 0)
    {
        if ([self.delegate respondsToSelector:@selector(didClickCancelBtnForCell:)]) {
            [self.delegate didClickCancelBtnForCell:self];
        }
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

- (void)payBtnPressed
{
    if ([self.delegate respondsToSelector:@selector(didClickPayBtnForCell:)]) {
        [self.delegate didClickPayBtnForCell:self];
    }
}

@end

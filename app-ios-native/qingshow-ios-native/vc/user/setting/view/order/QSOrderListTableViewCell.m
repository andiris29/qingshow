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
#import "QSPeopleUtil.h"
#import "QSUserManager.h"
@interface QSOrderListTableViewCell ()<UIAlertViewDelegate>

@property (strong, nonatomic) NSDictionary* tradeDict;
@property (strong, nonatomic) NSString *itemId;
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
    self.postDisCountImgView.layer.cornerRadius = self.postDisCountImgView.bounds.size.width / 2;
    self.postDisCountImgView.layer.masksToBounds = YES;
    self.postDisCountImgView.transform = CGAffineTransformMakeRotation(0.3);
    self.postDisCountImgView.userInteractionEnabled = YES;
    UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapExpectablePriceBtn:)];
    [self.postDisCountImgView addGestureRecognizer:ges];
    self.clickToWebpageBtn.layer.cornerRadius = self.clickToWebpageBtn.bounds.size.height / 8;
    self.clickToWebpageBtn.layer.borderColor = [UIColor colorWithRed:0.949 green:0.588 blue:0.643 alpha:1.000].CGColor;
    self.clickToWebpageBtn.layer.borderWidth = 1.f;
    self.itemImgView.userInteractionEnabled = YES;
    UITapGestureRecognizer *imgGes = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(didTapClickToWebViewPage:)];
    [self.itemImgView addGestureRecognizer:imgGes];
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
    
    NSString *str = [QSTradeUtil getItemId:tradeDict];
    self.itemId = str;
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
    if ([QSTradeUtil getActualPrice:tradeDict]) {
        self.priceLabel.text = [NSString stringWithFormat:@"期望价格：￥%@",[QSTradeUtil getActualPriceDesc:tradeDict]];
         _actualPrice = [QSTradeUtil getActualPriceDesc:tradeDict].floatValue;
    } else {
        self.priceLabel.text = [NSString stringWithFormat:@"期望价格：￥%@",[QSTradeUtil getExpectedPriceDesc:tradeDict]];
        _actualPrice = [QSTradeUtil getExpectedPriceDesc:tradeDict].floatValue;
    }
    self.quantityLabel.text = [NSString stringWithFormat:@"数量：%@",[QSTradeUtil getQuantityDesc:tradeDict]];
    self.hintLabel.text = [QSTradeUtil getHint:tradeDict];
    NSNumber* status = [QSTradeUtil getStatus:tradeDict];
    QSTradeStatus s = status.integerValue;
    if (s == 0 || s == 1) {
         self.dateLabel.text = [NSString stringWithFormat:@"申请日期: %@",[QSTradeUtil getDayDesc:tradeDict]];
    }else
    {
        self.dateLabel.text = [NSString stringWithFormat:@"付款日期: %@",[QSTradeUtil getDayDesc:tradeDict]];
    }
    self.exDiscountLabel.text = [NSString stringWithFormat:@"期望折扣：%@", [QSTradeUtil calculateDiscountDescWithPrice:@(_actualPrice) trade:tradeDict]];
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
            self.exchangeButton.hidden = NO;
            self.stateLabel.hidden = YES;
            self.saleImgView.hidden = YES;
            [self configBtn:self.exchangeButton];
            [self.exchangeButton setTitle:@"取消订单" forState:UIControlStateNormal];
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
            [self configBtn:self.exchangeButton];
            [self.exchangeButton setTitle:@"物流信息" forState:UIControlStateNormal];
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
    NSNumber* expectablePrice = [QSTradeUtil getItemExpectablePrice:tradeDict];
    __weak QSOrderListTableViewCell *weakSelf = self;
    if (s == 0 && expectablePrice) {
        [SHARE_NW_ENGINE getLoginUserOnSucced:^(NSDictionary *data, NSDictionary *metadata) {
            NSDictionary *peopleDic  = data;
            NSArray *unreadArray = [QSPeopleUtil getUnreadTrades:peopleDic];
            BOOL isRed = NO;
            for (NSDictionary *unreadDic in unreadArray) {
                if ([[QSPeopleUtil getUnreadTradId:unreadDic] isEqualToString:[QSTradeUtil getTradeId:tradeDict]]) {
                    isRed = YES;
                }
                
            }
            if (isRed == YES) {
                weakSelf.postDisCountImgView.hidden = NO;
                weakSelf.postDisCountImgView.userInteractionEnabled = YES;
                [weakSelf.postDisCountImgView setImage:[UIImage imageNamed:@"order_list_cell_discount_red"]];
            }else if(isRed == NO){
                weakSelf.postDisCountImgView.hidden = NO;
                weakSelf.postDisCountImgView.userInteractionEnabled = YES;
                [weakSelf.postDisCountImgView setImage:[UIImage imageNamed:@"order_list_cell_discount_gray"]];
            }
        } onError:^(NSError *error) {
            
        }];

    }
        NSString *itemId = [QSTradeUtil getItemId:tradeDict];
    
        [SHARE_NW_ENGINE getItemWithId:itemId onSucceed:^(NSArray *array, NSDictionary *metadata) {
            
            if (array.count) {
                NSDictionary *getItem = [array firstObject];
                NSArray *array = [QSTradeUtil getSkuProperties:tradeDict];
                NSString *key = [QSItemUtil getKeyValueForSkuTableFromeSkuProperties:array];
                int count = [QSItemUtil getFirstValueFromSkuTableWithkey:key itemDic:getItem];
                if (count < 1 || [QSItemUtil getDelist:getItem] ) {
                    weakSelf.postDisCountImgView.hidden = NO;
                    weakSelf.postDisCountImgView.userInteractionEnabled = NO;
                    [weakSelf.postDisCountImgView setImage:[UIImage imageNamed:@"order_list_cell_discount_outofsale"]];
                }
            }
        } onError:^(NSError *error) {
            
        }];
    
    
    
}
- (void)didTapClickToWebViewPage:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickToWebPageForCell:)]) {
        [self.delegate didClickToWebPageForCell:self];
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
      int status = [QSTradeUtil getStatus:self.tradeDict].intValue;
    if (status == 1) {
        if ([self.delegate respondsToSelector:@selector(didClickCancelBtnForCell:)]) {
            [self.delegate didClickCancelBtnForCell:self];
        }
    }else{
    if ([self.delegate respondsToSelector:@selector(didClickExchangeBtnForCell:)]) {
        [self.delegate didClickExchangeBtnForCell:self];
    }
    }
}

- (IBAction)clickToWebpageBtnPressed:(id)sender {
    [self didTapClickToWebViewPage:self];
}

- (void)payBtnPressed
{
    if ([self.delegate respondsToSelector:@selector(didClickPayBtnForCell:)]) {
        [self.delegate didClickPayBtnForCell:self];
    }
}
- (void)didTapExpectablePriceBtn:(UIGestureRecognizer*)ges {
    if ([self.delegate respondsToSelector:@selector(didClickExpectablePriceBtnForCell:)]) {
        [self.delegate didClickExpectablePriceBtnForCell:self];
    }
}
@end

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
#import "QSUnreadManager.h"


typedef NS_ENUM(NSUInteger, QSOrderListCellCircleType) {
    QSOrderListCellCircleTypeNone = 0,
    QSOrderListCellCircleTypeNewDiscount = 1,
    QSOrderListCellCircleTypePay = 2,
    QSOrderListCellCircleTypeShareToPay = 3,
    QSOrderListCellCircleTypeSaleOut = 4
};

@interface QSOrderListTableViewCell ()<UIAlertViewDelegate>

@property (strong, nonatomic) NSDictionary* tradeDict;
@property (strong, nonatomic) NSString *itemId;
@property (assign, nonatomic) float skuLabelBaseY;
@property (assign, nonatomic) float actualPrice;
@property (strong, nonatomic) NSArray* topRightBtns;

@property (assign, nonatomic) QSOrderListCellCircleType circleType;
@end

@implementation QSOrderListTableViewCell

#pragma mark - Init Config
- (void)awakeFromNib {
    // Initialization code
    self.selectionStyle = UITableViewCellSelectionStyleNone;

    
    //Circle Btn
    self.circleBtnImageView.layer.cornerRadius = self.circleBtnImageView.bounds.size.width / 2;
    self.circleBtnImageView.layer.masksToBounds = YES;
    self.circleBtnImageView.transform = CGAffineTransformMakeRotation(0.3);
    self.circleBtnImageView.userInteractionEnabled = NO;
    self.circleBtnImageView.hidden = YES;
    UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapCircleBtn:)];
    [self.circleBtnImageView addGestureRecognizer:ges];
    self.circleType = QSOrderListCellCircleTypeNone;
    self.circleBtnImageView.userInteractionEnabled = YES;
    
    //Web Page
    self.clickToWebpageBtn.layer.cornerRadius = self.clickToWebpageBtn.bounds.size.height / 8;
    self.clickToWebpageBtn.layer.borderColor = [UIColor colorWithRed:0.949 green:0.588 blue:0.643 alpha:1.000].CGColor;
    self.clickToWebpageBtn.layer.borderWidth = 1.f;
    self.itemImgView.userInteractionEnabled = YES;
    UITapGestureRecognizer *imgGes = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(didTapClickToWebViewPage:)];
    [self.itemImgView addGestureRecognizer:imgGes];
    
    //Top Right Btns
    self.topRightBtns = @[
                          self.submitButton,
                          self.refundButton,
                          self.logisticsButton,
                          self.cancelButton
                          ];
    for (UIButton* btn in self.topRightBtns) {
        [self configBtn:btn];
    }
    
    [self removeAllTopRightBtn];
    
    
}

#pragma mark - Top Right
- (void)removeAllTopRightBtn {
    for (UIView* v in self.topRightBtns) {
        [v removeFromSuperview];
    }
}
- (void)showTopRightBtns:(NSArray*)btns {
    [self removeAllTopRightBtn];
    CGFloat right = [UIScreen mainScreen].bounds.size.width;
    CGFloat borderWidth = 10.0;
    for (int i = (int)btns.count - 1; i >= 0; i--) {
        UIButton* btn = btns[i];
        right -= borderWidth;
        btn.center = CGPointMake(right - btn.bounds.size.width / 2, 20.0);
        right -= btn.bounds.size.width;
        [self.contentView addSubview:btn];
    }
}

#pragma mark - Circle Btn
- (void)updateCircleBtn {
    self.circleBtnImageView.hidden = NO;
    if (self.circleType == QSOrderListCellCircleTypeNone) {
        self.circleBtnImageView.hidden = YES;
    } else if (self.circleType == QSOrderListCellCircleTypeNewDiscount) {
#warning handle unread
        if ([[QSUnreadManager getInstance] shouldShowTradeUnreadOfType:QSUnreadTradeTypeExpectablePriceUpdated id:[QSEntityUtil getIdOrEmptyStr:self.tradeDict]]) {
            self.circleBtnImageView.image = [UIImage imageNamed:@"order_list_cell_discount_dot"];
        } else {
            self.circleBtnImageView.image = [UIImage imageNamed:@"order_list_cell_discount"];
        }

    } else if (self.circleType == QSOrderListCellCircleTypeShareToPay) {
        if ([[QSUnreadManager getInstance] shouldShowTradeUnreadOfType:QSUnreadTradeTypeTradeInitialized id:[QSEntityUtil getIdOrEmptyStr:self.tradeDict]]) {
#warning @mhy 变成带点的分享并付款
            self.circleBtnImageView.image = [UIImage imageNamed:@"order_list_cell_sharetopay_dot"];
        } else {
            self.circleBtnImageView.image = [UIImage imageNamed:@"order_list_cell_discount_share_to_pay"];
        }

    } else if (self.circleType == QSOrderListCellCircleTypePay) {
        self.circleBtnImageView.image = [UIImage imageNamed:@"order_list_cell_discount_pay"];
    } else if (self.circleType == QSOrderListCellCircleTypeSaleOut) {
        self.circleBtnImageView.image = [UIImage imageNamed:@"order_list_cell_discount_outofsale"];
    }
}

- (void)didTapCircleBtn:(UIGestureRecognizer*)ges {
    if (self.circleType == QSOrderListCellCircleTypeNone) {
    } else if (self.circleType == QSOrderListCellCircleTypeNewDiscount) {
        [[QSUnreadManager getInstance] clearTradeUnreadOfType:QSUnreadTradeTypeExpectablePriceUpdated id:[QSEntityUtil getIdOrEmptyStr:self.tradeDict]];
        [self didTapExpectablePriceBtn:ges];
    } else if (self.circleType == QSOrderListCellCircleTypeShareToPay) {
        [[QSUnreadManager getInstance] clearTradeUnreadOfType:QSUnreadTradeTypeTradeInitialized id:[QSEntityUtil getIdOrEmptyStr:self.tradeDict]];
        [self.delegate didClickPayBtnForCell:self];
    } else if (self.circleType == QSOrderListCellCircleTypePay) {
        [self.delegate didClickPayBtnForCell:self];
    } else if (self.circleType == QSOrderListCellCircleTypeSaleOut) {
        
    }
    [self updateCircleBtn];
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
    [self.circleBtnImageView setImage:nil];
    
    
    //itemUtil
    NSDictionary* itemDict = [QSTradeUtil getItemSnapshot:tradeDict];
    self.titleLabel.text = [QSItemUtil getItemName:itemDict];
    [self.itemImgView setImageFromURL:[QSItemUtil getThumbnail:itemDict]];
    NSString *oldPrice = [NSString stringWithFormat:@"原价：￥%@",[QSItemUtil getPriceDesc:itemDict]];
    [self.originPriceLabel setAttributedText:[QSItemUtil getAttrbuteStr:oldPrice]];
    self.nowPriceLabel.text = [NSString stringWithFormat:@"现价：￥%@",[QSItemUtil getPromoPriceDesc:itemDict]];
    
    
    //tradeUtil
    self.stateLabel.text = [QSTradeUtil getStatusDesc:tradeDict];
    if ([QSTradeUtil getActualPrice:tradeDict]) {
        self.priceLabel.text = [NSString stringWithFormat:@"期望价格：￥%@",[QSTradeUtil getActualPriceDesc:tradeDict]];
        _actualPrice = [QSTradeUtil getActualPriceDesc:tradeDict].floatValue;
    } else {
        self.priceLabel.text = [NSString stringWithFormat:@"期望价格：￥%@",[QSTradeUtil getExpectedPriceDesc:tradeDict]];
        _actualPrice = [QSTradeUtil getExpectedPriceDesc:tradeDict].floatValue;
    }
    self.sizeLabel.text = [QSTradeUtil getSizeText:tradeDict];
    self.quantityLabel.text = [NSString stringWithFormat:@"数量：%@",[QSTradeUtil getQuantityDesc:tradeDict]];
    self.exDiscountLabel.text = [NSString stringWithFormat:@"期望折扣：%@", [QSTradeUtil calculateDiscountDescWithPrice:@(_actualPrice) trade:tradeDict]];
    self.hintLabel.text = [QSTradeUtil getHint:tradeDict];
    NSNumber* status = [QSTradeUtil getStatus:tradeDict];
    QSTradeStatus s = status.integerValue;
    if (s == 0 || s == 1) {
        self.dateLabel.text = [NSString stringWithFormat:@"申请日期: %@",[QSTradeUtil getDayDesc:tradeDict]];
    }else
    {
        self.dateLabel.text = [NSString stringWithFormat:@"付款日期: %@",[QSTradeUtil getDayDesc:tradeDict]];
    }
    
    switch (s) {
        case 0:
        {
            self.stateLabel.hidden = YES;
            [self showTopRightBtns:@[self.cancelButton]];
            NSDictionary* itemDict = [QSTradeUtil getItemDic:tradeDict];
            if (![QSItemUtil getExpectableDict:itemDict]) {
                self.circleType = QSOrderListCellCircleTypeNone;
            } else {
                if ([QSItemUtil getExpectableIsExpire:itemDict]) {
                    self.circleType = QSOrderListCellCircleTypeSaleOut;
                } else {
                    NSNumber* price = [QSItemUtil getExpectablePrice:itemDict];
                    NSNumber* tradeExpPrice = [QSTradeUtil getExpectedPrice:tradeDict];
                    if (price.doubleValue > tradeExpPrice.doubleValue) {
                        self.circleType = QSOrderListCellCircleTypeNewDiscount;
                    } else {
                        if ([QSTradeUtil getShouldShare:tradeDict]) {
                            self.circleType = QSOrderListCellCircleTypeShareToPay;
                        } else {
                            self.circleType = QSOrderListCellCircleTypePay;
                        }
                    }
                }
            }
            
            break;
        }
        case 1:{

            self.stateLabel.hidden = YES;
            [self showTopRightBtns:@[self.cancelButton]];
            self.circleType = QSOrderListCellCircleTypePay;
            break;
        }
        case 3: {
            self.stateLabel.hidden = YES;


            if ([[QSUnreadManager getInstance] shouldShowTradeUnreadOfType:QSUnreadTradeTypeTradeShipped id:[QSEntityUtil getIdOrEmptyStr:self.tradeDict]]) {
#warning TODO @mhy 物流信息按钮变成带点图
                [self.logisticsButton setImage:nil forState:UIControlStateNormal];
                [self.logisticsButton setImage:[UIImage imageNamed:@"order_list_cell_wuliu_dot"] forState:UIControlStateNormal];
            } else {
#warning TODO @mhy 物流信息按钮变成不带点图
                [self.logisticsButton setImage:nil forState:UIControlStateNormal];
                [self.logisticsButton setImage:[UIImage imageNamed:@"order_list_cell_wuliu_normal"] forState:UIControlStateNormal];
            }
            
            [self showTopRightBtns:@[self.logisticsButton, self.refundButton]];
            break;
        }
        default: {
            self.stateLabel.hidden = NO;
            break;
        }
    }
    [self updateCircleBtn];
}


#pragma mark - Top Right 4 btns
- (IBAction)refundBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickRefundBtnForCell:)]) {
        [self.delegate didClickRefundBtnForCell:self];
    }
}
- (IBAction)submitBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickReceiveBtnForCell:)]) {
        [self.delegate didClickReceiveBtnForCell:self];
    }
}

- (IBAction)cancelBtnPressed:(id)sender {
    if ([self.delegate respondsToSelector:@selector(didClickCancelBtnForCell:)]) {
        [self.delegate didClickCancelBtnForCell:self];
    }
}

- (IBAction)logisticsBtnPressed:(id)sender {
#warning @mhy 处理物流按钮
    [[QSUnreadManager getInstance] clearTradeUnreadOfType:QSUnreadTradeTypeTradeShipped id:[QSEntityUtil getIdOrEmptyStr:self.tradeDict]];
}

#pragma mark - Web Page
- (IBAction)clickToWebpageBtnPressed:(id)sender {
    [self didTapClickToWebViewPage:self];
}
- (void)didTapClickToWebViewPage:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickToWebPageForCell:)]) {
        [self.delegate didClickToWebPageForCell:self];
    }
}



- (void)didTapExpectablePriceBtn:(UIGestureRecognizer*)ges {
    if ([self.delegate respondsToSelector:@selector(didClickExpectablePriceBtnForCell:)]) {
        [self.delegate didClickExpectablePriceBtnForCell:self];
    }
}
@end

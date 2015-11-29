//
//  QSTradeListTableViewCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSTradeListTableViewCell.h"
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

@interface QSTradeListTableViewCell ()<UIAlertViewDelegate>

@property (weak, nonatomic) NSDictionary* tradeDict;
@property (strong, nonatomic) NSString *itemId;
@property (assign, nonatomic) float skuLabelBaseY;
@property (assign, nonatomic) float actualPrice;
@property (strong, nonatomic) NSArray* topRightBtns;
@end

@implementation QSTradeListTableViewCell

#pragma mark - Init Config
- (void)awakeFromNib {
    // Initialization code
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    
    //Circle Btn
    self.circleBtnImageView.userInteractionEnabled = NO;
    self.circleBtnImageView.hidden = YES;
    
    //Web Page
    self.clickToWebpageBtn.layer.cornerRadius = self.clickToWebpageBtn.bounds.size.height / 8;
    self.clickToWebpageBtn.layer.borderColor = [UIColor colorWithRed:0.949 green:0.588 blue:0.643 alpha:1.000].CGColor;
    self.clickToWebpageBtn.layer.borderWidth = 1.f;
    self.itemImgView.userInteractionEnabled = YES;
    UITapGestureRecognizer *imgGes = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(didTapClickToWebViewPage:)];
    [self.itemImgView addGestureRecognizer:imgGes];
    
    //Top Right Btns
    self.topRightBtns = @[
                          self.refundButton,
                          self.payButton
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
    //cell将会被scale，所以按320宽度进行布局
    CGFloat right = 320.f;
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
}

#pragma mark - Binding
- (void)bindWithDict:(NSDictionary*)tradeDict
{
    self.tradeDict = tradeDict;
    [self.circleBtnImageView setImage:nil];
    
    //itemUtil
    NSDictionary* itemDict = [QSTradeUtil getItemDic:tradeDict];
    self.titleLabel.text = [QSItemUtil getItemName:itemDict];
    [self.itemImgView setImageFromURL:[QSItemUtil getThumbnail:itemDict]];
    NSString *oldPrice = [NSString stringWithFormat:@"原价:￥%@",[QSItemUtil getPriceDesc:itemDict]];
    [self.originPriceLabel setAttributedText:[QSItemUtil getAttrbuteStr:oldPrice]];
    if (self.cellType == QSTradeListTableViewCellNormal) {
        self.nowPriceLabel.text = [NSString stringWithFormat:@"预订价: ￥%@",[QSItemUtil getPromoPriceDesc:itemDict]];
    } else {
        self.nowPriceLabel.text = [NSString stringWithFormat:@"折扣价: ￥%@",[QSItemUtil getPromoPriceDesc:itemDict]];
    }

    
    //tradeUtil
    self.stateLabel.text = [QSTradeUtil getStatusDesc:tradeDict];

    _actualPrice = [QSTradeUtil getExpectedPriceDesc:tradeDict].floatValue;

    if (self.cellType == QSTradeListTableViewCellNormal) {
        self.sizeLabel.text = [QSTradeUtil getPropertiesDesc:tradeDict];
    } else {
        self.sizeLabel.text = [QSTradeUtil getPropertiesFullDesc:tradeDict];
    }

    self.quantityLabel.text = [NSString stringWithFormat:@"数量: %@",[QSTradeUtil getQuantityDesc:tradeDict]];

    NSNumber* status = [QSTradeUtil getStatus:tradeDict];
    QSTradeStatus s = status.integerValue;
    if (s == 0 || s == 1) {
        self.dateLabel.text = [NSString stringWithFormat:@"申请日期: %@",[QSTradeUtil getDayDesc:tradeDict]];
    }else
    {
        _actualPrice = [QSTradeUtil getPrice:tradeDict].floatValue;
        self.dateLabel.text = [NSString stringWithFormat:@"付款日期: %@",[QSTradeUtil getDayDesc:tradeDict]];
    }
    
    switch (s) {
        case 0:
        {
            self.stateLabel.hidden = YES;
            [self showTopRightBtns:@[]];
            self.nowPriceLabel.text = [NSString stringWithFormat:@"预订价: 尽请期待"];
            break;
        }
        case 1:{
            self.stateLabel.hidden = YES;
            if ([QSItemUtil getExpectableIsExpire:itemDict]) {
                [self showTopRightBtns:@[]];
#warning TODO 售罄逻辑
//                self.circleType = QSTradeListCellCircleTypeSaleOut;
            } else {
                if (
                    [[QSUnreadManager getInstance] shouldShowTradeUnreadOfType:QSUnreadTradeTypeExpectablePriceUpdated id:[QSEntityUtil getIdOrEmptyStr:self.tradeDict]]
                    ) {
                    self.nowPriceLabel.text = [NSString stringWithFormat:@"预订价: ?"];
                    [self showTopRightBtns:@[]];
                } else {
                    self.nowPriceLabel.text = [NSString stringWithFormat:@"预订价: ￥%@",[QSItemUtil getPromoPriceDesc:itemDict]];
                    [self showTopRightBtns:@[self.payButton]];
                }
            }
            break;
        }
        case 3: {
            self.stateLabel.hidden = YES;
            if ([[QSUnreadManager getInstance] shouldShowTradeUnreadOfType:QSUnreadTradeTypeTradeShipped id:[QSEntityUtil getIdOrEmptyStr:self.tradeDict]]) {
                [self.logisticsButton setTitle:@"" forState:UIControlStateNormal];
                [self.logisticsButton setImage:[UIImage imageNamed:@"order_list_cell_wuliu_dot"] forState:UIControlStateNormal];
            } else {
                [self.logisticsButton setTitle:@"" forState:UIControlStateNormal];
                [self.logisticsButton setImage:[UIImage imageNamed:@"order_list_cell_wuliu_normal"] forState:UIControlStateNormal];
            }
            
            [self showTopRightBtns:@[self.logisticsButton, self.refundButton]];
            break;
        }
        default: {
            self.stateLabel.hidden = NO;
            [self removeAllTopRightBtn];
            break;
        }
    }
    
    if (s == 1 && ![QSItemUtil getExpectableIsExpire:itemDict]) {
        NSString* msg = [QSItemUtil getMessageForPay:itemDict];
        if (msg && msg.length) {
            self.messageLabel.text = [NSString stringWithFormat:@"商品备注:%@", msg];
        } else {
            self.messageLabel.text = @"";
        }
    } else {
        self.messageLabel.text = @"";
    }
    
}


#pragma mark - Top Right 4 btns
- (IBAction)refundBtnPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickRefundBtnForCell:)]) {
        [self.delegate didClickRefundBtnForCell:self];
    }
}


- (IBAction)logisticsBtnPressed:(id)sender {
    if ([self.delegate respondsToSelector:@selector(didClickLogisticForCell:)]) {
        [self.delegate didClickLogisticForCell:self];
    }
    [[QSUnreadManager getInstance] clearTradeUnreadOfType:QSUnreadTradeTypeTradeShipped id:[QSEntityUtil getIdOrEmptyStr:self.tradeDict]];
}

- (IBAction)payBtnPressed:(id)sender {
    if ([self.delegate respondsToSelector:@selector(didClickPayBtnForCell:)]) {
        [self.delegate didClickPayBtnForCell:self];
    }
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

@end

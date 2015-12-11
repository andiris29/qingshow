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
#import "QSPeopleUtil.h"
#import "QSUserManager.h"
#import "QSUnreadManager.h"

@interface QSTradeListTableViewCell ()<UIAlertViewDelegate>

@property (weak, nonatomic) NSDictionary* tradeDict;
@property (strong, nonatomic) NSString *itemId;
@property (assign, nonatomic) float skuLabelBaseY;
@property (strong, nonatomic) NSArray* topRightBtns;
@end

@implementation QSTradeListTableViewCell

#pragma mark - Init Config
- (void)awakeFromNib {
    // Initialization code
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    
    //Web Page
    [self configBtn:self.clickToWebpageBtn];
    
    self.itemImgView.userInteractionEnabled = YES;
    UITapGestureRecognizer *imgGes = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(didTapClickToWebViewPage:)];
    [self.itemImgView addGestureRecognizer:imgGes];
    
    //Top Right Btns
    
    self.topRightBtns = @[
                          self.refundButton,
                          self.logisticsButton
                          ];
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
    CGFloat borderWidth = 5.0;
    for (int i = (int)btns.count - 1; i >= 0; i--) {
        UIButton* btn = btns[i];
        right -= borderWidth;
        btn.center = CGPointMake(right - btn.bounds.size.width / 2, 150.0);
        right -= btn.bounds.size.width;
        [self.contentView addSubview:btn];
    }
}

#pragma mark - Circle Btn
- (void)configBtn:(UIButton*)btn {
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

- (void)layoutSubviews {
    [super layoutSubviews];
}

#pragma mark - Binding
- (void)bindWithDict:(NSDictionary*)tradeDict
{
    self.tradeDict = tradeDict;
    
    //config Item info
    NSDictionary* itemDict = [QSTradeUtil getItemSnapshot:tradeDict];
    self.titleLabel.text = [QSItemUtil getItemName:itemDict];
    [self.itemImgView setImageFromURL:[QSItemUtil getThumbnail:itemDict]];
    self.priceLabel.text = [NSString stringWithFormat:@"￥%.2f", [QSItemUtil getPriceToPay:itemDict].floatValue];
    self.sizeLabel.text = [QSTradeUtil getPropertiesFullDesc:tradeDict];
    self.quantityLabel.text = [NSString stringWithFormat:@"数量: %@",[QSTradeUtil getQuantityDesc:tradeDict]];
    
    //Trade info
    NSInteger s = [QSTradeUtil getStatus:tradeDict].integerValue;
    self.dateLabel.text = [NSString stringWithFormat:@"付款日期: %@",[QSTradeUtil getDayDesc:tradeDict]];
    [self showTopRightBtns:@[]];
    switch (s) {
        case 2: {
            self.stateLabel.hidden = NO;
            self.stateLabel.text = @"备货中...";
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
        case 7: {
            self.stateLabel.text = @"退货中...";
            break;
        }
        case 9:
        case 10:
        case 17: {
            self.stateLabel.text = @"交易关闭";
            break;
        }
        case 18: {
            self.stateLabel.text = @"交易成功";
            break;
        }
        default: {
            self.stateLabel.hidden = NO;
            [self removeAllTopRightBtn];
            break;
        }
    }
}


#pragma mark - Top Right btns
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

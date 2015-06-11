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

@interface QSOrderListTableViewCell ()<UIAlertViewDelegate>

@property (strong, nonatomic) NSDictionary* tradeDict;

@property (assign, nonatomic) float skuLabelBaseY;
@end

@implementation QSOrderListTableViewCell

#pragma mark - Init Config
- (void)awakeFromNib {
    // Initialization code
//    [self configBtn:self.refundButton];
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
    
//    self.orderIdLabel.text = [QSCommonUtil getIdOrEmptyStr:tradeDict];
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
    NSLog(@"status = %@",status);
    if (status.intValue == 0) {
        self.submitButton.hidden = NO;
        self.returnButton.hidden = YES;
        self.exchangeButton.hidden = YES;
        [self.submitButton setTitle:@"付款" forState:UIControlStateNormal];
    } else if (status.intValue < 5 && status.intValue > 0) {
        self.exchangeButton.hidden = YES;
        self.submitButton.hidden = YES;
        self.returnButton.hidden = YES;
    } else if(status.intValue == 5 ){
        [self.submitButton setTitle:@"已收货" forState:UIControlStateNormal];
        self.returnButton.hidden = YES;
        self.exchangeButton.hidden = YES;
    }else if(status.intValue == 6)
    {
        [self.submitButton setTitle:@"退货中" forState:UIControlStateNormal];
        self.returnButton.hidden = YES;
        self.exchangeButton.hidden = YES;
    }
    else
    {
        self.submitButton.hidden = NO;
        self.returnButton.hidden = NO;
        self.exchangeButton.hidden = NO;
        [self.submitButton setTitle:@"确认收货" forState:UIControlStateNormal];
        
    }
    
}
- (void)updateView:(UIView*)view y:(float)y
{
    CGRect rect = view.frame;
    rect.origin.y = y;
    view.frame = rect;
}

#pragma mark - Getter And Setter
//- (void)setType:(QSOrderListTableViewCellType)type
//{
//    _type = type;
//    BOOL fHiddenLabel = NO;
//    switch (type) {
//        case QSOrderListTableViewCellTypeComplete:
//        {
//            self.stateLabel.text = @"交易完成";
//            fHiddenLabel = NO;
//
//            break;
//        }
//        case QSOrderListTableViewCellTypeWaiting:
//        {
//            self.stateLabel.text = @"待收货";
//            fHiddenLabel = YES;
//            break;
//        }
//        default:
//        {
//            break;
//        }
//    }
//    self.dateEndLabel.hidden = fHiddenLabel;
//    self.dateEndTextLabel.hidden = fHiddenLabel;
//    self.dateStartLabel.hidden = fHiddenLabel;
//    self.dateStartTextLabel.hidden = fHiddenLabel;
//    self.submitButton.hidden = !fHiddenLabel;
//    self.refundButton.hidden = !fHiddenLabel;
//}

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
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"" message:@"您的换货申请已经受理，我们的客服会尽快与您联系" delegate:self cancelButtonTitle:@"确认" otherButtonTitles:nil, nil];
    [alert show];
    if ([self.delegate respondsToSelector:@selector(didClickExchangeBtnForCell:)]) {
        [self.delegate didClickExchangeBtnForCell:self];
    }
}
//- (void)willPresentAlertView:(UIAlertView *)alertView
//{
//    
//  
//    for (UIView *view in alertView.subviews) {
//        NSLog(@"111");
//        NSLog(@"view.class == %@",view.class);
//        if ([view isKindOfClass:[UILabel class]]) {
//            UILabel *label = (UILabel *)view;
//            if ([label.text isEqualToString:alertView.message]) {
//                label.font = NEWFONT;
//            }
//            NSLog(@"label.text = %@",label.text);
//            
//        }
//    }
//}
- (void)payBtnPressed
{
    if ([self.delegate respondsToSelector:@selector(didClickPayBtnForCell:)]) {
        [self.delegate didClickPayBtnForCell:self];
    }
}

@end

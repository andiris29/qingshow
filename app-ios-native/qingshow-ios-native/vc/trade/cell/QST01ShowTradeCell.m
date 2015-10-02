//
//  QST01ShowTradeCell.m
//  qingshow-ios-native
//
//  Created by mhy on 15/9/6.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QST01ShowTradeCell.h"
#import "QSTradeUtil.h"
#import "QSPeopleUtil.h"
#import "QSItemUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
@implementation QST01ShowTradeCell
{
    NSDictionary *_peopleDic;
}
- (void)awakeFromNib {
    // Initialization code
    self.outOfSaleLabel.layer.masksToBounds = YES;
    self.outOfSaleLabel.layer.cornerRadius = self.outOfSaleLabel.bounds.size.height/2;
    self.outOfSaleLabel.hidden = YES;
    self.headerImgView.layer.masksToBounds = YES;
    self.headerImgView.layer.cornerRadius = self.headerImgView.bounds.size.height/2;
    UITapGestureRecognizer *ges = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(didTapHeader)];
    self.headerImgView.userInteractionEnabled = YES;
    [self.headerImgView addGestureRecognizer: ges];
}

- (void)bindWithDic:(NSDictionary *)dict
{
    NSDictionary *peopleDic = [QSTradeUtil getPeopleDic:dict];
    _peopleDic = peopleDic;
    [self.headerImgView setImageFromURL:[QSPeopleUtil getHeadIconUrl:peopleDic type:QSImageNameType100]];
    self.userNameLabel.text = [QSPeopleUtil getNickname:peopleDic];
    self.timeLabel.text = [QSTradeUtil getDayDesc:dict];
    NSDictionary *itemDict = [QSTradeUtil getItemDic:dict];
    
    self.actualPriceLabel.text = [NSString stringWithFormat:@"￥%@",[QSItemUtil getExpectablePriceDesc:itemDict]];

    self.clothNameLabel.text = [QSItemUtil getItemName:itemDict];
    [self.itemImgView setImageFromURL:[QSItemUtil getThumbnail:itemDict]];
    NSString *oldPrice = [NSString stringWithFormat:@"原价：￥%@",[QSItemUtil getPriceDesc:itemDict]];
    NSMutableAttributedString *attri = [[NSMutableAttributedString alloc] initWithString:oldPrice];
    [attri addAttribute:NSStrikethroughStyleAttributeName value:@(NSUnderlinePatternSolid | NSUnderlineStyleSingle) range:NSMakeRange(0, oldPrice.length)];
    [attri addAttribute:NSStrikethroughColorAttributeName value:[UIColor colorWithWhite:0.400 alpha:1.000] range:NSMakeRange(0, oldPrice.length)];
    [self.priceLabel setAttributedText:attri];
    self.promoPriceLabel.text = [NSString stringWithFormat:@"现价：￥%@",[QSItemUtil getPromoPriceDesc:itemDict]];
    self.infoLabel.text = [QSTradeUtil getSizeText:dict];

    self.countLabel.text = [NSString stringWithFormat:@"数量：%@",[QSTradeUtil getQuantityDesc:dict]];
    self.disCountLabel.text = [NSString stringWithFormat:@"%@", [QSTradeUtil calculateDiscountDescWithPrice:[QSTradeUtil getExpectedPrice:dict] trade:dict]];
    if ([QSItemUtil getDelist:itemDict] == YES) {
        self.outOfSaleLabel.hidden = NO;
    }else{
        self.outOfSaleLabel.hidden = YES;
    }
}
- (void)didTapHeader
{
    if ([self.delegate respondsToSelector:@selector(didtapHeaderInT01VC:)]) {
        [self.delegate didtapHeaderInT01VC:_peopleDic];
    }
}
- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    // Configure the view for the selected state
}

@end

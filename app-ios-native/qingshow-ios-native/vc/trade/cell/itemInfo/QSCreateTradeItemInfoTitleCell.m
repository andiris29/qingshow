//
//  QSCreateTradeItemInfoTitleCell.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/16/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSCreateTradeItemInfoTitleCell.h"
#import "QSItemUtil.h"
#import "QSTradeUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "UIView+QSExtension.h"

@implementation QSCreateTradeItemInfoTitleCell
- (void)awakeFromNib {
    [super awakeFromNib];
    [self.iconImageView configBorderColor:[UIColor colorWithRed:40.f/255.f green:45.f/255.f blue:91.f/255.f alpha:1.f] width:1.f cornerRadius:4];
}

- (void)bindWithDict:(NSDictionary*)dict
{
    NSDictionary* tradeDict = dict;
    NSDictionary* itemDict = [QSTradeUtil getItemDic:tradeDict];
    self.titleLabel.text = [QSItemUtil getItemName:itemDict];
    
    self.priceLabel.text = [NSString stringWithFormat:@"￥%.2f", [QSItemUtil getPriceToPay:itemDict].floatValue];
    [self.priceLabel sizeToFit];
    [self.iconImageView setImageFromURL:[QSItemUtil getThumbnail:itemDict]];
    self.selectedSkuLabel.text = [QSTradeUtil getPropertiesFullDesc:tradeDict];
    self.quantityLabel.text = [NSString stringWithFormat:@"数量: %@",[QSTradeUtil getQuantityDesc:tradeDict]];
}

- (CGFloat)getHeightWithDict:(NSDictionary*)dict {
    return 157.f;
}
@end

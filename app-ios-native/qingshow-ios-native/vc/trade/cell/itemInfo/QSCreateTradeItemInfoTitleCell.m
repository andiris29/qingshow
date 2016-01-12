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
#import "QSItemTagView.h"

@interface QSCreateTradeItemInfoTitleCell ()
@property (strong, nonatomic) QSItemTagView* tagView;

@end

@implementation QSCreateTradeItemInfoTitleCell
- (void)awakeFromNib {
    [super awakeFromNib];
    [self.iconImageView configBorderColor:[UIColor colorWithRed:40.f/255.f green:45.f/255.f blue:91.f/255.f alpha:1.f] width:1.f cornerRadius:4];
    self.tagView = [QSItemTagView generateView];
    [self.contentView addSubview:self.tagView];
    self.tagView.hidden = YES;
}

- (void)bindWithDict:(NSDictionary*)dict
{
    NSDictionary* tradeDict = dict;
    NSDictionary* itemDict = [QSTradeUtil getItemDic:tradeDict];
    self.titleLabel.text = [QSItemUtil getItemName:itemDict];
    
    self.priceLabel.text = [NSString stringWithFormat:@"￥%.2f", [QSItemUtil getPromoPrice:itemDict].floatValue];
    [self.priceLabel sizeToFit];
    NSNumber* reduction = [QSItemUtil getExpectableReduction:itemDict];
    if (reduction) {
        self.priceLabel.isWithStrikeThrough = YES;
        self.tagView.hidden = NO;
        self.tagView.tagLabel.text = [NSString stringWithFormat:@"%@", reduction];
        self.tagView.center = self.priceLabel.center;
        CGRect rect = self.tagView.frame;
        rect.origin.x = self.priceLabel.frame.origin.x + self.priceLabel.frame.size.width + 20.f;
        self.tagView.frame = rect;
        [self.tagView updateSize];
    } else {
        self.priceLabel.isWithStrikeThrough = NO;
        self.tagView.hidden = YES;
    }
    
    
    [self.iconImageView setImageFromURL:[QSItemUtil getThumbnail:itemDict]];
    self.selectedSkuLabel.text = [QSTradeUtil getPropertiesFullDesc:tradeDict];
    self.quantityLabel.text = [NSString stringWithFormat:@"数量: %@",[QSTradeUtil getQuantityDesc:tradeDict]];
}

- (CGFloat)getHeightWithDict:(NSDictionary*)dict {
    return 157.f;
}
@end

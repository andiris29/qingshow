//
//  QSItemListCell.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSItemListCell.h"
#import "QSItemUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSCategoryUtil.h"
#import "QSCategoryManager.h"
#import "QSImageNameUtil.h"
@implementation QSItemListCell

- (void)awakeFromNib {
    // Initialization code
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    self.bgImageView.layer.cornerRadius = 45/2.f;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)bindWithDic:(NSDictionary *)itemDic
{
    self.priceLabel.text = [NSString stringWithFormat:@"价格:%@",[QSItemUtil getPriceDesc:itemDic]];
    NSURL *url = [QSItemUtil getThumbnail:itemDic];
    NSURL *reNamedUrl = [QSImageNameUtil appendImageNameUrl:url type:QSImageNameTypeS];
    [self.itemIcomImageView setImageFromURL:reNamedUrl];
    self.itemNameLabel.text = [QSItemUtil getItemName:itemDic];
}
@end

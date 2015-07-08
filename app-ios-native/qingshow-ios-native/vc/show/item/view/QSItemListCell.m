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

@implementation QSItemListCell

- (void)awakeFromNib {
    // Initialization code
    self.selectionStyle = UITableViewCellSelectionStyleNone;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)bindWithDic:(NSDictionary *)itemDic
{
    NSDictionary* category = [QSItemUtil getCategoryRef:itemDic];
    [self.itemIcomImageView setImageFromURL:[QSCategoryUtil getIconUrl:category]];
    self.itemNameLabel.text = [QSItemUtil getItemName:itemDic];
}
@end

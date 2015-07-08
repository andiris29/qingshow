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
    NSString *iconNum = itemDic[@"categoryRef"];
   // NSLog(@"num = %@",iconNum);
    //NSString *iconStr = [NSString stringWithFormat:@"itemIcon%@",iconNum];
    [self.itemIcomImageView setImageFromURL:[NSURL URLWithString:iconNum]];
    self.itemNameLabel.text = [QSItemUtil getItemName:itemDic];
}
@end

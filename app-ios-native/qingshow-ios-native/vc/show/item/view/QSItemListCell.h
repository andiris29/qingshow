//
//  QSItemListCell.h
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/5/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSItemListCell : UITableViewCell

//cell icon
@property (weak, nonatomic) IBOutlet UIImageView *itemIcomImageView;
//cell contents
@property (weak, nonatomic) IBOutlet UILabel *itemNameLabel;
@property (weak, nonatomic) IBOutlet UIImageView *bgImageView;
@property (weak, nonatomic) IBOutlet UILabel *priceLabel;

//cell赋值方法
- (void)bindWithDic:(NSDictionary *)itemDic;
@end

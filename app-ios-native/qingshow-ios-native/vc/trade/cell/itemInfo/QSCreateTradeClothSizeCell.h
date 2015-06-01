//
//  QSCreateTableViewSizeCell.h
//  qingshow-ios-native
//
//  Created by mhy on 15/5/26.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSCreateTradeHeigh60Cell.h"

@interface QSCreateTradeClothSizeCell : QSCreateTradeTableViewCellBase
@property (weak, nonatomic) IBOutlet UILabel* titleLabel;
@property (weak, nonatomic) IBOutlet UITextField *bustCircleOrWaistlineTextField;
@property (weak, nonatomic) IBOutlet UITextField *shoulderOrHiplineTextField;
@property (weak, nonatomic) IBOutlet UILabel* label1;
@property (weak, nonatomic) IBOutlet UILabel* label2;

@property (weak, nonatomic) IBOutlet UIImageView *bodyPartImgView;


@end

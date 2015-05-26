//
//  QSCreateTableViewSizeCell.h
//  qingshow-ios-native
//
//  Created by mhy on 15/5/26.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSCreateTradeTableViewCellBase.h"

@interface QSCreateTableViewSizeCell : QSCreateTradeTableViewCellBase
@property (weak, nonatomic) IBOutlet UITextField *bustCircleOrWaistlineTextField;
@property (weak, nonatomic) IBOutlet UITextField *shoulderOrHiplineTextField;
@property (weak, nonatomic) IBOutlet UIImageView *bodyPartImgView;


@end

//
//  QSDIscountInfoCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSAbstractDiscountTableViewCell.h"
#import "UILabelStrikeThrough.h"

@interface QSDiscountInfoCell : QSAbstractDiscountTableViewCell

@property (weak, nonatomic) IBOutlet UIImageView* iconImgView;
@property (weak, nonatomic) IBOutlet UILabel* nameLabel;
@property (weak, nonatomic) IBOutlet UILabel* priceLabel;
@property (weak, nonatomic) IBOutlet UIButton* detailBtn;


- (IBAction)detailBtnPressed:(id)sender;
@end

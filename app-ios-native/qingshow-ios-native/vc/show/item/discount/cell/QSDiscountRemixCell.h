//
//  QSDiscountRemixCell.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSAbstractDiscountTableViewCell.h"

@interface QSDiscountRemixCell : QSAbstractDiscountTableViewCell

@property (weak, nonatomic) IBOutlet UILabel* titleLabel;
@property (weak, nonatomic) IBOutlet UIView* remixContainer;
@property (weak, nonatomic) IBOutlet UIButton* remixBtn;

- (IBAction)remixBtnPressed:(UIButton*)sender;
- (IBAction)previousRemixBtnPressed:(UIButton*)sender;
- (IBAction)nextRemixBtnPressed:(UIButton*)sender;

- (void)bindWithRemix:(NSDictionary*)remixInfoDict;

@end

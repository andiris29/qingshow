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

@property (weak, nonatomic) IBOutlet UIButton* previousBtn;
@property (weak, nonatomic) IBOutlet UIButton* nextBtn;
@property (weak, nonatomic) IBOutlet UILabel* mesageLabel;

- (IBAction)remixBtnPressed:(UIButton*)sender;
- (IBAction)previousRemixBtnPressed:(UIButton*)sender;
- (IBAction)nextRemixBtnPressed:(UIButton*)sender;

- (void)bindWithItem:(NSDictionary*)itemDict remix:(NSDictionary*)remixInfoDict;

@end

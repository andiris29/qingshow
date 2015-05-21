//
//  QSS11ItemDetailVideoViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/17/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSVideoBaseViewController.h"
@class UILabelStrikeThrough;

#warning TODO adjust super class from QSVideoBaseViewController to UIViewController
@interface QSS10ItemDetailVideoViewController : QSVideoBaseViewController

@property (strong, nonatomic) IBOutlet UIButton* buyBtn;

- (IBAction)shopBtnPressed:(id)sender;
@property (weak, nonatomic) IBOutlet UIImageView *shadow;
@property (weak, nonatomic) IBOutlet UIView *btnPanel;

@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabelStrikeThrough *priceLabel;
@property (weak, nonatomic) IBOutlet UILabel *priceAfterDiscountLabel;
@property (weak, nonatomic) IBOutlet UIView *labelContainer;

- (instancetype)initWithItem:(NSDictionary*)itemDict;

@property (weak, nonatomic) IBOutlet UIView *discountContainer;

@end

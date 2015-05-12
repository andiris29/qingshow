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

@interface QSS10ItemDetailVideoViewController : QSVideoBaseViewController

@property (strong, nonatomic) IBOutlet UIButton* buyBtn;

- (IBAction)shopBtnPressed:(id)sender;
@property (weak, nonatomic) IBOutlet UIImageView *shadow;
@property (weak, nonatomic) IBOutlet UIView *btnPanel;

@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabelStrikeThrough *priceLabel;
@property (weak, nonatomic) IBOutlet UILabel *priceAfterDiscountLabel;
@property (weak, nonatomic) IBOutlet UIView *labelContainer;
@property (weak, nonatomic) IBOutlet UIButton *likeButton;


- (instancetype)initWithItem:(NSDictionary*)itemDict;
- (IBAction)likeButtonPressed:(id)sender;


@end

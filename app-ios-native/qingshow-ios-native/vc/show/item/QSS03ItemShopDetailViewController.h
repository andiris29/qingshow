//
//  QSS03ShopDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 12/16/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSAllItemImageScrollView.h"

@interface QSS03ItemShopDetailViewController : UIViewController <UIScrollViewDelegate, QSImageScrollViewBaseDelegate>

@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UIButton *showBtn;
@property (weak, nonatomic) IBOutlet UILabel *label1;
@property (weak, nonatomic) IBOutlet UILabel *label2;
- (IBAction)shopButtonPressed:(id)sender;
//@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;

@property (weak, nonatomic) IBOutlet UIView *contentView;
- (id)initWithShow:(NSDictionary*)showDict currentItemIndex:(int)index;
- (IBAction)backBtnPressed:(id)sender;

@end

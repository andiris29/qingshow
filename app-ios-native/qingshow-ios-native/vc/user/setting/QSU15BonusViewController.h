//
//  QSU15BonusViewController.h
//  qingshow-ios-native
//
//  Created by mhy on 15/8/31.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSU15BonusViewController : UIViewController<UIScrollViewDelegate,UITextFieldDelegate>
@property (weak, nonatomic) IBOutlet UITextField *alipayTextField;
@property (weak, nonatomic) IBOutlet UILabel *allBonusLabel;
@property (weak, nonatomic) IBOutlet UILabel *currBonusLabel;
@property (weak, nonatomic) IBOutlet UIButton *shareToGetBtn;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;

- (instancetype)initwithBonuesArray:(NSArray *)array;
@end

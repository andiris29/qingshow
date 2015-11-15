//
//  QSU15BonusViewController.h
//  qingshow-ios-native
//
//  Created by mhy on 15/8/31.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSU15BonusViewController : UIViewController<UIScrollViewDelegate,UITextFieldDelegate,UIAlertViewDelegate>
@property (weak, nonatomic) IBOutlet UITextField *alipayTextField;
@property (weak, nonatomic) IBOutlet UILabel *allBonusLabel;
@property (weak, nonatomic) IBOutlet UILabel *currBonusLabel;
@property (weak, nonatomic) IBOutlet UIButton *shareToGetBtn;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak, nonatomic) IBOutlet UIImageView *alipayIconImgView;
@property (weak, nonatomic) IBOutlet UILabel *descTextLabel;


@property (strong,nonatomic)NSString *peopleId;
@property (strong,nonatomic)NSString *alipayId;
- (instancetype)initwithBonuesArray:(NSArray *)array;

@end

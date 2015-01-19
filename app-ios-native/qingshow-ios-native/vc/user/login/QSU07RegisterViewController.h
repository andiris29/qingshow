//
//  QSU07RegisterViewController.h
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/14.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSU07RegisterViewController : UIViewController<UITextFieldDelegate>
@property (weak, nonatomic) IBOutlet UILabel *genderLabel;
@property (weak, nonatomic) IBOutlet UILabel *shoeSizeLabel;
@property (weak, nonatomic) IBOutlet UILabel *clothingSizeLabel;

@property (weak, nonatomic) IBOutlet UIButton *femaleButton;
@property (weak, nonatomic) IBOutlet UIButton *maleButton;

@property (weak, nonatomic) IBOutlet UIButton *xxsButton;
@property (weak, nonatomic) IBOutlet UIButton *xsButton;
@property (weak, nonatomic) IBOutlet UIButton *sButton;
@property (weak, nonatomic) IBOutlet UIButton *mButton;
@property (weak, nonatomic) IBOutlet UIButton *lButton;
@property (weak, nonatomic) IBOutlet UIButton *xlButton;
@property (weak, nonatomic) IBOutlet UIButton *xxlButton;
@property (weak, nonatomic) IBOutlet UIButton *xxxlButton;

@property (weak, nonatomic) IBOutlet UIButton *three4Button;
@property (weak, nonatomic) IBOutlet UIButton *three5Button;
@property (weak, nonatomic) IBOutlet UIButton *three6Button;
@property (weak, nonatomic) IBOutlet UIButton *three7Button;
@property (weak, nonatomic) IBOutlet UIButton *three8Button;
@property (weak, nonatomic) IBOutlet UIButton *three9Button;
@property (weak, nonatomic) IBOutlet UIButton *four0Button;
@property (weak, nonatomic) IBOutlet UIButton *four1Button;
@property (weak, nonatomic) IBOutlet UIButton *four2Button;
@property (weak, nonatomic) IBOutlet UIButton *four3Button;
@property (weak, nonatomic) IBOutlet UIButton *four4Button;

@property (assign, nonatomic) NSInteger gender;
@property (assign, nonatomic) NSInteger clothingSize;
@property (assign, nonatomic) NSInteger shoeSize;

@property (nonatomic, assign) id currentResponder;

- (IBAction)selectGender:(id)sender;

- (IBAction)selectClothingSize:(id)sender;

- (IBAction)selectShoeSize:(id)sender;
@property (strong, nonatomic) IBOutlet UIView *contentView;

@property (weak, nonatomic) IBOutlet UIScrollView *containerScrollView;
@end

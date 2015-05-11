//
//  QSU13PersonalizeViewController.h
//  qingshow-ios-native
//
//  Created by ching show on 15/5/5.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSU13PersonalizeViewController : UIViewController
@property (weak, nonatomic) IBOutlet UILabel *ageLabel;
@property (weak, nonatomic) IBOutlet UISlider *ageSlider;
@property (weak, nonatomic) IBOutlet UILabel *hightLabel;
@property (weak, nonatomic) IBOutlet UISlider *hightSlider;
@property (weak, nonatomic) IBOutlet UILabel *weightLabel;

@property (weak, nonatomic) IBOutlet UISlider *weightSlider;
@property (weak, nonatomic) IBOutlet UIButton *Abutton;
@property (weak, nonatomic) IBOutlet UIButton *VButton;
@property (weak, nonatomic) IBOutlet UIButton *HButton;
@property (weak, nonatomic) IBOutlet UIButton *XButton;
@property (weak, nonatomic) IBOutlet UIButton *JKButton;
@property (weak, nonatomic) IBOutlet UIButton *EAButton;
@property (weak, nonatomic) IBOutlet UIButton *thinButton;
@property (weak, nonatomic) IBOutlet UIButton *hightButton;
@property (weak, nonatomic) IBOutlet UIButton *shenButton;
@property (weak, nonatomic) IBOutlet UIButton *duButton;
@property (weak, nonatomic) IBOutlet UIButton *armButton;
@property (weak, nonatomic) IBOutlet UIButton *tunButton;
@property (weak, nonatomic) IBOutlet UIButton *OKButton;

@property (nonatomic,assign)NSInteger bodyType;
@property (nonatomic,assign)NSInteger dressStyle;
@property (nonatomic,strong)NSMutableArray *expectations;

@end

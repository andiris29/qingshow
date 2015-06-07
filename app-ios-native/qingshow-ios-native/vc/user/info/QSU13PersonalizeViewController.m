//
//  QSU13PersonalizeViewController.m
//  qingshow-ios-native
//
//  Created by ching show on 15/5/5.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU13PersonalizeViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+QSExtension.h"
#import "UIViewController+ShowHud.h"

#define PAGE_ID @"U13 - 个性信息"

@interface QSU13PersonalizeViewController ()

@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;

@end

@implementation QSU13PersonalizeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.bodyType = -1;
    self.dressStyle = -1;
    // Do any additional setup after loading the view from its nib.
    //设置滑块图片
    UIImage *sliderLeftTrack = [UIImage imageNamed:@"slider_left_image"];
    UIImage *sliderRightTrack = [UIImage imageNamed:@"slider_right_image"];
    UIImage *tumbImage = [UIImage imageNamed:@"slider_mid_image"];
    //年龄滑块
    [self.ageSlider setMinimumTrackImage:sliderLeftTrack forState:UIControlStateNormal];
    [self.ageSlider setMaximumTrackImage:sliderRightTrack forState:UIControlStateNormal];
    self.ageSlider.continuous = YES;
    [self.ageSlider setThumbImage:tumbImage forState:UIControlStateNormal];
    [self.ageSlider setThumbImage:tumbImage forState:UIControlStateHighlighted];
    self.ageSlider.transform = CGAffineTransformMakeScale(0.6f, 0.6f);
    [self.ageSlider addTarget:self action:@selector(getWithAge) forControlEvents:UIControlEventValueChanged];
    
    //身高滑块
    [self.hightSlider setMinimumTrackImage:sliderLeftTrack forState:UIControlStateNormal];
    [self.hightSlider setMaximumTrackImage:sliderRightTrack forState:UIControlStateNormal];
    self.hightSlider.continuous = YES;
    [self.hightSlider setThumbImage:tumbImage forState:UIControlStateNormal];
    [self.hightSlider setThumbImage:tumbImage forState:UIControlStateHighlighted];
    self.hightSlider.transform = CGAffineTransformMakeScale(0.6f, 0.6f);
    [self.hightSlider addTarget:self action:@selector(getWithHight) forControlEvents:UIControlEventValueChanged];
    
    //体重滑块
    [self.weightSlider setMinimumTrackImage:sliderLeftTrack forState:UIControlStateNormal];
    [self.weightSlider setMaximumTrackImage:sliderRightTrack forState:UIControlStateNormal];
    self.weightSlider.continuous = YES;
    [self.weightSlider setThumbImage:tumbImage forState:UIControlStateNormal];
    [self.weightSlider setThumbImage:tumbImage forState:UIControlStateHighlighted];
    self.weightSlider.transform = CGAffineTransformMakeScale(0.6f, 0.6f);
    [self.weightSlider addTarget:self action:@selector(getWithWeight) forControlEvents:UIControlEventValueChanged];
    
    
    
    
    self.expectations = [NSMutableArray arrayWithCapacity:0];
    
    self.JKButton.layer.cornerRadius = 4.f;
    self.JKButton.layer.masksToBounds = YES;
    self.JKButton.layer.borderWidth = 1.0f;
    self.JKButton.layer.borderColor = [[UIColor whiteColor]CGColor];
    
    self.EAButton.layer.cornerRadius = 4.f;
    self.EAButton.layer.masksToBounds = YES;
    self.EAButton.layer.borderWidth = 1.0f;
    self.EAButton.layer.borderColor = [[UIColor whiteColor]CGColor];
    
    self.thinButton.layer.cornerRadius = 4.f;
    self.thinButton.layer.masksToBounds = YES;
    self.thinButton.layer.borderWidth = 1.0f;
    self.thinButton.layer.borderColor = [[UIColor whiteColor]CGColor];
    
    self.hightButton.layer.cornerRadius = 4.f;
    self.hightButton.layer.masksToBounds = YES;
    self.hightButton.layer.borderWidth = 1.0f;
    self.hightButton.layer.borderColor = [[UIColor whiteColor]CGColor];
    
    self.shenButton.layer.cornerRadius = 4;
    self.shenButton.layer.masksToBounds = YES;
    self.shenButton.layer.borderWidth = 1.0f;
    self.shenButton.layer.borderColor = [[UIColor whiteColor]CGColor];
    
    self.tunButton.layer.cornerRadius = 4.f;
    self.tunButton.layer.masksToBounds = YES;
    self.tunButton.layer.borderWidth = 1.0f;
    self.tunButton.layer.borderColor = [[UIColor whiteColor]CGColor];
    
    self.duButton.layer.cornerRadius = 4.f;
    self.duButton.layer.masksToBounds = YES;
    self.duButton.layer.borderWidth = 1.0f;
    self.duButton.layer.borderColor = [[UIColor whiteColor]CGColor];
    
    self.armButton.layer.cornerRadius = 4.f;
    self.armButton.layer.masksToBounds = YES;
    self.armButton.layer.borderWidth = 1.0f;
    self.armButton.layer.borderColor = [[UIColor whiteColor]CGColor];

    
    self.OKButton.backgroundColor = [UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1];
    self.OKButton.layer.cornerRadius = self.OKButton.frame.size.height / 8;
    self.OKButton.layer.masksToBounds = YES;
    
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    [MobClick beginLogPageView:PAGE_ID];
    
    // Do any additional setup after loading the view from its nib.
    _scrollView.contentSize = CGSizeMake(self.view.frame.size.width, 1000);
    self.JKButton.layer.cornerRadius = self.JKButton.frame.size.height / 8;
    self.JKButton.clipsToBounds = YES;
    self.EAButton.layer.cornerRadius = self.EAButton.frame.size.height / 8;
    self.EAButton.clipsToBounds = YES;
    self.OKButton.layer.cornerRadius = self.OKButton.frame.size.height / 8;
    self.OKButton.clipsToBounds = YES;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}

- (void)getWithAge
{
    self.ageLabel.text =  [NSString stringWithFormat:@"%.0f 岁", self.ageSlider.value];
}

- (void)getWithHight
{
    self.hightLabel.text = [NSString stringWithFormat:@"%.0f cm", self.hightSlider.value];
}
- (void)getWithWeight
{
    self.weightLabel.text = [NSString stringWithFormat:@"%.0f kg", self.weightSlider.value];
}

- (IBAction)getWithJK:(id)sender {
    self.dressStyle = 0;
    [self.EAButton setBackgroundColor:[UIColor clearColor]];
    self.EAButton.layer.borderWidth = 1.0f;
    [self.JKButton setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
    self.JKButton.layer.cornerRadius = 4.f;
    self.JKButton.layer.masksToBounds = YES;
    self.JKButton.layer.borderWidth = 0.f;
}

- (IBAction)getWithEA:(id)sender {
    self.dressStyle = 1;
    [self.JKButton setBackgroundColor:[UIColor clearColor]];
    self.JKButton.layer.borderWidth = 1.0f;
    [self.EAButton setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
    self.EAButton.layer.cornerRadius = 4.f;
    self.EAButton.layer.masksToBounds = YES;
    //self.EAButton.layer.borderColor = [[UIColor clearColor]CGColor];
    self.EAButton.layer.borderWidth = 0.f;

}
- (IBAction)getWithBodA:(id)sender {
    self.bodyType = 0;
    [self.Abutton setImage:[UIImage imageNamed:@"user_person_check_a"] forState:UIControlStateNormal];
    [self.HButton setImage:[UIImage imageNamed:@"user_person_h"] forState:UIControlStateNormal];
    [self.VButton setImage:[UIImage imageNamed:@"user_person_v"] forState:UIControlStateNormal];
    [self.XButton setImage:[UIImage imageNamed:@"user_person_x"] forState:UIControlStateNormal];
}
- (IBAction)getWithBodH:(id)sender {
    self.bodyType = 1;
    [self.Abutton setImage:[UIImage imageNamed:@"user_person_a"] forState:UIControlStateNormal];
    [self.HButton setImage:[UIImage imageNamed:@"user_person_check_h"] forState:UIControlStateNormal];
    [self.VButton setImage:[UIImage imageNamed:@"user_person_v"] forState:UIControlStateNormal];
    [self.XButton setImage:[UIImage imageNamed:@"user_person_x"] forState:UIControlStateNormal];

}
- (IBAction)getWithBodV:(id)sender {
    self.bodyType = 2;
    [self.VButton setImage:[UIImage imageNamed:@"user_person_check_v"] forState:UIControlStateNormal];
    [self.Abutton setImage:[UIImage imageNamed:@"user_person_a"] forState:UIControlStateNormal];
    [self.HButton setImage:[UIImage imageNamed:@"user_person_h"] forState:UIControlStateNormal];
    [self.XButton setImage:[UIImage imageNamed:@"user_person_x"] forState:UIControlStateNormal];
}
- (IBAction)getWithBodX:(id)sender {
    self.bodyType = 3;
    [self.Abutton setImage:[UIImage imageNamed:@"user_person_a"] forState:UIControlStateNormal];
    [self.HButton setImage:[UIImage imageNamed:@"user_person_h"] forState:UIControlStateNormal];
    [self.VButton setImage:[UIImage imageNamed:@"user_person_v"] forState:UIControlStateNormal];
    [self.XButton setImage:[UIImage imageNamed:@"user_person_check_x"] forState:UIControlStateNormal];
}

- (IBAction)back:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}
//点击期望
- (IBAction)selectMacth:(id)sender {
    UIButton *button = (UIButton *)sender;
    NSInteger expect = button.tag - 101;

    NSString *matStr = [NSString stringWithFormat:@"%d", expect];
    NSNumber* mat = @(matStr.intValue);
    
    switch (button.tag) {
            case 101:
                if (button.selected) {
                    [button setSelected:NO];
                    button.layer.borderWidth = 1.0f;
                    [button setBackgroundColor:[UIColor clearColor]];
                    [self.expectations removeObject:mat];
                } else{
                    [button setSelected:YES];
                    button.layer.borderWidth = 0.f;
                    button.layer.cornerRadius = 4.f;
                    button.layer.masksToBounds = YES;
                    [button setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
                    [self.expectations addObject:mat];
                }
                break;
            case 102:
                if (button.selected) {
                    [button setSelected:NO];
                     button.layer.borderWidth = 1.0f;
                     [button setBackgroundColor:[UIColor clearColor]];
                    [self.expectations removeObject:mat];
                } else{
                    [button setSelected:YES];
                    button.layer.borderWidth = 0.f;
                    button.layer.cornerRadius = 4.f;
                    button.layer.masksToBounds = YES;
                    [button setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
                    [self.expectations addObject:mat];
                }
                break;
            case 103:
                if (button.selected) {
                    [button setSelected:NO];
                     button.layer.borderWidth = 1.0f;
                    [button setBackgroundColor:[UIColor clearColor]];
                    [self.expectations removeObject:mat];
                } else{
                    [button setSelected:YES];
                    button.layer.borderWidth = 0.f;
                    button.layer.cornerRadius = 4.f;
                    button.layer.masksToBounds = YES;
                    [button setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
                    [self.expectations addObject:mat];
                }
                break;
            case 104:
                if (button.selected) {
                    [button setSelected:NO];
                     button.layer.borderWidth = 1.0f;
                    [button setBackgroundColor:[UIColor clearColor]];
                    [self.expectations removeObject:mat];
                } else{
                    [button setSelected:YES];
                    button.layer.borderWidth = 0.f;
                    button.layer.cornerRadius = 4.f;
                    button.layer.masksToBounds = YES;
                    [button setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
                    [self.expectations addObject:mat];
                }

                break;
            case 105:
                if (button.selected) {
                    [button setSelected:NO];
                     button.layer.borderWidth = 1.0f;
                    [button setBackgroundColor:[UIColor clearColor]];
                    [self.expectations removeObject:mat];
                } else{
                    [button setSelected:YES];
                    button.layer.borderWidth = 0.f;
                    button.layer.cornerRadius = 4.f;
                    button.layer.masksToBounds = YES;
                    [button setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
                    [self.expectations addObject:mat];
                }

                break;
                
            default:
                if (button.selected) {
                    [button setSelected:NO];
                     button.layer.borderWidth = 1.0f;
                    [button setBackgroundColor:[UIColor clearColor]];
                    [self.expectations removeObject:mat];
                } else{
                    [button setSelected:YES];
                    button.layer.borderWidth = 0.f;
                    button.layer.cornerRadius = 4.f;
                    button.layer.masksToBounds = YES;
                    [button setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
                    [self.expectations addObject:mat];
                }

                break;
        }
    
    
}
- (IBAction)personalizeOK:(id)sender {
    if (self.bodyType < 0) {
        [self showErrorHudWithText:@"请选择体型"];
        return;
    }
    if (self.dressStyle < 0) {
        [self showErrorHudWithText:@"请选择穿衣风格"];
        return;
    }
    if (!self.expectations.count) {
        [self showErrorHudWithText:@"请选择搭配期望"];
        return;
    }
//    UIViewController *vc = [[QSS15ChosenViewController alloc] init];
//    [self.navigationController pushViewController:vc animated:YES];
    self.age = [NSString stringWithFormat:@"%.0f", self.ageSlider.value];
    self.hight = [NSString stringWithFormat:@"%.0f", self.hightSlider.value];
    self.weight = [NSString stringWithFormat:@"%.0f", self.weightSlider.value];
    
    [SHARE_NW_ENGINE updatePeople:@{@"age" : @(self.age.intValue),
                                    @"height" : @(self.hight.intValue),
                                    @"weight" : @(self.weight.intValue),
                                    @"bodyType" : @(self.bodyType),
                                    @"dressStyle" : @(self.dressStyle),
                                    @"expectations" : self.expectations}
                        onSuccess:^(NSDictionary *data, NSDictionary *metadata) {
                            [self.navigationController popViewControllerAnimated:YES];
                        } onError:^(NSError *error) {
                            [self handleError:error];
                        }];
}

@end

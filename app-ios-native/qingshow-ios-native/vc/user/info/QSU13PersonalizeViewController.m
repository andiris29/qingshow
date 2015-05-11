//
//  QSU13PersonalizeViewController.m
//  qingshow-ios-native
//
//  Created by ching show on 15/5/5.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU13PersonalizeViewController.h"
#import "QSS15ChosenViewController.h"

#define PAGE_ID @"U13 - 个性信息"

@interface QSU13PersonalizeViewController ()

@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;

@end

@implementation QSU13PersonalizeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    _scrollView.contentSize = CGSizeMake(self.view.frame.size.width, 1000);
    
    UIImage *sliderLeftTrack = [UIImage imageNamed:@"slider_left_image"];
    UIImage *sliderRightTrack = [UIImage imageNamed:@"slider_right_image"];
    UIImage *tumbImage = [UIImage imageNamed:@"slider_mid_image"];
    [self.ageSlider setMinimumTrackImage:sliderLeftTrack forState:UIControlStateNormal];
    [self.ageSlider setMaximumTrackImage:sliderRightTrack forState:UIControlStateNormal];
    self.ageSlider.continuous = YES;
    [self.ageSlider setThumbImage:tumbImage forState:UIControlStateNormal];
    [self.ageSlider setThumbImage:tumbImage forState:UIControlStateHighlighted];
    
    [self.ageSlider addTarget:self action:@selector(getWithAge) forControlEvents:UIControlEventValueChanged];
    [self.hightSlider addTarget:self action:@selector(getWithHight) forControlEvents:UIControlEventValueChanged];
    [self.weightSlider addTarget:self action:@selector(getWithWeight) forControlEvents:UIControlEventValueChanged];
    
    [self.ageSlider minimumValueImageRectForBounds:<#(CGRect)#>]
    
    
    self.expectations = [NSMutableArray arrayWithCapacity:0];
    
    self.JKButton.layer.cornerRadius = 4;
    self.JKButton.layer.masksToBounds = YES;
    self.JKButton.layer.borderWidth = 1.0f;
    self.JKButton.layer.borderColor = [[UIColor whiteColor]CGColor];
    self.EAButton.layer.cornerRadius = 4;
    self.EAButton.layer.masksToBounds = YES;
    self.EAButton.layer.borderWidth = 1.0f;
    self.EAButton.layer.borderColor = [[UIColor whiteColor]CGColor];
    
    
    self.OKButton.backgroundColor = [UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1];
    self.OKButton.layer.cornerRadius = self.OKButton.frame.size.height / 8;
    self.OKButton.layer.masksToBounds = YES;
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    [MobClick beginLogPageView:PAGE_ID];
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
        [self.JKButton setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
}

- (IBAction)getWithEA:(id)sender {
    self.dressStyle = 1;
    [self.JKButton setBackgroundColor:[UIColor clearColor]];
    [self.EAButton setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
}
- (IBAction)getWithBodA:(id)sender {
    self.bodyType = ((UIButton *)sender).tag;
    [self.Abutton setImage:[UIImage imageNamed:@"user_person_check_a"] forState:UIControlStateNormal];
    [self.HButton setImage:[UIImage imageNamed:@"user_person_h"] forState:UIControlStateNormal];
    [self.VButton setImage:[UIImage imageNamed:@"user_person_v"] forState:UIControlStateNormal];
    [self.XButton setImage:[UIImage imageNamed:@"user_person_x"] forState:UIControlStateNormal];
}
- (IBAction)getWithBodH:(id)sender {
    self.bodyType = ((UIButton *)sender).tag;
    [self.Abutton setImage:[UIImage imageNamed:@"user_person_a"] forState:UIControlStateNormal];
    [self.HButton setImage:[UIImage imageNamed:@"user_person_check_h"] forState:UIControlStateNormal];
    [self.VButton setImage:[UIImage imageNamed:@"user_person_v"] forState:UIControlStateNormal];
    [self.XButton setImage:[UIImage imageNamed:@"user_person_x"] forState:UIControlStateNormal];

}
- (IBAction)getWithBodV:(id)sender {
    self.bodyType = ((UIButton *)sender).tag;
    [self.VButton setImage:[UIImage imageNamed:@"user_person_check_v"] forState:UIControlStateNormal];
    [self.Abutton setImage:[UIImage imageNamed:@"user_person_a"] forState:UIControlStateNormal];
    [self.HButton setImage:[UIImage imageNamed:@"user_person_h"] forState:UIControlStateNormal];
    [self.XButton setImage:[UIImage imageNamed:@"user_person_x"] forState:UIControlStateNormal];
}
- (IBAction)getWithBodX:(id)sender {
    self.bodyType = ((UIButton *)sender).tag;
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
    NSInteger expect = button.tag;
    NSNumber *mat = [NSNumber numberWithInteger:expect];
    switch (button.tag) {
            case 101:
                if (button.selected) {
                    [button setSelected:NO];
                    [button setBackgroundColor:[UIColor clearColor]];
                    [self.expectations removeObject:mat];
                } else{
                    [button setSelected:YES];
                    [button setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
                    [self.expectations addObject:mat];
                }
                break;
            case 102:
                if (button.selected) {
                    [button setSelected:NO];
                     [button setBackgroundColor:[UIColor clearColor]];
                    [self.expectations removeObject:mat];
                } else{
                    [button setSelected:YES];
                    [button setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
                    [self.expectations addObject:mat];
                }
                break;
            case 103:
                if (button.selected) {
                    [button setSelected:NO];
                    [button setBackgroundColor:[UIColor clearColor]];
                    [self.expectations removeObject:mat];
                } else{
                    [button setSelected:YES];
                    [button setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
                    [self.expectations addObject:mat];
                }
                break;
            case 104:
                if (button.selected) {
                    [button setSelected:NO];
                    [button setBackgroundColor:[UIColor clearColor]];
                    [self.expectations removeObject:mat];
                } else{
                    [button setSelected:YES];
                    [button setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
                    [self.expectations addObject:mat];
                }

                break;
            case 105:
                if (button.selected) {
                    [button setSelected:NO];
                    [button setBackgroundColor:[UIColor clearColor]];
                    [self.expectations removeObject:mat];
                } else{
                    [button setSelected:YES];
                    [button setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
                    [self.expectations addObject:mat];
                }

                break;
                
            default:
                if (button.selected) {
                    [button setSelected:NO];
                    [button setBackgroundColor:[UIColor clearColor]];
                    [self.expectations removeObject:mat];
                } else{
                    [button setSelected:YES];
                    [button setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
                    [self.expectations addObject:mat];
                }

                break;
        }
    
    
}
- (CGRect)minimumValueImageRectForBounds
{
    return CGRectMake(0, 0, 70, 5);
}
- (IBAction)personalizeOK:(id)sender {
//    UIViewController *vc = [[QSS15ChosenViewController alloc] init];
//    [self.navigationController pushViewController:vc animated:YES];
    NSLog(@"%@", self.ageLabel.text);
    NSLog(@"%@", self.weightLabel.text);
    NSLog(@"%@", self.hightLabel.text);
    NSLog(@"%d", self.bodyType);
    NSLog(@"%d", self.dressStyle);
    NSLog(@"%@", self.expectations);
}

@end

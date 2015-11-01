//
//  QSU20NewBonusViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/1.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU20NewBonusViewController.h"

@interface QSU20NewBonusViewController ()



@property (weak, nonatomic) IBOutlet UIImageView *itemImageView;

@property (weak, nonatomic) IBOutlet UIImageView *userHeadIconImageView;

@property (weak, nonatomic) IBOutlet UILabel *userNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *bonusNumberLabel;
@property (weak, nonatomic) IBOutlet UIView *otherUserHeadIconContainer;
@property (weak, nonatomic) IBOutlet UIButton *withdrawBtn;


@end

@implementation QSU20NewBonusViewController

#pragma mark - Init
- (instancetype)init {
    self = [super initWithNibName:@"QSU20NewBonusViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self _configUi];
}
- (void)_configUi {
    self.itemImageView.layer.cornerRadius = 10.f;
    self.itemImageView.layer.masksToBounds = YES;
    
    self.userHeadIconImageView.layer.cornerRadius = self.userHeadIconImageView.bounds.size.height / 2;
    self.userHeadIconImageView.layer.masksToBounds = YES;
    self.userHeadIconImageView.layer.borderColor = [UIColor whiteColor].CGColor;
    self.userHeadIconImageView.layer.cornerRadius = 3.f;
    
    self.withdrawBtn.layer.cornerRadius = self.withdrawBtn.bounds.size.height / 2;
    self.withdrawBtn.layer.masksToBounds = YES;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction
- (IBAction)withdrawBtnPressed:(id)sender {
}

- (IBAction)closeBtnPressed:(id)sender {
}


@end

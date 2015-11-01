//
//  QSU21NewParticipantBonusViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/1.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU21NewParticipantBonusViewController.h"

@interface QSU21NewParticipantBonusViewController ()

@property (weak, nonatomic) IBOutlet UIImageView *itemImageView;
@property (weak, nonatomic) IBOutlet UIImageView *userHeadIconImageView;
@property (weak, nonatomic) IBOutlet UILabel *userNameLabel;

@property (weak, nonatomic) IBOutlet UILabel *priceLabel;
@property (weak, nonatomic) IBOutlet UIButton *withdrawBtn;

@end

@implementation QSU21NewParticipantBonusViewController

#pragma mark - Init
- (instancetype)init {
    self = [super initWithNibName:@"QSU21NewParticipantBonusViewController" bundle:nil];
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

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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

#pragma mark - IBAction
- (IBAction)closeBtnPressed:(id)sender {
}
- (IBAction)withdrawBtnPressed:(id)sender {
}



@end

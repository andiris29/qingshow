//
//  QSU15BonusViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/8/31.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU15BonusViewController.h"
#import "QSU16BonusListViewController.h"
#import "MKNetworkKit.h"
#import "QSPeopleUtil.h"
#import "QSShareService.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSUserManager.h"
#import "QSUnreadManager.h"
#import "QSNetworkEngine+ShareService.h"
#import "QSShareUtil.h"
#import "UIViewController+QSExtension.h"
#import "QSThirdPartLoginService.h"
#import "NSDictionary+QSExtension.h"

@interface QSU15BonusViewController ()

@property (assign, nonatomic) float availableMoney;
@property (assign, nonatomic) float totalMoney;
@end

@implementation QSU15BonusViewController

#pragma mark - Init
- (instancetype)init {
    if (self == [super initWithNibName:@"QSU15BonusViewController" bundle:nil]) {
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self _configNav];
    
    [self _reloadData];

    [self _configUI];
    
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[QSUnreadManager getInstance] clearBonuUnread];
    self.navigationController.navigationBarHidden = NO;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    self.containerView.frame = self.view.bounds;
}

#pragma mark - IBAction
- (void)bonusListBtnPressed:(id)sender
{
    QSU16BonusListViewController *vc = [[QSU16BonusListViewController alloc]init];

    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    vc.navigationItem.leftBarButtonItem = backItem;
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)withdrawBtnPressed:(id)sender {
    if (self.availableMoney == 0) {
        return;
    }
    if (self.availableMoney < 1.f) {
        [self showErrorHudWithText:@"收益需要1元以上才能提取"];
        return;
    }
    NSDictionary *peopleDic = [QSUserManager shareUserManager].userInfo;
    NSString *peopleId = [QSPeopleUtil getPeopleId:peopleDic];
    
    if ([QSPeopleUtil hasBindWechat:peopleDic]) {
        [SHARE_NW_ENGINE shareCreateBonus:peopleId onSucceed:^(NSDictionary *shareDic) {
            [[QSShareService shareService]shareWithWechatMoment:[QSShareUtil getShareTitle:shareDic] desc:[QSShareUtil getShareDesc:shareDic] imagePath:[QSShareUtil getShareIcon:shareDic] url:[QSShareUtil getshareUrl:shareDic] onSucceed:^{
                [self showSuccessHudWithText:@"提取成功"];
            } onError:nil];
        } onError:^(NSError *error) {
            [self handleError:error];
        }];
    } else {
        [[QSThirdPartLoginService getInstance] bindWithWechatOnSucceed:^{
            [self _configUI];
        } onError:^(NSError *error) {
            [self handleError:error];
        }];
    }
}

- (IBAction)faqBtnPressed:(id)sender {
    if (!self.faqLayer.superview) {
        [self.navigationController.view addSubview:self.faqLayer];
        self.faqLayer.frame = self.navigationController.view.bounds;
    }

}
- (IBAction)closeFaqBtnPressed:(id)sender {
    [self.faqLayer removeFromSuperview];
}


#pragma mark - AlertView Delegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (alertView.tag == 345) {
        [self.navigationController popToRootViewControllerAnimated:YES];
    }
}

#pragma mark - Private
- (void)_configUI {
    NSDictionary* userDict = [QSUserManager shareUserManager].userInfo;
    //Withdraw Btn
    if ([QSPeopleUtil hasBindWechat:userDict]) {
        [self.withdrawBtn setTitle:@"分享提现" forState:UIControlStateNormal];
    } else {
        [self.withdrawBtn setTitle:@"登陆微信分享提现" forState:UIControlStateNormal];
    }
    self.withdrawBtn.layer.cornerRadius = self.withdrawBtn.bounds.size.height / 2;
    
    self.faqBtn.layer.cornerRadius = self.faqBtn.bounds.size.height / 2;
    self.faqBtn.layer.borderWidth = 1.f;
    UIColor* faqBtnTitleColor = [self.faqBtn titleColorForState:UIControlStateNormal];
    self.faqBtn.layer.borderColor = faqBtnTitleColor.CGColor;
    
    self.scrollView.scrollEnabled = YES;
    
    CGSize screenSize = [UIScreen mainScreen].bounds.size;
    self.bonusContentView.transform = CGAffineTransformMakeScale(screenSize.width / 320.f, screenSize.width / 320.f);
    CGRect rect = self.bonusContentView.frame;
    rect.origin = CGPointZero;
    self.bonusContentView.frame = rect;
    
    self.containerView.contentSize = self.bonusContentView.bounds.size;
    [self.containerView addSubview:self.bonusContentView];
    
    //Faq
    self.faqContainerScrollView.contentSize = self.faqContentImgView.bounds.size;
    [self.faqContainerScrollView addSubview:self.faqContentImgView];
}

- (void)_configNav {
    self.title = @"收益账户";
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    self.navigationItem.leftBarButtonItem = backItem;
    UIBarButtonItem *rightItem = [[UIBarButtonItem alloc]initWithTitle:@"收益明细" style:UIBarButtonItemStyleDone target:self action:@selector(bonusListBtnPressed:)];

    self.navigationItem.rightBarButtonItem = rightItem;
}

- (void)_reloadData {
    NSDictionary* userDict = [QSUserManager shareUserManager].userInfo;
    
    self.availableMoney = [QSPeopleUtil getAvailableBonus:userDict].floatValue;
    self.totalMoney = [QSPeopleUtil getTotalBonus:userDict].floatValue;
    
    self.currBonusLabel.text = [NSString stringWithFormat:@"￥%.2f",self.availableMoney];
    self.allBonusLabel.text = [NSString stringWithFormat:@"￥%.2f",self.totalMoney];
    if (self.availableMoney  ==  0) {
        self.withdrawBtn.backgroundColor = [UIColor lightGrayColor];
        self.withdrawBtn.userInteractionEnabled = NO;
        self.navigationItem.rightBarButtonItem.action = nil;
        self.navigationItem.rightBarButtonItem.enabled = NO;
        self.navigationItem.rightBarButtonItem.tintColor = [UIColor lightGrayColor];
    }
}

@end

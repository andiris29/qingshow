//
//  QSU19LoginGuideViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/10/25.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSU19LoginGuideViewController.h"
#import "QSU06LoginViewController.h"
#import "QSU07RegisterViewController.h"
#import "QSThirdPartLoginService.h"
#import "QSNetworkKit.h"
#import "UIViewController+QSExtension.h"
#import "QSBindMobileViewController.h"

#import "WXApi.h"

#import "UIViewController+ShowHud.h"
#import "QSPeopleUtil.h"
#import "QSUserManager.h"
#import "QSRootNotificationHelper.h"

@interface QSU19LoginGuideViewController ()
@property (weak, nonatomic) IBOutlet UIButton *closeBtn;

@property (weak, nonatomic) IBOutlet UIButton *mobileLoginBtn;

@property (weak, nonatomic) IBOutlet UIButton *weichatLoginBtn;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *topConstraint;
@property (weak, nonatomic) IBOutlet UIButton *registerBtn;

@end

@implementation QSU19LoginGuideViewController
- (void)setFShowCloseBtn:(BOOL)fShowCloseBtn {
    _fShowCloseBtn = fShowCloseBtn;
    self.closeBtn.hidden = !_fShowCloseBtn;
}
#pragma mark - Init
- (instancetype)init {
    self = [super initWithNibName:@"QSU19LoginGuideViewController" bundle:nil];
    if (self) {
        self.fShowCloseBtn = YES;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    CGFloat screenHeight = [UIScreen mainScreen].bounds.size.height;
    CGFloat divide = (screenHeight - 309.5) / 2;
    self.topConstraint.constant = divide;
    // Do any additional setup after loading the view from its nib.
    
    if (![WXApi isWXAppInstalled]) {
        [self.weichatLoginBtn setImage:[UIImage imageNamed:@"loginguide_wechat_gray"] forState:UIControlStateNormal];
        self.weichatLoginBtn.userInteractionEnabled = NO;
    } else {
        [self.weichatLoginBtn setImage:[UIImage imageNamed:@"loginguide_wechat"] forState:UIControlStateNormal];
        self.weichatLoginBtn.userInteractionEnabled = YES;
    }
    self.registerBtn.layer.borderColor = [UIColor whiteColor].CGColor;
    self.registerBtn.layer.borderWidth = 1.f;
    self.registerBtn.layer.cornerRadius = self.registerBtn.bounds.size.height / 2;
    self.navigationController.navigationBarHidden = YES;
    if (!self.fShowCloseBtn) {
        [self.closeBtn removeFromSuperview];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark - IBAction
- (IBAction)mobileLoginPressed:(id)sender {
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionFade;
    [self.navigationController.view.layer addAnimation:tran forKey:@"key"];
    QSU06LoginViewController* loginVc = [[QSU06LoginViewController alloc] init];
    [self.navigationController pushViewController:loginVc animated:NO];
}
- (IBAction)wechatLoginPressed:(id)sender {
    [[QSThirdPartLoginService getInstance] loginWithWechatOnSuccees:^{
        if ([QSPeopleUtil checkMobileExist:[QSUserManager shareUserManager].userInfo]) {
            [QSRootNotificationHelper postHideLoginPrompNoti];
        } else {
            QSBindMobileViewController* vc = [[QSBindMobileViewController alloc] init];
            CATransition* tran = [[CATransition alloc] init];
            tran.type = kCATransitionFade;
            [self.navigationController.view.layer addAnimation:tran forKey:@"key"];
            [self.navigationController pushViewController:vc animated:NO];
        }
    } onError:^(NSError *error) {
        [self showErrorHudWithError:error];
    }];
}

- (IBAction)registerBtnPressed:(id)sender {
    QSU07RegisterViewController* registerVc = [[QSU07RegisterViewController alloc] init];
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionFade;
    [self.navigationController.view.layer addAnimation:tran forKey:@"key"];
    [self.navigationController pushViewController:registerVc animated:NO];

}

- (void)_popToPreviousVc {
    [QSRootNotificationHelper postHideLoginPrompNoti];
}
- (IBAction)closeBtnPressed:(id)sender {
    [self _popToPreviousVc];
}

@end

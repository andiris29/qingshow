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

#import "WXApi.h"

#import "UIViewController+ShowHud.h"

@interface QSU19LoginGuideViewController ()
@property (weak, nonatomic) IBOutlet UIButton *closeBtn;

@property (weak, nonatomic) IBOutlet UIButton *mobileLoginBtn;

@property (weak, nonatomic) IBOutlet UIButton *weichatLoginBtn;
@property (weak, nonatomic) IBOutlet UIButton *weiboLoginBtn;
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
    // Do any additional setup after loading the view from its nib.
    if (![WXApi isWXAppInstalled]) {
        self.weichatLoginBtn.hidden = YES;
    } else {
        self.weichatLoginBtn.hidden = NO;
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
        [SHARE_NW_ENGINE updatePeople:@{@"role":[NSNumber numberWithInt:1]} onSuccess:nil onError:nil];
        
        [self registerBtnPressed:nil];
        
    } onError:^(NSError *error) {
        [self showErrorHudWithError:error];
    }];
}
- (IBAction)weiboLoginPressed:(id)sender {
    [[QSThirdPartLoginService getInstance] loginWithWeiboOnSuccees:^{
        [SHARE_NW_ENGINE updatePeople:@{@"role":[NSNumber numberWithInt:1]} onSuccess:nil onError:nil];
        [self registerBtnPressed:nil];
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

- (void)popToPreviousVc {
    [self hideLoginPrompVc];
}
- (IBAction)closeBtnPressed:(id)sender {
    [self popToPreviousVc];
}

@end

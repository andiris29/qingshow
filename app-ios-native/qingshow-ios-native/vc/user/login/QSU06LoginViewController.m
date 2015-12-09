//
//  QSU06LoginViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/11.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSU06LoginViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "UIViewController+ShowHud.h"
#import "UIViewController+QSExtension.h"
#import "QSEntityUtil.h"
#import "QSU17ResetPswStep1ViewController.h"
#import "QSU18ResetPswStep2ViewController.h"
#import "QSRootNotificationHelper.h"
#define PAGE_ID @"U06 - 登录"

@interface QSU06LoginViewController ()
@property (weak, nonatomic) IBOutlet UIButton *loginButton;
@property (weak, nonatomic) IBOutlet UITextField *userText;
@property (weak, nonatomic) IBOutlet UITextField *passwordText;
@property (assign, nonatomic) BOOL fRemoveLoginAndRegisterVc;
@end

@implementation QSU06LoginViewController
- (void)popToPreviousVc {
    [QSRootNotificationHelper postHideLoginPrompNoti];
}
#pragma mark - Init
- (instancetype)init
{
    self = [super initWithNibName:@"QSU06LoginViewController" bundle:nil];
    if (self) {
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    self.fRemoveLoginAndRegisterVc = NO;
    // Do any additional setup after loading the view from its nib.
    self.userText.delegate = self;
    self.passwordText.delegate = self;
    
    [self.userText setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
    [self.passwordText setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
    [self.userText setTintColor:[UIColor whiteColor]];
    [self.passwordText setTintColor:[UIColor whiteColor]];
    
    self.loginButton.layer.cornerRadius = self.loginButton.frame.size.height / 8;
    self.loginButton.layer.masksToBounds = YES;
    
    // tap Setting
    UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(resignOnTap:)];
    [singleTap setNumberOfTapsRequired:1];
    [singleTap setNumberOfTouchesRequired:1];
    [self.view addGestureRecognizer:singleTap];
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    [MobClick beginLogPageView:PAGE_ID];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
    
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}
- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
}

#pragma mark - UITextFieldDelegate
-(void)textFieldDidEndEditing:(UITextField *)textField {
    [textField resignFirstResponder];
}

-(void)textFieldDidBeginEditing:(UITextField *)textField {
    self.currentResponder = textField;
}

#pragma mark - Action
- (void)resignOnTap:(id)sender {
    [self.currentResponder resignFirstResponder];
}
- (IBAction)back:(id)sender {
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionFade;
    [self.navigationController.view.layer addAnimation:tran forKey:@"key"];
    [self.navigationController popViewControllerAnimated:NO];
}
- (IBAction)forgetPswBtnPressed:(id)sender {
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionFade;
    [self.navigationController.view.layer addAnimation:tran forKey:@"key"];
    
    QSU17ResetPswStep1ViewController *vc = [[QSU17ResetPswStep1ViewController alloc]init];
    vc.previousVc = self;
    [self.navigationController pushViewController:vc animated:NO];
}

- (IBAction)login:(id)sender {
    NSString *user = self.userText.text;
    NSString *passwd = self.passwordText.text;
    
    if (user.length == 0 ) {
        [self showErrorHudWithText:@"请输入账号"];
        return;
    }
    
    if (passwd.length == 0) {
        [self showErrorHudWithText:@"请输入密码"];
        return;
    }
    
    EntitySuccessBlock success = ^(NSDictionary *people, NSDictionary *metadata){
        self.fRemoveLoginAndRegisterVc = YES;
        [self showSuccessHudWithText:@"登陆成功"];
        [SHARE_NW_ENGINE updatePeople:@{@"role":[NSNumber numberWithInt:1]} onSuccess:nil onError:nil];
        [self performSelector:@selector(popToPreviousVc) withObject:nil afterDelay:0.5f];
    };
    
    ErrorBlock error = ^(NSError *error) {
        [self handleError:error];
    };
    
    [SHARE_NW_ENGINE loginWithName:user
                          password:passwd
                         onSucceed:success
                           onError:error];

}

@end

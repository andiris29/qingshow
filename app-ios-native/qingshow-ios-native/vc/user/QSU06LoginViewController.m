//
//  QSU06LoginViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/11.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSU06LoginViewController.h"
#import "QSU07RegisterViewController.h"
#import "QSS01RootViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "UIViewController+ShowHud.h"
#import "UIViewController+QSExtension.h"

#define PAGE_ID @"U06 - 登录"

@interface QSU06LoginViewController ()
@property (weak, nonatomic) IBOutlet UIButton *loginButton;
@property (weak, nonatomic) IBOutlet UITextField *userText;
@property (weak, nonatomic) IBOutlet UITextField *passwordText;

@property (assign, nonatomic) BOOL fPopToRoot;
@property (assign, nonatomic) BOOL fRemoveLoginAndRegisterVc;
@end

@implementation QSU06LoginViewController

#pragma mark - Init
- (instancetype)initWithPopToRootAfterLogin:(BOOL)fShowUserDetail
{
    self = [super initWithNibName:@"QSU06LoginViewController" bundle:nil];
    if (self) {
        self.fPopToRoot = fShowUserDetail;
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
    [self.loginButton setBackgroundColor:[UIColor colorWithRed:146.f / 255.f green:8.f / 255.f blue:62.f / 255.f alpha:1]];
    
    // tap Setting
    UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(resignOnTap:)];
    [singleTap setNumberOfTapsRequired:1];
    [singleTap setNumberOfTouchesRequired:1];
    [self.view addGestureRecognizer:singleTap];
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
    [self.navigationController popViewControllerAnimated:YES];
}

- (IBAction)login:(id)sender {
    NSLog(@"login to qingshow");
    
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
        if (metadata[@"error"] == nil && people != nil) {
            self.fRemoveLoginAndRegisterVc = YES;
            [self showSuccessHudWithText:@"登陆成功"];
            if (self.fPopToRoot) {
                [self.navigationController popToRootViewControllerAnimated:YES];
            } else {
                [self.navigationController popViewControllerAnimated:YES];
            }

        } else {
            [self showErrorHudWithText:@"登陆失败"];
        }
    };
    
    ErrorBlock error = ^(NSError *error) {
        if (error.userInfo[@"error"] != nil) {
            NSNumber *errorCode = (NSNumber *)error.userInfo[@"error"];
            if (errorCode.longValue == 1001) {
                [self showErrorHudWithText:@"账号或者密码错误。"];
            }
        } else {
            [self showErrorHudWithText:@"网络连接失败"];
        }
    };
    
    [SHARE_NW_ENGINE loginWithName:user
                          password:passwd
                         onSucceed:success
                           onError:error];

}

#pragma mark - Callback

- (void)gotoRegister {
    UIViewController *vc = [[QSU07RegisterViewController alloc]initWithNibName:@"QSU07RegisterViewController" bundle:nil];
    [self.navigationController pushViewController:vc animated:YES];
}


@end

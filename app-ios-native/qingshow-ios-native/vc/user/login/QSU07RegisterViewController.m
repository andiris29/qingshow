//
//  QSU07RegisterViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/14.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSU06LoginViewController.h"
#import "QSU07RegisterViewController.h"
#import "UIViewController+ShowHud.h"
#import "UIViewController+Utility.h"
#import "UIViewController+QSExtension.h"
#import "QSNetworkKit.h"
#import "QSRootNotificationHelper.h"

#import "QSEntityUtil.h"

#define PAGE_ID @"U07 - 注册"
#define w ([UIScreen mainScreen].bounds.size.width)
@interface QSU07RegisterViewController ()
@property (weak, nonatomic) IBOutlet UITextField *passwdCfmTextField;
@property (weak, nonatomic) IBOutlet UITextField *mobileTextField;
@property (weak, nonatomic) IBOutlet UITextField *verifyCodeTextField;
@property (weak, nonatomic) IBOutlet UIButton *getVerifyCodeBtn;
@property (weak, nonatomic) IBOutlet UIButton *registerBtn;
@property (strong,nonatomic) NSTimer *timer;

@end

@implementation QSU07RegisterViewController

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.automaticallyAdjustsScrollViewInsets = NO;
    [self registerForKeyboardNotifications];
    [self configScrollView];
    [self.containerScrollView addSubview:self.contentView];
    self.passwdCfmTextField.delegate = self;
    self.mobileTextField.delegate = self;
    [self.passwdCfmTextField setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
    [self.mobileTextField setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
    [self.verifyCodeTextField setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
    
    self.passwdCfmTextField.tintColor = [UIColor whiteColor];
    self.mobileTextField.tintColor = [UIColor whiteColor];
    self.verifyCodeTextField.tintColor = [UIColor whiteColor];
    
    self.registerBtn.layer.cornerRadius = self.registerBtn.frame.size.height / 8;
    self.registerBtn.layer.masksToBounds = YES;
    
    self.getVerifyCodeBtn.layer.cornerRadius = self.getVerifyCodeBtn.frame.size.height / 8;
    
    UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(resignOnTap:)];
    self.view.userInteractionEnabled = YES;
    [self.view addGestureRecognizer:ges];
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

- (void)dealloc
{
    [self unregisterKeyboardNotifications];
}

# pragma mark - UITextFieldDelegate
- (void)textFieldDidBeginEditing:(UITextField *)textField {
    self.currentResponder = textField;
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    [textField resignFirstResponder];
}

#pragma mark - IBAction
- (IBAction)backBtnPressed:(id)sender {
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionFade;
    [self.navigationController.view.layer addAnimation:tran forKey:@"key"];
    [self.navigationController popViewControllerAnimated:NO];
}

- (IBAction)getTestNumberButtonPressed:(id)sender {
    if(![self checkMobile:self.mobileTextField.text]){
        [self showErrorHudWithText:@"请输入正确的手机号"];
        return;
    }
    
    NSString *mobileNum = self.mobileTextField.text;
    MBProgressHUD* hud = [self showNetworkWaitingHud];
    [SHARE_NW_ENGINE getVerifyCodeForMobile:mobileNum onSucceed:^{
        [hud hide:YES];
        [self showTextHud:@"已发送验证码"];
        [self configTimer];
    } onError:^(NSError *error) {
        [hud hide:YES];
        [self handleError:error];
    }];
}


- (IBAction)registerBtnPressed:(id)sender {
    NSString *passwdCfm = self.passwdCfmTextField.text;
    NSString *mobilePhone = self.mobileTextField.text;
    NSString *code = self.verifyCodeTextField.text;
    
    if(![self checkMobile:mobilePhone]){
        [self showErrorHudWithText:@"请输入正确的手机号"];
        return;
    }
    
    if (passwdCfm.length == 0) {
        [self showErrorHudWithText:@"请输入密码"];
        return;
    }
    
    if ([self checkPasswd:passwdCfm] != YES) {
        [self showErrorHudWithText:@"请输入6位以上英文或数字密码"];
        return;
    }
    
    
    if (code.length == 0) {
        [self showErrorHudWithText:@"请填写验证码"];
        return;
    }
    
    EntitySuccessBlock successBloc = ^(NSDictionary *people, NSDictionary *meta) {
        [self showSuccessHudWithText:@"登陆成功"];
        [self performSelector:@selector(popToPreviousVc) withObject:nil afterDelay:0.5f];
    };
    
    ErrorBlock errorBlock = ^(NSError *error) {
        [self handleError:error];
    };
    [SHARE_NW_ENGINE registerByMobile:mobilePhone password:passwdCfm verifyCode:code onSucceessd:successBloc onErrer:errorBlock];
}

#pragma mark - Gesture
- (void)resignOnTap:(id)iSender {
    [self hideKeyboard];
}
- (void)hideKeyboard
{
    for (UITextField* t in @[self.passwdCfmTextField, self.mobileTextField,self.verifyCodeTextField]) {
        [t resignFirstResponder];
    }
}

#pragma mark - Keyboard
- (void)registerForKeyboardNotifications
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWasShown:) name:UIKeyboardDidShowNotification object:nil];
    
    [[NSNotificationCenter defaultCenter]  addObserver:self selector:@selector(keyboardWasHidden:) name:UIKeyboardWillHideNotification object:nil];
}
- (void)unregisterKeyboardNotifications
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)keyboardWasShown:(NSNotification *) notif
{
    NSDictionary *info = [notif userInfo];
    NSValue *value = [info objectForKey:UIKeyboardFrameBeginUserInfoKey];
    CGSize keyboardSize = [value CGRectValue].size;
    self.containerScrollView.contentInset = UIEdgeInsetsMake(0, 0, keyboardSize.height, 0);
}
- (void)keyboardWasHidden:(NSNotification *) notif
{
    [UIView animateWithDuration:0.3f animations:^{
        self.containerScrollView.contentInset = UIEdgeInsetsMake(0, 0, 0, 0);
    }];
    
}

#pragma mark - Timer
- (void)configTimer
{
    _timer = [NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(timerUpdate) userInfo:nil repeats:YES];
    [_timer fire];
    
    self.getVerifyCodeBtn.userInteractionEnabled = NO;
}
- (void)timerUpdate
{
#warning remove static
    static int num = 60;
    [self.getVerifyCodeBtn setTitle:[NSString stringWithFormat:@"%d秒后可重发",num] forState:UIControlStateNormal];
    num -= 1;
    if (num < 1) {
        [_timer invalidate];
        _timer = nil;
        num = 60;
        [self.getVerifyCodeBtn setTitle:@"获取验证码" forState:UIControlStateNormal];
        self.getVerifyCodeBtn.userInteractionEnabled = YES;
    }
}

#pragma mark - Helper
- (void)popToPreviousVc {
    [QSRootNotificationHelper postHideLoginPrompNoti];
}

- (void)configScrollView
{
    CGSize scrollViewSize = self.containerScrollView.bounds.size;
    CGSize contentSize = self.contentView.bounds.size;
    float height = scrollViewSize.height > contentSize.height ? scrollViewSize.height : contentSize.height;
    self.containerScrollView.contentSize = CGSizeMake(scrollViewSize.width, height + 20);
    [self.view addSubview:self.contentView];
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
}

@end

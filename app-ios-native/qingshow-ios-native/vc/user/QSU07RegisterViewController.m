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
#import "QSU13PersonalizeViewController.h"

#import "QSEntityUtil.h"

#define PAGE_ID @"U07 - 注册"
#define w ([UIScreen mainScreen].bounds.size.width)
@interface QSU07RegisterViewController ()
@property (weak, nonatomic) IBOutlet UITextField *passwdCfmText;
@property (weak, nonatomic) IBOutlet UITextField *mailAndPhoneText;
@property (weak, nonatomic) IBOutlet UIButton *registerButton;
@property (weak, nonatomic) IBOutlet UITextField *testTextField;
@property (weak, nonatomic) IBOutlet UIButton *geTestNumBtn;


//@property (assign,nonatomic)static int num;
@property (strong,nonatomic)NSTimer *timer;

@end

@implementation QSU07RegisterViewController

- (void)popToPreviousVc {
    [self hideLoginPrompVc];
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
- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.automaticallyAdjustsScrollViewInsets = NO;
    [self registerForKeyboardNotifications];
    [self configScrollView];
    [self.containerScrollView addSubview:self.contentView];
    self.passwdCfmText.delegate = self;
    self.mailAndPhoneText.delegate = self;
    [self.passwdCfmText setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
    [self.mailAndPhoneText setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
    [self.testTextField setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
    
    self.passwdCfmText.tintColor = [UIColor whiteColor];
    self.mailAndPhoneText.tintColor = [UIColor whiteColor];
    self.testTextField.tintColor = [UIColor whiteColor];
    
    self.registerButton.layer.cornerRadius = self.registerButton.frame.size.height / 8;
    self.registerButton.layer.masksToBounds = YES;
    
    self.geTestNumBtn.layer.cornerRadius = self.geTestNumBtn.frame.size.height / 8;
    
    UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(resignOnTap:)];
    self.view.userInteractionEnabled = YES;
    [self.view addGestureRecognizer:ges];


    
}
- (void)dealloc
{
    [self unregisterKeyboardNotifications];
}
- (IBAction)getTestNumberButtonPressed:(id)sender {
    NSString *mobileNum = self.mailAndPhoneText.text;
        [SHARE_NW_ENGINE getTestNumberWithMobileNumber:mobileNum onSucceed:^{
            [self showTextHud:@"已成功发送验证码"];
            [self setTimer];
        } onError:^(NSError *error) {
            [self handleError:error];
        }];
}
- (void)setTimer
{
    _timer = [NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(timerRun) userInfo:nil repeats:YES];
    [_timer fire];
    
    self.geTestNumBtn.userInteractionEnabled = NO;
}
- (void)timerRun
{
#warning remove static
    static int num = 60;
    [self.geTestNumBtn setTitle:[NSString stringWithFormat:@"%d秒后可重发",num] forState:UIControlStateNormal];
    num -= 1;
    if (num < 1) {
        [_timer invalidate];
        _timer = nil;
        num = 60;
        [self.geTestNumBtn setTitle:@"发送验证码" forState:UIControlStateNormal];
        self.geTestNumBtn.userInteractionEnabled = YES;
    }
    
}
- (IBAction)back:(id)sender {
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionFade;
    [self.navigationController.view.layer addAnimation:tran forKey:@"key"];
    [self.navigationController popViewControllerAnimated:NO];
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
# pragma mark - UITextFieldDelegate
- (void)textFieldDidBeginEditing:(UITextField *)textField {
    self.currentResponder = textField;
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    [textField resignFirstResponder];
}


# pragma mark - Action
- (void)resignOnTap:(id)iSender {
    [self hideKeyboard];
}
- (void)hideKeyboard
{
    for (UITextField* t in @[self.passwdCfmText, self.mailAndPhoneText,self.testTextField]) {
        [t resignFirstResponder];
    }
}

- (IBAction)registers:(id)sender {
    NSString *passwdCfm = self.passwdCfmText.text;
    NSString *mailAndPhone = self.mailAndPhoneText.text;
    NSString *code = self.testTextField.text;
    
    if (passwdCfm.length == 0) {
        [self showErrorHudWithText:@"请输入密码"];
        return;
    }
    
    
    if([self checkEmail:mailAndPhone] != YES && [self checkMobile:mailAndPhone] != YES){
            [self showErrorHudWithText:@"请输入正确的邮箱或手机号"];
            return;
        }
    if (code.length == 0) {
        [self showErrorHudWithText:@"请填写验证码"];
        return;
    }
    
    if ([self checkPasswd:passwdCfm] != YES) {
        [self showErrorHudWithText:@"请输入6位以上英文或数字密码"];
        return;
    }
    
    EntitySuccessBlock successBloc = ^(NSDictionary *people, NSDictionary *meta) {
        [self showSuccessHudWithText:@"登陆成功"];
        [self popToPreviousVc];
    };
    
    ErrorBlock errorBlock = ^(NSError *error) {
        [self handleError:error];
    };
    [SHARE_NW_ENGINE registerByNickname:nil Password:passwdCfm Id:mailAndPhone mobile:mailAndPhone code:code onSucceessd:successBloc onErrer:errorBlock];
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

        [self showErrorHudWithError:error];
        [self showErrorHudWithError:error];
@end

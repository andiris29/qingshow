//
//  QSU17ResetPswStep1ViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/9/27.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU17ResetPswStep1ViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "UIViewController+QSExtension.h"
#import "QSU18ResetPswStep2ViewController.h"
@interface QSU17ResetPswStep1ViewController ()
@property (weak, nonatomic) IBOutlet UIButton *nextStepBtn;
@property (weak, nonatomic) IBOutlet UIButton *getCodeBtn;
@property (weak, nonatomic) IBOutlet UITextField *phoneTextField;
@property (weak, nonatomic) IBOutlet UITextField *codeTextField;


@property (strong,nonatomic)NSTimer *timer;
@end

@implementation QSU17ResetPswStep1ViewController

- (instancetype)init {
    self = [super initWithNibName:@"QSU17ResetPswStep1ViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self _configUI];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
#pragma mark - Gesture
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    for (id textfield in self.view.subviews) {
        if ([textfield isKindOfClass:[UITextField class]]) {
            [textfield resignFirstResponder];
        }
    }
}

#pragma mark - IBAction
- (IBAction)getCodeBtnPressed:(id)sender {
    NSString *phoneStr = self.phoneTextField.text;
#warning TODO 使用regex验证手机号
    if (phoneStr.length == 11) {
        [SHARE_NW_ENGINE getVerifyCodeForMobile:phoneStr onSucceed:^{
            [self showTextHud:@"已成功发送验证码"];
            [self setTimer];
        } onError:^(NSError *error) {
            
            [self showErrorHudWithError:error];
        }];

    }else{
        [self showTextHud:@"请正确填写手机号码"];
    }
    
}

- (IBAction)nextStepBtnPressed:(id)sender {
    [self.phoneTextField resignFirstResponder];
    [self.codeTextField resignFirstResponder];
    
    NSString *PhoneStr = self.phoneTextField.text;
    NSString *codeStr = self.codeTextField.text;
    if (PhoneStr == nil) {
        PhoneStr = @"";
    }
    if (codeStr == nil) {
        codeStr = @"";
    }
    __weak QSU17ResetPswStep1ViewController *weakSelf = self;
    [SHARE_NW_ENGINE forgetPasswordPhone:PhoneStr verifyCode:codeStr onSucceed:^ {
        QSU18ResetPswStep2ViewController *vc = [[QSU18ResetPswStep2ViewController alloc]init];
        vc.previousVc = self.previousVc;
        [weakSelf.navigationController pushViewController:vc animated:YES];
    } onError:^(NSError *error) {
        [self handleError:error];
    }];
}

- (IBAction)backBtnPressed:(id)sender {
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionFade;
    [self.navigationController.view.layer addAnimation:tran forKey:@"key"];
    [self.navigationController popViewControllerAnimated:NO];
}

#pragma mark - Private
- (void)_configUI {
    self.nextStepBtn.layer.cornerRadius = self.nextStepBtn.bounds.size.height / 8;
    self.getCodeBtn.layer.cornerRadius  =self.getCodeBtn.bounds.size.height / 8;
    [self.phoneTextField setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
    [self.codeTextField setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
}

- (void)setTimer
{
    _timer = [NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(timerRun) userInfo:nil repeats:YES];
    [_timer fire];
    
    self.getCodeBtn.userInteractionEnabled = NO;
}

- (void)timerRun
{
    static int num = 60;
    [self.getCodeBtn setTitle:[NSString stringWithFormat:@"%d秒后可重发",num] forState:UIControlStateNormal];
    num -= 1;
    if (num < 1) {
        [_timer invalidate];
        _timer = nil;
        num = 60;
        [self.getCodeBtn setTitle:@"获取验证码" forState:UIControlStateNormal];
        self.getCodeBtn.userInteractionEnabled = YES;
    }
}
@end

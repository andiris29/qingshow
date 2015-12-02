//
//  QSBindMobileViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/21.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSBindMobileViewController.h"
#import "UIViewController+ShowHud.h"
#import "UIViewController+QSExtension.h"
#import "QSNetworkKit.h"
#import "QSRootNotificationHelper.h"

@interface QSBindMobileViewController ()

@property (strong,nonatomic)NSTimer *timer;

//IBOutlet
@property (weak, nonatomic) IBOutlet UITextField *phoneTextField;
@property (weak, nonatomic) IBOutlet UITextField *verifyCodeTextField;
@property (weak, nonatomic) IBOutlet UIButton *submitBtn;
@property (weak, nonatomic) IBOutlet UIButton *getVerifyCodeBtn;


@end

@implementation QSBindMobileViewController

#pragma mark - Init
- (instancetype)init {
    self = [super initWithNibName:@"QSBindMobileViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self _configUI];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];

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
- (IBAction)backBtnPressed:(id)sender {
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionFade;
    [self.navigationController.view.layer addAnimation:tran forKey:@"key"];
    [self.navigationController popViewControllerAnimated:NO];
}

- (IBAction)getVerifyCodeBtnPressed:(id)sender {
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

- (IBAction)submitBtnPressed:(id)sender {
#warning TODO Network
    [self.phoneTextField resignFirstResponder];
    [self.verifyCodeTextField resignFirstResponder];
    
    NSString *PhoneStr = self.phoneTextField.text;
    NSString *codeStr = self.verifyCodeTextField.text;
    if (PhoneStr == nil) {
        PhoneStr = @"";
    }
    if (codeStr == nil) {
        codeStr = @"";
    }
    
    
    MBProgressHUD* hud = [self showNetworkWaitingHud];
    [SHARE_NW_ENGINE userBindMobile:PhoneStr verifyCode:codeStr onSucceed:^(NSDictionary *data, NSDictionary *metadata) {
        [hud hide:YES];
        [self showSuccessHudWithText:@"成功"];
        [self performSelector:@selector(_popToRoot) withObject:nil afterDelay:0.5f];
    } onError:^(NSError *error) {
        [hud hide:YES];
        [self handleError:error];
    }];
}

#pragma mark - Private
- (void)_popToRoot {
    [QSRootNotificationHelper postHideLoginPrompNoti];
}

- (void)_configUI {
    self.submitBtn.layer.cornerRadius = self.submitBtn.bounds.size.height / 8;
    self.getVerifyCodeBtn.layer.cornerRadius  =self.getVerifyCodeBtn.bounds.size.height / 8;
    [self.phoneTextField setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
    [self.verifyCodeTextField setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
}


- (void)setTimer {
    _timer = [NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(timerRun) userInfo:nil repeats:YES];
    [_timer fire];
    
    self.getVerifyCodeBtn.userInteractionEnabled = NO;
}

- (void)timerRun {
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



@end

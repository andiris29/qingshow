//
//  QSU18ResetPswStep2ViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/9/27.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU18ResetPswStep2ViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSU06LoginViewController.h"
#import "UIViewController+QSExtension.h"
@interface QSU18ResetPswStep2ViewController ()

@property (weak, nonatomic) IBOutlet UITextField *passWordTextField;
@property (weak, nonatomic) IBOutlet UIButton *submitBtn;

@end

@implementation QSU18ResetPswStep2ViewController

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    [self _configUI];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction
- (IBAction)backBtnPressed:(id)sender {
    [self.navigationController popToRootViewControllerAnimated:YES];
}

- (IBAction)reSetPasswordBtnPressed:(id)sender {
    [self.passWordTextField resignFirstResponder];
    
    NSString *psw = self.passWordTextField.text;
    if (!psw.length) {
        [self showTextHud:@"请输入密码"];
        return;
    }
    
    [SHARE_NW_ENGINE resetPassword:psw onSucceed:^{
        CATransition* tran = [[CATransition alloc] init];
        tran.type = kCATransitionFade;
        [self.navigationController.view.layer addAnimation:tran forKey:@"key"];
        [self.navigationController popViewControllerAnimated:NO];
    } onError:^(NSError *error) {
        [self handleError:error];
    }];
}

#pragma mark - Gesture
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [self.passWordTextField resignFirstResponder];
}

#pragma mark - Private
- (void)_configUI
{
    self.submitBtn.layer.cornerRadius = self.submitBtn.bounds.size.height / 8;
    [self.passWordTextField setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
}

@end

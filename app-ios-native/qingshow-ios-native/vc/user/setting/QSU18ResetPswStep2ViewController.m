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
@interface QSU18ResetPswStep2ViewController ()

@property (weak, nonatomic) IBOutlet UITextField *passWordTextField;
@property (weak, nonatomic) IBOutlet UIButton *submitBtn;

@end

@implementation QSU18ResetPswStep2ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self configUI];

}
- (void)configUI
{
    self.submitBtn.layer.cornerRadius = self.submitBtn.bounds.size.height / 8;
    [self.password setValue:[UIColor whiteColor] forKeyPath:@"_placeholderLabel.textColor"];
}
- (IBAction)backBtnPressed:(id)sender {
    [self.navigationController popToRootViewControllerAnimated:YES];
}
- (IBAction)reSetPasswordBtnPressed:(id)sender {
    [self.passWordTextField resignFirstResponder];
    
    NSString *psw = self.passWordTextField.text;
//    NSString *pswAgain = self.passWordAgainTextField.text;
    if (!psw.length) {
        [self showTextHud:@"请输入密码"];
    }
    else
    {
        [SHARE_NW_ENGINE loginWithName:self.mobile password:self.password onSucceed:^(NSDictionary *data, NSDictionary *metadata) {
            [SHARE_NW_ENGINE updatePeople:@{@"id":self.mobile ,@"currentPassword":self.password, @"password":self.passWordTextField.text} onSuccess:^(NSDictionary *data, NSDictionary *metadata) {
                [self showTextHud:@"修改密码成功！"];
                [SHARE_NW_ENGINE logoutOnSucceed:^{
                    [self.navigationController popToRootViewControllerAnimated:YES];
                    BOOL f = true;
                    NSArray* vcs = self.navigationController.viewControllers;
                    for (UIViewController* vc in vcs) {
                        if ([vc isKindOfClass:[QSU06LoginViewController class]]) {
                            [self.navigationController popToViewController:vc animated:NO];
                            f = false;
                            break;
                        }
                    }
                    if (f) {
                        [self.navigationController popToRootViewControllerAnimated:NO];
                    }
                    CATransition* tran = [[CATransition alloc] init];
                    tran.type = kCATransitionFade;
                    [self.navigationController.view.layer addAnimation:tran forKey:@"key"];
                    [self.navigationController popViewControllerAnimated:NO];
                    
                } onError:nil];
            } onError:^(NSError *error) {
                [self showTextHud:@"修改密码失败，请重新核对信息"];
            }];
        } onError:^(NSError *error) {
            
        }];
        
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [self.passWordTextField resignFirstResponder];
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end

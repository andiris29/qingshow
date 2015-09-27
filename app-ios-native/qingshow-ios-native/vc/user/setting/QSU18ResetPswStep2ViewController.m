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
@interface QSU18ResetPswStep2ViewController ()
@property (weak, nonatomic) IBOutlet UITextField *passWordTextField;
@property (weak, nonatomic) IBOutlet UITextField *passWordAgainTextField;
@property (weak, nonatomic) IBOutlet UIButton *submitBtn;

@end

@implementation QSU18ResetPswStep2ViewController
- (instancetype)init
{
    if (self = [super initWithNibName:@"QSU18ResetPswStep2ViewController.h" bundle:nil]) {
        
    }
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}
- (void)configUI
{
    self.submitBtn.layer.cornerRadius = self.submitBtn.bounds.size.width / 8;
}
- (IBAction)backBtnPressed:(id)sender {
    [self.navigationController popToRootViewControllerAnimated:YES];
}
- (IBAction)reSetPasswordBtnPressed:(id)sender {
    NSString *psw = self.passWordTextField.text;
    NSString *pswAgain = self.passWordAgainTextField.text;
    if (![psw isEqualToString:pswAgain]) {
        [self showTextHud:@"两次输入的密码不一致！"];
    }
    else
    {
        [SHARE_NW_ENGINE updatePeople:@{@"password":self.passWordAgainTextField.text} onSuccess:^(NSDictionary *data, NSDictionary *metadata) {
            [self showTextHud:@"修改密码成功！"];
        } onError:^(NSError *error) {
            [self showTextHud:@"修改密码失败，请重新核对信息"];
        }];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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

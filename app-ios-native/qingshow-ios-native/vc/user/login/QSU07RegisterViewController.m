//
//  QSU07RegisterViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/14.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSU07RegisterViewController.h"
#import "UIViewController+ShowHud.h"

@interface QSU07RegisterViewController ()
@property (weak, nonatomic) IBOutlet UITextField *accountText;
@property (weak, nonatomic) IBOutlet UITextField *passwdText;
@property (weak, nonatomic) IBOutlet UITextField *passwdCfmText;
@property (weak, nonatomic) IBOutlet UIButton *registerButton;

@end

@implementation QSU07RegisterViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    // View全体
    self.view.backgroundColor=[UIColor colorWithRed:240.f/255.f green:240.f/255.f blue:240.f/255.f alpha:1.f];
    
    // Navibar
    self.navigationItem.title = @"注册";
    self.navigationItem.backBarButtonItem.title = @"";
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    
//    UIBarButtonItem *btnSave = [[UIBarButtonItem alloc]initWithTitle:@"注册"
//                                                               style:UIBarButtonItemStylePlain
//                                                              target:self
//                                                              action:@selector(gotoRegister)];
//    
//    [[self navigationItem] setRightBarButtonItem:btnSave];
//    
//    
    // 登陆
    self.registerButton.backgroundColor = [UIColor colorWithRed:251.f/255.f green:145.f/255.f blue:95.f/255.f alpha:1.f];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

# pragma mark - Action

- (IBAction)register:(id)sender {

    NSString *account = self.accountText.text;
    NSString *passwd = self.passwdText.text;
    NSString *passwdCfm = self.passwdCfmText.text;
    
    if (account.length == 0) {
        [self showErrorHudWithText:@"请输入账号"];
        return;
    }
    
    if (passwd.length == 0) {
        [self showErrorHudWithText:@"请输入账号"];
        return;
    }
}

# pragma mark - private



@end

//
//  QSU06LoginViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/11.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSU06LoginViewController.h"

@interface QSU06LoginViewController ()
@property (weak, nonatomic) IBOutlet UIButton *loginButton;
@property (weak, nonatomic) IBOutlet UITextField *userText;
@property (weak, nonatomic) IBOutlet UITextField *passwordText;
@end

@implementation QSU06LoginViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    // View全体
    self.view.backgroundColor=[UIColor colorWithRed:240.f/255.f green:240.f/255.f blue:240.f/255.f alpha:1.f];

    // Navibar
    self.navigationItem.title = @"登陆";
    self.navigationItem.backBarButtonItem.title = @"";
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    
    UIBarButtonItem *btnSave = [[UIBarButtonItem alloc]initWithTitle:@"注册"
                                                               style:UIBarButtonItemStylePlain
                                                              target:self
                                                              action:@selector(gotoRegister)];
    
    [[self navigationItem] setRightBarButtonItem:btnSave];


    // 登陆
    self.loginButton.backgroundColor = [UIColor colorWithRed:251.f/255.f green:145.f/255.f blue:95.f/255.f alpha:1.f];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
    
}

#pragma mark - Action

- (IBAction)login:(id)sender {
    NSLog(@"login to qingshow");
}


#pragma mark - Callback

- (void)gotoRegister {
    
}

@end

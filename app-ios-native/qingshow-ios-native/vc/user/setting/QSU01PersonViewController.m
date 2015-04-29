//
//  QSU01PersonViewController.m
//  qingshow-ios-native
//
//  Created by ching show on 15/4/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU01PersonViewController.h"
#import "QSU07RegisterViewController.h"
#import "QSS15TopicViewController.h"

@interface QSU01PersonViewController ()

@end

@implementation QSU01PersonViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    //[self configNavBar];
   // self.navigationController.navigationBarHidden = YES;
    
    // Do any additional setup after loading the view from its nib.
}
- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
}
- (IBAction)pushNextButton:(id)sender {
    UIViewController *vc = [[QSU07RegisterViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}
- (IBAction)backButon:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}
//- (void)configNavBar
//{
//    self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:89.f/255.f green:86.f/255.f blue:86.f/255.f alpha:1.f];
//    UIImageView* titleImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"nav_btn_image_logo"]];
//    self.navigationItem.titleView = titleImageView;
//    
//    UIBarButtonItem* rightButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_btn_account"] style:UIBarButtonItemStylePlain target:self action:@selector(accountButtonPressed)];
//    self.navigationItem.rightBarButtonItem = rightButtonItem;
//}

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

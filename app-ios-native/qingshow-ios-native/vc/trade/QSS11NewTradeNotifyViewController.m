//
//  QSS11NewTradeNotifyViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/8/7.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS11NewTradeNotifyViewController.h"
#import "QS11OrderInfoCell.h"
#import "QS11TextCell.h"
#import "QSS01MatchShowsViewController.h"
#import "QSTableViewBasicProvider.h"

#define PAGE_ID @"推荐折扣"

@interface QSS11NewTradeNotifyViewController ()

@property (strong,nonatomic)QSTableViewBasicProvider *provider;

@end

@implementation QSS11NewTradeNotifyViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.provider = [[QSTableViewBasicProvider alloc]init];
    
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (IBAction)closeNotifyViewController:(id)sender {
    QSS01MatchShowsViewController *vc = [[QSS01MatchShowsViewController alloc]init];
    [self.navigationController pushViewController:vc animated:YES];
    
}
- (IBAction)shareAndBuyBtnPressed:(id)sender {
    
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

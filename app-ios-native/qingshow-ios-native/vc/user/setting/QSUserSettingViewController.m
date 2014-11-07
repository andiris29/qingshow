//
//  QSUserSettingViewController.m
//  qingshow-ios-native
//
//  Created by 瞿盛 on 14/11/6.
//  Copyright (c) 2014年 QS. All rights reserved.
//

#import "QSUserSettingViewController.h"

@interface QSUserSettingViewController ()

@end

@implementation QSUserSettingViewController

- (id)init {
    self = [self initWithNibName:@"QSUserSettingViewController"
                          bundle:nil];
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self initNavigation];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark - Private
- (void)initNavigation {
    NSLog(@"initNavigation");
    self.navigationItem.title = @"设置";
    UIBarButtonItem *btnSave = [[UIBarButtonItem alloc]initWithTitle:@"保存"
                                                               style:UIBarButtonItemStylePlain
                                                              target:self
                                                              action:@selector(saveSetting)];
    
    self.navigationItem.rightBarButtonItem = btnSave;
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

#pragma mark - Action
- (void) saveSetting {
    
}

@end

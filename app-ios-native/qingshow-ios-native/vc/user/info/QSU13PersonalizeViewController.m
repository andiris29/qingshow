//
//  QSU13PersonalizeViewController.m
//  qingshow-ios-native
//
//  Created by ching show on 15/5/5.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU13PersonalizeViewController.h"

@interface QSU13PersonalizeViewController ()
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;

@end

@implementation QSU13PersonalizeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"www";
    // Do any additional setup after loading the view from its nib.
    _scrollView.contentSize = CGSizeMake(self.view.frame.size.width, self.view.frame.size.height * 2);
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

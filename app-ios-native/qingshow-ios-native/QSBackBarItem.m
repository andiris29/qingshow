//
//  QSBackBarItem.m
//  qingshow-ios-native
//
//  Created by mhy on 15/6/4.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSBackBarItem.h"

@implementation QSBackBarItem
{
    UIViewController *_actionVC;
}
- (UIBarButtonItem *)initWithActionVC:(UIViewController *)vc
{

    if (self = [super init]) {
        self.image = [UIImage imageNamed:@"nav_btn_back"];
        self.style = UIBarButtonItemStyleDone;
        self.target = self;
        self.action = @selector(backAction);
        self.accessibilityFrame = CGRectMake(0, 0, 29, 29);
        _actionVC = vc;
    }
    return self;
}
- (void)backAction
{
    [_actionVC.navigationController popViewControllerAnimated:YES];
}
@end

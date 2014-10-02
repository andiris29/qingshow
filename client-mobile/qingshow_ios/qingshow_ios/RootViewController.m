//
//  ViewController.m
//  qingshow_ios
//
//  Created by wxy325 on 10/2/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "RootViewController.h"

#define ROOT_URL @"http://121.199.31.4/antSoftware/com.focosee.chingshow/trunk/dev/web/index.html#debug"

@interface RootViewController ()

@end

@implementation RootViewController

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    self.rootWebView.frame = [UIScreen mainScreen].applicationFrame;    //Adjust for status bar
    [self.rootWebView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:ROOT_URL] cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:30.f]];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

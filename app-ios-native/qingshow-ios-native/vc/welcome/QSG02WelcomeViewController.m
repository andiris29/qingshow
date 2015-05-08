//
//  QSG02WelcomeViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSG02WelcomeViewController.h"

@interface QSG02WelcomeViewController ()

@end

@implementation QSG02WelcomeViewController
- (instancetype)init
{
    self = [super initWithNibName:@"QSG02WelcomeViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction
- (IBAction)skipBtnPressed:(id)sender {
    if ([self.delegate respondsToSelector:@selector(dismissWelcomePage:)]) {
        [self.delegate dismissWelcomePage:self];
    }
}

@end

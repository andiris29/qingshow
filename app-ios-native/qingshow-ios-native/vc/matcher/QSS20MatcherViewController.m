//
//  QSS20MatcherViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/21/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS20MatcherViewController.h"
#import "QSAbstractRootViewController.h"
@interface QSS20MatcherViewController ()

@end

@implementation QSS20MatcherViewController

#pragma mark - Init
- (instancetype)init {
    self = [super initWithNibName:@"QSS20MatcherViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (IBAction)menuBtnPressed:(id)sender {
    [self.menuProvider didClickMenuBtn];
}
@end

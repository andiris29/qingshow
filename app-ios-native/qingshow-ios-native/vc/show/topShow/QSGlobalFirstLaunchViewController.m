//
//  QSGlobalFirstLaunchViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSGlobalFirstLaunchViewController.h"
#import "QSUserManager.h"

@interface QSGlobalFirstLaunchViewController ()

@end

@implementation QSGlobalFirstLaunchViewController

#pragma mark - Init
- (instancetype)init {
    self = [super initWithNibName:@"QSGlobalFirstLaunchViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.titleLabel.text = [QSUserManager shareUserManager].globalFirstLaunchTitle;
    NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy.MM.dd"];
    [dateFormatter setTimeZone:[NSTimeZone timeZoneForSecondsFromGMT:0]];
    NSString* dateStr = [dateFormatter stringFromDate:[NSDate date]];
    self.dateLabel.text = dateStr;
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction

- (IBAction)closeBtnPressed:(id)sender {
    [UIView animateWithDuration:0.5 animations:^{
        self.view.alpha = 0.f;
    } completion:^(BOOL finished) {

        [self.view removeFromSuperview];
        [self removeFromParentViewController];
        self.view.alpha = 1.f;
    }];

}
@end

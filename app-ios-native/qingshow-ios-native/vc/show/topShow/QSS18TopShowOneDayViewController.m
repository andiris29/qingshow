//
//  QSS18TopShowOneDayViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS18TopShowOneDayViewController.h"

@interface QSS18TopShowOneDayViewController ()

@property (strong, nonatomic) NSDate* date;

@end

@implementation QSS18TopShowOneDayViewController
#pragma mark - Init
- (instancetype)initWithDate:(NSDate*)date
{
    self = [super initWithNibName:@"QSS18TopShowOneDayViewController" bundle:nil];
    if (self) {
        self.date = date;
        
        
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

#pragma mark -

- (void)configView {
    self.title = @"美搭榜单";
    
}
- (void)configProvider {

}
@end

//
//  QSU12RefundViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/14/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU12RefundViewController.h"

@interface QSU12RefundViewController ()

@property (strong, nonatomic) NSDictionary* orderDict;
@end

@implementation QSU12RefundViewController

#pragma mark - Init
- (instancetype)initWithDict:(NSDictionary*)orderDict
{
    self = [super initWithNibName:@"QSU12RefundViewController" bundle:nil];
    
    if (self) {
        self.orderDict = orderDict;

        if ([self respondsToSelector:@selector(setAutomaticallyAdjustsScrollViewInsets:)]) {
            self.automaticallyAdjustsScrollViewInsets = NO;
        }
    }
    
    return self;
}

#pragma mark - Life Cycle

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.title = @"退货方式";
    self.widthCon.constant = [UIScreen mainScreen].bounds.size.width;
//    ((UIScrollView*)self.view).contentInset = UIEdgeInsetsMake(0, 0, 300.f, 0);
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}



@end

//
//  QSS01MatchShowsViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS01MatchShowsViewController.h"
#import "QSGlobalFirstLaunchViewController.h"
#import "QSMatchCollectionViewProvider.h"
#define PAGE_ID @""

@interface QSS01MatchShowsViewController ()

@property (nonatomic,assign) NSInteger segIndex;
@property (nonatomic, strong) UIBarButtonItem* firstLaunchItem;
@property (strong, nonatomic) QSGlobalFirstLaunchViewController* firstLaunchVc;
@property (nonatomic,strong) QSMatchCollectionViewProvider *matchCollectionViewProvider;

@end

@implementation QSS01MatchShowsViewController

- (UIBarButtonItem*)firstLaunchItem {
    if (!_firstLaunchItem) {
        UIImageView* barImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"global_first_launch"]];
        barImageView.userInteractionEnabled = YES;
        UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didClickFirstLaunch:)];
        [barImageView addGestureRecognizer:ges];
        
        UIBarButtonItem* item = [[UIBarButtonItem alloc] initWithCustomView:barImageView];
        _firstLaunchItem = item;
    }
    return _firstLaunchItem;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self configNav];
}
- (void)configProvider
{
    _matchCollectionViewProvider = [[QSMatchCollectionViewProvider alloc]init];
    _matchCollectionViewProvider.delegate = self;
    
}

- (void)configNav
{
    
    UISegmentedControl *segmentControl = [[UISegmentedControl alloc]initWithItems:@[@"最热",@"最新"]];
    segmentControl.frame = CGRectMake(120, 20, 120, 40);
    [segmentControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor colorWithRed:1.000 green:0.659 blue:0.743 alpha:1.000]} forState:UIControlStateNormal];
    [segmentControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor whiteColor]} forState:UIControlStateSelected];
    [segmentControl addTarget:self action:@selector(changeEvents) forControlEvents:UIControlEventValueChanged];
    [self.navigationController.navigationBar addSubview:segmentControl];
}

- (void)changeEvents
{
    if(_segIndex == 1)
    {
        
    }
    else
    {
        
    }
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

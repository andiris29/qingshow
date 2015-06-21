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
#import "MKNetworkKit.h"

#define PAGE_ID @"新美搭榜单"

@interface QSS01MatchShowsViewController ()<QSMatchCollectionViewDelegate>

@property (nonatomic,assign) NSInteger segIndex;
@property (nonatomic, strong) UIBarButtonItem* firstLaunchItem;
@property (nonatomic,strong) QSGlobalFirstLaunchViewController* firstLaunchVc;
@property (nonatomic,strong) QSMatchCollectionViewProvider *matchCollectionViewProvider;

@end

@implementation QSS01MatchShowsViewController

- (instancetype)init {
    self = [super initWithNibName:@"QSS01MatchShowsViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

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
    [self configProvider];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
}

- (void)configProvider
{
    _matchCollectionViewProvider = [[QSMatchCollectionViewProvider alloc]init];
    _matchCollectionViewProvider.delegate = self;
    [_matchCollectionViewProvider bindWithCollectionView:self.collectionView];
    
   // _matchCollectionViewProvider.networkBlock = nil;
   // [_matchCollectionViewProvider fetchDataOfPage:1];
    //[self.matchCollectionViewProvider reloadData];
}

- (void)configNav
{
    
    UISegmentedControl *segmentControl = [[UISegmentedControl alloc]initWithItems:@[@"最热",@"最新"]];
    segmentControl.frame = CGRectMake(110, 10, 120, 30);
    [segmentControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor colorWithRed:1.000 green:0.659 blue:0.743 alpha:1.000]} forState:UIControlStateNormal];
    [segmentControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor whiteColor]} forState:UIControlStateHighlighted];
    [segmentControl addTarget:self action:@selector(changeEvents) forControlEvents:UIControlEventValueChanged];
    segmentControl.tintColor = [UIColor colorWithRed:1.000 green:0.659 blue:0.743 alpha:1.000];
    [self.navigationController.navigationBar addSubview:segmentControl];
}
- (void)didClickFirstLaunch:(UIGestureRecognizer*)ges {
    QSGlobalFirstLaunchViewController* vc = [[QSGlobalFirstLaunchViewController alloc] init];
    
    //    [self addChildViewController:vc];
    vc.view.alpha = 0.f;
    [self.navigationController.view addSubview:vc.view];
    [UIView animateWithDuration:0.5 animations:^{
        vc.view.alpha = 1.f;
    }];
    self.firstLaunchVc = vc;
}

#warning CHANGE dic
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

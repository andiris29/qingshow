//
//  QSS17ViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/20.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS17ViewController.h"
#import "QSNetworkKit.h"
#import "QSShowUtil.h"
#import "QSS18TopShowOneDayViewController.h"
#import "UIViewController+ShowHud.h"
#import "UIViewController+QSExtension.h"
#import "QSUserManager.h"
#import "QSGlobalFirstLaunchViewController.h"

#define PAGE_ID @"S17 - 美搭榜单"
#define SS17CellId @"SS17TableViewCellId"

@interface QSS17ViewController ()<QSS17ProviderDelegate>

@property(nonatomic,strong)QSS17TavleViewProvider *provider;
@property (nonatomic, strong) UIBarButtonItem* firstLaunchItem;
@property (strong, nonatomic) QSGlobalFirstLaunchViewController* firstLaunchVc;

@end

@implementation QSS17ViewController
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


- (instancetype)init
{
    if (self = [super initWithNibName:@"QSS17ViewController" bundle:nil]) {
        
    }
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.

    [self configProvider];
    self.topShowTableView.separatorColor = [UIColor grayColor];
    self.topShowTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
   self.title = @"美搭榜单";
    [self.navigationController.navigationBar setTitleTextAttributes:
     
  @{NSFontAttributeName:NAVNEWFONT,
    
    NSForegroundColorAttributeName:[UIColor blackColor]}];
    //self.navigationController.navigationBar setBackgroundImage:<#(UIImage *)#> forBarMetrics:<#(UIBarMetrics)#>
    [self hideNaviBackBtnTitle];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveFirstLaunchChange:) name:kGlobalFirstUpdateNotification object:nil];
    
    NSLog(@"count ======   %d",self.navigationController.childViewControllers.count);
}

- (void)didReceiveFirstLaunchChange:(NSNotification*)noti{
    [self showGlobalFirstLaunchIcon];
}
- (void)configProvider
{
    self.provider = [[QSS17TavleViewProvider alloc] init];
    self.provider.delegate = self;
    [self.provider bindWithTableView:self.topShowTableView];
    self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getHotFeedingPage:page onSucceed:succeedBlock onError:errorBlock];
//        return  [SHARE_NW_ENGINE getTestShowsOnSucceed:succeedBlock onError:errorBlock];
    };
    [self.provider fetchDataOfPage:1];
    [self.provider reloadData];

}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [MobClick beginLogPageView:PAGE_ID];
    
    [self showGlobalFirstLaunchIcon];

}

- (void)showGlobalFirstLaunchIcon {

    if ([[NSDate date] compare:[QSUserManager shareUserManager].globalFirstLaunchShowDueDate] < 0) {
        //show icon
        self.navigationItem.rightBarButtonItem = self.firstLaunchItem;
    } else {
        self.navigationItem.rightBarButtonItem = nil;
    }
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

- (void)dealloc{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - QSS17ProviderDelegate - 点击跳转的方法

- (void)didClickedDate:(NSDate*)date ofProvider:(QSS17TavleViewProvider*)provider
{
    QSS18TopShowOneDayViewController* vc = [[QSS18TopShowOneDayViewController alloc] initWithDate:date];
    
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    vc.navigationItem.leftBarButtonItem = backItem;
    [self.navigationController pushViewController:vc animated:YES];
  
    
}
- (void)backAction
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)handleNetworkError:(NSError *)error
{
    [self showErrorHudWithError:error];
}

@end





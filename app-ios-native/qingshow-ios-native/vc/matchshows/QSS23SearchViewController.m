//
//  QSS23SearchViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/12.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS23SearchViewController.h"
#import "QSS24SearchResultViewController.h"
#import "QSUserManager.h"
#import "QSCategoryManager.h"

@interface QSS23SearchViewController ()
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (strong, nonatomic) QSS23TableViewProvider* provider;

@end

@implementation QSS23SearchViewController

#pragma mark - Init
- (instancetype)init {
    self = [super initWithNibName:@"QSS23SearchViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self _configNavBar];
    [self _configProvider];
    self.automaticallyAdjustsScrollViewInsets = NO;
}
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    self.navigationController.navigationBarHidden = NO;
    
    [self.navigationController.navigationBar setTitleTextAttributes:@{NSFontAttributeName:NEWFONT}];
    
    self.title = @"搜索搭配";
    //刷新所有的cell
    UIScrollView *scrollView = self.tableView;
    CGPoint p = CGPointMake(0, scrollView.contentSize.height);
    [scrollView setContentOffset:p];
    [scrollView setContentOffset:CGPointZero];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Private
- (void)_configProvider {
    self.provider = [[QSS23TableViewProvider alloc] init];
    self.provider.dataArray = [QSCategoryManager getInstance].categories;
    self.provider.delegate = self;
    self.provider.selectedArray = [@[] mutableCopy];
    [self.provider bindWithTableView:self.tableView];
}
- (void)_configNavBar
{
    UIImage *backImage = [UIImage imageNamed:@"nav_btn_back"];
    backImage  = [backImage imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    UIBarButtonItem *leftItem = [[UIBarButtonItem alloc] initWithImage:backImage style:UIBarButtonItemStylePlain target:self  action:@selector(_backBtnPressed:)];
    
    self.navigationItem.leftBarButtonItem = leftItem;
}
- (void)_backBtnPressed:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)provider:(QSS23TableViewProvider*)provider didSelectCategory:(NSDictionary*)category {
    UIViewController* vc = [[QSS24SearchResultViewController alloc] initWithCategory:category];
    [self.navigationController pushViewController:vc animated:YES];
}
@end

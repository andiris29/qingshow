//
//  QSS21CategorySelectorVC.m
//  qingshow-ios-native
//
//  Created by 刘少毅 on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS21CategorySelectionViewController.h"
#import "QSS21TableViewProvider.h"
#import "QSBackBarItem.h"

@interface QSS21CategorySelectionViewController () <QSS21TableViewProviderDelegate>

@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (strong, nonatomic) NSArray* categories;
@property (strong, nonatomic) NSArray* selectedCategories;
@property (strong , nonatomic)QSS21TableViewProvider *provider;
@property (strong, nonatomic) NSDictionary* modelParentCategory;

@end

@implementation QSS21CategorySelectionViewController

#pragma mark - Init
- (instancetype)initWithCategories:(NSArray*)array
                selectedCategories:(NSArray*)selectedCategories
               modelParentCategory:(NSDictionary*)modelParentCategory {
    self = [super initWithNibName:@"QSS21CategorySelectionViewController" bundle:nil];
    if (self) {
        self.categories = array;
        self.selectedCategories = selectedCategories;
        self.modelParentCategory = modelParentCategory;
    }
    return self;
}

#pragma mark - Life Cycle

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self setLeftBarButtonItem];
    
    [self setProvider];
    
}

#pragma mark -- 返回按钮设置
- (void)setLeftBarButtonItem
{
    UIImage *backImage = [UIImage imageNamed:@"nav_btn_back"];
    backImage  = [backImage imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    UIBarButtonItem *leftItem = [[UIBarButtonItem alloc] initWithImage:backImage style:UIBarButtonItemStylePlain target:self  action:@selector(gotoBack)];
    
    self.navigationItem.leftBarButtonItem = leftItem;
    
}
- (void)gotoBack
{
    [self.navigationController popViewControllerAnimated:YES];
}
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    
    [self.navigationController.navigationBar setTitleTextAttributes:@{NSFontAttributeName:NEWFONT}];
    
    self.title = @"百搭秀场";
    //刷新所有的cell
    UIScrollView *scrollView = self.tableView;
    CGPoint p = CGPointMake(0, scrollView.contentSize.height);
    [scrollView setContentOffset:p];
    [scrollView scrollsToTop];
}

#pragma mark -- bindTableView
- (void)setProvider
{
    self.provider = [[QSS21TableViewProvider alloc] init];
    self.provider.delegate = self;
    self.provider.dataArray = self.categories;
    self.provider.selectedArray = [self.selectedCategories mutableCopy];
    self.provider.modelParentDict = self.modelParentCategory;
    [self.provider bindWithTableView:self.tableView];
}

#pragma mark -- 开始搭配
- (IBAction)goToMakeShow:(UIButton *)sender {
    NSArray *result = [self.provider getResultArray];
    if ([self.delegate respondsToSelector:@selector(didSelectCategories:)]) {
        [self.delegate didSelectCategories:result];
    }
    [self.navigationController popViewControllerAnimated:YES];
}

@end

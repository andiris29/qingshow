//
//  QSBrandListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSP03BrandListViewController.h"
#import "QSP04BrandDetailViewController.h"
#import "QSNetworkKit.h"

#import "QSBrandTitleView.h"
@interface QSP03BrandListViewController ()

@property (strong, nonatomic) QSBigImageTableViewDelegateObj* delegateObj;
@property (strong, nonatomic) QSBrandTableViewHeaderView* headerView;
@property (strong, nonatomic) NSNumber* type;
@end

@implementation QSP03BrandListViewController

#pragma mark - Init Method
- (id)init
{
    self = [self initWithNibName:@"QSP03BrandListViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self configDelegateObj];
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    self.headerView = [QSBrandTableViewHeaderView generateView];
    self.headerView.delegate = self;
    self.tableView.tableHeaderView = self.headerView;
    self.navigationItem.titleView = [QSBrandTitleView generateView];
    
}
- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    CGRect rect = self.headerView.frame;
    rect.size.height = 44.f;
    self.headerView.frame = rect;
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [self.delegateObj refreshClickedData];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - 
- (void)configDelegateObj
{
    self.delegateObj = [[QSBigImageTableViewDelegateObj alloc] init];
    self.delegateObj.type = QSBigImageTableViewCellTypeBrand;
//    self.delegateObj.delegate = self;
    [self.delegateObj bindWithTableView:self.tableView];
    __weak QSP03BrandListViewController* weakSelf = self;
    self.delegateObj.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE queryBrands:weakSelf.type page:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.delegateObj.delegate = self;
    [self.delegateObj fetchDataOfPage:1];
}
#pragma mark - QSBrandCollectionViewDelegateObjDelegate
- (void)didClickBrand:(NSDictionary*)brandDict {
    UIViewController* vc = [[QSP04BrandDetailViewController alloc] initWithBrand:brandDict];
    [self.navigationController pushViewController:vc animated:YES];
}
- (void)didClickCell:(UITableViewCell *)cell ofData:(NSDictionary *)dict type:(QSBigImageTableViewCellType)type
{
//    [self didClickBrand:dict];
}
- (void)clickDetailOfDict:(NSDictionary *)dict type:(QSBigImageTableViewCellType)type
{
    [self didClickBrand:dict];
}
- (void)didClickOnline
{
    self.type = @0;
    [self.delegateObj refreshWithAnimation];
}
- (void)didClickOffline
{
    self.type = @1;
    [self.delegateObj refreshWithAnimation];
}
@end


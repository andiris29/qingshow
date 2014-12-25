//
//  QSBrandListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSP03BrandListViewController.h"
#import "QSP03BrandDetailViewController.h"
#import "QSNetworkKit.h"
#import "QSBrandTableViewHeaderView.h"
#import "QSBrandTitleView.h"
@interface QSP03BrandListViewController ()

@property (strong, nonatomic) QSBigImageTableViewDelegateObj* delegateObj;
@property (strong, nonatomic) QSBrandTableViewHeaderView* headerView;

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
    self.tableView.tableHeaderView = self.headerView;
    self.navigationItem.titleView = [QSBrandTitleView generateView];
    
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
//    self.delegateObj.delegate = self;
    [self.delegateObj bindWithTableView:self.tableView];
    self.delegateObj.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE queryBrands:0 page:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.delegateObj.delegate = self;
    [self.delegateObj fetchDataOfPage:1];
}
#pragma mark - QSBrandCollectionViewDelegateObjDelegate
- (void)didClickBrand:(NSDictionary*)brandDict {
    UIViewController* vc = [[QSP03BrandDetailViewController alloc] initWithBrand:brandDict];
    [self.navigationController pushViewController:vc animated:YES];
}
- (void)didClickCell:(UITableViewCell *)cell ofData:(NSDictionary *)dict
{
    [self didClickBrand:dict];
}
@end


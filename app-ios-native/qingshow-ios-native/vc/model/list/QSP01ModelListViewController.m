//
//  QSModelListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSP01ModelListViewController.h"
#import "QSP02ModelDetailViewController.h"


#import "QSNetworkEngine.h"

@interface QSP01ModelListViewController ()

@property (strong, nonatomic) QSModelListTableViewDelegateObj* delegateObj;
- (void)configView;
@end

@implementation QSP01ModelListViewController

#pragma mark - Init Method
- (id)init
{
    self = [self initWithNibName:@"QSP01ModelListViewController" bundle:nil];
    if (self) {
    }
    return self;
}
- (void)configDelegateObj
{
    self.delegateObj = [[QSModelListTableViewDelegateObj alloc] init];
    self.delegateObj.delegate = self;
    [self.delegateObj bindWithTableView:self.tableView];
    self.delegateObj.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getModelListPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    [self.delegateObj fetchDataOfPage:1];
}

#pragma mark - Life Cycle
- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self configView];
    
    [self configDelegateObj];
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - View
- (void)configView
{
    self.navigationItem.title = @"人气达人";
    self.navigationItem.backBarButtonItem.title = @"";
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
}

#pragma mark - QSModelListTableViewDelegateObjDelegate
- (void)clickModel:(NSDictionary*)model
{
    UIViewController* vc = [[QSP02ModelDetailViewController alloc] initWithModel:model];
    [self.navigationController pushViewController:vc animated:YES];
}
- (void)addFavorModel:(NSDictionary*)model
{
    
}

@end
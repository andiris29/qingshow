//
//  QSModelListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSP01ModelListViewController.h"
#import "QSP02ModelDetailViewController.h"
#import "UIViewController+ShowHud.h"
#import "UIViewController+QSExtension.h"
#import "QSNetworkKit.h"

#define PAGE_ID @"P01 - 模特一览"

@interface QSP01ModelListViewController ()

@property (strong, nonatomic) QSModelListTableViewProvider* delegateObj;
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
    self.delegateObj = [[QSModelListTableViewProvider alloc] init];
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
- (void)viewWillAppear:(BOOL)animated
{

    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [self.delegateObj refreshClickedData];
    [MobClick beginLogPageView:PAGE_ID];
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
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

#pragma mark - QSModelListTableViewProviderDelegate
- (void)clickModel:(NSDictionary*)model
{
    [self showPeopleDetailViewControl:model];
}
- (void)followBtnPressed:(NSDictionary*)model
{
    [SHARE_NW_ENGINE handleFollowModel:model onSucceed:^(BOOL fFollow) {
        if (fFollow) {
            [self showTextHud:@"关注成功"];
        }
        else
        {
            [self showTextHud:@"取消关注成功"];
        }
        NSUInteger index = [self.delegateObj.resultArray indexOfObject:model];
        [self.delegateObj.view reloadRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:index inSection:0]] withRowAnimation:UITableViewRowAnimationNone];
    } onError:^(NSError *error) {
        [self handleError:error];
    }];
}
- (void)handleNetworkError:(NSError *)error
{
    [self handleError:error];
}
@end

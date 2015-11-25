//
//  QSNewestHourViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/25.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSNewestHourViewController.h"
#import "QSNewestHourTitleView.h"
#import "QSShowCollectionViewProvider.h"
#import "QSNetworkKit.h"
#import "QSS03ShowDetailViewController.h"
#import "QSU01UserDetailViewController.h"
#import "UIViewController+QSExtension.h"

@interface QSNewestHourViewController () <QSShowProviderDelegate>

@property (strong, nonatomic) NSDate* date;
@property (strong, nonatomic) QSNewestHourTitleView* titleView;
@property (strong,nonatomic) QSShowCollectionViewProvider *provider;
@end

@implementation QSNewestHourViewController

#pragma mark - Init
- (instancetype)initWithDate:(NSDate*)date {
    self = [super initWithNibName:@"QSNewestHourViewController" bundle:nil];
    if (self) {
        self.date = date;
    }
    return self;
}
#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];

    [self _configNav];
    [self _configProvider];
}
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Delegate

#pragma mark - Private
- (void)_configNav {
    self.titleView = [QSNewestHourTitleView generateView];
    [self.titleView bindWithDate:self.date];
    self.navigationItem.titleView = self.titleView;
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    self.navigationItem.leftBarButtonItem = backItem;
}
- (void)_configProvider {
    self.provider = [[QSShowCollectionViewProvider alloc] init];
    [self.provider bindWithCollectionView:self.collectionView];
    QSNewestHourViewController* weakSelf = self;
    self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        NSDate* date = [weakSelf.date dateByAddingTimeInterval:60 * 60];
        return [SHARE_NW_ENGINE getfeedingMatchNewFromDate:weakSelf.date toDate:date page:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.provider.delegate = self;
    [self.provider reloadData];
}

- (void)didClickShow:(NSDictionary*)showDict provider:(QSAbstractListViewProvider *)provider
{
    QSS03ShowDetailViewController* vc = [[QSS03ShowDetailViewController alloc] initWithShow:showDict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)didClickPeople:(NSDictionary*)peopleDict provider:(QSAbstractListViewProvider*)provider
{
//        QSU01UserDetailViewController *vc = [[QSU01UserDetailViewController alloc]initWithPeople:sender];

    //    [self.navigationController pushViewController:vc animated:YES];
    
}

@end

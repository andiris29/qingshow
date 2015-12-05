//
//  QSNewestHourViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/25.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSNewestHourViewController.h"
#import "QSShowCollectionViewProvider.h"
#import "QSNetworkKit.h"
#import "QSS03ShowDetailViewController.h"
#import "QSU01UserDetailViewController.h"
#import "UIViewController+QSExtension.h"
#import "QSDateUtil.h"

@interface QSNewestHourViewController () <QSShowProviderDelegate>

@property (strong, nonatomic) NSDate* fromDate;
@property (strong, nonatomic) NSDate* toDate;
@property (strong,nonatomic) QSShowCollectionViewProvider *provider;
@end

@implementation QSNewestHourViewController

#pragma mark - Init
- (instancetype)initWithFromDate:(NSDate*)fromDate toDate:(NSDate*)toDate {
    self = [super initWithNibName:@"QSNewestHourViewController" bundle:nil];
    if (self) {
        self.fromDate = fromDate;
        self.toDate = toDate;
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
    [self.provider refreshClickedData];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Delegate

#pragma mark - Private
- (void)_configNav {
    if ([self.toDate timeIntervalSinceDate:self.fromDate] > 60 * 60 + 1) {
        self.title = [QSDateUtil buildDayStringFromDate:self.fromDate];
    } else {
        self.title = [QSDateUtil getTime:self.fromDate];
    }
    
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    self.navigationItem.leftBarButtonItem = backItem;
}
- (void)_configProvider {
    self.provider = [[QSShowCollectionViewProvider alloc] init];
    [self.provider bindWithCollectionView:self.collectionView];
    QSNewestHourViewController* weakSelf = self;
    self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getfeedingMatchTimeFromDate:weakSelf.fromDate
                                                     toDate:weakSelf.toDate
                                                       page:page
                                                  onSucceed:succeedBlock
                                                    onError:errorBlock];
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
    QSU01UserDetailViewController *vc = [[QSU01UserDetailViewController alloc]initWithPeople:peopleDict];
    [self.navigationController pushViewController:vc animated:YES];
}

@end

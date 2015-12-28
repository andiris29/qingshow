//
//  QSNewestHourViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/25.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSS24ViewController.h"
#import "QSShowCollectionViewProvider.h"
#import "QSNetworkKit.h"
#import "QSS03ShowDetailViewController.h"
#import "QSU01UserDetailViewController.h"
#import "UIViewController+QSExtension.h"
#import "QSDateUtil.h"

typedef NS_ENUM(NSUInteger, QSS24Type) {
    QSS24TypeNew = 0,
    QSS24TypeHot = 1
};

@interface QSS24ViewController () <QSShowProviderDelegate>

@property (strong, nonatomic) NSDate* fromDate;
@property (strong, nonatomic) NSDate* toDate;
@property (strong,nonatomic) QSShowCollectionViewProvider *provider;
@property (assign, nonatomic) QSS24Type type;

@property (weak, nonatomic) IBOutlet UIButton *hotestBtn;
@property (weak, nonatomic) IBOutlet UIButton *newestBtn;

@end

@implementation QSS24ViewController

#pragma mark - Init
- (instancetype)initWithFromDate:(NSDate*)fromDate toDate:(NSDate*)toDate {
    self = [super initWithNibName:@"QSS24ViewController" bundle:nil];
    if (self) {
        self.fromDate = fromDate;
        self.toDate = toDate;
        self.type = QSS24TypeNew;
    }
    return self;
}
#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];

    [self _configNav];
    [self _configProvider];
    [self _updateBtnVisible];
}
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [self.provider refreshClickedData];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveUserLoginNoti:) name:kUserLoginNotification object:nil];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
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
    QSS24ViewController* weakSelf = self;
    self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        if (weakSelf.type == QSS24TypeNew) {
            return [SHARE_NW_ENGINE getfeedingMatchTimeFromDate:weakSelf.fromDate
                                                         toDate:weakSelf.toDate
                                                           page:page
                                                      onSucceed:succeedBlock
                                                        onError:errorBlock];
        } else {
            return [SHARE_NW_ENGINE getfeedingHotMatchTimeFromDate:weakSelf.fromDate
                                                            toDate:weakSelf.toDate
                                                              page:page
                                                         onSucceed:succeedBlock
                                                           onError:errorBlock];
        }
    };
    self.provider.delegate = self;
    [self.provider reloadData];
    
}

- (void)didClickShow:(NSDictionary*)showDict provider:(QSAbstractListViewProvider *)provider
{
    [self showShowDetailViewController:showDict];
}

- (void)didClickPeople:(NSDictionary*)peopleDict provider:(QSAbstractListViewProvider*)provider
{
    QSU01UserDetailViewController *vc = [[QSU01UserDetailViewController alloc]initWithPeople:peopleDict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)didReceiveUserLoginNoti:(NSNotification*)noti {
    [self.provider reloadData];
}

- (void)_updateBtnVisible {
    self.hotestBtn.hidden = self.type != QSS24TypeNew;
    self.newestBtn.hidden = self.type == QSS24TypeNew;
}
- (IBAction)hotOrNewBtnPressed:(id)sender {
    if (self.type == QSS24TypeNew) {
        self.type = QSS24TypeHot;
    } else {
        self.type = QSS24TypeNew;
    }
    [self _updateBtnVisible];
    [self.provider reloadData];
}
@end

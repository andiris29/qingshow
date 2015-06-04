//
//  QSS18TopShowOneDayViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS18TopShowOneDayViewController.h"

#import "QSNetworkKit.h"
#import "UIViewController+QSExtension.h"

#define PAGE_ID @"S18 - 美搭每天"

@interface QSS18TopShowOneDayViewController ()

@property (strong, nonatomic) NSDate* date;
@property (strong, nonatomic) QSS18WaterfallProvider* provider;
@end

@implementation QSS18TopShowOneDayViewController
#pragma mark - Init
- (instancetype)initWithDate:(NSDate*)date
{
    self = [super initWithNibName:@"QSS18TopShowOneDayViewController" bundle:nil];
    if (self) {
        self.date = date;
        
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    [self configView];
    [self configProvider];

}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
//        [self.navigationController.navigationItem.backBarButtonItem setImage: [UIImage imageNamed:@"nav_btn_back"]];
    [MobClick beginLogPageView:PAGE_ID];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}

#pragma mark -
- (void)configView {
    self.title = @"美搭榜单";
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
}
- (void)configProvider {
    self.provider = [[QSS18WaterfallProvider alloc] initWithDate:self.date];
    self.provider.delegate = self;
    [self.provider bindWithCollectionView:self.collectionView];
    __weak QSS18TopShowOneDayViewController* weakSelf = self;
    self.provider.networkBlock =  ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getRecommendationFeedingDate:weakSelf.date
                                                        page:page
                                                   onSucceed:succeedBlock
                                                     onError:errorBlock];
//        return [SHARE_NW_ENGINE getTestShowsOnSucceed:succeedBlock onError:errorBlock];
    };
    [self.provider reloadData];
}

- (void)didClickShow:(NSDictionary*)show ofProvider:(QSS18WaterfallProvider*)provider
{
    [self showShowDetailViewController:show];
}
@end

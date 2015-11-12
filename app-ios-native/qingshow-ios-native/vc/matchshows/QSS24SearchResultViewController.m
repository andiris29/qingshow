//
//  QSS24SearchResultViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/12.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSS24SearchResultViewController.h"

#import "UIViewController+ShowHud.h"
#import "QSNetworkKit.h"
#import "QSS03ShowDetailViewController.h"
#import "QSU01UserDetailViewController.h"
#import "UIViewController+QSExtension.h"
#import "QSCategoryUtil.h"

@interface QSS24SearchResultViewController ()

@property (strong, nonatomic) NSDictionary* categoryDict;
@property (nonatomic,strong) QSMatchCollectionViewProvider *matchCollectionViewProvider;
@end

@implementation QSS24SearchResultViewController

- (instancetype)initWithCategory:(NSDictionary*)categoryDict {
    self = [super initWithNibName:@"QSS24SearchResultViewController" bundle:nil];
    if (self) {
        self.categoryDict = categoryDict;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    _matchCollectionViewProvider = [[QSMatchCollectionViewProvider alloc]init];
    _matchCollectionViewProvider.delegate = self;
    _matchCollectionViewProvider.hasRefreshControl = YES;
    _backToTopbtn.hidden = YES;
    [self configProvider];
    
    UIImage *backImage = [UIImage imageNamed:@"nav_btn_back"];
    backImage  = [backImage imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    UIBarButtonItem *leftItem = [[UIBarButtonItem alloc] initWithImage:backImage style:UIBarButtonItemStylePlain target:self  action:@selector(_backBtnPressed:)];
    
    self.navigationItem.leftBarButtonItem = leftItem;
    self.title = [QSCategoryUtil getName:self.categoryDict];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [self.matchCollectionViewProvider refreshVisibleData];
}
- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.matchCollectionViewProvider cancelImageLoading];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)configProvider
{
    
    [_matchCollectionViewProvider bindWithCollectionView:self.collectionView];
    __weak QSS24SearchResultViewController* weakSelf = self;
    _matchCollectionViewProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock,ErrorBlock errorBlock,int page){
        return [SHARE_NW_ENGINE getFeedingSearch:[QSCategoryUtil getName:weakSelf.categoryDict] page:page onSucceed:succeedBlock onError:errorBlock];
    };
    //NSLog(@" new count == %@",self.matchCollectionViewProvider.resultArray);
    [_matchCollectionViewProvider fetchDataOfPage:1];
    [self.matchCollectionViewProvider reloadData];
}

#pragma mark - Delegate
- (void)didSelectedCellInCollectionView:(id)sender
{
    QSS03ShowDetailViewController *vc = [[QSS03ShowDetailViewController alloc] initWithShow:sender];
    //    NSLog(@"%@",[QSEntityUtil getStringValue:sender keyPath:@"_id"]) ;
    // vc.menuProvider = self.menuProvider;
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    vc.navigationItem.leftBarButtonItem = backItem;
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)reloadCollectionViewData
{
    [self hideNewworkWaitingHud];
    MBProgressHUD* hud = [self showNetworkWaitingHud];
    [hud hide:YES];
    [self.matchCollectionViewProvider reloadDataOnCompletion:^{
        [hud hide:YES];
    }];
}

- (void)didClickHeaderImgView:(id)sender
{
    QSU01UserDetailViewController *vc = [[QSU01UserDetailViewController alloc]initWithPeople:sender];
    vc.navigationController.navigationBar.hidden = NO;
    [self.navigationController pushViewController:vc animated:YES];
    
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if (self.collectionView.contentOffset.y != 0) {
        _backToTopbtn.hidden = NO;
    }
    else
    {
        _backToTopbtn.hidden = YES;
    }
}
- (IBAction)backToTopBtnPressed:(id)sender {
    CGPoint p = [self.collectionView contentOffset];
    p.y = 0;
    [self.collectionView setContentOffset:p animated:YES];
    _backToTopbtn.hidden = YES;
}
- (void)handleNetworkError:(NSError *)error {
    [self handleError:error];
}
- (void)_backBtnPressed:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}
@end

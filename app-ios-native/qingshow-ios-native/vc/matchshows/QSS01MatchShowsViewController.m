//
//  QSS01MatchShowsViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS01MatchShowsViewController.h"
#import "QSMatchCollectionViewProvider.h"
#import "QSBlock.h"
#import "QSNetworkKit.h"
#import "QSShareViewController.h"
#import "QSS03ShowDetailViewController.h"
#import "QSU01UserDetailViewController.h"

#define PAGE_ID @"新美搭榜单"

@interface QSS01MatchShowsViewController ()<UICollectionViewDelegate,QSMatchCollectionViewProviderDelegate>

@property (nonatomic,assign) NSInteger segIndex;
@property (nonatomic,strong) QSMatchCollectionViewProvider *matchCollectionViewProvider;

@end

@implementation QSS01MatchShowsViewController

- (instancetype)init {
    self = [super initWithNibName:@"QSS01MatchShowsViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib
    _matchCollectionViewProvider = [[QSMatchCollectionViewProvider alloc]init];
    _matchCollectionViewProvider.delegate = self;
    _matchCollectionViewProvider.type = 1;
    [self configNav];
    [self configProvider];

}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
}

- (void)configProvider
{
   
    [_matchCollectionViewProvider bindWithCollectionView:self.collectionView];

//    _matchCollectionViewProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
//        return [SHARE_NW_ENGINE getHotFeedingPage:page onSucceed:succeedBlock onError:errorBlock];
//        //        return  [SHARE_NW_ENGINE getTestShowsOnSucceed:succeedBlock onError:errorBlock];
//    };
    _matchCollectionViewProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock,ErrorBlock errorBlock,int page){
        return [SHARE_NW_ENGINE getfeedingMatchHot:nil page:page onSucceed:succeedBlock onError:errorBlock];
    };
    [_matchCollectionViewProvider fetchDataOfPage:1];
    [self.matchCollectionViewProvider reloadData];
}

- (void)configNav
{
    
    UISegmentedControl *segmentControl = [[UISegmentedControl alloc]initWithItems:@[@"最热",@"最新"]];
    segmentControl.frame = CGRectMake(0, 0, 120, 30);
    [segmentControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor colorWithRed:1.000 green:0.659 blue:0.743 alpha:1.000]} forState:UIControlStateNormal];
    [segmentControl setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor whiteColor]} forState:UIControlStateHighlighted];
    [segmentControl addTarget:self action:@selector(changeEvents) forControlEvents:UIControlEventValueChanged];
    segmentControl.tintColor = [UIColor colorWithRed:1.000 green:0.659 blue:0.743 alpha:1.000];
    _segIndex = segmentControl.selectedSegmentIndex;
    self.navigationItem.titleView = segmentControl;
    segmentControl.selectedSegmentIndex = 0;
    [self.navigationController.navigationBar removeGestureRecognizer:self.showVersionTapGesture];
}


- (void)changeEvents
{

    if(_segIndex == 1)
    {
        _matchCollectionViewProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock,ErrorBlock errorBlock,int page){
            return [SHARE_NW_ENGINE getfeedingMatchNew:nil page:page onSucceed:succeedBlock onError:errorBlock];
        };

        [self.matchCollectionViewProvider reloadData];
    }
    else
    {
        _matchCollectionViewProvider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock,ErrorBlock errorBlock,int page){
            return [SHARE_NW_ENGINE getfeedingMatchHot:nil page:page onSucceed:succeedBlock onError:errorBlock];
        };
        

       
        [self.matchCollectionViewProvider reloadData];
    }
}
#pragma mark - Delegate
- (void)didSelectedCellInCollectionView:(id)sender
{

    QSS03ShowDetailViewController *vc = [[QSS03ShowDetailViewController alloc]initWithShow:sender];
    NSLog(@"sender = %@",sender);
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    vc.navigationItem.leftBarButtonItem = backItem;
    [self.navigationController pushViewController:vc animated:YES];
}


- (void)didClickHeaderImgView:(id)sender
{
    QSU01UserDetailViewController *vc = [[QSU01UserDetailViewController alloc]initWithPeople:sender];
    vc.menuProvider = self.menuProvider;
    vc.navigationController.navigationBar.hidden = NO;
    vc.nemuBtn.hidden = YES;
    [self.navigationController pushViewController:vc animated:YES];
    
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end

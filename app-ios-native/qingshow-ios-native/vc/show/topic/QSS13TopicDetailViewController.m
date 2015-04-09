//
//  QSS13TopicDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 4/1/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS13TopicDetailViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+QSExtension.h"
#import "UIViewController+ShowHud.h"
#import "QSS03ShowDetailViewController.h"
#import "QSNetworkKit.h"
#import "QSTopicUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#define PAGE_ID @"S13"

@interface QSS13TopicDetailViewController ()
@property (strong, nonatomic) NSDictionary* topicDict;
@property (strong, nonatomic) QSShowCollectionViewProvider* provider;
@end

@implementation QSS13TopicDetailViewController
#pragma mark - Init
- (id)initWithTopic:(NSDictionary*)topicDict
{
    self = [super initWithNibName:@"QSS13TopicDetailViewController" bundle:nil];
    if (self) {
        self.topicDict = topicDict;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self disableAutoAdjustScrollViewInset];
    self.collectionView.contentInset = UIEdgeInsetsMake(200.f, 0, 0, 0);
    [self configProvider];
    
    self.titleLabel.text = [QSTopicUtil getTitle:self.topicDict];
    self.numberLabel.text = [QSTopicUtil getShowNumberDesc:self.topicDict];
    [self.imageView setImageFromURL:[QSTopicUtil getHorizontalCoverUrl:self.topicDict]];
    
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    [MobClick beginLogPageView:PAGE_ID];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction

- (IBAction)backBtnPressed:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark - QSShowProviderDelegate
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    CGFloat top = - (scrollView.contentInset.top + scrollView.contentOffset.y);
    if (top >= 0) {
        top = 0;
    } else if (top < -200) {
        top = -200;
    }
    self.topConstraint.constant = top;
    [self.view layoutIfNeeded];
}

- (void)didClickShow:(NSDictionary*)showDict
{

}

- (void)didClickPlayButtonOfShow:(NSDictionary*)showDict
{
    UIViewController* vc = [[QSS03ShowDetailViewController alloc] initWithShow:showDict];
    [self.navigationController pushViewController:vc animated:YES];
}

#pragma mark - Private Method
- (void)configProvider
{
    self.provider = [[QSShowCollectionViewProvider alloc] init];
    self.provider.delegate = self;
    self.provider.type = QSShowWaterfallDelegateObjTypeWithoutDate;
    self.provider.cellType = QSShowCollectionViewCellTypeTopic;
    self.provider.hasRefreshControl = NO;
    [self.provider bindWithCollectionView:self.collectionView];
    __weak QSS13TopicDetailViewController* weakSelf = self;
    self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE feedingByTopic:weakSelf.topicDict page:page onSucceed:succeedBlock onError:errorBlock];
    };
    [self.provider fetchDataOfPage:1];
}
@end

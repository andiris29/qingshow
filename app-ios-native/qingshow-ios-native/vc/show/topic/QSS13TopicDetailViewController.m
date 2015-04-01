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
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
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
#warning TODO move header
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
    self.provider.type = QSShowWaterfallDelegateObjTypeWithDate;
    self.provider.cellType = QSShowCollectionViewCellTypeTopic;
    [self.provider bindWithCollectionView:self.collectionView];
    __weak QSS13TopicDetailViewController* weakSelf = self;
    self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getChosenFeedingType:0 page:page onSucceed:succeedBlock onError:^(NSError *error) {
            if ([error.domain isEqualToString:NSURLErrorDomain] && error.code == -1009) {
                UIAlertView* a = [[UIAlertView alloc] initWithTitle:@"未连接网络或信号不好" message:nil delegate:weakSelf cancelButtonTitle:@"确定" otherButtonTitles: nil];
                [a show];
            } else {
                errorBlock(error);
            }
            
        }];
    };
    [self.provider fetchDataOfPage:1];
}
@end

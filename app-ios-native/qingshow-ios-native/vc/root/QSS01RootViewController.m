//
//  QSViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//
#import "UIViewController+ShowHud.h"
#import "QSNetworkKit.h"

#import <QuartzCore/QuartzCore.h>

#import "QSS01RootViewController.h"

#import "QSShowUtil.h"
#import "QSError.h"
#import "UIViewController+QSExtension.h"
#import "UIImage+BlurryImage.h"
#import "QSS03ShowDetailViewController.h"

#define PAGE_ID @"S01 - 倾秀首页"


@interface QSS01RootViewController ()

@property (strong, nonatomic) QSShowCollectionViewProvider* delegateObj;

@end

@implementation QSS01RootViewController
#pragma mark - 
- (id)init
{
    self = [self initWithNibName:@"QSS01RootViewController" bundle:nil];
    if (self) {

    }
    return self;
}
#pragma mark - Life Cycle
- (void)viewDidLoad
{
    [super viewDidLoad];
    [self configProvider];
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
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
#pragma mark - Network
- (void)configProvider
{
    self.delegateObj = [[QSShowCollectionViewProvider alloc] init];
    self.delegateObj.delegate = self;
    self.delegateObj.type = QSShowWaterfallDelegateObjTypeWithDate;
    [self.delegateObj bindWithCollectionView:self.collectionView];
    __weak QSS01RootViewController* weakSelf = self;
    self.delegateObj.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getChosenFeedingType:0 page:page onSucceed:succeedBlock onError:^(NSError *error) {
            if ([error.domain isEqualToString:NSURLErrorDomain] && error.code == -1009) {
                UIAlertView* a = [[UIAlertView alloc] initWithTitle:@"未连接网络或信号不好" message:nil delegate:weakSelf cancelButtonTitle:@"确定" otherButtonTitles: nil];
                [a show];
            } else {
                errorBlock(error);
            }

        }];
    };
    [self.delegateObj fetchDataOfPage:1];
}

#pragma mark - QSWaterFallCollectionViewCellDelegate
- (void)addFavorShow:(NSDictionary*)showDict
{
    if ([QSShowUtil getIsLike:showDict]) {
        [SHARE_NW_ENGINE unlikeShow:showDict onSucceed:^{
            [self showSuccessHudWithText:@"取消喜欢成功"];
            [self.delegateObj updateShow:showDict];
        } onError:^(NSError *error) {
            [self handleError:error];
        }];
    } else {
        [SHARE_NW_ENGINE likeShow:showDict onSucceed:^{
            [self showSuccessHudWithText:@"喜欢成功"];
            [self.delegateObj updateShow:showDict];
        } onError:^(NSError *error) {
            [self handleError:error];
        }];
    }
}

#pragma mark - QSShowWaterfallDelegateObjDelegate
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{

}
- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
    [self hideMenu];
}

#pragma mark -
- (void)didClickShow:(NSDictionary*)showDict
{
    [self hideMenu];
    UIViewController* vc = [[QSS03ShowDetailViewController alloc] initWithShow:showDict];
    [self.navigationController pushViewController:vc animated:NO];

    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromRight;
    
    [self.navigationController.view.layer addAnimation:tran forKey:@"transition_to_show_detail"];
}

- (void)didClickPeople:(NSDictionary *)peopleDict
{
    [self hideMenu];
    UIViewController* vc = [self generateDetailViewControlOfPeople:peopleDict];
    [self.navigationController pushViewController:vc animated:NO];
    
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromRight;
    [self.navigationController.view.layer addAnimation:tran forKey:@"transition_to_people_detail"];
}

- (void)handleNetworkError:(NSError*)error
{
    [self handleError:error];
}
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self hideMenu];
}
#pragma mark - AlertView
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    [self.delegateObj reloadData];
}
@end

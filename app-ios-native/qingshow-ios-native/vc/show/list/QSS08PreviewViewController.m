//
//  QSS02FashionViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/21/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSS08PreviewViewController.h"
#import "QSNetworkKit.h"
#import "QSS04CommentListViewController.h"
#import "UIViewController+QSExtension.h"
#import "UIViewController+ShowHud.h"

#define PAGE_ID @"S08 - 潮流时尚"

@interface QSS08PreviewViewController ()

@property (strong, nonatomic) QSBigImageTableViewProvider* delegateObj;
@property (strong, nonatomic) QSShareViewController* shareVc;
@end

@implementation QSS08PreviewViewController
- (id)init
{
    self = [super initWithNibName:@"QSS08PreviewViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}
#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self configDelegateObj];
    self.title = @"潮流时尚";
    self.navigationController.navigationBarHidden = NO;
    self.shareVc = [[QSShareViewController alloc] init];
    [self.view addSubview:self.shareVc.view];

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

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    self.shareVc.view.frame = self.view.bounds;
}

#pragma mark - 
- (void)configDelegateObj
{
    self.delegateObj = [[QSBigImageTableViewProvider alloc] init];
    self.delegateObj.type = QSBigImageTableViewCellTypeFashion;
    [self.delegateObj bindWithTableView:self.tableView];
    self.delegateObj.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getPreviewFeedingPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    [self.delegateObj fetchDataOfPage:1];
    self.delegateObj.delegate = self;
}

#pragma mark - QSBigImageTableViewProviderDelegate
- (void)clickCommentOfDict:(NSDictionary*)dict
{
    UIViewController* vc = [[QSS04CommentListViewController alloc] initWithPreview:dict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)clickShareOfDict:(NSDictionary*)dict
{
    return;
    NSLog(@"share");
    [self.shareVc showSharePanelWithUrl:nil];
}
- (void)clickLikeOfDict:(NSDictionary*)dict
{
    [SHARE_NW_ENGINE handlePreviewLike:dict onSucceed:^(BOOL f) {
        if (f) {
            [self showTextHud:@"喜欢成功"];

        } else {
            [self showTextHud:@"取消喜欢成功"];
        }
        [self.delegateObj rebindData:dict];
    } onError:^(NSError *error) {
        [self handleError:error];
    }];
}
- (void)didShareWeiboSuccess
{
    [self showSuccessHudWithText:@"分享成功"];
}
@end

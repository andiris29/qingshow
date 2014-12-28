//
//  QSS02FashionViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/21/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSS02FashionViewController.h"
#import "QSNetworkKit.h"
#import "QSCommentListViewController.h"
#import "UIViewController+QSExtension.h"
#import "UIViewController+ShowHud.h"

@interface QSS02FashionViewController ()

@property (strong, nonatomic) QSBigImageTableViewDelegateObj* delegateObj;

@end

@implementation QSS02FashionViewController
- (id)init
{
    self = [super initWithNibName:@"QSS02FashionViewController" bundle:nil];
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
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - 
- (void)configDelegateObj
{
    self.delegateObj = [[QSBigImageTableViewDelegateObj alloc] init];
    self.delegateObj.type = QSBigImageTableViewCellTypeFashion;
    [self.delegateObj bindWithTableView:self.tableView];
    self.delegateObj.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getPreviewFeedingPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    [self.delegateObj fetchDataOfPage:1];
    self.delegateObj.delegate = self;
}

#pragma mark - QSBigImageTableViewDelegateObjDelegate
- (void)clickCommentOfDict:(NSDictionary*)dict
{
    UIViewController* vc = [[QSCommentListViewController alloc] initWithPreview:dict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)clickShareOfDict:(NSDictionary*)dict
{
    NSLog(@"share");
}
- (void)clickLikeOfDict:(NSDictionary*)dict
{
    [SHARE_NW_ENGINE handlePreviewLike:dict onSucceed:^(BOOL f) {
        if (f) {
            [self showTextHud:@"like successfully"];

        } else {
            [self showTextHud:@"unlike successfully"];
        }
        [self.delegateObj rebindData:dict];
    } onError:^(NSError *error) {
        [self handleError:error];
    }];
}
@end

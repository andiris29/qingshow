//
//  QSS15TopicViewController.m
//  qingshow-ios-native
//
//  Created by ching show on 15/4/28.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS15TopicViewController.h"
#import "QSS13TopicDetailViewController.h"
#import "QSNetworkKit.h"
#define PAGE_ID @"S15"

@interface QSS15TopicViewController ()

@property (strong, nonatomic)QSTopicTableViewProvider *provider;

@end

@implementation QSS15TopicViewController

- (instancetype)init
{
    self = [super initWithNibName:@"QSS15TopicViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    [self configProvider];
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.provider refreshClickedData];
    [MobClick beginLogPageView:PAGE_ID];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    
}

#pragma mark - Private
- (void)configProvider
{
    __weak QSS15TopicViewController* weakSelf = self;
    self.provider = [[QSTopicTableViewProvider alloc] init];
    self.provider.hasPaging = NO;
    self.provider.delegate = self;
    [self.provider bindWithTableView:self.tableView];
    self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE queryTopicPage:page OnSucceed:succeedBlock onError:^(NSError *error) {
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
#pragma mark - QSTopicTableViewProviderDelegate
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    
}
- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
    [self hideMenu];
}

- (void)didClickTopic:(NSDictionary*)topicDict
{
    UIViewController* vc = [[QSS13TopicDetailViewController alloc] initWithTopic:topicDict];
    [self.navigationController pushViewController:vc animated:YES];
}


@end

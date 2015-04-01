//
//  QSS12TopicViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS12TopicViewController.h"
#import "QSS13TopicDetailViewController.h"
#import "QSNetworkKit.h"

@interface QSS12TopicViewController ()

@property (strong, nonatomic) QSTopicTableViewProvider* provider;

@end

@implementation QSS12TopicViewController

- (instancetype)init
{
    self = [super initWithNibName:@"QSS12TopicViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    [self configProvider];
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.provider refreshClickedData];
#warning TODO MobClick
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];

}

#pragma mark - Private
- (void)configProvider
{
    __weak QSS12TopicViewController* weakSelf = self;
    self.provider = [[QSTopicTableViewProvider alloc] init];
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

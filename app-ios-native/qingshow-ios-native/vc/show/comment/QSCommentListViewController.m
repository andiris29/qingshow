//
//  QSCommentListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSCommentListViewController.h"
#import "QSCommentTableViewCell.h"
#import "QSNetworkEngine.h"

@interface QSCommentListViewController ()

@property (strong, nonatomic) IBOutlet UITableView* tableView;

@property (strong, nonatomic) NSDictionary* showDict;
@property (strong, nonatomic) QSCommentListTableViewDelegateObj* delegateObj;
@end

@implementation QSCommentListViewController

#pragma mark - Init
- (id)initWithShow:(NSDictionary*)showDict;
{
    self = [self initWithNibName:@"QSCommentListViewController" bundle:nil];
    if (self) {
        __weak QSCommentListViewController* weakSelf = self;
        self.showDict = showDict;
        self.delegateObj = [[QSCommentListTableViewDelegateObj alloc] init];
        self.delegateObj.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
            return [SHARE_NW_ENGINE getCommentsOfShow:weakSelf.showDict page:page onSucceed:succeedBlock onError:errorBlock];
        };
        [self.delegateObj fetchDataOfPage:1];
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self.delegateObj bindWithTableView:self.tableView];
    self.delegateObj.delegate = self;
    self.title = @"评论";
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - QSCommentListTableViewDelegateObj
- (void)didClickComment:(NSDictionary*)commemntDict atIndex:(int)index
{
    
}

@end

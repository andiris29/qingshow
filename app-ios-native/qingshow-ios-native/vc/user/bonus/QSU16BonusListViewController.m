//
//  QSU16BonusListViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/8/31.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU16BonusListViewController.h"
#import "QSU15BonusListTableViewCell.h"
#import "QSPeopleUtil.h"
#import "QSBonusUtil.h"
#import "QSNetworkEngine+ShowService.h"
#import "QSNetworkKit.h"
#import "QSS10ItemDetailViewController.h"
#import "UIViewController+QSExtension.h"
#import "QSBonusTableViewProvider.h"

#define QSU16CELLID @"QSU16TableViewCellId"
@interface QSU16BonusListViewController () <QSBonusTableViewProviderDelegate>

@property (strong,nonatomic)NSArray *listArray;

@property (strong, nonatomic) UITableView* tableView;
@property (strong, nonatomic) QSBonusTableViewProvider* provider;

@end

@implementation QSU16BonusListViewController

- (instancetype)init {
    self = [super init];
    if (self) {

    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = @"收益明细";
    UITableView *tableView = [[UITableView alloc]initWithFrame:self.view.bounds style:UITableViewStylePlain];
    self.tableView = tableView;
    tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:tableView];
    
    self.provider = [[QSBonusTableViewProvider alloc] init];
    [self.provider bindWithTableView:self.tableView];
    self.provider.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page) {
        return [SHARE_NW_ENGINE queryOwnedBonusPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.provider.delegate = self;
    [self.provider reloadData];
}
- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    self.tableView.frame = self.view.bounds;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)provider:(QSBonusTableViewProvider*)provider didTapBonus:(NSDictionary*)bonusDict {
    NSNumber* t = [QSBonusUtil getType:bonusDict];
    if (t && t.intValue == 2) {
        return;
    }
    NSString *itemId = [QSBonusUtil getTradeItemId:bonusDict];

    __weak QSU16BonusListViewController *weakSelf = self;
    [SHARE_NW_ENGINE getItemWithId:itemId onSucceed:^(NSDictionary *itemDic, NSDictionary *metadata) {
        if (itemDic) {
            QSS10ItemDetailViewController *vc = [[QSS10ItemDetailViewController alloc] initWithItem:itemDic];
            [weakSelf.navigationController pushViewController:vc animated:YES];
        }
    } onError:^(NSError *error) {
        [self handleError:error];
    }];
}

- (void)handleNetworkError:(NSError *)error {
    [self handleError:error];
}
@end

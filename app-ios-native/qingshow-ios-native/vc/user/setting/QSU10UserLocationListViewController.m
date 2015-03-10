//
//  QSU10UserLocationListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU10UserLocationListViewController.h"
#import "QSUserLocationTableViewCell.h"

@interface QSU10UserLocationListViewController ()

@end

@implementation QSU10UserLocationListViewController

#pragma mark - Init
- (instancetype)init
{
    self = [super initWithNibName:@"QSU10UserLocationListViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self.tableView registerNib:[UINib nibWithNibName:@"QSUserLocationTableViewCell" bundle:nil] forCellReuseIdentifier:QSUserLocationTableViewCellIdentifier];
    self.title = @"收获地址管理";

#warning TODO 新增地址
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UITableView Datasource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 5;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSUserLocationTableViewCell* cell = [self.tableView dequeueReusableCellWithIdentifier:QSUserLocationTableViewCellIdentifier forIndexPath:indexPath];
#warning TODO binding
    NSDictionary* testDict = @{ @"name" : @"黄小仙",
                                @"phone" : @"13332223334",
                                @"privince" : @"上海",
                                @"address" : @"上海市普陀区中山北路号楼层",
                                @"default" : @""
                                };
    [cell bindWithDict:testDict];
    return cell;
}

#pragma mark - UITableView Delegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return QSUserLocationTableViewCellHeight;
}

@end

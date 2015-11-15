//
//  QSU02UserChangeDressEffectViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/30.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU02UserChangeDressEffectViewController.h"
#import "QSUserManager.h"
#import "QSNetworkKit.h"
#import "QSPeopleUtil.h"


#define PAGE_ID @"修改穿衣效果"
@interface QSU02UserChangeDressEffectViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (strong, nonatomic) UITableView* tableView;
@property (strong, nonatomic) NSArray* dataArray;
@property (strong, nonatomic) NSMutableArray* selectedArray;

@end

@implementation QSU02UserChangeDressEffectViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = @"修改穿衣效果";
    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    self.navigationItem.leftBarButtonItem = backItem;
    
    [self creatTableView];
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    
    [MobClick beginLogPageView:PAGE_ID];
    
}
- (void)creatTableView
{
    self.selectedArray = [[QSPeopleUtil getExpectations:[QSUserManager shareUserManager].userInfo] mutableCopy];
    self.dataArray = [NSMutableArray arrayWithObjects:@"显瘦",@"显高",@"显身材",@"遮臀部",@"遮肚腩",@"遮手臂", nil];
    _tableView = [[UITableView alloc]initWithFrame:self.view.bounds style:UITableViewStylePlain];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    [self.view addSubview:_tableView];
    
    
}
#pragma mark - tableviewDelegate
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.dataArray.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"effectCell"];
    cell.textLabel.font = NEWFONT;
    cell.textLabel.text = self.dataArray[indexPath.row];
    if ([self.selectedArray containsObject:@(indexPath.row)]) {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
    } else {
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
    
    return cell;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    UITableViewCell* cell = [tableView cellForRowAtIndexPath:indexPath];
    NSNumber* n = @(indexPath.row);
    if ([self.selectedArray containsObject:n]) {
        [self.selectedArray removeObject:n];
        cell.accessoryType = UITableViewCellAccessoryNone;
    } else {
        [self.selectedArray addObject:n];
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
    }
    [self updateExpectation];
}
- (void)updateExpectation {
    [SHARE_NW_ENGINE updatePeople:@{@"expectations" : self.selectedArray} onSuccess:nil onError:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end

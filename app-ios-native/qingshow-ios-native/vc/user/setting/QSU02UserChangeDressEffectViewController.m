//
//  QSU02UserChangeDressEffectViewController.m
//  qingshow-ios-native
//
//  Created by mhy on 15/5/30.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU02UserChangeDressEffectViewController.h"

#define PAGE_ID @"修改穿衣效果"
@interface QSU02UserChangeDressEffectViewController ()<UITableViewDelegate,UITableViewDataSource>
{
    NSMutableArray *_effectArray;
    UITableView *_tableView;
}
@end

@implementation QSU02UserChangeDressEffectViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = @"修改穿衣效果";
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
    _effectArray = [NSMutableArray arrayWithObjects:@"显瘦",@"显高",@"显身材",@"遮臀部",@"遮肚腩",@"遮手臂", nil];
    _tableView = [[UITableView alloc]initWithFrame:self.view.bounds style:UITableViewStylePlain];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    [self.view addSubview:_tableView];
    
}
#pragma mark - tableviewDelegate
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _effectArray.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"effectCell"];
    cell.textLabel.font = NEWFONT;
    cell.textLabel.text = _effectArray[indexPath.row];
    
    
    return cell;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
#warning expected upload data to Server and refresh tableView
   NSLog(@"%@", _tableView.visibleCells[indexPath.row]);
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

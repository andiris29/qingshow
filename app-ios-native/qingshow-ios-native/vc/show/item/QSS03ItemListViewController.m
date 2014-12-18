//
//  QSS03ItemListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/15/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSS03ItemListViewController.h"
#import "QSItemListHeaderView.h"
#import "QSItemListTableViewCell.h"
#import "QSShowUtil.h"
#import "QSNetworkKit.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSS03ItemShopDetailViewController.h"

@interface QSS03ItemListViewController ()

@property (strong, nonatomic) NSDictionary* showDict;

@end

@implementation QSS03ItemListViewController

#pragma mark - Init Method
- (id)initWithShow:(NSDictionary*)showDict
{
    self = [super initWithNibName:@"QSS03ItemListViewController" bundle:nil];
    if (self) {
        self.showDict = showDict;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    UIView* headerView = [QSItemListHeaderView generateView];
    self.tableView.tableHeaderView = headerView;
    [self.tableView registerNib:[UINib nibWithNibName:@"QSItemListTableViewCell" bundle:nil] forCellReuseIdentifier:QSItemListTableViewCellIdentifier];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.bgImageView setImageFromURL:[QSShowUtil getCoverUrl:self.showDict] placeHolderImage:[UIImage imageNamed:@"root_cell_placehold_image1"] animation:YES];
    
    [SHARE_NW_ENGINE queryShowDetail:self.showDict onSucceed:^(NSDictionary * dict) {
        self.showDict = dict;
        [self.tableView reloadData];
    } onError:^(NSError *error) {
        
    }];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction
- (IBAction)closeBtnPressed:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark - UITableView Datasource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [QSShowUtil getItems:self.showDict].count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSItemListTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:QSItemListTableViewCellIdentifier forIndexPath:indexPath];
    [cell bindWithItem:[QSShowUtil getItemFromShow:self.showDict AtIndex:indexPath.row]];
    return cell;
}
#pragma mark - UITableView Delegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    UIViewController* vc = [[QSS03ItemShopDetailViewController alloc] initWithShow:self.showDict currentItemIndex:indexPath.row];
    [self.navigationController pushViewController:vc animated:YES];
    
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 46.f;
}

@end

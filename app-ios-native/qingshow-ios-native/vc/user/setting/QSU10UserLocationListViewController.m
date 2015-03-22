//
//  QSU10UserLocationListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU10UserLocationListViewController.h"
#import "QSU11LocationEditingViewController.h"
#import "QSUserManager.h"
#import "QSPeopleUtil.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"

@interface QSU10UserLocationListViewController ()

@property (strong, nonatomic) NSArray* locationArray;

@property (strong, nonatomic) MKNetworkOperation* removeOperation;
@end

@implementation QSU10UserLocationListViewController

#pragma mark - Init
- (instancetype)init
{
    self = [super initWithNibName:@"QSU10UserLocationListViewController" bundle:nil];
    if (self) {
        [self updateLocationList];
    }
    return self;
}

- (void)updateLocationList
{
    self.locationArray = [QSPeopleUtil getReceiverList:[QSUserManager shareUserManager].userInfo];
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self.tableView registerNib:[UINib nibWithNibName:@"QSUserLocationTableViewCell" bundle:nil] forCellReuseIdentifier:QSUserLocationTableViewCellIdentifier];
    self.title = @"收获地址管理";
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    
    UIBarButtonItem* item = [[UIBarButtonItem alloc] initWithTitle:@"新增地址" style:UIBarButtonItemStylePlain target:self action:@selector(newLocationBtnPressed)];
    item.tintColor = [UIColor colorWithRed:169.f/255.f green:26.f/255.f blue:78.f/255.f alpha:1.f];
    self.navigationItem.rightBarButtonItem = item;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [SHARE_NW_ENGINE getLoginUserOnSucced:^(NSDictionary *data, NSDictionary *metadata) {
        [self updateLocationList];
        [self.tableView reloadData];
    } onError:^(NSError *error) {
        [self showErrorHudWithError:error];
    }];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UITableView Datasource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.locationArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSUserLocationTableViewCell* cell = [self.tableView dequeueReusableCellWithIdentifier:QSUserLocationTableViewCellIdentifier forIndexPath:indexPath];
    cell.delegate = self;
    NSDictionary* dict = [self locationDictForIndexPath:indexPath];
    [cell bindWithDict:dict];
    return cell;
}

#pragma mark - UITableView Delegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return QSUserLocationTableViewCellHeight;
}

#pragma mark - QSUserLocationTableViewCellDelegate
- (void)didClickEditButtonOfCell:(QSUserLocationTableViewCell*)cell
{
    NSDictionary* dict = [self locationDictForCell:cell];
    UIViewController* vc = [[QSU11LocationEditingViewController alloc] initWithDict:dict];
    [self.navigationController pushViewController:vc animated:YES];
}
- (void)didClickDeleteButtonOfCell:(QSUserLocationTableViewCell*)cell
{
    if (self.removeOperation) {
        return;
    }
    NSIndexPath* indexPath = [self.tableView indexPathForCell:cell];
    NSDictionary* locationDict = [self locationDictForCell:cell];
    self.removeOperation =
    [SHARE_NW_ENGINE removeReceiver:locationDict onSuccess:^{
        self.removeOperation = nil;
        if ([self.locationArray isKindOfClass:[NSMutableArray class]]) {
            NSMutableArray* m = (NSMutableArray*)self.locationArray;
            [m removeObject:locationDict];
            [self.tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
        }
    } onError:^(NSError *error) {
        self.removeOperation = nil;
        [self showErrorHudWithError:error];
    }];
}
- (void)didClickSelectedIndicatorOfCell:(QSUserLocationTableViewCell*)cell
{
    UIViewController* vc = [[QSU11LocationEditingViewController alloc] initWithDict:[self locationDictForCell:cell]];
    [self.navigationController pushViewController:vc animated:YES];
}

#pragma mark - Private
- (void)newLocationBtnPressed
{
    UIViewController* vc = [[QSU11LocationEditingViewController alloc] initWithDict:nil];
    [self.navigationController pushViewController:vc animated:YES];
}
- (NSDictionary*)locationDictForIndexPath:(NSIndexPath*)indexPath
{
    NSInteger row = indexPath.row;
    if (row < self.locationArray.count) {
        return self.locationArray[row];
    } else {
        return nil;
    }
}

- (NSDictionary*)locationDictForCell:(UITableViewCell*)cell
{
    NSIndexPath* indexPath = [self.tableView indexPathForCell:cell];
    return [self locationDictForIndexPath:indexPath];
}
@end

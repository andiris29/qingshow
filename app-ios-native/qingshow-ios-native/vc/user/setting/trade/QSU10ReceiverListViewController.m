//
//  QSU10UserLocationListViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSU10ReceiverListViewController.h"
#import "QSU11ReceiverEditingViewController.h"
#import "QSUserManager.h"
#import "QSPeopleUtil.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "UIViewController+QSExtension.h"
#import "QSReceiverUtil.h"
#define PAGE_ID @"U10 - 收获地址一览"
#define w ([UIScreen mainScreen].bounds.size.width)

@interface QSU10ReceiverListViewController ()

@property (strong, nonatomic) NSArray* receiverArray;
@property (strong, nonatomic) NSDictionary* selectedRecevier;
@property (strong, nonatomic) MKNetworkOperation* removeOperation;
@property (strong, nonatomic) QSUserLocationTableViewCell* toRemoveCell;
@end

@implementation QSU10ReceiverListViewController

#pragma mark - Init
- (instancetype)init
{
    self = [super initWithNibName:@"QSU10ReceiverListViewController" bundle:nil];
    if (self) {
        [self updateLocationList];
    }
    return self;
}

- (void)updateLocationList
{
    self.receiverArray = [QSPeopleUtil getReceiverList:[QSUserManager shareUserManager].userInfo];
    self.selectedRecevier = [QSReceiverUtil getDefaultReceiver:self.receiverArray];
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self.tableView registerNib:[UINib nibWithNibName:@"QSUserLocationTableViewCell" bundle:nil] forCellReuseIdentifier:QSUserLocationTableViewCellIdentifier];
    self.title = @"收货地址管理";
    [self hideNaviBackBtnTitle];
    
    UIBarButtonItem* item = [[UIBarButtonItem alloc] initWithTitle:@"新增地址" style:UIBarButtonItemStylePlain target:self action:@selector(newLocationBtnPressed)];
    item.tintColor = [UIColor colorWithRed:40.0f/255.f green:45.0f/255.f blue:90.0f/255.f alpha:1.f];
    self.navigationItem.rightBarButtonItem = item;
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSFontAttributeName:NAVNEWFONT,
       NSForegroundColorAttributeName:[UIColor blackColor]}];

    QSBackBarItem *backItem = [[QSBackBarItem alloc]initWithActionVC:self];
    self.navigationItem.leftBarButtonItem = backItem;

}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [SHARE_NW_ENGINE getLoginUserOnSucced:^(NSDictionary *data, NSDictionary *metadata) {
        [self updateLocationList];
        [self.tableView reloadData];
    } onError:^(NSError *error) {
        [self handleError:error];
    }];
    [MobClick beginLogPageView:PAGE_ID];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UITableView Datasource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.receiverArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSUserLocationTableViewCell* cell = [self.tableView dequeueReusableCellWithIdentifier:QSUserLocationTableViewCellIdentifier forIndexPath:indexPath];
    cell.delegate = self;
    NSDictionary* dict = [self receiverDictForIndexPath:indexPath];
    [cell bindWithDict:dict];
    cell.isSelectedReceiver = self.selectedRecevier == dict;
    cell.contentView.transform = CGAffineTransformMakeScale(w/320, w/320);
    return cell;
}

#pragma mark - UITableView Delegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [QSUserLocationTableViewCell getHeightWithDict:[self receiverDictForIndexPath:indexPath]];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSDictionary* receiver = [self receiverDictForIndexPath:indexPath];
    self.selectedRecevier = receiver;
    [self.tableView reloadData];
    [SHARE_NW_ENGINE setDefaultReceiver:self.selectedRecevier onSuccess:^{
        [QSReceiverUtil setDefaultReceiver:receiver ofReceivers:self.receiverArray];
    } onError:^(NSError *error) {
        
    }];
    
    if ([self.delegate respondsToSelector:@selector(receiverListVc:didSelectReceiver:)]) {
        [self.delegate receiverListVc:self didSelectReceiver:receiver];
    }
}

#pragma mark - QSUserLocationTableViewCellDelegate
- (void)didClickEditButtonOfCell:(QSUserLocationTableViewCell*)cell
{
    NSDictionary* dict = [self receiverDictForCell:cell];
    UIViewController* vc = [[QSU11ReceiverEditingViewController alloc] initWithDict:dict];
    UIBarButtonItem *backItem = [[UIBarButtonItem alloc]initWithImage:[UIImage imageNamed:@"nav_btn_back"] style:UIBarButtonItemStyleDone target:self action:@selector(backAction)];
    vc.navigationItem.leftBarButtonItem = backItem;
    [self.navigationController pushViewController:vc animated:YES];
}
- (void)backAction
{
    [self.navigationController popViewControllerAnimated:YES];
}
- (void)didClickDeleteButtonOfCell:(QSUserLocationTableViewCell*)cell
{
    if (self.removeOperation) {
        return;
    }
    self.toRemoveCell = cell;
    UIAlertView* v =  [[UIAlertView alloc] initWithTitle:@"确认删除" message:nil delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确定", nil];
    [v show];
}


#pragma mark - Private
- (void)newLocationBtnPressed
{
    UIViewController* vc = [[QSU11ReceiverEditingViewController alloc] initWithDict:nil];
    UIBarButtonItem *backItem = [[UIBarButtonItem alloc]initWithImage:[UIImage imageNamed:@"nav_btn_back"] style:UIBarButtonItemStyleDone target:self action:@selector(backAction)];
    vc.navigationItem.leftBarButtonItem = backItem;
    [self.navigationController pushViewController:vc animated:YES];
}
- (NSDictionary*)receiverDictForIndexPath:(NSIndexPath*)indexPath
{
    NSInteger row = indexPath.row;
    if (row < self.receiverArray.count) {
        return self.receiverArray[row];
    } else {
        return nil;
    }
}

- (NSDictionary*)receiverDictForCell:(UITableViewCell*)cell
{
    NSIndexPath* indexPath = [self.tableView indexPathForRowAtPoint:cell.center];
    return [self receiverDictForIndexPath:indexPath];
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    
    UITableViewCell* cell = self.toRemoveCell;
    self.toRemoveCell = nil;
    NSIndexPath* indexPath = [self.tableView indexPathForRowAtPoint:cell.center];;
    NSDictionary* locationDict = [self receiverDictForCell:cell];
    
    if (buttonIndex != alertView.cancelButtonIndex) {
        self.removeOperation =
        [SHARE_NW_ENGINE removeReceiver:locationDict onSuccess:^{
            self.removeOperation = nil;
            if ([self.receiverArray isKindOfClass:[NSMutableArray class]]) {
                NSMutableArray* m = (NSMutableArray*)self.receiverArray;
                [m removeObject:locationDict];
                [self.tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
            }
        } onError:^(NSError *error) {
            self.removeOperation = nil;
            [self handleError:error];
        }];
    }
}
@end

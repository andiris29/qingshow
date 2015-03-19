//
//  QSCreateTradeViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/15/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS11CreateTradeViewController.h"
#import "QSCreateTradeTableViewCellBase.h"
#import "QSTaobaoInfoUtil.h"
#import "QSItemUtil.h"
#import "QSNetworkKit.h"
#import "QSUserManager.h"
#import "QSReceiverUtil.h"
#import "QSPeopleUtil.h"
#import "UIViewController+ShowHud.h"

@interface QSS11CreateTradeViewController ()

@property (strong, nonatomic) NSDictionary* itemDict;


@property (strong, nonatomic) NSArray* cellGroupArray;


@property (strong, nonatomic) NSArray* itemInfoCellArray;

@property (strong, nonatomic) NSArray* receiverInfoCellArray;

@property (strong, nonatomic) NSArray* payWayCellArray;

@property (strong, nonatomic) NSArray* totalPriceCellArray;

@property (strong, nonatomic) NSArray* headerArray;
@end

@implementation QSS11CreateTradeViewController

#pragma mark - Init
- (id)initWithDict:(NSDictionary*)dict
{
    self = [super initWithNibName:@"QSS11CreateTradeViewController" bundle:nil];
    if (self) {
        self.itemDict = dict;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.

    [self configCellArray];
    [self configView];
    
    [self updateAllCell];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark - Private Method
- (void)updateAllCell
{
    [self.cellGroupArray enumerateObjectsUsingBlock:^(NSArray* cellArray, NSUInteger idx, BOOL *stop) {
        [cellArray enumerateObjectsUsingBlock:^(QSCreateTradeTableViewCellBase* cell, NSUInteger idx, BOOL *stop) {
            [cell bindWithDict:self.itemDict];
        }];
    }];
}
- (void)configView
{
    self.title = @"订单确认";
    NSMutableArray* headerArray = [@[] mutableCopy];
    for (int i = 0; i < self.cellGroupArray.count; i++) {
        UIView* view = [[UIView alloc] init];
        view.backgroundColor = [UIColor colorWithRed:204.f/255.f green:204.f/255.f blue:204.f/255.f alpha:1.f];
        [headerArray addObject:view];
    }
    
    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {
        self.tableView.separatorInset = UIEdgeInsetsZero;
    }
    if ([self.tableView respondsToSelector:@selector(setLayoutMargins:)]) {
        self.tableView.layoutMargins = UIEdgeInsetsZero;
    }
}

- (void)configCellArray
{

    self.itemInfoCellArray = @[self.itemInfoTitleCell,
                               self.itemInfoColorCell,
                               self.itemInfoSizeCell,
                               self.itemInfoQuantityCell];
    
    self.receiverInfoCellArray = @[self.receiverInfoTitleCell,
                                   self.receiverInfoNameCell,
                                   self.receiverInfoPhoneCell,
                                   self.receiverInfoLocationCell,
                                   self.receiverInfoDetailLocationCell];
    
    self.payWayCellArray = @[self.payInfoTitleCell,
                             self.payInfoWechatCell,
                             self.payInfoAllipayCell,
                             self.payInfoBandCell];
    self.totalPriceCellArray = @[self.totalCell];
    
    self.cellGroupArray =
    @[
      self.itemInfoCellArray,
      self.receiverInfoCellArray,
      self.payWayCellArray,
      self.totalPriceCellArray
      ];
}

- (QSCreateTradeTableViewCellBase*)cellForIndexPath:(NSIndexPath*)indexPath
{
    QSCreateTradeTableViewCellBase* cell = nil;
    
    NSArray* cellArray = self.cellGroupArray[indexPath.section];
    cell = cellArray[indexPath.row];
    
    return cell;
}

- (void)hidekeyboard
{
    for (NSArray* a in self.cellGroupArray) {
        for (QSCreateTradeTableViewCellBase* c in a) {
            [c hideKeyboard];
        }
    }
}

#pragma mark - UITableView DataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    NSArray* cellArray = self.cellGroupArray[section];
    return cellArray.count;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath;
{
    QSCreateTradeTableViewCellBase* cell = [self cellForIndexPath:indexPath];
    [cell bindWithDict:self.itemDict];
    return cell;
}
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return self.cellGroupArray.count;
}

#pragma mark - UITableView Delegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    QSCreateTradeTableViewCellBase* cell = [self cellForIndexPath:indexPath];
    return [cell getHeightWithDict:self.itemDict];
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    return self.headerArray[section];
}
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 5.f;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

#pragma mark - UIScrollView Delegate
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    [self hidekeyboard];
}

- (IBAction)submitButtonPressed:(id)sender {
    NSDictionary* taobaoInfo = [QSItemUtil getTaobaoInfo:self.itemDict];
    NSArray* skus = [QSTaobaoInfoUtil getSkusArray:taobaoInfo];
    NSDictionary* sku = skus[0];
    NSArray* r = [QSPeopleUtil getReceiverList:[QSUserManager shareUserManager].userInfo];
    NSDictionary* rec = r[0];
    [QSReceiverUtil getUuid:rec];
    [SHARE_NW_ENGINE createTradeTotalFee:1
                                quantity:1
                                   price:1
                                    item:self.itemDict
                                     sku:sku[@"sku_id"]
                            receiverUuid:[QSReceiverUtil getUuid:rec]
                               onSucceed:^
     {
         [self showTextHud:@"success"];
     }
                                 onError:^(NSError *error)
     {
         [self showErrorHudWithError:error];
     }];
}
@end

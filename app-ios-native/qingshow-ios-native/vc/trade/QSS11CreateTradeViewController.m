//
//  QSCreateTradeViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/15/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS11CreateTradeViewController.h"
#import "QSCreateTradeTableViewCellBase.h"
#import "QSCreateTradePayInfoSelectCell.h"
#import "QSCreateTradeSkuPropertyCell.h"
#import "QSItemUtil.h"
#import "QSReceiverUtil.h"
#import "QSPeopleUtil.h"
#import "QSTradeUtil.h"
#import "QSEntityUtil.h"
#import "QSOrderUtil.h"

#import "QSNetworkKit.h"
#import "QSUserManager.h"

#import "UIViewController+ShowHud.h"
#import "UIViewController+QSExtension.h"
#import "QSPaymentService.h"
#import "QSU09OrderListViewController.h"
#define PAGE_ID @"S11 - 交易生成"

@interface QSS11CreateTradeViewController ()

@property (strong, nonatomic) NSDictionary* tradeDict;


@property (strong, nonatomic) NSArray* cellGroupArray;

@property (strong, nonatomic) NSArray* itemInfoCellArray;

@property (strong, nonatomic) NSArray* receiverInfoCellArray;

@property (strong, nonatomic) NSArray* payWayCellArray;

@property (strong, nonatomic) NSArray* totalPriceCellArray;

@property (strong, nonatomic) NSArray* headerArray;

@property (strong, nonatomic) NSDictionary* selectedReceiver;

@property (strong, nonatomic) MKNetworkOperation* userUpdateOp;
@property (strong, nonatomic) MKNetworkOperation* prepayOp;
@property (strong, nonatomic) MKNetworkOperation* saveReceiverOp;

@property (strong, nonatomic) QSLocationPickerProvider* locationProvider;

@property (assign, nonatomic) BOOL isShowKeyboard;

@end

@implementation QSS11CreateTradeViewController

#pragma mark - Init
- (id)initWithDict:(NSDictionary*)tradeDict
{
    self = [super initWithNibName:@"QSS11CreateTradeViewController" bundle:nil];
    if (self) {
        self.tradeDict = tradeDict;
        self.isShowKeyboard = NO;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.locationProvider = [[QSLocationPickerProvider alloc] initWithPicker:self.locationPicker];
    [self configCellArray];
    [self configView];
    [self updateAllCell];

    NSDictionary* orderDict = [QSTradeUtil getFirstOrder:self.tradeDict];
    NSNumber* price = nil;
    if ([QSOrderUtil getActualPrice:orderDict]) {
        price = [QSOrderUtil getActualPrice:orderDict];
    } else {
        price = [QSOrderUtil getExpectedPrice:orderDict];
    }
    NSNumber* quantity = [QSOrderUtil getQuantity:orderDict];
    NSNumber* totalPrice = @(price.doubleValue * quantity.integerValue);
    [self.totalCell updateWithPrice:[NSString stringWithFormat:@"%.2f", totalPrice.doubleValue]];
    
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSFontAttributeName:NAVNEWFONT,
       NSForegroundColorAttributeName:[UIColor blackColor]}];
}
- (void)receiverConfig
{
    NSDictionary* userInfo = [QSUserManager shareUserManager].userInfo;
    NSArray* receivers = [QSPeopleUtil getReceiverList:userInfo];
    NSDictionary* defaultReceiver = [QSReceiverUtil getDefaultReceiver:receivers];
    [self bindWithReceiver:defaultReceiver];
}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = NO;
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillShow:)
                                                 name:UIKeyboardWillShowNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillHide:)
                                                 name:UIKeyboardWillHideNotification
                                               object:nil];
    [MobClick beginLogPageView:PAGE_ID];
//    [self.tableView reloadData];
    [self updateAllCell];
    [self receiverConfig];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [MobClick endLogPageView:PAGE_ID];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self hidekeyboardAndPicker];
}
#pragma mark - Private Method
- (void)updateAllCell
{
    [self.cellGroupArray enumerateObjectsUsingBlock:^(NSArray* cellArray, NSUInteger idx, BOOL *stop) {
        [cellArray enumerateObjectsUsingBlock:^(QSCreateTradeTableViewCellBase* cell, NSUInteger idx, BOOL *stop) {
            [cell bindWithDict:self.tradeDict];
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
    self.tableView.backgroundColor = [UIColor colorWithRed:204.f/255.f green:204.f/255.f blue:204.f/255.f alpha:1.f];
    self.view.backgroundColor = [UIColor colorWithRed:204.f/255.f green:204.f/255.f blue:204.f/255.f alpha:1.f];
    
    if ([self.tableView respondsToSelector:@selector(setSeparatorInset:)]) {
        self.tableView.separatorInset = UIEdgeInsetsZero;
    }
    if ([self.tableView respondsToSelector:@selector(setLayoutMargins:)]) {
        self.tableView.layoutMargins = UIEdgeInsetsZero;
    }
    
    [self hideNaviBackBtnTitle];
}

- (void)configCellArray
{
    NSMutableArray* array = [@[] mutableCopy];
    [array addObject:self.itemInfoTitleCell];
    NSDictionary* orderDict = [QSTradeUtil getFirstOrder:self.tradeDict];
    NSArray* propArray = [QSOrderUtil getSkuProperties:orderDict];
    for (NSString* propStr in propArray) {
        QSCreateTradeSkuPropertyCell* propCell = [QSCreateTradeSkuPropertyCell generateCell];
        [propCell bindSkuProperty:propStr];
        [array addObject:propCell];
    }
    
    [array addObject:self.itemInfoQuantityCell];
    self.itemInfoCellArray = array;
    
    self.receiverInfoCellArray = @[self.receiverInfoTitleCell,
                                   self.receiverInfoNameCell,
                                   self.receiverInfoPhoneCell,
                                   self.receiverInfoLocationCell,
                                   self.receiverInfoDetailLocationCell];
    
    self.payWayCellArray = @[self.payInfoTitleCell,
                             self.payInfoWechatCell,
                             self.payInfoAlipayCell
//                             ,self.payInfoBankCell
                             ];
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

- (void)hidekeyboardAndPicker
{
    if (!self.isShowKeyboard && self.locationPicker.hidden) {
        return;
    }
    [self hideKeyboard];
    [self hidePicker];
    [self configContentInset:0];
}

- (void)hideKeyboard
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
    [cell bindWithDict:self.tradeDict];

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
    return [cell getHeightWithDict:self.tradeDict];
    
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    return self.headerArray[section];
}
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    if (section == 0) {
        return 0.f;
    }
    else
    {
    return 5.f;
    }
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    UITableViewCell* cell = [tableView cellForRowAtIndexPath:indexPath];
    if ([self.payWayCellArray indexOfObject:cell] != NSNotFound) {
        if (cell != self.payInfoTitleCell) {
            for (QSCreateTradePayInfoSelectCell* c in self.payWayCellArray) {
                if (c == self.payInfoTitleCell) {
                    continue;
                }
                c.isSelect = c == cell;
            }
        }
    }
    if (cell == self.receiverInfoLocationCell) {
        [self showPicker];
    }

    [self hidekeyboardAndPicker];

    
}

#pragma mark - UIScrollView Delegate
//- (void)scrollViewDidScroll:(UIScrollView *)scrollView
//{
//    [self hidekeyboardAndPicker];
//}

- (IBAction)submitButtonPressed:(id)sender {
    if (![self checkFullInfo]) {
        [self showErrorHudWithText:@"请填写完整信息"];
        return;
    }
    if (self.userUpdateOp) {
        return;
    }
    

    [self saveReceiverAndCreateTrade];
    
}

- (void)saveReceiverAndCreateTrade {
    if ([self checkNewReceiver]) {
        if (self.saveReceiverOp ) {
            return;
        }
        self.saveReceiverOp =
        [SHARE_NW_ENGINE saveReceiver:nil
                                 name:self.receiverInfoNameCell.getInputData
                                phone:self.receiverInfoPhoneCell.getInputData
                             province:self.receiverInfoLocationCell.getInputData
                              address:self.receiverInfoDetailLocationCell.getInputData
                            isDefault:YES
                            onSuccess:^(NSDictionary *people, NSString *uuid, NSDictionary *metadata) {
                                [self submitOrderWithReceiver:uuid];
                                self.saveReceiverOp = nil;
                            } onError:^(NSError *error) {
                                [self showErrorHudWithError:error];
                                self.saveReceiverOp = nil;
                            }];
    } else {
        [self submitOrderWithReceiver:[QSReceiverUtil getUuid:self.selectedReceiver]];
    }
}

- (BOOL)checkNewReceiver
{
    if (!self.selectedReceiver) {
        return YES;
    }
    return ![[QSReceiverUtil getName:self.selectedReceiver] isEqualToString:self.receiverInfoNameCell.textField.text] ||
            ![[QSReceiverUtil getPhone:self.selectedReceiver] isEqualToString:self.receiverInfoPhoneCell.textField.text] ||
            ![[QSReceiverUtil getProvince:self.selectedReceiver] isEqualToString:self.receiverInfoLocationCell.label.text]||
            ![[QSReceiverUtil getAddress:self.selectedReceiver] isEqualToString:self.receiverInfoDetailLocationCell.textField.text];
}
- (BOOL)checkFullInfo
{
    
    if (!self.receiverInfoNameCell.getInputData ||
        !self.receiverInfoPhoneCell.getInputData ||
        !self.receiverInfoLocationCell.getInputData ||
        !self.receiverInfoDetailLocationCell.getInputData) {
        return NO;
    }
    
    if (!self.payInfoAlipayCell.isSelect && !self.payInfoWechatCell.isSelect) {
        return NO;
    }
    return YES;
}


- (void)submitOrderWithReceiver:(NSString*)uuid
{
    if (self.prepayOp) {
        return;
    }
    
    PaymentType paymentType = 0;
    if (self.payInfoAlipayCell.isSelect) {
        paymentType = PaymentTypeAlipay;
    } else if (self.payInfoWechatCell.isSelect) {
        paymentType = PaymentTypeWechat;
    }
    
    NSDictionary* orderDict = [QSTradeUtil getFirstOrder:self.tradeDict];
    
    NSNumber* price = [QSOrderUtil getActualPrice:orderDict];
    NSNumber* quantity = [QSOrderUtil getQuantity:orderDict];
    NSNumber* totalPrice = @(quantity.doubleValue * price.doubleValue);


    [self.totalCell updateWithPrice:totalPrice.stringValue];
    
    __weak QSS11CreateTradeViewController* weakSelf = self;
    self.prepayOp = [SHARE_NW_ENGINE prepayTrade:self.tradeDict type:paymentType receiverUuid:uuid onSucceed:^(NSDictionary *tradeDict) {
        [SHARE_PAYMENT_SERVICE payForTrade:tradeDict
                                 onSuccess:^{
                                     UIAlertView* alertView = [[UIAlertView alloc] initWithTitle:@"支付成功" message:nil delegate:weakSelf cancelButtonTitle:nil otherButtonTitles:@"继续逛逛", @"查看订单", nil];
                                     [alertView show];}
                                 onError:^(NSError *error) {
                                     [weakSelf showErrorHudWithText:@"支付失败"];
                                 }];
                                     self.prepayOp = nil;

    } onError:^(NSError *error) {
        [self handleError:error];
        self.prepayOp = nil;
    }];
}

#pragma mark - QSU10ReceiverListViewControllerDelegate
- (IBAction)receiverManageBtnPressed:(id)sender {
    QSU10ReceiverListViewController* vc = [[QSU10ReceiverListViewController alloc] init];
    vc.delegate = self;
    UIBarButtonItem *backItem = [[UIBarButtonItem alloc]initWithImage:[UIImage imageNamed:@"nav_btn_back"] style:UIBarButtonItemStyleDone target:self action:@selector(backAction)];
    vc.navigationItem.leftBarButtonItem = backItem;
    [self.navigationController pushViewController:vc animated:YES];
}
- (void)backAction
{
     [self.tableView reloadData];
    [self.navigationController popViewControllerAnimated:YES];
    
}
- (void)receiverListVc:(QSU10ReceiverListViewController*)vc didSelectReceiver:(NSDictionary*)receiver
{
    self.selectedReceiver = receiver;
    [self bindWithReceiver:self.selectedReceiver];
}
- (void)bindWithReceiver:(NSDictionary*)r
{
    self.receiverInfoNameCell.textField.text = [QSReceiverUtil getName:r];
    self.receiverInfoPhoneCell.textField.text = [QSReceiverUtil getPhone:r];
    self.receiverInfoDetailLocationCell.textField.text = [QSReceiverUtil getAddress:r];
    self.receiverInfoLocationCell.label.text = [QSReceiverUtil getProvince:r];

}

#pragma mark - Location Picker
- (void)showPicker
{
    if (!self.locationPicker.hidden) {
        return;
    }
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromTop;
    [self.locationPicker.layer addAnimation:tran forKey:@"show"];
    self.locationPicker.hidden = NO;
    [self configContentInset:100];
    [self hideKeyboard];
}
- (void)hidePicker
{
    if (self.locationPicker.hidden) {
        return;
    }
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromBottom;
    [self.locationPicker.layer addAnimation:tran forKey:@"hide"];
    self.locationPicker.hidden = YES;

}

#pragma mark - QSLocationPickerProviderDelegate
- (void)locationValueChange:(QSLocationPickerProvider*)provider
{
    self.receiverInfoLocationCell.label.text = [provider getSelectedValue];
}

#pragma mark - Keyboard
- (void)configContentInset:(float)height
{

    self.tableView.contentInset = UIEdgeInsetsMake(0, 0, height, 0);
    [self scrollToBottom:height];
}

- (void)keyboardWillShow:(NSNotification *)notif {
    self.isShowKeyboard = YES;
    [self configContentInset:300.f];
}

- (void)keyboardWillHide:(NSNotification *)notif {
    self.isShowKeyboard = NO;
}
- (void)scrollToBottom:(float)keyboardHeight
{
    [self.tableView setContentOffset:CGPointMake(0, keyboardHeight) animated:YES];
}

#pragma mark - UIAlertView Delegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    QSU09OrderListViewController *vc = [[QSU09OrderListViewController alloc]init];
    [vc.tableView reloadData];
    [self.navigationController pushViewController:vc animated:YES];
    
    //[self.navigationController popViewControllerAnimated:YES];
//    if (buttonIndex == 0) {
//        //继续逛逛
//        [self.navigationController popViewControllerAnimated:YES];
//    } else if (buttonIndex == 1) {
//        //查看订单
//        UIViewController* vc = [[QSU09OrderListViewController alloc] init];
//        [self.navigationController pushViewController:vc animated:YES];
//    }
}

@end

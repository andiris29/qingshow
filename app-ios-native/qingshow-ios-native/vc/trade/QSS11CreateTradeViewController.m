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
#import "QSTaobaoInfoUtil.h"
#import "QSItemUtil.h"
#import "QSReceiverUtil.h"
#import "QSPeopleUtil.h"
#import "QSTradeUtil.h"
#import "QSCommonUtil.h"
#import "QSOrderUtil.h"

#import "QSNetworkKit.h"
#import "QSUserManager.h"

#import "UIViewController+ShowHud.h"
#import "UIViewController+QSExtension.h"
#import "QSPaymentService.h"

@interface QSS11CreateTradeViewController ()

@property (strong, nonatomic) NSDictionary* itemDict;


@property (strong, nonatomic) NSArray* cellGroupArray;


@property (strong, nonatomic) NSArray* itemInfoCellArray;

@property (strong, nonatomic) NSArray* receiverInfoCellArray;

@property (strong, nonatomic) NSArray* payWayCellArray;

@property (strong, nonatomic) NSArray* totalPriceCellArray;

@property (strong, nonatomic) NSArray* headerArray;

@property (strong, nonatomic) NSDictionary* selectedReceiver;

@property (strong, nonatomic) MKNetworkOperation* createTradeOp;
@property (strong, nonatomic) MKNetworkOperation* saveReceiverOp;

@property (strong, nonatomic) QSLocationPickerProvider* locationProvider;

@property (assign, nonatomic) BOOL isShowKeyboard;

@end

@implementation QSS11CreateTradeViewController

#pragma mark - Init
- (id)initWithDict:(NSDictionary*)dict
{
    self = [super initWithNibName:@"QSS11CreateTradeViewController" bundle:nil];
    if (self) {
        self.itemDict = dict;
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
    [self receiverConfig];
    
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
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
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
    
    NSDictionary* taobaoInfo = [QSItemUtil getTaobaoInfo:self.itemDict];
    if ([QSTaobaoInfoUtil hasColorSku:taobaoInfo]) {
        [array addObject:self.itemInfoColorCell];
    } else {
        self.itemInfoColorCell = nil;
    }
    
    if ([QSTaobaoInfoUtil hasSizeSku:taobaoInfo]) {
        [array addObject:self.itemInfoSizeCell];
    } else {
        self.itemInfoSizeCell = nil;
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
    cell.delegate = self;
//    if ([self.receiverInfoCellArray indexOfObject:cell] != NSNotFound) {
//        [cell bindWithDict:self.selectedReceiver];
//    } else {
    [cell bindWithDict:self.itemDict];
//    }

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
    if (![self.receiverInfoCellArray containsObject:cell] || cell == self.receiverInfoTitleCell) {
        [self hidekeyboardAndPicker];
    }
    
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
    if ([self checkNewReceiver]) {
        if (self.saveReceiverOp ) {
            return;
        }
        self.saveReceiverOp =
        [SHARE_NW_ENGINE saveReceiver:nil
                                 name:self.receiverInfoNameCell.textField.text
                                phone:self.receiverInfoPhoneCell.textField.text
                             province:self.receiverInfoLocationCell.label.text
                              address:self.receiverInfoDetailLocationCell.textField.text
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
#warning TODO 检查input是否完整
    return YES;
}


- (void)submitOrderWithReceiver:(NSString*)uuid
{
    if (self.createTradeOp) {
        return;
    }
    NSDictionary* taobaoInfo = [QSItemUtil getTaobaoInfo:self.itemDict];
    NSString* sizeSku = [self.itemInfoSizeCell getInputData];
    NSString* colorSku = [self.itemInfoColorCell getInputData];
    NSNumber* quantity = [self.itemInfoQuantityCell getInputData];
    
    PaymentType paymentType = 0;
    if (self.payInfoAlipayCell.isSelect) {
        paymentType = PaymentTypeAlipay;
    } else if (self.payInfoWechatCell.isSelect) {
        paymentType = PaymentTypeWechat;
    }
    
    NSNumber* sku = [QSTaobaoInfoUtil getSkuOfSize:sizeSku color:colorSku taobaoInfo:taobaoInfo];
    NSNumber* price = [QSTaobaoInfoUtil getPromoPriceNumOfSize:sizeSku color:colorSku taobaoInfo:taobaoInfo quanitty:@1];
    
    NSNumber* totalPrice = [QSTaobaoInfoUtil getPromoPriceNumOfSize:sizeSku color:colorSku taobaoInfo:taobaoInfo quanitty:[self.itemInfoQuantityCell getInputData]];
    
    NSString* totalPriceStr = [QSTaobaoInfoUtil getPromoPriceOfSize:sizeSku color:colorSku taobaoInfo:taobaoInfo quanitty:[self.itemInfoQuantityCell getInputData]];
    [self.totalCell updateWithPrice:totalPriceStr];
    
    totalPrice = @(0.1f);
    quantity = @1;
    price = @(0.1f);
    self.createTradeOp =
    [SHARE_NW_ENGINE createTradeTotalFee:totalPrice.doubleValue
                                quantity:quantity.intValue
                                   price:price.doubleValue
                                    item:self.itemDict
                                     sku:sku
                            receiverUuid:uuid
                                    type:paymentType
                               onSucceed:^(NSDictionary* tradeDict)
     {
         [SHARE_PAYMENT_SERVICE payForTrade:tradeDict];
#warning TODO use callback func
         
         [self showTextHud:@"success"];
         self.createTradeOp = nil;
     }
                                 onError:^(NSError *error)
     {
         [self showErrorHudWithError:error];
         self.createTradeOp = nil;
     }];
}



#pragma mark - QSCreateTradeTableViewCellBaseDelegate
- (void)updateCellTriggerBy:(QSCreateTradeTableViewCellBase*)cell
{
    if (cell == self.itemInfoColorCell) {
        NSString* v = [self.itemInfoColorCell getInputData];
        [self.itemInfoSizeCell updateWithColorSelected:v item:self.itemDict];
        [self updatePriceRelatedCell];
    } else if (cell == self.itemInfoSizeCell) {
        NSString* v = [self.itemInfoSizeCell getInputData];
        [self.itemInfoColorCell updateWithSizeSelected:v item:self.itemDict];
        [self updatePriceRelatedCell];
    }else if (cell == self.itemInfoQuantityCell) {
        [self updatePriceRelatedCell];
    }
    
//    for (NSArray* a in self.cellGroupArray) {
//        for (QSCreateTradeTableViewCellBase* c in a) {
//            if (cell == c) {
//                continue;
//            }
//            
//        }
//    }
}
- (void)updatePriceRelatedCell {
    NSString* sizeSku = [self.itemInfoSizeCell getInputData];
    NSString* colorSku = [self.itemInfoColorCell getInputData];
    [self.itemInfoTitleCell updateWithSize:sizeSku color:colorSku item:self.itemDict];
    
    NSString* totalPrice = [QSTaobaoInfoUtil getPromoPriceOfSize:sizeSku color:colorSku taobaoInfo:[QSItemUtil getTaobaoInfo:self.itemDict] quanitty:[self.itemInfoQuantityCell getInputData]];
    [self.totalCell updateWithPrice:totalPrice];
}

#pragma mark - QSU10ReceiverListViewControllerDelegate
- (IBAction)receiverManageBtnPressed:(id)sender {
    QSU10ReceiverListViewController* vc = [[QSU10ReceiverListViewController alloc] init];
    vc.delegate = self;
    [self.navigationController pushViewController:vc animated:YES];
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
//    [UIView animateWithDuration:0.5f animations:^{
//        self.tableView.contentInset = UIEdgeInsetsZero;
//    }];
}
- (void)scrollToBottom:(float)keyboardHeight
{
    [self.tableView setContentOffset:CGPointMake(0, keyboardHeight) animated:YES];
}
@end

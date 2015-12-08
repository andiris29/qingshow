//
//  QSItemBuyViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "NSArray+QSExtension.h"
#import "QSAbstractDiscountTableViewCell.h"
#import "QSDiscountTaobaoInfoCell.h"
#import "QSDiscountInfoCell.h"
#import "QSDiscountQuantityCell.h"
#import "QSDiscountRemixCell.h"
#import "QSItemUtil.h"
#import "QSPeopleUtil.h"
#import "QSUserManager.h"
#import "QSTradeUtil.h"

#import "QSS11ItemBuyViewController.h"
#import "QSDiscountQuantityCell.h"
#import "QSS10ItemDetailViewController.h"

#import "QSNetworkKit.h"
#import "UIViewController+QSExtension.h"
#import "UIViewController+ShowHud.h"
#import "QSU14CreateTradeViewController.h"
#import "QSShareService.h"
#import "QSPaymentService.h"

@interface QSS11ItemBuyViewController () <QSDiscountTableViewCellDelegate,QSDiscountTaobaoInfoCellDelegate>

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (strong, nonatomic) NSDictionary* itemDict;
@property (copy, nonatomic) NSString* promoterId;

#pragma mark Cell
@property (strong, nonatomic) NSArray* cellArray;

@property (strong, nonatomic) QSDiscountRemixCell* remixCell;
@property (strong, nonatomic) QSDiscountQuantityCell* quantityCell;
@property (strong, nonatomic) NSArray* propCellArray;

@property (strong, nonatomic) MKNetworkOperation* updateRemixOp;
@property (strong, nonatomic) MKNetworkOperation* createTradeOp;
@property (assign, nonatomic) BOOL hasShared;

@property (strong, nonatomic) NSMutableArray* remixArray;
@property (assign, nonatomic) NSUInteger currentRemixIndex;
@property (strong, nonatomic) NSDictionary* masterDict;
@property (assign, nonatomic) CGFloat backBtnTopPreConst;
@property (assign, nonatomic) BOOL cache;
@end

@implementation QSS11ItemBuyViewController

#pragma mark - Init
- (instancetype)initWithItem:(NSDictionary*)itemDict promoterId:(NSString*)promoterId {
    self = [super initWithNibName:@"QSS11ItemBuyViewController" bundle:nil];
    if (self) {
        self.itemDict = itemDict;
        self.masterDict = self.itemDict;
        self.promoterId = promoterId;
        self.hasShared = NO;
        self.remixArray = [@[] mutableCopy];
        self.cache = YES;
    }
    return self;
}
#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    [self _configCells];
    [self _queryNewRemix];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [SHARE_NW_ENGINE getItemWithId:[QSEntityUtil getIdOrEmptyStr:self.itemDict] onSucceed:^(NSDictionary *data, NSDictionary *metadata) {
        self.itemDict = data;
        self.masterDict = data;
        [self _bindWithItemDict:self.itemDict];
    } onError:nil];
    self.btnContainer.layer.shadowColor = [UIColor blackColor].CGColor;
    self.btnContainer.layer.shadowOffset = CGSizeMake(0, -4);
    self.btnContainer.layer.shadowOpacity = 0.5f;
    self.backBtnTopPreConst = self.backTopConstraint.constant;
    self.automaticallyAdjustsScrollViewInsets = NO;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}


- (void)disCountBtnPressed:(NSArray *)btnArray btnIndex:(NSInteger)infoIndex
{
    NSDictionary *itemDic = self.itemDict;

    NSArray* filterValue = [[[self.propCellArray mapUsingBlock:^id(QSDiscountTaobaoInfoCell *cell) {
        NSString* v = [cell getSelectedValue];
        if (v) {
            return v;
        } else {
            return @"";
        }
    }] filteredArrayUsingBlock:^BOOL(NSString* str) {
        return str.length;
    }] mapUsingBlock:^NSString*(NSString* str) {
        return [str stringByReplacingOccurrencesOfString:@"." withString:@""];
    }];
    
    for (QSDiscountTaobaoInfoCell *cell in self.propCellArray) {
        [cell updateBtnStateWithItem:itemDic selectProps:filterValue];
    }
}

#pragma mark - Table view data source
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    // Return the number of rows in the section.
    return self.cellArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    QSAbstractDiscountTableViewCell* cell = self.cellArray[indexPath.row];
    CGFloat rate = [UIScreen mainScreen].bounds.size.width / 320;
    cell.transform = CGAffineTransformMakeScale(rate, rate);
    return [cell getHeight:self.itemDict] * rate;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    QSAbstractDiscountTableViewCell* cell = self.cellArray[indexPath.row];
    [cell bindWithData:self.itemDict];
    CGFloat rate = [UIScreen mainScreen].bounds.size.width / 320;
    cell.transform = CGAffineTransformMakeScale(rate, rate);
    return cell;
}

#pragma mark - QSDiscountTableViewCellDelegate
- (void)discountCellUpdateTotalPrice:(QSAbstractDiscountTableViewCell*)cell {
#warning TODO
}
- (void)discountCellDetailBtnPressed:(QSAbstractDiscountTableViewCell*)cell {
    QSS10ItemDetailViewController* vc = [[QSS10ItemDetailViewController alloc] initWithItem:self.itemDict];
    [self.navigationController pushViewController:vc animated:YES];
}
- (void)discountCellRemixBtnPressed:(QSAbstractDiscountTableViewCell*)cell {
    [self _queryNewRemix];
}
- (void)discountCellPreviousRemixBtnPressed:(QSAbstractDiscountTableViewCell*)cell {
    if (self.currentRemixIndex <= 0) {
        return;
    }
    self.currentRemixIndex -= 1;
    NSDictionary* remixDict = self.remixArray[self.currentRemixIndex];
    [self _updateWithRemix:remixDict];
}
- (void)discountCellNextRemixBtnPressed:(QSAbstractDiscountTableViewCell*)cell {
    if (self.currentRemixIndex >= self.remixArray.count - 1) {
        return;
    }
    self.currentRemixIndex += 1;
    NSDictionary* remixDict = self.remixArray[self.currentRemixIndex];
    [self _updateWithRemix:remixDict];
}

- (void)discountCell:(QSAbstractDiscountTableViewCell*)cell didSelectItem:(NSDictionary*)item {
    self.itemDict = item;
    [self _bindWithItemDict:self.itemDict];
}

- (BOOL)discountCellHasPreviousRemix:(QSAbstractDiscountTableViewCell*)cell {
    return self.currentRemixIndex > 0;
}
- (BOOL)discountCellHasNextRemix:(QSAbstractDiscountTableViewCell*)cell {
    return self.currentRemixIndex < self.remixArray.count - 1;
}

#pragma mark - IBAction
- (IBAction)buyBtnPressed:(id)sender {
    if (self.createTradeOp) {
        return;
    }
    if (![self _checkComplete]) {
        [self showErrorHudWithText:[self _getIncompleteMessage]];
        return;
    }
    
    NSArray* selectedSku = [self.propCellArray mapUsingBlock:^id(QSDiscountTaobaoInfoCell* cell) {
        return [cell getResult];
    }];
    
    self.createTradeOp =
    [SHARE_NW_ENGINE createTradeItemRef:[QSEntityUtil getIdOrEmptyStr:self.itemDict]
                            promoterRef:self.promoterId
                  selectedSkuProperties:selectedSku
                               quantity:self.quantityCell.quantity
                              onSucceed:^(NSDictionary *dict) {
                                  [[QSPaymentService shareService] sharedForTrade:dict onSucceed:^(NSDictionary *dict) {                                      QSU14CreateTradeViewController* vc =[[QSU14CreateTradeViewController alloc] initWithDict:dict];
                                      [self.navigationController pushViewController:vc animated:YES];
                                      self.createTradeOp = nil;
                                  } onError:^(NSError *error) {
                                      self.createTradeOp = nil;
                                      if (error) {
                                          [self handleError:error];
                                      }

                                  }];
                                  
                              }
                                onError:^(NSError *error) {
                                    [self handleError:error];
                                    self.createTradeOp = nil;
                                }];
}

#pragma mark - Private

- (void)_configCells {
    NSMutableArray* array = [@[] mutableCopy];
    NSMutableArray* propCells = [@[] mutableCopy];
    
    
    QSAbstractDiscountTableViewCell* cell = nil;
    
    self.remixCell = [QSDiscountRemixCell generateCell];
    cell = self.remixCell;
    cell.delegate = self;
    [array addObject:cell];
    
    cell = [QSDiscountInfoCell generateCell];
    cell.delegate = self;
    [array addObject:cell];
    NSArray* props = [QSItemUtil getSkuProperties:self.itemDict];
    for (int i = 0; i < props.count; i++) {
        QSDiscountTaobaoInfoCell* taobaoInfoCell = [QSDiscountTaobaoInfoCell generateCell];
        taobaoInfoCell.infoIndex = i;
        taobaoInfoCell.delegate = self;
        [array addObject:taobaoInfoCell];
        [propCells addObject:taobaoInfoCell];
    }
    self.quantityCell = [QSDiscountQuantityCell generateCell];
    self.quantityCell.delegate = self;
    [array addObject:self.quantityCell];
    
    self.cellArray = array;
    self.propCellArray = propCells;
}
- (BOOL)_checkComplete {
    for (QSAbstractDiscountTableViewCell* cell in self.cellArray) {
        if (![cell checkComplete]) {
            return NO;
        }
    }
    return YES;
}

- (NSString*)_getIncompleteMessage {
    NSMutableString* m = [@"" mutableCopy];
    [self.propCellArray enumerateObjectsUsingBlock:^(QSDiscountTaobaoInfoCell* cell, NSUInteger idx, BOOL *stop) {
        if (![cell checkComplete] && cell.title.length) {
            if (m.length) {
                [m appendFormat:@"、%@", cell.title];
            } else {
                [m appendFormat:@"%@", cell.title];
            }
        }
    }];
    if (!m.length) {
        [m appendString:@"规格"];
    }
    
    return [NSString stringWithFormat:@"请选择%@",m];
}

- (void)_updateWithRemix:(NSDictionary*)remixDict {
    NSString* masterId = [QSEntityUtil getIdOrEmptyStr:self.masterDict];
    [[QSUserManager shareUserManager].remixCache setObject:remixDict forKey:masterId];
    self.itemDict = self.masterDict;
    [self.remixCell bindWithItem:self.itemDict remix:remixDict];
    

}
- (void)_queryNewRemix {
    if (self.updateRemixOp) {
        return;
    }
    NSString* masterId = [QSEntityUtil getIdOrEmptyStr:self.masterDict];
    BOOL f = self.cache;
    self.cache = NO;
    
    NSCache* remixCache = [QSUserManager shareUserManager].remixCache;
    NSDictionary* cacheRemixInfo = [remixCache objectForKey:masterId];
    if ([cacheRemixInfo isKindOfClass:[NSNull class]]) {
        cacheRemixInfo = nil;
    }
    
    if (f && cacheRemixInfo) {
        [self.remixArray addObject:cacheRemixInfo];
        self.currentRemixIndex = self.remixArray.count - 1;
        [self _updateWithRemix:cacheRemixInfo];
    } else {
        self.updateRemixOp = [SHARE_NW_ENGINE matcherRemixByItem:self.masterDict
                                                       onSucceed:^(NSDictionary *remixInfo) {
                                                           self.updateRemixOp = nil;
                                                           [self.remixArray addObject:remixInfo];
                                                           self.currentRemixIndex = self.remixArray.count - 1;
                                                           [self _updateWithRemix:remixInfo];

                                                       }
                                                         onError:^(NSError *error) {
                                                             self.updateRemixOp = nil;
                                                             [self handleError:error];
                                                         }];
    }
}
- (void)_bindWithItemDict:(NSDictionary*)dict {
    NSNumber* reduction = [QSItemUtil getExpectableReduction:dict];
    
    self.discountInfoBtn.hidden = NO;
    [self.discountInfoBtn setTitle:[NSString stringWithFormat:@"分享搭配立减%.2f元", reduction.floatValue] forState:UIControlStateNormal];

    [self.buyBtn setTitle:[NSString stringWithFormat:@"%.2f 购买", ([QSItemUtil getPromoPrice:dict].doubleValue - reduction.doubleValue)] forState:UIControlStateNormal];
    [self.tableView reloadData];
    self.titleLabel.text = [QSItemUtil getShopNickName:dict];
}

- (IBAction)backBtnPressed:(id)sender {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    self.backTopConstraint.constant = self.backBtnTopPreConst - scrollView.contentOffset.y;
}

@end

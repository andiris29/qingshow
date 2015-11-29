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

#import "QSItemBuyViewController.h"
#import "QSDiscountQuantityCell.h"
#import "QSS10ItemDetailViewController.h"

#import "QSNetworkKit.h"
#import "UIViewController+QSExtension.h"

@interface QSItemBuyViewController () <QSDiscountTableViewCellDelegate,QSDiscountTaobaoInfoCellDelegate>

@property (strong, nonatomic) NSDictionary* itemDict;
@property (copy, nonatomic) NSString* promoterId;

#pragma mark Cell
@property (strong, nonatomic) NSArray* cellArray;

@property (strong, nonatomic) QSDiscountRemixCell* remixCell;
@property (strong, nonatomic) QSDiscountQuantityCell* quantityCell;
@property (strong, nonatomic) NSArray* propCellArray;

@property (strong, nonatomic) MKNetworkOperation* updateRemixOp;
@end

@implementation QSItemBuyViewController

#pragma mark - Init
- (instancetype)initWithItem:(NSDictionary*)itemDict promoterId:(NSString*)promoterId {
    self = [super initWithNibName:@"QSItemBuyViewController" bundle:nil];
    if (self) {
        self.itemDict = itemDict;
        self.promoterId = promoterId;
    }
    return self;
}
#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    [self _configCells];
    [self _updateRemix];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
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
    return [cell getHeight:self.itemDict];
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    QSAbstractDiscountTableViewCell* cell = self.cellArray[indexPath.row];
    [cell bindWithData:self.itemDict];
    return cell;
}

#pragma mark - QSDiscountTableViewCellDelegate
- (void)discountCellUpdateTotalPrice:(QSAbstractDiscountTableViewCell*)cell {
    
}
- (void)discountCellDetailBtnPressed:(QSAbstractDiscountTableViewCell*)cell {
    QSS10ItemDetailViewController* vc = [[QSS10ItemDetailViewController alloc] initWithItem:self.itemDict];
    [self.navigationController pushViewController:vc animated:YES];
}
- (void)discountCellRemixBtnPressed:(QSAbstractDiscountTableViewCell*)cell {
    
}
- (void)discountCellPreviousRemixBtnPressed:(QSAbstractDiscountTableViewCell*)cell {
    
}
- (void)discountCellNextRemixBtnPressed:(QSAbstractDiscountTableViewCell*)cell {
    
}

#pragma mark - IBAction
- (IBAction)buyBtnPressed:(id)sender {
    
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
- (NSDictionary*)_getResult {
    NSMutableDictionary* retDict = [@{} mutableCopy];
    if (self.promoterId) {
        retDict[@"promoterRef"] = self.promoterId;
    }
    retDict[@"itemSnapshot"] = self.itemDict;
    retDict[@"selectedSkuProperties"] = [self.propCellArray mapUsingBlock:^id(QSDiscountTaobaoInfoCell* cell) {
        return [cell getResult];
    }];
    retDict[@"quantity"] = @(self.quantityCell.quantity);
    return retDict;
}

- (void)_updateRemix {
    if (self.updateRemixOp) {
        return;
    }
    self.updateRemixOp = [SHARE_NW_ENGINE matcherRemix:self.itemDict onSucceed:^(NSDictionary *remixInfo) {

    } onError:^(NSError *error) {
        [self handleError:error];
    }];
}
@end

//
//  QSDiscountTableViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "NSArray+QSExtension.h"
#import "QSDiscountTableViewController.h"

#import "QSDiscountTitleCell.h"
#import "QSDiscountInfoCell.h"
#import "QSDiscountQuantityCell.h"
#import "QSDiscountResultCell.h"
#import "QSItemUtil.h"
#import "QSPeopleUtil.h"
#import "QSUserManager.h"
#import "QSTradeUtil.h"
@interface QSDiscountTableViewController ()


@property (strong, nonatomic) NSArray* cellArray;

@property (strong, nonatomic) QSDiscountQuantityCell* quantityCell;
@property (strong, nonatomic) QSDiscountResultCell* resultCell;
@property (strong, nonatomic) NSArray* propCellArray;

@end

@implementation QSDiscountTableViewController
#pragma mark - Init
- (instancetype)initWithItem:(NSDictionary*)itemDict {
    self = [super init];
    if (self) {
        self.itemDict = itemDict;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    [self configCells];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.backgroundColor = [UIColor clearColor];
    self.view.backgroundColor = [UIColor clearColor];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
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
#pragma mark - 
- (void)configCells {
    NSMutableArray* array = [@[] mutableCopy];
    NSMutableArray* propCells = [@[] mutableCopy];
    QSAbstractDiscountTableViewCell* cell = [QSDiscountTitleCell generateCell];
    cell.delegate = self;
    [array addObject:cell];
    cell = [QSDiscountInfoCell generateCell];
    cell.delegate = self;
    [array addObject:cell];
    self.resultCell = [QSDiscountResultCell generateCell];
    self.resultCell.delegate = self;
    [array addObject:self.resultCell];
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

- (void)updateTotalPrice {
    self.resultCell.quantity = self.quantityCell.quantity;
}

- (BOOL)checkComplete {
    for (QSAbstractDiscountTableViewCell* cell in self.cellArray) {
        if (![cell checkComplete]) {
            return NO;
        }
    }
    return YES;
}

- (void)disCountBtnPressed:(NSArray *)btnArray btnIndex:(NSInteger)infoIndex
{
//    NSLog(@"%@",[self getResult]);
//    NSDictionary *newTradeDic = [self getResult];
//    NSDictionary *itemDic = [QSTradeUtil getItemSnapshot:newTradeDic];
//    NSArray *skuArray = newTradeDic[@"selectedSkuProperties"];
//    NSString *key = [QSItemUtil getKeyValueForSkuTableFromeSkuProperties:skuArray];
//    int count = [QSItemUtil getFirstValueFromSkuTableWithkey:key itemDic:itemDic];
//    NSLog(@"count ============ %d",count);
//    NSLog(@"index = %ld",(long)infoIndex);
//    NSLog(@"cell.co = %lu",(unsigned long)self.propCellArray.count);
//    [self.propCellArray mapUsingBlock:^id(QSDiscountTaobaoInfoCell *cell) {
//        NSLog(@"cell.btn.co = %lu",(unsigned long)cell.btnArray.count);
//        return [cell getResult];
//    }];
}
- (NSDictionary*)getResult {
    NSMutableDictionary* retDict = [@{} mutableCopy];
    retDict[@"promoterRef"] = self.showId;
    retDict[@"itemSnapshot"] = self.itemDict;
    retDict[@"selectedSkuProperties"] = [self.propCellArray mapUsingBlock:^id(QSDiscountTaobaoInfoCell* cell) {
        return [cell getResult];
    }];
    retDict[@"quantity"] = @(self.quantityCell.quantity);
    retDict[@"expectedPrice"] = [self.resultCell getSinglePrice];
    return retDict;
}
- (void)refresh {
    [self.tableView reloadData];
}
@end

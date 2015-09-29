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
- (NSString*)getIncompleteMessage {

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
- (NSDictionary*)getResult {
    NSMutableDictionary* retDict = [@{} mutableCopy];
    if (self.peopleId) {
        retDict[@"promoterRef"] = self.peopleId;
    }
    retDict[@"itemSnapshot"] = self.itemDict;
    retDict[@"selectedSkuProperties"] = [self.propCellArray mapUsingBlock:^id(QSDiscountTaobaoInfoCell* cell) {
        return [cell getResult];
    }];
    retDict[@"quantity"] = @(self.quantityCell.quantity);
    int singlePrice = [[self.resultCell getSinglePrice] intValue];
    retDict[@"expectedPrice"] = [NSNumber numberWithInt:(singlePrice * self.quantityCell.quantity)];
    return retDict;
}
- (void)refresh {
    [self.tableView reloadData];
}
@end

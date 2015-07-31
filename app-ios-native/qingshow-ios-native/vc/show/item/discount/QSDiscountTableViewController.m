//
//  QSDiscountTableViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/30/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSDiscountTableViewController.h"
#import "QSAbstractDiscountTableViewCell.h"
#import "QSDiscountTitleCell.h"
#import "QSDiscountInfoCell.h"
#import "QSDiscountTaobaoInfoCell.h"
#import "QSDiscountQuantityCell.h"
#import "QSDiscountResultCell.h"
#import "QSItemUtil.h"


@interface QSDiscountTableViewController ()

@property (strong, nonatomic) NSDictionary* itemDict;
@property (strong, nonatomic) NSArray* cellArray;
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
    [array addObject:[QSDiscountTitleCell generateCell]];
    [array addObject:[QSDiscountInfoCell generateCell]];
    NSArray* props = [QSItemUtil getSkuProperties:self.itemDict];
    for (int i = 0; i < props.count; i++) {
        QSDiscountTaobaoInfoCell* taobaoInfoCell = [QSDiscountTaobaoInfoCell generateCell];
        taobaoInfoCell.infoIndex = i;
        [array addObject:taobaoInfoCell];
    }
    [array addObject:[QSDiscountQuantityCell generateCell]];
    [array addObject:[QSDiscountResultCell generateCell]];
    self.cellArray = array;
}
@end

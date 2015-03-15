//
//  QSCreateTradeViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/15/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS11CreateTradeViewController.h"

@interface QSS11CreateTradeViewController ()

@property (strong, nonatomic) NSArray* cellGroupArray;


@property (strong, nonatomic) NSArray* itemInfoCellArray;

@property (strong, nonatomic) NSArray* receiverInfoCellArray;

@property (strong, nonatomic) NSArray* payWayCellArray;

@property (strong, nonatomic) NSArray* totalPriceCellArray;

@end

@implementation QSS11CreateTradeViewController

#pragma mark - Init
- (id)initWithDict:(NSDictionary*)dict
{
    self = [super initWithNibName:@"QSS11CreateTradeViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self configView];
    [self configCellArray];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark - Private Method
- (void)configView
{
    self.title = @"订单确认";
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

- (UITableViewCell*)cellForIndexPath:(NSIndexPath*)indexPath
{
    UITableViewCell* cell = nil;
    
    NSArray* cellArray = self.cellGroupArray[indexPath.section];
    cell = cellArray[indexPath.row];
    
    return cell;
}

#pragma mark - UITableView DataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    NSArray* cellArray = self.cellGroupArray[section];
    return cellArray.count;
}

// Row display. Implementers should *always* try to reuse cells by setting each cell's reuseIdentifier and querying for available reusable cells with dequeueReusableCellWithIdentifier:
// Cell gets various attributes set automatically based on table (separators) and data source (accessory views, editing controls)

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath;
{
    return nil;
}
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return self.cellGroupArray.count;
}

@end

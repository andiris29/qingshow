//
//  QSTableViewBasicDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSTableViewBasicProvider.h"
#import "MKNetworkOperation.h"
#import "NSNumber+QSExtension.h"
#import "QSMetadataUtil.h"
#import "QSAbstractListViewProvider+Protect.h"

@interface QSTableViewBasicProvider ()

@property (assign, nonatomic) Class cellClass;
@property (strong, nonatomic) UINib* cellNib;
@property (strong, nonatomic) NSString* identifier;


@property (strong, nonatomic) UIRefreshControl* refreshControl;
@end

@implementation QSTableViewBasicProvider

#pragma mark - Init
- (id)initWithCellClass:(Class)cellClass identifier:(NSString*)identifier
{
    self = [self initWIthIdentifier:identifier];
    if (self) {
        self.cellClass = cellClass;
    }
    return self;
}

- (id)initWithCellNib:(UINib*)cellNib identifier:(NSString*)identifier
{
    self = [self initWIthIdentifier:identifier];
    if (self) {
        self.cellNib = cellNib;
    }
    return self;
}
- (id)initWIthIdentifier:(NSString*)identifier
{
    self = [self init];
    if (self) {
        self.identifier = identifier;
    }
    return self;
}


- (void)dealloc
{
    self.view.delegate = nil;
    self.view.dataSource = nil;
}
#pragma mark - 
- (void)bindWithTableView:(UITableView*)tableView
{
    self.view = tableView;
    self.view.dataSource = self;
    self.view.delegate = self;
    tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    
    UIRefreshControl* refreshControl = [[UIRefreshControl alloc] init];
    [tableView addSubview:refreshControl];
    [refreshControl addTarget:self action:@selector(tableViewDidPullToRefresh:) forControlEvents:UIControlEventValueChanged];
    self.refreshControl = refreshControl;
    
    [self registerCell];
}
- (void)registerCell
{
    //Method to be override
}

#pragma mark - Network
- (MKNetworkOperation*)fetchDataOfPage:(int)page completion:(VoidBlock)block
{
    return [self fetchDataOfPage:page viewRefreshBlock:^{
        [self.view reloadData];
    } completion:block];
}

#pragma mark -
- (void)tableViewDidPullToRefresh:(UIRefreshControl*)refreshControl
{
    [self fetchDataOfPage:1 completion:^{
        [refreshControl endRefreshing];
    }];
}

#pragma mark - TableView
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.resultArray.count;
}
- (void)refreshWithAnimation
{
    [self.refreshControl beginRefreshing];
    [self.view scrollRectToVisible:CGRectMake(0, -self.refreshControl.frame.size.height, 1, 1) animated:YES];
    [self tableViewDidPullToRefresh:self.refreshControl];
}
- (void)removeData:(NSDictionary*)data withAnimation:(BOOL)fAnimate
{
    NSUInteger i = [self.resultArray indexOfObject:data];
    [self.resultArray removeObject:data];
    NSIndexPath* indexPath = [NSIndexPath indexPathForRow:i inSection:0];
    UITableViewRowAnimation a = fAnimate? UITableViewRowAnimationAutomatic : UITableViewRowAnimationNone;
    [self.view deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:a];
    
    NSDictionary* metaData = [self.metadataDict mutableCopy];
    [QSMetadataUtil addTotalNum:-1ll forDict:metaData];
    self.metadataDict = metaData;
    
}

- (void)refreshClickedData
{
}
- (NSString*)getTotalCountDesc
{
    if (!self.metadataDict) {
        return @"0";
    }
    long long filterCount = 0;
    for (NSDictionary* dict in self.resultArray) {
        if (self.filterBlock) {
            if (self.filterBlock(dict)){
                filterCount++;
            }
        }
    }
    long long t = [QSMetadataUtil getNumberTotal:self.metadataDict] - filterCount;
    t = t >= 0ll? t : 0ll;
    return @(t).kmbtStringValue;
}
@end

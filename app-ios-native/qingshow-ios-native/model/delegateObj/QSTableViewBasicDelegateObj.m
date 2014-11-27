//
//  QSTableViewBasicDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/23/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSTableViewBasicDelegateObj.h"
#import "MKNetworkOperation.h"

@interface QSTableViewBasicDelegateObj ()

@property (assign, nonatomic) Class cellClass;
@property (strong, nonatomic) UINib* cellNib;
@property (strong, nonatomic) NSString* identifier;


@property (strong, nonatomic) MKNetworkOperation* refreshOperation;
@property (strong, nonatomic) MKNetworkOperation* loadMoreOperation;
@property (assign, nonatomic) BOOL fIsAll;

@end

@implementation QSTableViewBasicDelegateObj

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
- (id)init
{
    self = [super init];
    if (self) {
        self.resultArray = [@[] mutableCopy];
        
        self.fIsAll = NO;
        self.loadMoreOperation = nil;
        self.refreshOperation = nil;
        _currentPage = 1;
    }
    return self;
}
#pragma mark - 
- (void)bindWithTableView:(UITableView*)tableView
{
    self.tableView = tableView;
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    
    UIRefreshControl* refreshControl = [[UIRefreshControl alloc] init];
    [tableView addSubview:refreshControl];
    [refreshControl addTarget:self action:@selector(tableViewDidPullToRefresh:) forControlEvents:UIControlEventValueChanged];
    
    [self registerCell];
}
- (void)registerCell
{
    //Method to be override
}

#pragma mark - Network
- (void)reloadData
{
    [self fetchDataOfPage:1];
}
- (MKNetworkOperation*)fetchDataOfPage:(int)page
{
    return [self fetchDataOfPage:page onComplete:nil];
}

- (MKNetworkOperation*)fetchDataOfPage:(int)page onComplete:(VoidBlock)block
{
    MKNetworkOperation* op = self.networkBlock(^(NSArray *array, NSDictionary *metadata) {
        if (page == 1) {
            [self.resultArray removeAllObjects];
            self.refreshOperation = nil;
            _currentPage = 1;
        }
        
        [self.resultArray addObjectsFromArray:array];
        [self.tableView reloadData];
        if (block) {
            block();
        }
    },^(NSError *error) {
        if (error.code == 1009) {
            self.fIsAll = YES;
        }
        if ([self.delegate respondsToSelector:@selector(handleNetworkError:)]) {
            [self.delegate handleNetworkError:error];
        }
        if (block) {
            block();
        }
    },page);
    
    if (page == 1) {
        [self.loadMoreOperation cancel];
        self.loadMoreOperation = nil;
        self.fIsAll = NO;
        self.refreshOperation = op;
    }
    return op;
}

#pragma mark - scroll view
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if ([self.delegate respondsToSelector:@selector(scrollViewDidScroll:)]) {
        [self.delegate scrollViewDidScroll:scrollView];
    }
    
    if (self.fIsAll || self.refreshOperation || self.loadMoreOperation) {
        return;
    }
    
    if (scrollView.contentOffset.y + scrollView.frame.size.height >= scrollView.contentSize.height) {
        self.loadMoreOperation = [self fetchDataOfPage:self.currentPage + 1 onComplete:^{
            _currentPage++;
            self.loadMoreOperation = nil;
        }];
    }
}
#pragma mark -
- (void)tableViewDidPullToRefresh:(UIRefreshControl*)refreshControl
{
    [self fetchDataOfPage:1 onComplete:^{
        [refreshControl endRefreshing];
    }];
}

#pragma mark - TableView
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.resultArray.count;
}

@end

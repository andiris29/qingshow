//
//  QSWaterfallBasicDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSWaterfallBasicDelegateObj.h"
#import "MKNetworkOperation.h"
@interface QSWaterfallBasicDelegateObj ()
@property (strong, nonatomic) MKNetworkOperation* refreshOperation;
@property (strong, nonatomic) MKNetworkOperation* loadMoreOperation;
@property (assign, nonatomic) BOOL fIsAll;

@end


@implementation QSWaterfallBasicDelegateObj

#pragma mark - Method To Be Override
- (void)registerCell
{
    return;
}

-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionViews cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return nil;
}

#pragma mark - Init
- (id)init
{
    self = [super init];
    if (self) {
        _resultArray = [@[] mutableCopy];
        _currentPage = 1;
        self.fIsAll = NO;
        self.loadMoreOperation = nil;
        self.refreshOperation = nil;
    }
    return self;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return CGSizeZero;
}


#pragma mark - Config
- (void)bindWithCollectionView:(UICollectionView *)collectionView
{
    _collectionView = collectionView;
    self.collectionView.dataSource = self;
    self.collectionView.delegate = self;
    collectionView.alwaysBounceVertical = YES;
    
    QSWaterFallCollectionViewLayout* layout = [[QSWaterFallCollectionViewLayout alloc] init];
    self.collectionView.collectionViewLayout = layout;
    self.collectionView.translatesAutoresizingMaskIntoConstraints = NO;
    
    self.collectionView.scrollEnabled=YES;
    self.collectionView.backgroundColor=[UIColor colorWithRed:240.f/255.f green:240.f/255.f blue:240.f/255.f alpha:1.f];

    [self registerCell];
    
    UIRefreshControl* refreshControl = [[UIRefreshControl alloc] init];
    [refreshControl addTarget:self action:@selector(didPullRefreshControl:) forControlEvents:UIControlEventValueChanged];
    [collectionView addSubview:refreshControl];
}


#pragma mark - Network
- (void)reloadData
{
    [self fetchDataOfPage:1];
}

- (MKNetworkOperation*)fetchDataOfPage:(int)page
{
    return [self fetchDataOfPage:page completion:nil];
}

- (MKNetworkOperation*)fetchDataOfPage:(int)page completion:(VoidBlock)block
{

    MKNetworkOperation* op = self.networkBlock(^(NSArray *showArray, NSDictionary *metadata) {
        int preCount = 0;
        if (page == 1) {
            [self.resultArray removeAllObjects];
            self.refreshOperation = nil;
            _currentPage = 1;
        }
        else {
            preCount = self.resultArray.count;
        }
        
        [self.resultArray addObjectsFromArray:showArray];
        if (page == 1) {
            [self.collectionView reloadData];
        } else {
            NSMutableArray* indexPaths = [@[] mutableCopy];
            for (int i = preCount; i < self.resultArray.count; i++){
                [indexPaths addObject:[NSIndexPath indexPathForItem:i inSection:0]];
            }
            [self.collectionView insertItemsAtIndexPaths:indexPaths];
        }

        if (block) {
            block();
        }
    }, ^(NSError *error) {
        if (error.code == 1009) {
            self.fIsAll = YES;
        }
        if ([self.delegate respondsToSelector:@selector(handleNetworkError:)]) {
            [self.delegate handleNetworkError:error];
        }
        if (block) {
            block();
        }
    }, page);
    
    if (page == 1) {
        [self.loadMoreOperation cancel];
        self.loadMoreOperation = nil;
        self.fIsAll = NO;
        self.refreshOperation = op;
    }
    
    return op;
}

#pragma mark - UICollecitonView Datasource And Delegate

//margin
-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    return UIEdgeInsetsMake(10, 10, 10, 10);
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [collectionView deselectItemAtIndexPath:indexPath animated:YES];
}


-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return self.resultArray.count;
}

#pragma mark - Scroll View
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if (self.fIsAll || self.refreshOperation || self.loadMoreOperation) {
        return;
    }
    
    if (scrollView.contentOffset.y + scrollView.frame.size.height >= scrollView.contentSize.height) {
        self.loadMoreOperation = [self fetchDataOfPage:self.currentPage + 1 completion:^{
            _currentPage++;
            self.loadMoreOperation = nil;
        }];
    }
}


#pragma mark - Refresh Control
- (void)didPullRefreshControl:(UIRefreshControl*)refreshControl
{
    [self fetchDataOfPage:1 completion:^{
        [refreshControl endRefreshing];
    }];
}
@end

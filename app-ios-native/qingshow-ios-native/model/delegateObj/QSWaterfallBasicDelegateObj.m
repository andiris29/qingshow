//
//  QSWaterfallBasicDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSWaterfallBasicDelegateObj.h"

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
    
    QSWaterFallCollectionViewLayout* layout = [[QSWaterFallCollectionViewLayout alloc] init];
    self.collectionView.collectionViewLayout = layout;
    self.collectionView.translatesAutoresizingMaskIntoConstraints = NO;
    
    self.collectionView.scrollEnabled=YES;
    self.collectionView.backgroundColor=[UIColor colorWithRed:240.f/255.f green:240.f/255.f blue:240.f/255.f alpha:1.f];

    [self registerCell];
}


#pragma mark - Network
- (void)reloadData
{
    [self fetchDataOfPage:1];
}

- (MKNetworkOperation*)fetchDataOfPage:(int)page
{
    MKNetworkOperation* op = self.networkBlock(^(NSArray *showArray, NSDictionary *metadata) {
        if (page == 1) {
            [self.resultArray removeAllObjects];
        }
        [self.resultArray addObjectsFromArray:showArray];
        [self.collectionView reloadData];
    }, ^(NSError *error) {
        NSLog(@"error");
    }, page);
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

@end

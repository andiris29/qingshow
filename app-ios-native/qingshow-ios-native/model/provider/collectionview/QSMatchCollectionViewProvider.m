//
//  QSMatchCollectionViewProvider.m
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSMatchCollectionViewProvider.h"

#import "QSS03ShowDetailViewController.h"
#import "QSIImageLoadingCancelable.h"
#import "QSMatcherCollectionViewHeader.h"

#define S01MATCHCELL @"matchShowsForS01CellId"

#define w ([UIScreen mainScreen].bounds.size.width)
#define h ([UIScreen mainScreen].bounds.size.height)
@interface QSMatchCollectionViewProvider()

@property (strong, nonatomic) QSMatcherCollectionViewHeader* headerView;

@property (strong, nonatomic) NSArray* owners;
@property (assign, nonatomic) int numOwners;
@property (assign, nonatomic) int ownIndex;

@end

@implementation QSMatchCollectionViewProvider

@dynamic delegate;
- (void)setCurrentDate:(NSDate *)currentDate {
    _currentDate = currentDate;
    [self.view reloadData];
}
- (MKNetworkOperation*)fetchDataOfPage:(int)page completion:(VoidBlock)block {
    MKNetworkOperation* op = [super fetchDataOfPage:page completion:block];
    if (page == 1) {
        [self _refreshHeader];
    }
    return op;
}
- (void)_refreshHeader {
    if (!self.headerNetworkBlock) {
        return;
    }
    self.headerNetworkBlock(^(NSArray* owners, int numOwners, int ownIndex) {
        self.owners = owners;
        self.numOwners = numOwners;
        self.ownIndex = ownIndex;
        
        NSIndexPath* path = [NSIndexPath indexPathForItem:0 inSection:0];
        [self.view reloadItemsAtIndexPaths:@[path]];
    }, ^(NSError *error) {
    });
}
#pragma mark -
- (void)bindWithCollectionView:(UICollectionView *)collectionView {
    [super bindWithCollectionView:collectionView];
    self.headerView = [QSMatcherCollectionViewHeader generateView];
}

- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSMatcherCollectionViewHeader" bundle:nil] forCellWithReuseIdentifier:@"QSMatcherCollectionViewHeader"];
    [self.view registerNib:[UINib nibWithNibName:@"QSMatchShowsCell" bundle:nil] forCellWithReuseIdentifier:S01MATCHCELL];
}
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return CGSizeMake([UIScreen mainScreen].bounds.size.width/2-10, w);
}
- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    return 1;
}
- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionViews cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.item == 0) {
        QSMatcherCollectionViewHeader* headerCell =(QSMatcherCollectionViewHeader*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSMatcherCollectionViewHeader" forIndexPath:indexPath];
        if (!headerCell) {
            headerCell = [[[NSBundle mainBundle]loadNibNamed:@"QSMatcherCollectionViewHeader" owner:nil options:nil]lastObject];
        }
        [headerCell bindWithOwners:self.owners ownerCount:self.numOwners index:self.ownIndex];
        return headerCell;
    }
    QSMatchShowsCell *cell = (QSMatchShowsCell *)[collectionViews dequeueReusableCellWithReuseIdentifier:S01MATCHCELL forIndexPath:indexPath];
    if (!cell) {
        cell = [[[NSBundle mainBundle]loadNibNamed:@"QSMatchShowsCell" owner:nil options:nil]lastObject];
    }
    if(self.resultArray.count) {
        [cell bindWithDic:self.resultArray[indexPath.item] withIndex:(int)indexPath.item - 1];
    }
    if (w == 414) {
        cell.contentView.transform = CGAffineTransformMakeScale(w/(320-15), w/(320-12));
    } else {
        cell.contentView.transform = CGAffineTransformMakeScale(w/(320-15), w/(320-16));
    }
    
    cell.backgroundColor = [UIColor whiteColor];
    cell.delegate = self;
    return (UICollectionViewCell *)cell;
    
}
- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
   
}

-(CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout referenceSizeForHeaderInSection:(NSInteger)section
{
    return CGSizeMake(320.f, 100.f);
}


- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return self.resultArray.count + 1;
}
- (CGFloat)heightForHeaderInCollectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout {
    return 100.f;
}
#pragma mark - Delegate
- (void)headerImgViewPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(provider:didClickHeaderImgView:)]) {
        [self.delegate provider:self didClickHeaderImgView:sender];
        self.clickedData = sender;
    }
}
- (void)matchImgViewPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(provider:didSelectedCellInCollectionView:)]) {
        [self.delegate provider:self didSelectedCellInCollectionView:sender];
        self.clickedData = sender;
    }
}

- (void)refreshClickedData
{
    if (self.clickedData) {
        
        NSUInteger i = [self.resultArray indexOfObject:self.clickedData];
        if (i != NSNotFound) {
            NSIndexPath* indexPath = [NSIndexPath indexPathForItem:i inSection:0];
            QSMatchShowsCell* cell = (QSMatchShowsCell*)[self.view cellForItemAtIndexPath:indexPath];
            [cell bindWithDic:self.clickedData withIndex:(int)indexPath.item];
        }
        
        self.clickedData = nil;
    }
}
@end

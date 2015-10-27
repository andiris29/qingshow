//
//  QSMatchCollectionViewProvider.m
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSMatchCollectionViewProvider.h"

#import "QSS03ShowDetailViewController.h"

#define S01MATCHCELL @"matchShowsForS01CellId"

#define w ([UIScreen mainScreen].bounds.size.width)
#define h ([UIScreen mainScreen].bounds.size.height)
@implementation QSMatchCollectionViewProvider

@dynamic delegate;

- (void)registerCell
{
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
    QSMatchShowsCell *cell = (QSMatchShowsCell *)[collectionViews dequeueReusableCellWithReuseIdentifier:S01MATCHCELL forIndexPath:indexPath];
    if (!cell) {
        cell = [[[NSBundle mainBundle]loadNibNamed:@"QSMatchShowsCell" owner:nil options:nil]lastObject];
    }
    if(self.resultArray.count)
    {
        [cell bindWithDic:self.resultArray[indexPath.item] withIndex:(int)indexPath.item];
    }
    if (w == 414) {
        cell.contentView.transform = CGAffineTransformMakeScale(w/(320-15), w/(320-12));
    }
    else
    {
        cell.contentView.transform = CGAffineTransformMakeScale(w/(320-15), w/(320-16));
    }
    
    cell.backgroundColor = [UIColor whiteColor];
    cell.delegate = self;
    return (UICollectionViewCell *)cell;
    
}
- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
   
   
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return self.resultArray.count;
}
#pragma mark - Delegate
- (void)headerImgViewPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickHeaderImgView:)]) {
        [self.delegate didClickHeaderImgView:sender];
        self.clickedData = sender;
    }
}
- (void)matchImgViewPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didSelectedCellInCollectionView:)]) {
        [self.delegate  didSelectedCellInCollectionView:sender];
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

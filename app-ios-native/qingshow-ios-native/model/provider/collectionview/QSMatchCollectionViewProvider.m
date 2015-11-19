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

#define S01MATCHCELL @"matchShowsForS01CellId"
#define U01MATCHCELL @"matchShowsForU01CellId"

#define w ([UIScreen mainScreen].bounds.size.width)
#define h ([UIScreen mainScreen].bounds.size.height)
@implementation QSMatchCollectionViewProvider

@dynamic delegate;

- (void)bindWithCollectionView:(UICollectionView *)collectionView {
    [super bindWithCollectionView:collectionView];
    
}

- (void)registerCell
{
    if (_type == 1) {
        [self.view registerNib:[UINib nibWithNibName:@"QSMatchShowsCell" bundle:nil] forCellWithReuseIdentifier:S01MATCHCELL];
    }
    else
    {
        [self.view registerNib:[UINib nibWithNibName:@"QSU01MatchCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:U01MATCHCELL];
    }
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
    if (_type == 1) {
        QSMatchShowsCell *cell = (QSMatchShowsCell *)[collectionViews dequeueReusableCellWithReuseIdentifier:S01MATCHCELL forIndexPath:indexPath];
        if (!cell) {
            cell = [[[NSBundle mainBundle]loadNibNamed:@"QSMatchShowsCell" owner:nil options:nil]lastObject];
        }
//        if (indexPath.item == 1) {
//            NSLog(@"result Array = %@",self.resultArray[indexPath.item]);
//        }
//        
        //NSLog(@"count === %d",self.resultArray.count);
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
    else
    {
        QSU01MatchCollectionViewCell *cell = (QSU01MatchCollectionViewCell *)[collectionViews dequeueReusableCellWithReuseIdentifier:U01MATCHCELL forIndexPath:indexPath];
        if (cell == nil) {
            cell = [[[NSBundle mainBundle]loadNibNamed:@"QSU01MatchCollectionViewCell" owner:nil options:nil]lastObject];
        }
        if (self.resultArray.count) {
             [cell bindWithDic:self.resultArray[indexPath.item]];
        }
        cell.contentView.transform = CGAffineTransformMakeScale(w/(320-15), w/(320-16));
        cell.backgroundColor = [UIColor whiteColor];
        return (UICollectionViewCell *)cell;
    }
    
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
    if ([self.delegate respondsToSelector:@selector(provider:didClickHeaderImgView:)]) {
        
        [self.delegate provider:self didClickHeaderImgView:sender];
    }
}
- (void)matchImgViewPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(provider:didSelectedCellInCollectionView:)]) {
        [self.delegate provider:self didSelectedCellInCollectionView:sender];
    }
}
@end

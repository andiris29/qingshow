//
//  QSMatchCollectionViewProvider.m
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSMatchCollectionViewProvider.h"
#import "QSMatchShowsCell.h"
#import "QSU01MatchCollectionViewCell.h"

#define S01MATCHCELL @"matchShowsForS01CellId"
#define U01MATCHCELL @"matchShowsForU01CellId"

@implementation QSMatchCollectionViewProvider

@dynamic delegate;

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
    return CGSizeMake([UIScreen mainScreen].bounds.size.width/2-10, 320);
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
        
       // [cell bindWithDic:self.resultArray[indexPath.item]];
        return (UICollectionViewCell *)cell;
    }
    else
    {
        QSU01MatchCollectionViewCell *cell = (QSU01MatchCollectionViewCell *)[collectionViews dequeueReusableCellWithReuseIdentifier:U01MATCHCELL forIndexPath:indexPath];
        if (cell == nil) {
            cell = [[[NSBundle mainBundle]loadNibNamed:@"QSU01MatchCollectionViewCell" owner:nil options:nil]lastObject];
        }
        //[cell bindWithDic:self.resultArray[indexPath.item]];
        return (UICollectionViewCell *)cell;
    }
    
}
- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
#warning PUSH SHOW
}
- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    if (self.resultArray.count) {
        return self.resultArray.count;
    }
    return 10;
}

@end

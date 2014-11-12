//
//  QSShowWaterfallDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/6/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSShowCollectionViewDelegateObj.h"
#import "QSTimeCollectionViewCell.h"

@interface QSShowCollectionViewDelegateObj ()

@end

@implementation QSShowCollectionViewDelegateObj

- (void)registerCell
{
    [self.collectionView registerNib:[UINib nibWithNibName:@"QSShowCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSShowCollectionViewCell"];
    [self.collectionView registerNib:[UINib nibWithNibName:@"QSTimeCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSTimeCollectionViewCell"];
}

#pragma mark - UICollecitonView Datasource And Delegate

- (NSDictionary*)getShowDictForIndexPath:(NSIndexPath*)indexPath
{
    return self.resultArray[indexPath.row - 1];
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        return CGSizeMake(145, 35);
    } else {
        NSDictionary* dict = [self getShowDictForIndexPath:indexPath];
        float height = [QSShowCollectionViewCell getHeightWithData:dict];
        return CGSizeMake(145, height);
    }
    
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary* showDict = self.resultArray[indexPath.row];
    if ([self.delegate respondsToSelector:@selector(didClickShow:)]) {
        [self.delegate didClickShow:showDict];
    }
}


-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return self.resultArray.count + 1;
}


-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionViews cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        QSTimeCollectionViewCell* cell = (QSTimeCollectionViewCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSTimeCollectionViewCell" forIndexPath:indexPath];
        return cell;
    } else {
        QSShowCollectionViewCell* cell = (QSShowCollectionViewCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSShowCollectionViewCell" forIndexPath:indexPath];
        cell.delegate = self;
        NSDictionary* dict = [self getShowDictForIndexPath:indexPath];
        [cell bindData:dict];
        
        return cell;
    }
}
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if ([self.delegate respondsToSelector:@selector(scrollViewDidScroll:)]) {
        [self.delegate scrollViewDidScroll:scrollView];
    }
}
#pragma QSShowCollectionViewCellDelegate
- (void)favorBtnPressed:(QSShowCollectionViewCell*)cell
{
    NSIndexPath* indexPath = [self.collectionView indexPathForCell:cell];
    NSDictionary* showDict = self.resultArray[indexPath.row];
    if ([self.delegate respondsToSelector:@selector(addFavorShow:)]) {
        [self.delegate addFavorShow:showDict];
    }
}
@end

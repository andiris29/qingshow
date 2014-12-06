//
//  QSShowWaterfallDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/6/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSShowCollectionViewDelegateObj.h"
#import "QSTimeCollectionViewCell.h"
#import "QSShowUtil.h"

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
    NSInteger index = indexPath.row;
    if (self.type == QSShowWaterfallDelegateObjTypeWithDate) {
        index -= 1;
    }
    if (index < 0) {
        return nil;
    } else {
        return self.resultArray[index];
    }
}
- (NSIndexPath*)getIndexPathOfShow:(NSDictionary*)showDict
{
    NSInteger index = [self.resultArray indexOfObject:showDict];
    if (self.type == QSShowWaterfallDelegateObjTypeWithDate) {
        index += 1;
    }
    
    return [NSIndexPath indexPathForRow:index inSection:0];
}


- (void)updateShow:(NSDictionary*)showDict
{
    NSIndexPath* i = [self getIndexPathOfShow:showDict];
    [self.collectionView reloadItemsAtIndexPaths:@[i]];
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.type == QSShowWaterfallDelegateObjTypeWithDate && indexPath.row == 0) {
        return CGSizeMake(145, 35);
    } else {
        NSDictionary* dict = [self getShowDictForIndexPath:indexPath];
        float height = [QSShowCollectionViewCell getHeightWithData:dict];
        return CGSizeMake(145, height);
    }
    
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    
    NSDictionary* showDict = [self getShowDictForIndexPath:indexPath];
    if (showDict && [self.delegate respondsToSelector:@selector(didClickShow:)]) {
        [self.delegate didClickShow:showDict];
    }
}


-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    if (self.type == QSShowWaterfallDelegateObjTypeWithDate) {
        return self.resultArray.count + 1;
    } else {
        return self.resultArray.count;
    }
}


-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionViews cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.type == QSShowWaterfallDelegateObjTypeWithDate && indexPath.row == 0) {
        QSTimeCollectionViewCell* cell = (QSTimeCollectionViewCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSTimeCollectionViewCell" forIndexPath:indexPath];
        [cell bindWithMetadata:self.metaDataDict];
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
    if ([super respondsToSelector:@selector(scrollViewDidScroll:)]) {
        [super scrollViewDidScroll:scrollView];
    }
    if ([self.delegate respondsToSelector:@selector(scrollViewDidScroll:)]) {
        [self.delegate scrollViewDidScroll:scrollView];
    }
}
- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
    if ([self.delegate respondsToSelector:@selector(scrollViewWillBeginDragging:)]) {
        [self.delegate scrollViewWillBeginDragging:scrollView];
    }
}

#pragma QSShowCollectionViewCellDelegate
- (void)favorBtnPressed:(QSShowCollectionViewCell*)cell
{
    NSIndexPath* indexPath = [self.collectionView indexPathForCell:cell];
    NSDictionary* showDict = [self getShowDictForIndexPath:indexPath];
    if ([self.delegate respondsToSelector:@selector(addFavorShow:)]) {
        [self.delegate addFavorShow:showDict];
    }
}

- (void)peoplePressed:(QSShowCollectionViewCell*)cell
{
    NSIndexPath* indexPath = [self.collectionView indexPathForCell:cell];
    NSDictionary* showDict = [self getShowDictForIndexPath:indexPath];
    if ([self.delegate respondsToSelector:@selector(didClickPeople:)]) {
        [self.delegate didClickPeople:[QSShowUtil getPeopleFromShow:showDict]];
    }
}

@end

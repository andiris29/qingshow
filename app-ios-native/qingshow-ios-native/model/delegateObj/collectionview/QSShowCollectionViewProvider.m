//
//  QSShowWaterfallDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/6/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSShowCollectionViewProvider.h"
#import "QSTimeCollectionViewCell.h"
#import "QSShowUtil.h"

@interface QSShowCollectionViewProvider ()

@end

@implementation QSShowCollectionViewProvider

#pragma mark - Cell
- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSShowCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSShowCollectionViewCell"];
    [self.view registerNib:[UINib nibWithNibName:@"QSTimeCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSTimeCollectionViewCell"];
}

#pragma mark - UICollecitonView Datasource And Delegate
- (void)refreshClickedData
{
    if (self.clickedData) {
        NSIndexPath* indexPath = [self getIndexPathOfShow:self.clickedData];
        QSShowCollectionViewCell* cell = (QSShowCollectionViewCell*)[self.view cellForItemAtIndexPath:indexPath];
        if (self.filterBlock) {
            if (!self.filterBlock(self.clickedData)) {
                //remove
                [self.resultArray removeObject:self.clickedData];
                [self.view reloadData];
//                [self.collectionView deleteItemsAtIndexPaths:@[indexPath]];
                return;
            }
        }

        [cell bindData:self.clickedData];
        self.clickedData = nil;
    }
}


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
    QSShowCollectionViewCell* cell = (QSShowCollectionViewCell*)[self.view cellForItemAtIndexPath:i];
    [cell bindData:showDict];

}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.type == QSShowWaterfallDelegateObjTypeWithDate && indexPath.row == 0) {
        
        return CGSizeMake(([UIScreen mainScreen].bounds.size.width - 2) / 2, 35);
    } else {
        NSDictionary* dict = [self getShowDictForIndexPath:indexPath];
        return [QSShowCollectionViewCell getSizeWithData:dict];
    }
    
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    
    NSDictionary* showDict = [self getShowDictForIndexPath:indexPath];
    self.clickedData = showDict;
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
        [cell bindWithMetadata:self.metadataDict];
        return cell;
    } else {
        QSShowCollectionViewCell* cell = (QSShowCollectionViewCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSShowCollectionViewCell" forIndexPath:indexPath];
        cell.delegate = self;
        NSDictionary* dict = [self getShowDictForIndexPath:indexPath];
        [cell bindData:dict];
        
        return cell;
    }
}



#pragma QSShowCollectionViewCellDelegate
- (void)favorBtnPressed:(QSShowCollectionViewCell*)cell
{
    NSIndexPath* indexPath = [self.view indexPathForCell:cell];
    NSDictionary* showDict = [self getShowDictForIndexPath:indexPath];
    self.clickedData = showDict;
    if ([self.delegate respondsToSelector:@selector(addFavorShow:)]) {
        [self.delegate addFavorShow:showDict];
    }
}

- (void)peoplePressed:(QSShowCollectionViewCell*)cell
{
    NSIndexPath* indexPath = [self.view indexPathForCell:cell];
    NSDictionary* showDict = [self getShowDictForIndexPath:indexPath];
    self.clickedData = showDict;
    if ([self.delegate respondsToSelector:@selector(didClickPeople:)]) {
        [self.delegate didClickPeople:[QSShowUtil getPeopleFromShow:showDict]];
    }
}

@end

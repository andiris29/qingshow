//
//  QSItemCollectionViewProvider.m
//  qingshow-ios-native
//
//  Created by wxy325 on 2/1/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSItemCollectionViewProvider.h"
#import "QSItemCollectionViewCell.h"
#import "QSTimeCollectionViewCell.h"
@implementation QSItemCollectionViewProvider

#pragma mark - Cell
- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSItemCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSItemCollectionViewCell"];
    [self.view registerNib:[UINib nibWithNibName:@"QSTimeCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSTimeCollectionViewCell"];
}

#pragma mark - UICollecitonView Datasource And Delegate
- (void)refreshClickedData
{
    if (self.clickedData) {
        NSIndexPath* indexPath = [self getIndexPathOfItem:self.clickedData];
        QSItemCollectionViewCell* cell = (QSItemCollectionViewCell*)[self.view cellForItemAtIndexPath:indexPath];
        if (self.filterBlock) {
            if (!self.filterBlock(self.clickedData)) {
                //remove
                [self.resultArray removeObject:self.clickedData];
                [self.view reloadData];
                return;
            }
        }
        
        [cell bindWithItem:self.clickedData];
        self.clickedData = nil;
    }
}

- (NSDictionary*)getItemDictForIndexPath:(NSIndexPath*)indexPath
{
    NSInteger index = indexPath.row;
    if (self.type == QSItemWaterfallDelegateObjTypeWithDate) {
        index -= 1;
    }
    if (index < 0) {
        return nil;
    } else {
        return self.resultArray[index];
    }
}
- (NSIndexPath*)getIndexPathOfItem:(NSDictionary*)itemDict
{
    NSInteger index = [self.resultArray indexOfObject:itemDict];
    if (self.type == QSItemWaterfallDelegateObjTypeWithDate) {
        index += 1;
    }
    
    return [NSIndexPath indexPathForRow:index inSection:0];
}


- (void)updateItem:(NSDictionary*)itemDict
{
    NSIndexPath* i = [self getIndexPathOfItem:itemDict];
    QSItemCollectionViewCell* cell = (QSItemCollectionViewCell*)[self.view cellForItemAtIndexPath:i];
    [cell bindWithItem:itemDict];
    
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.type == QSItemWaterfallDelegateObjTypeWithDate && indexPath.row == 0) {
        
        return CGSizeMake(([UIScreen mainScreen].bounds.size.width - 2) / 2, 35);
    } else {
        NSDictionary* dict = [self getItemDictForIndexPath:indexPath];
        return [QSItemCollectionViewCell getSize:dict];
    }
    
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary* itemDict = [self getItemDictForIndexPath:indexPath];
    self.clickedData = itemDict;
    if (itemDict && [self.delegate respondsToSelector:@selector(didClickItem:)]) {
        [self.delegate didClickItem:itemDict];
    }
}


-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    if (self.type == QSItemWaterfallDelegateObjTypeWithDate) {
        return self.resultArray.count + 1;
    } else {
        return self.resultArray.count;
    }
}


-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionViews cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.type == QSItemWaterfallDelegateObjTypeWithDate && indexPath.row == 0) {
        QSTimeCollectionViewCell* cell = (QSTimeCollectionViewCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSTimeCollectionViewCell" forIndexPath:indexPath];
        [cell bindWithMetadata:self.metadataDict];
        return cell;
    } else {
        QSItemCollectionViewCell* cell = (QSItemCollectionViewCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSItemCollectionViewCell" forIndexPath:indexPath];
        NSDictionary* dict = [self getItemDictForIndexPath:indexPath];
        [cell bindWithItem:dict];
        
        return cell;
    }
}




@end

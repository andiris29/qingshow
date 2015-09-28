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


#define w ([UIScreen mainScreen].bounds.size.width)
#define h ([UIScreen mainScreen].bounds.size.height)
@interface QSShowCollectionViewProvider ()

@end

@implementation QSShowCollectionViewProvider
@dynamic delegate;
#pragma mark - Init
- (id)init
{
    self = [super init];
    if (self) {
        self.type = QSShowProviderTypeNew;
    }
    return self;
}

#pragma mark - Cell
- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSShowCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSShowCollectionViewCell"];
    [self.view registerNib:[UINib nibWithNibName:@"QSTimeCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSTimeCollectionViewCell"];
    [self.view registerNib:[UINib nibWithNibName:@"QSMatchShowsCell"  bundle:nil] forCellWithReuseIdentifier:@"QSMatchShowsCellWithU01"];
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
    if (self.type == QSShowProviderTypeWithDate) {
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
    if (self.type == QSShowProviderTypeWithDate) {
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
    if (self.type == QSShowProviderTypeWithDate && indexPath.row == 0) {
        
        return CGSizeMake(([UIScreen mainScreen].bounds.size.width - 2) / 2, 35);
    }
    else if(self.type == QSShowProviderTypeNew){
        return CGSizeMake([UIScreen mainScreen].bounds.size.width/2-10, w);
    }
    else{
        NSDictionary* dict = [self getShowDictForIndexPath:indexPath];
        return [QSShowCollectionViewCell getSizeWithData:dict];
    }
    
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    
    NSDictionary* showDict = [self getShowDictForIndexPath:indexPath];
    self.clickedData = showDict;
    if (showDict && [self.delegate respondsToSelector:@selector(didSelectedCellInCollectionView:provider:)]) {
        [self.delegate didSelectedCellInCollectionView:showDict provider:self];
    }
}


-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    if (self.type == QSShowProviderTypeWithDate) {
        return self.resultArray.count + 1;
    } else {
        return self.resultArray.count;
    }
}


-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionViews cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.type == QSShowProviderTypeWithDate && indexPath.row == 0) {
        QSTimeCollectionViewCell* cell = (QSTimeCollectionViewCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSTimeCollectionViewCell" forIndexPath:indexPath];
        [cell bindWithMetadata:self.metadataDict];
        return cell;
    } else if(self.type == QSShowProviderTypeNew)
    {
        QSMatchShowsCell* cell = (QSMatchShowsCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSMatchShowsCellWithU01" forIndexPath:indexPath];
        cell.delegate = self;
        [cell bindWithDic:self.resultArray[indexPath.item] withIndex:(int)indexPath.item];
                if (w == 414) {
                    cell.contentView.transform = CGAffineTransformMakeScale(w/(320-15), w/(320-12));
                }
                else
                {
                    cell.contentView.transform = CGAffineTransformMakeScale(w/(320-15), w/(320-16));
                }
        
                cell.backgroundColor = [UIColor whiteColor];

        return (UICollectionViewCell *)cell;

    }
    else
    {
        QSShowCollectionViewCell* cell = (QSShowCollectionViewCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSShowCollectionViewCell" forIndexPath:indexPath];
        cell.delegate = self;
        cell.backgroundColor = [UIColor whiteColor];
        cell.contentView.transform = CGAffineTransformMakeScale([UIScreen mainScreen].bounds.size.width/320, [UIScreen mainScreen].bounds.size.width/320);
        NSDictionary* dict = [self getShowDictForIndexPath:indexPath];
        [cell bindData:dict];
        return cell;
    }
}
#pragma mark - QSMatchShowsCellDelegate
- (void)headerImgViewPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didClickHeaderImgView:)]) {
        [self.delegate didClickHeaderImgView:sender];
    }
}
- (void)matchImgViewPressed:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didSelectedCellInCollectionView:provider:)]) {
        [self.delegate  didSelectedCellInCollectionView:sender provider:self];
    }
}


#pragma QSShowCollectionViewCellDelegate
- (void)favorBtnPressed:(QSShowCollectionViewCell*)cell
{
    NSIndexPath* indexPath = [self.view indexPathForItemAtPoint:cell.center];
    NSDictionary* showDict = [self getShowDictForIndexPath:indexPath];
    self.clickedData = showDict;
    if ([self.delegate respondsToSelector:@selector(addFavorShow:provider:)]) {
        [self.delegate addFavorShow:showDict provider:self];
    }
}

- (void)playBtnPressed:(QSShowCollectionViewCell *)cell
{
    NSIndexPath* indexPath = [self.view indexPathForItemAtPoint:cell.center];
    NSDictionary* showDict = [self getShowDictForIndexPath:indexPath];
    self.clickedData = showDict;
    if ([self.delegate respondsToSelector:@selector(didClickPlayButtonOfShow: provider:)]) {
        [self.delegate didClickPlayButtonOfShow:showDict provider:self];
    }
}

@end

//
//  QSBrandCollectionViewDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSBrandCollectionViewDelegateObj.h"
#import "QSBrandListCollectionViewCell.h"
@implementation QSBrandCollectionViewDelegateObj
- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSBrandListCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSBrandListCollectionViewCell"];
}


#pragma mark - Collection View
-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionViews cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    
    QSBrandListCollectionViewCell* cell = (QSBrandListCollectionViewCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSBrandListCollectionViewCell" forIndexPath:indexPath];
    NSDictionary* dict = self.resultArray[indexPath.row];
    [cell bindWithBrandDict:dict];
    return cell;
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [collectionView deselectItemAtIndexPath:indexPath animated:YES];
    NSDictionary* brandDict = self.resultArray[indexPath.row];
    self.clickedData = brandDict;
    if ([self.delegate respondsToSelector:@selector(didClickBrand:)]) {
        [self.delegate didClickBrand:brandDict];
    }
}
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return CGSizeMake(150, 200);

}

- (void)refreshClickedData
{
    if (self.clickedData) {
        NSUInteger row = [self.resultArray indexOfObject:self.clickedData];
        
        NSIndexPath* indexPath = [NSIndexPath indexPathForItem:row inSection:0];
        QSBrandListCollectionViewCell* cell = (QSBrandListCollectionViewCell*)[self.view cellForItemAtIndexPath:indexPath];
        [cell bindWithBrandDict:self.clickedData];
        self.clickedData = nil;
    }
}

@end

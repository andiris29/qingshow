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
    [self.collectionView registerNib:[UINib nibWithNibName:@"QSBrandListCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSBrandListCollectionViewCell"];
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
    if ([self.delegate respondsToSelector:@selector(didClickBrand:)]) {
        [self.delegate didClickBrand:self.resultArray[indexPath.row]];
    }
}
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return CGSizeMake(150, 200);

}
@end

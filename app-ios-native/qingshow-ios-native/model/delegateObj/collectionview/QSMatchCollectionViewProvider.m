//
//  QSMatchCollectionViewProvider.m
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSMatchCollectionViewProvider.h"

#define MATCHCELL @"matchShowsCell"
@implementation QSMatchCollectionViewProvider


- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSMatchShowsCell" bundle:nil] forCellWithReuseIdentifier:MATCHCELL];
}
- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return nil;
}
- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    
}

@end

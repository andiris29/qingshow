//
//  QSWaterFallCollectionViewLayout.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSWaterFallCollectionViewLayout.h"
#define ITEM_SIZE 70
@implementation QSWaterFallCollectionViewLayout
-(void)prepareLayout

{
    [super prepareLayout];
    self.itemWidth=158;
    self.sectionInset=UIEdgeInsetsMake(3, 0, 5, 4);
    self.delegate = (id<QSWaterFallLayoutDelegate>)self.collectionView.delegate;
    CGSize size = self.collectionView.frame.size;
    _cellCount = [[self collectionView] numberOfItemsInSection:0];
    _center = CGPointMake(size.width / 2.0, size.height / 2.0);
    _radius = MIN(size.width, size.height) / 2.5;
}

-(CGSize)collectionViewContentSize
{
    return CGSizeMake(320, (leftY>rightY?leftY:rightY));
}

- (UICollectionViewLayoutAttributes *)layoutAttributesForItemAtIndexPath:(NSIndexPath *)indexPath  withIndex:(int)index
{
    CGSize itemSize = [self.delegate collectionView:self.collectionView layout:self sizeForItemAtIndexPath:indexPath];
    CGFloat itemHeight = floorf(itemSize.height * self.itemWidth / itemSize.width);
    UICollectionViewLayoutAttributes *attributes = [UICollectionViewLayoutAttributes layoutAttributesForCellWithIndexPath:indexPath];

    if (leftY <= rightY) {
        //Left
        x = 0;
        leftY += self.sectionInset.top;
        attributes.frame = CGRectMake(x, leftY, self.itemWidth, itemHeight);
        leftY += itemHeight;
    } else {
        //Right
        x = (self.itemWidth + 4);
        rightY += self.sectionInset.top;
        attributes.frame = CGRectMake(x, rightY, self.itemWidth, itemHeight);
        rightY += itemHeight;
    }
    
//    index += 1;
//    if (index % 2 == 0)
//    {
//        x += (self.itemWidth + 4);
//        rightY += self.sectionInset.top;
//        attributes.frame = CGRectMake(x, rightY, self.itemWidth, itemHeight);
//        rightY += itemHeight;
//    }
//    else
//    {
//        x = 0;
//        leftY += self.sectionInset.top;
//        attributes.frame = CGRectMake(x, leftY, self.itemWidth, itemHeight);
//        leftY += itemHeight;
//    }
    return attributes;
}

-(NSArray*)layoutAttributesForElementsInRect:(CGRect)rect
{
    x = 0;
    leftY = 0;
    rightY=0;
    NSMutableArray* attributes = [NSMutableArray array];
    
    for (NSInteger i=0 ; i < self.cellCount; i++) {
        
        NSIndexPath* indexPath = [NSIndexPath indexPathForItem:i inSection:0];
        [attributes addObject:[self layoutAttributesForItemAtIndexPath:indexPath withIndex:i]];
    }
    return attributes;
}


- (UICollectionViewLayoutAttributes *)initialLayoutAttributesForInsertedItemAtIndexPath:(NSIndexPath *)itemIndexPath
{
    UICollectionViewLayoutAttributes* attributes = [self layoutAttributesForItemAtIndexPath:itemIndexPath];
    attributes.alpha = 0.0;
    attributes.center = CGPointMake(_center.x, _center.y);
    return attributes;
}

- (UICollectionViewLayoutAttributes *)finalLayoutAttributesForDeletedItemAtIndexPath:(NSIndexPath *)itemIndexPath
{
    UICollectionViewLayoutAttributes* attributes = [self layoutAttributesForItemAtIndexPath:itemIndexPath];
    attributes.alpha = 0.0;
    attributes.center = CGPointMake(_center.x, _center.y);
    attributes.transform3D = CATransform3DMakeScale(0.1, 0.1, 1.0);
    return attributes;
}
@end

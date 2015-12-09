//
//  QSHorizontalCollectionViewLayout.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSHorizontalCollectionViewLayout.h"

@interface QSHorizontalCollectionViewLayout ()

@property (assign, nonatomic) NSInteger cellCount;
@property (assign, nonatomic) CGFloat verticalSpace;
@end

@implementation QSHorizontalCollectionViewLayout

- (void)setItemHeight:(CGFloat)itemHeight {
    _itemHeight = itemHeight;

}
- (void)prepareLayout {
    [super prepareLayout];
    self.cellCount = [self.collectionView numberOfItemsInSection:0];
    self.verticalSpace = (self.collectionView.bounds.size.height - self.itemHeight) / 2;
}

- (CGSize)collectionViewContentSize {
    return CGSizeMake(self.cellCount * (self.itemWidth + self.horizontalSpace), self.collectionView.bounds.size.height - self.verticalSpace * 2);
}

- (UICollectionViewLayoutAttributes *)layoutAttributesForItemAtIndexPath:(NSIndexPath *)indexPath {
    
    UICollectionViewLayoutAttributes *attributes = [UICollectionViewLayoutAttributes layoutAttributesForCellWithIndexPath:indexPath];
    CGFloat x = self.horizontalSpace / 2 + indexPath.item * (self.itemWidth + self.horizontalSpace);
    attributes.frame = CGRectMake(x, self.verticalSpace, self.itemWidth, self.itemHeight);
    return attributes;
    
}


-(NSArray*)layoutAttributesForElementsInRect:(CGRect)rect
{
    NSMutableArray* attributes = [NSMutableArray array];
    
    for (NSInteger i=0 ; i < self.cellCount; i++) {
        
        NSIndexPath* indexPath = [NSIndexPath indexPathForItem:i inSection:0];
        [attributes addObject:[self layoutAttributesForItemAtIndexPath:indexPath]];
    }
    return attributes;
}

- (UICollectionViewLayoutAttributes *)initialLayoutAttributesForInsertedItemAtIndexPath:(NSIndexPath *)itemIndexPath
{
    UICollectionViewLayoutAttributes* attributes = [self layoutAttributesForItemAtIndexPath:itemIndexPath];
    attributes.alpha = 0.0;
    attributes.center = CGPointMake(self.itemWidth / 2, self.itemHeight / 2 + self.verticalSpace);
    return attributes;
}

- (UICollectionViewLayoutAttributes *)finalLayoutAttributesForDeletedItemAtIndexPath:(NSIndexPath *)itemIndexPath
{
    UICollectionViewLayoutAttributes* attributes = [self layoutAttributesForItemAtIndexPath:itemIndexPath];
    attributes.alpha = 0.0;
    attributes.center = CGPointMake(self.itemWidth / 2, self.itemHeight / 2 + self.verticalSpace);
    attributes.transform3D = CATransform3DMakeScale(0.1, 0.1, 1.0);
    return attributes;
}

@end

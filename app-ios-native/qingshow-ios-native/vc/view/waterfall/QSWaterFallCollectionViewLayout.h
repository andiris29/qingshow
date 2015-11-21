//
//  QSWaterFallCollectionViewLayout.h
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#pragma mark QSWaterFallLayoutDelegate
@protocol QSWaterFallLayoutDelegate <UICollectionViewDelegate>
@required
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath;
@optional
- (CGFloat)heightForHeaderInCollectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout;

@end


@interface QSWaterFallCollectionViewLayout : UICollectionViewLayout
{
    float x;
    float leftY;
    float rightY;
}
@property float itemWidth;
@property (nonatomic, assign) CGPoint center;
@property (nonatomic, assign) CGFloat radius;
@property (nonatomic, assign) NSInteger cellCount;

/// The delegate will point to collection view's delegate automatically.
@property (nonatomic, weak) id <QSWaterFallLayoutDelegate> delegate;
/// Array to store attributes for all items includes headers, cells, and footers
@property (nonatomic, strong) NSMutableArray *allItemAttributes;
@property (nonatomic, assign) UIEdgeInsets sectionInset;
@end

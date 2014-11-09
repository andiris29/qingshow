//
//  QSShowWaterfallDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/6/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSBlock.h"
#import "QSWaterFallCollectionViewCell.h"
#import "QSWaterFallCollectionViewLayout.h"

@protocol QSShowWaterfallDelegateObjDelegate <NSObject>

@optional
- (void)scrollViewDidScroll:(UIScrollView *)scrollView;

@end

typedef NS_ENUM(NSInteger, QSShowWaterfallDelegateObjType) {
    QSShowWaterfallDelegateObjTypeWithDate,
    QSShowWaterfallDelegateObjTypeWithoutDate
};

@interface QSShowWaterfallDelegateObj : NSObject<QSWaterFallLayoutDelegate, UICollectionViewDataSource, UICollectionViewDelegate, QSWaterFallCollectionViewCellDelegate, UIScrollViewDelegate>

@property (strong, nonatomic) NSMutableArray* resultArray;
@property (assign, nonatomic) QSShowWaterfallDelegateObjType type;

@property (strong, nonatomic) ArrayNetworkBlock networkBlock;
@property (weak, nonatomic) NSObject<QSShowWaterfallDelegateObjDelegate>* delegate;

- (void)bindWithCollectionView:(UICollectionView*)collectionView;
- (void)fetchDataOfPage:(int)page;
@end

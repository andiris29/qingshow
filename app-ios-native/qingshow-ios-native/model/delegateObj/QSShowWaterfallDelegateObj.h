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

typedef NS_ENUM(NSInteger, QSShowWaterfallDelegateObjType) {
    QSShowWaterfallDelegateObjTypeWithDate,
    QSShowWaterfallDelegateObjTypeWithoutDate
};

@interface QSShowWaterfallDelegateObj : NSObject<QSWaterFallLayoutDelegate, UICollectionViewDataSource, UICollectionViewDelegate, QSWaterFallCollectionViewCellDelegate>

@property (strong, nonatomic) NSMutableArray* resultArray;
@property (assign, nonatomic) QSShowWaterfallDelegateObjType type;

@property (strong, nonatomic) ArrayNetworkBlock networkBlock;


- (void)bindWithCollectionView:(UICollectionView*)collectionView;
- (void)fetchDataOfPage:(int)page;
@end

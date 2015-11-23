//
//  QSWaterfallBasicDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSWaterFallCollectionViewLayout.h"
#import "QSAbstractListViewProvider.h"
#import "QSBlock.h"

@interface QSWaterfallBasicProvider : QSAbstractListViewProvider <UICollectionViewDataSource, UICollectionViewDelegate,QSWaterFallLayoutDelegate, UIScrollViewDelegate>

- (void)bindWithCollectionView:(UICollectionView*)collectionView;

#pragma mark - Private
@property (weak, nonatomic) UICollectionView* view;
@property (strong, nonatomic) QSWaterFallCollectionViewLayout* collectionViewLayout;
#pragma mark - Virtual Method
- (void)registerCell;
@end

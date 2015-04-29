//
//  QSFashionCollectionViewProvider.h
//  qingshow-ios-native
//
//  Created by ching show on 15/4/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//
#import <Foundation/Foundation.h>
#import "QSAbstractListViewProvider.h"

@interface QSFashionCollectionViewProvider : QSAbstractListViewProvider<UICollectionViewDataSource, UICollectionViewDelegate, UICollectionViewDelegateFlowLayout>
- (void)bindWithCollectionView:(UICollectionView*)collectionView;

#pragma mark - Private
@property (strong, nonatomic) UICollectionView* view;

#pragma mark - Virtual Method
- (void)registerCell;

@end

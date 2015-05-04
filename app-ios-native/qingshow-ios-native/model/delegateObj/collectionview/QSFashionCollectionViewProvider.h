//
//  QSFashionCollectionViewProvider.h
//  qingshow-ios-native
//
//  Created by ching show on 15/4/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//
#import <Foundation/Foundation.h>
#import "QSAbstractListViewProvider.h"

@protocol QSFashionDelegate <NSObject>

@optional
- (void)didClickShow:(NSDictionary*)showDict;

@end

@interface QSFashionCollectionViewProvider : QSAbstractListViewProvider


- (void)bindWithCollectionView:(UICollectionView*)collectionView;




#pragma mark - Private
@property (strong, nonatomic) UICollectionView* view;
@property (weak, nonatomic)NSObject<QSFashionDelegate> *delegate;
#pragma mark - Virtual Method
- (void)registerCell;

@end

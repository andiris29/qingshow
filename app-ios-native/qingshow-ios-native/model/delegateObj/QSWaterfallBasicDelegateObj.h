//
//  QSWaterfallBasicDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSWaterFallCollectionViewLayout.h"
#import "QSBlock.h"

@protocol QSWaterfallBasicDelegateObjDelegate <NSObject>

@optional
- (void)handleNetworkError:(NSError*)error;

@end

@interface QSWaterfallBasicDelegateObj : NSObject <UICollectionViewDataSource, UICollectionViewDelegate,QSWaterFallLayoutDelegate, UIScrollViewDelegate>

@property (readonly, nonatomic) NSMutableArray* resultArray;
@property (strong, nonatomic) ArrayNetworkBlock networkBlock;
@property (weak, nonatomic) NSObject<QSWaterfallBasicDelegateObjDelegate>* delegate;
@property (strong, nonatomic) NSDictionary* metaDataDict;




- (void)bindWithCollectionView:(UICollectionView*)collectionView;
- (void)reloadData;

@property (strong, nonatomic) NSDictionary* clickedData;
- (void)refreshClickedData;

- (MKNetworkOperation*)fetchDataOfPage:(int)page;

#pragma mark - Private
@property (readonly, nonatomic) UICollectionView* collectionView;
@property (readonly, nonatomic) int currentPage;

#pragma mark - Virtual Method
- (void)registerCell;

@end

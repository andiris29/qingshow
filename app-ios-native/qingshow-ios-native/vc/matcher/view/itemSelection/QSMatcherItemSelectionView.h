   //
//  QSMatcherItemSelectionView.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/22/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@class QSMatcherItemSelectionView;

@protocol QSMatcherItemSelectionViewDelegate <NSObject>
@optional
- (void)selectionView:(QSMatcherItemSelectionView*)view didSelectItemAtIndex:(NSUInteger)index;
- (void)selectionViewDidReachEnd:(QSMatcherItemSelectionView *)view;
@end

@protocol QSMatcherItemSelectionViewDataSource <NSObject>

- (NSUInteger)numberOfItemInSelectionView:(QSMatcherItemSelectionView*)view;
- (NSDictionary*)selectionView:(QSMatcherItemSelectionView*)view itemDictAtIndex:(NSUInteger)index;

@end

@interface QSMatcherItemSelectionView : UIView <UIScrollViewDelegate>

@property (weak, nonatomic) NSObject<QSMatcherItemSelectionViewDelegate>* delegate;
@property (weak, nonatomic) NSObject<QSMatcherItemSelectionViewDataSource>* datasource;
@property (assign, nonatomic) int selectIndex;
- (void)reloadData;
- (void)offsetToZero:(BOOL)fAnimate;


+ (instancetype)generateView;


@end

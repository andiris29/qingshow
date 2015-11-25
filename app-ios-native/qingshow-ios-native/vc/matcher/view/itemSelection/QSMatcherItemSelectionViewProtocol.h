//
//  QSMatcherItemSelectionViewProtocol.h
//  qingshow-ios-native
//
//  Created by wxy325 on 7/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

@protocol QSMatcherItemSelectionViewDelegate <NSObject>
@optional
- (void)selectionView:(UIView*)view didSelectItemAtIndex:(NSUInteger)index;
- (void)selectionViewDidReachEnd:(UIView*)view;
@end

@protocol QSMatcherItemSelectionViewDataSource <NSObject>

- (NSUInteger)numberOfItemInSelectionView:(UIView*)view;
- (NSDictionary*)selectionView:(UIView*)view itemDictAtIndex:(NSUInteger)index;
@optional
- (BOOL)selectionView:(UIView*)view hasSelectItemId:(NSString*)itemId;
@end


@protocol QSMatcherItemSelectionViewProtocol <NSObject>

@property (assign, nonatomic) int selectIndex;
- (void)reloadData;
- (void)offsetToZero:(BOOL)fAnimate;
@optional
- (void)showSelect:(BOOL)fAnimate;

@property (weak, nonatomic) NSObject<QSMatcherItemSelectionViewDelegate>* delegate;
@property (weak, nonatomic) NSObject<QSMatcherItemSelectionViewDataSource>* datasource;

@end



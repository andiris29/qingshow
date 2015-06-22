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

- (void)selectionView:(QSMatcherItemSelectionView*)view didSelectItemAtIndex:(NSUInteger)index;

@end

@protocol QSMatcherItemSelectionViewDataSource <NSObject>

- (NSUInteger)numberOfItemInSelectionView:(QSMatcherItemSelectionView*)view;
- (NSDictionary*)selectionView:(QSMatcherItemSelectionView*)view itemDictAtIndex:(NSUInteger)index;

@end

@interface QSMatcherItemSelectionView : UIView

@property (weak, nonatomic) NSObject<QSMatcherItemSelectionViewDelegate>* delegate;
@property (weak, nonatomic) NSObject<QSMatcherItemSelectionViewDataSource>* datasource;

- (void)reloadData;

+ (instancetype)generateView;


@end

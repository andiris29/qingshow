//
//  QSMatcherItemsProvider.h
//  qingshow-ios-native
//
//  Created by wxy325 on 6/25/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSMatcherItemSelectionViewProtocol.h"
#import "QSMatcherItemPageSelectionView.h"


@class QSMatcherItemsProvider;

@protocol QSMatcherItemsProviderDelegate

- (void)matcherItemProvider:(QSMatcherItemsProvider*)provider ofCategory:(NSDictionary*)categoryDict didSelectItem:(NSDictionary*)itemDict;
- (void)matcherItemProvider:(QSMatcherItemsProvider*)provider didFinishNetworkLoading:(NSDictionary*)categoryDict;

@end

@interface QSMatcherItemsProvider : NSObject <QSMatcherItemSelectionViewDataSource, QSMatcherItemSelectionViewDelegate>

- (instancetype)initWithCategory:(NSDictionary*)categoryDict;

@property (strong, nonatomic) NSDictionary* categoryDict;
@property (assign, nonatomic) NSUInteger selectIndex;
@property (strong, nonatomic) NSMutableArray* resultArray;
@property (weak, nonatomic) NSObject<QSMatcherItemsProviderDelegate>* delegate;
@property (weak, nonatomic) NSMutableArray* selectItemIds;
@property (assign, nonatomic) BOOL fIsFirst;
- (void)reloadData;

@end

//
//  QSAbstractScrollDelegateObj.h
//  qingshow-ios-native
//
//  Created by wxy325 on 1/25/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QSBlock.h"

@protocol QSAbstractScrollProviderDelegate <NSObject>

@optional
- (void)handleNetworkError:(NSError*)error;
- (void)scrollViewDidScroll:(UIScrollView *)scrollView;
- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView;
- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerat;
- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView;
@end

@interface QSAbstractListViewProvider : NSObject <UIScrollViewDelegate>

@property (strong, nonatomic) NSMutableArray* resultArray;
@property (strong, nonatomic) NSDictionary* metadataDict;
@property (strong, nonatomic) ArrayNetworkBlock networkBlock;
@property (strong, nonatomic) FilterBlock filterBlock;
@property (assign, nonatomic) int currentPage;
@property (strong, nonatomic) NSDictionary* clickedData;
@property (weak, nonatomic) UIScrollView* view;

@property (weak, nonatomic) NSObject<QSAbstractScrollProviderDelegate>* delegate;

@property (assign, nonatomic) BOOL hasRefreshControl;
@property (assign, nonatomic) BOOL hasPaging;

@property (strong, nonatomic) NSArray* additionalResult;

@property (strong, nonatomic) VoidBlock networkDataFinalHandlerBlock;

#pragma mark - Network
- (void)reloadData;
- (void)reloadDataOnCompletion:(VoidBlock)block;
- (void)refreshData:(NSDictionary*)dict;
- (MKNetworkOperation*)fetchDataOfPage:(int)page;
- (MKNetworkOperation*)fetchDataOfPage:(int)page completion:(VoidBlock)block;

- (NSString*)getTotalCountDesc;

- (void)refreshClickedData;
- (void)refreshVisibleData;
- (void)cancelImageLoading;

#pragma mark - Refresh Control
- (void)addRefreshControl;
- (void)didPullRefreshControl:(UIRefreshControl*)refreshControl;
- (void)refreshWithAnimation;
@end

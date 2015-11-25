//
//  QSMatcherItemsProvider.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/25/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSMatcherItemsProvider.h"
#import "QSNetworkKit.h"
#import "QSEntityUtil.h"


@interface QSMatcherItemsProvider ()

@property (assign, nonatomic) int currentPage;
@property (strong, nonatomic) MKNetworkOperation* loadMoreOp;
@property (strong, nonatomic) MKNetworkOperation* reloadOp;
@end

@implementation QSMatcherItemsProvider

- (instancetype)initWithCategory:(NSDictionary*)categoryDict {
    self = [super init];
    if (self) {
        self.categoryDict = categoryDict;
        self.resultArray = [@[] mutableCopy];
        self.fIsFirst = YES;
    }
    return self;
}


#pragma mark - QSMatcherItemSelectionView
- (NSUInteger)numberOfItemInSelectionView:(UIView*)view {
    return self.resultArray.count;
}
- (NSDictionary*)selectionView:(UIView*)view itemDictAtIndex:(NSUInteger)index {
    return self.resultArray[index];
}
- (BOOL)selectionView:(UIView*)view hasSelectItemId:(NSString*)itemId {
    return [self.selectItemIds indexOfObject:itemId] != NSNotFound;
}

- (void)selectionView:(UIView*)view didSelectItemAtIndex:(NSUInteger)index {
    self.selectIndex = index;
    if (index >= self.resultArray.count) {
        return;
    }
    
    NSDictionary* item = self.resultArray[index];
    if ([self.delegate respondsToSelector:@selector(matcherItemProvider:ofCategory:didSelectItem:)]) {
        [self.delegate matcherItemProvider:self ofCategory:self.categoryDict didSelectItem:item];
    }
}
- (void)selectionViewDidReachEnd:(UIView*)view {
    if (self.reloadOp || self.loadMoreOp) {
        return;
    }
    
    
    __block int nextPage = self.currentPage + 1;
    self.loadMoreOp = [SHARE_NW_ENGINE matcherQueryItemsCategory:self.categoryDict page:nextPage onSucceed:^(NSArray *array, NSDictionary *metadata) {
        self.currentPage = nextPage;
        
        [self.resultArray addObjectsFromArray:array];
        
        if ([self.delegate respondsToSelector:@selector(matcherItemProvider:didFinishNetworkLoading:)]) {
            [self.delegate matcherItemProvider:self didFinishNetworkLoading:self.categoryDict];
        }
        self.loadMoreOp = nil;
    } onError:^(NSError *error) {
        self.loadMoreOp = nil;
    }];
    
}


- (void)reloadData {
    self.currentPage = 1;
    if (self.reloadOp) {
        return;
    }
    if (self.loadMoreOp) {
        [self.loadMoreOp cancel];
        self.loadMoreOp = nil;
    }

    self.reloadOp = [SHARE_NW_ENGINE matcherQueryItemsCategory:self.categoryDict page:self.currentPage onSucceed:^(NSArray *array, NSDictionary *metadata) {
        [self.resultArray removeAllObjects];
        [self.resultArray addObjectsFromArray:array];
        
        self.selectIndex = 0;
        if (self.resultArray.count) {
            if ([self.delegate respondsToSelector:@selector(matcherItemProvider:ofCategory:didSelectItem:)]) {
                [self.delegate matcherItemProvider:self ofCategory:self.categoryDict didSelectItem:self.resultArray[0]];
            }
        }
        
        if ([self.delegate respondsToSelector:@selector(matcherItemProvider:didFinishNetworkLoading:)]) {
            [self.delegate matcherItemProvider:self didFinishNetworkLoading:self.categoryDict];
        }
        self.reloadOp = nil;
    } onError:^(NSError *error) {
        self.reloadOp = nil;
    }];
}
@end

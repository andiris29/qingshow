//
//  QSMatcherItemsProvider.m
//  qingshow-ios-native
//
//  Created by wxy325 on 6/25/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSMatcherItemsProvider.h"
#import "QSNetworkKit.h"
#import "QSCommonUtil.h"


@interface QSMatcherItemsProvider ()

@property (assign, nonatomic) int currentPage;
@end

@implementation QSMatcherItemsProvider

- (instancetype)initWithCategory:(NSDictionary*)categoryDict {
    self = [super init];
    if (self) {
        self.categoryDict = categoryDict;
        self.resultArray = [@[] mutableCopy];
    }
    return self;
}


#pragma mark - QSMatcherItemSelectionView
- (NSUInteger)numberOfItemInSelectionView:(QSMatcherItemSelectionView*)view {
    return self.resultArray.count;
}
- (NSDictionary*)selectionView:(QSMatcherItemSelectionView*)view itemDictAtIndex:(NSUInteger)index {
    return self.resultArray[index];
}

- (void)selectionView:(QSMatcherItemSelectionView*)view didSelectItemAtIndex:(NSUInteger)index {

    NSDictionary* item = self.resultArray[index];
    if ([self.delegate respondsToSelector:@selector(matcherItemProvider:ofCategory:didSelectItem:)]) {
        [self.delegate matcherItemProvider:self ofCategory:self.categoryDict didSelectItem:item];
    }
}


- (void)reloadData {
    [SHARE_NW_ENGINE matcherQueryItemsCategory:self.categoryDict page:1 onSucceed:^(NSArray *array, NSDictionary *metadata) {
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
    } onError:nil];
}
@end

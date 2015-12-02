//
//  QSWaterfallBasicDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSWaterfallBasicProvider.h"
#import "MKNetworkOperation.h"
#import "QSMetadataUtil.h"
#import "NSNumber+QSExtension.h"
#import "QSAbstractListViewProvider+Protect.h"
#import "UIViewController+ShowHud.h"
#import "QSIImageLoadingCancelable.h"
@interface QSWaterfallBasicProvider ()

@end


@implementation QSWaterfallBasicProvider
@dynamic view;
#pragma mark - Getter And Setter
- (void)setView:(UICollectionView *)view {
    if (view != self.view) {
        [self cancelImageLoading];
    }
    [super setView:view];
}

#pragma mark - Method To Be Override
- (void)registerCell
{
    return;
}

-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionViews cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return nil;
}

#pragma mark - Init

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return CGSizeZero;
}

- (void)dealloc
{
    self.view.dataSource = nil;
    self.view.delegate = nil;
}

#pragma mark - Config
- (void)refreshClickedData
{}

- (void)bindWithCollectionView:(UICollectionView *)collectionView
{
    self.view = collectionView;
    self.view.dataSource = self;
    self.view.delegate = self;
    collectionView.alwaysBounceVertical = YES;
    collectionView.showsVerticalScrollIndicator = NO;
    
    QSWaterFallCollectionViewLayout* layout = [[QSWaterFallCollectionViewLayout alloc] init];
    self.view.collectionViewLayout = layout;
    self.collectionViewLayout = layout;
    self.view.translatesAutoresizingMaskIntoConstraints = NO;
    
    self.view.scrollEnabled=YES;
    self.view.backgroundColor=[UIColor colorWithRed:242.f/255.f green:242.f/255.f blue:242.f/255.f alpha:1.f];

    [self registerCell];
    
    [self addRefreshControl];

}


#pragma mark - Network
- (MKNetworkOperation*)fetchDataOfPage:(int)page completion:(VoidBlock)block
{
    __weak QSWaterfallBasicProvider* weakSelf = self;

    return [self fetchDataOfPage:page viewRefreshBlock:^{
        if (page == 1) {
            [UIView performWithoutAnimation:^{
                [self.view reloadData];
            }];

        } else {
            NSMutableArray* indexPaths = [@[] mutableCopy];
            NSUInteger preCount = 0;
            if (page != 1) {
                preCount = weakSelf.resultArray.count;
            }
            for (NSUInteger i = preCount; i < self.resultArray.count; i++){
                [indexPaths addObject:[NSIndexPath indexPathForItem:i inSection:0]];
            }
            if (indexPaths.count) {
                [UIView performWithoutAnimation:^{
                    [self.view performBatchUpdates:^{
                        [self.view insertItemsAtIndexPaths:indexPaths];
                    } completion:nil];
                }];

            }

        }
    } completion:block];
}

#pragma mark - UICollecitonView Datasource And Delegate

//margin
-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    return UIEdgeInsetsMake(10, 10, 10, 10);
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [collectionView deselectItemAtIndexPath:indexPath animated:YES];
}


-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return self.resultArray.count;
}
- (void)cancelImageLoading {
    NSArray* cells = [self.view visibleCells];
    for (NSObject* c in cells) {
        if ([c conformsToProtocol:@protocol(QSIImageLoadingCancelable)]) {
            NSObject<QSIImageLoadingCancelable>* i = (NSObject<QSIImageLoadingCancelable>*)c;
            [i cancelImageLoading];
        }
    }
}
- (void)refreshVisibleData {
    [self.view reloadData];
}
@end


//
//  QSFashionCollectionViewProvider.m
//  qingshow-ios-native
//
//  Created by ching show on 15/4/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSFashionCollectionViewProvider.h"

#import "QSS08FashionCollectionViewCell.h"

@implementation QSFashionCollectionViewProvider
#pragma mark - Method To Be Override
- (void)registerCell
{
    return [self.view registerNib:[UINib nibWithNibName:@"QSS08FashionCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:FASHION_COLLECTION_VIEW_INDENTIFIER];
}


#pragma mark - Init

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
#warning TODO Return Size Of Cell
    return CGSizeMake([UIScreen mainScreen].bounds.size.width - 10, 200);
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
    
    UICollectionViewLayout* layout = [[UICollectionViewLayout alloc] init];
    self.view.collectionViewLayout = layout;
    self.view.translatesAutoresizingMaskIntoConstraints = NO;
    
    self.view.scrollEnabled=YES;
    self.view.backgroundColor=[UIColor colorWithRed:242.f/255.f green:242.f/255.f blue:242.f/255.f alpha:1.f];
    
    [self registerCell];
    
    [self addRefreshControl];
    
}
//-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionViews cellForItemAtIndexPath:(NSIndexPath *)indexPath
//{
//   // [self bindWithCollectionView:collectionViews];
//    QSS08FashionCollectionViewCell *cell = (QSS08FashionCollectionViewCell *)[collectionViews dequeueReusableCellWithReuseIdentifier:FASHION_COLLECTION_VIEW_INDENTIFIER forIndexPath:indexPath];
//    
//    return cell;
//}
- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    QSS08FashionCollectionViewCell *cell = (QSS08FashionCollectionViewCell *)[collectionView dequeueReusableCellWithReuseIdentifier:FASHION_COLLECTION_VIEW_INDENTIFIER forIndexPath:indexPath];
    
    return cell;
}

//#pragma mark - Network
//- (MKNetworkOperation*)fetchDataOfPage:(int)page completion:(VoidBlock)block
//{
//    NSUInteger preCount = 0;
//    if (page != 1) {
//        preCount = self.resultArray.count;
//    }
//    
//    return [self fetchDataOfPage:page viewRefreshBlock:^{
//        if (page == 1) {
//            [self.view reloadData];
//        } else {
//            NSMutableArray* indexPaths = [@[] mutableCopy];
//            for (NSUInteger i = preCount; i < self.resultArray.count; i++){
//                [indexPaths addObject:[NSIndexPath indexPathForItem:i inSection:0]];
//            }
//            
//            if ([[UIView class] respondsToSelector:@selector(performWithoutAnimation:)]) {
//                [UIView performWithoutAnimation:^{
//                    [self.view insertItemsAtIndexPaths:indexPaths];
//                }];
//            }
//        }
//    } completion:block];
//}

#pragma mark - UICollecitonView Datasource And Delegate

//margin
-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    return UIEdgeInsetsMake(10, 10, 10, 10);
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    //[collectionView deselectItemAtIndexPath:indexPath animated:YES];
    NSDictionary *dict = [self topicForIndexPath:indexPath];
    if ([self.delegate respondsToSelector:@selector(didClickShow:)]) {
        [self.delegate didClickShow:dict];
    }
}


-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    //return self.resultArray.count;
    return 10;
}
- (NSDictionary*)topicForCell:(UICollectionViewCell*)cell
{
    return [self topicForIndexPath:[self.view indexPathForCell:cell]];
}

- (NSDictionary*)topicForIndexPath:(NSIndexPath*)indexPath
{
    NSInteger row = indexPath.row;
    if (row < self.resultArray.count) {
        return self.resultArray[row];
    } else {
        return nil;
    }
}

@end

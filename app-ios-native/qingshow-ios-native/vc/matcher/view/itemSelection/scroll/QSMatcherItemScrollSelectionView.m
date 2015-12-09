//
//  QSMatcherItemScrollSelectionView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSMatcherItemScrollSelectionView.h"
#import "QSMatcherItemScrollSelectionCollectionViewCell.h"
#import "UINib+QSExtension.h"
#import "QSHorizontalCollectionViewLayout.h"

@interface QSMatcherItemScrollSelectionView ()

@property (weak, nonatomic) IBOutlet UICollectionView* collectionView;

@end

@implementation QSMatcherItemScrollSelectionView

@synthesize selectIndex = _selectIndex;
@synthesize datasource = _datasource;
@synthesize delegate = _delegate;

- (void)setSelectIndex:(int)selectIndex {
    NSIndexPath* indexPath = [NSIndexPath indexPathForItem:_selectIndex inSection:0];
    QSMatcherItemScrollSelectionCollectionViewCell* cell = (QSMatcherItemScrollSelectionCollectionViewCell*)[self.collectionView cellForItemAtIndexPath:indexPath];
    cell.hover = NO;
    
    _selectIndex = selectIndex;
    indexPath = [NSIndexPath indexPathForItem:_selectIndex inSection:0];
    cell = (QSMatcherItemScrollSelectionCollectionViewCell*)[self.collectionView cellForItemAtIndexPath:indexPath];
    cell.hover = YES;
}

#pragma mark - Static
+ (instancetype)generateView {
    return [UINib generateViewWithNibName:@"QSMatcherItemScrollSelectionView"];
}
#pragma mark - Life Cycle
- (void)awakeFromNib {
    [self.collectionView registerNib:[UINib nibWithNibName:@"QSMatcherItemScrollSelectionCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:QSMatcherItemScrollSelectionCollectionViewCellIdentifier];
    self.collectionView.dataSource = self;
    self.collectionView.delegate = self;
    QSHorizontalCollectionViewLayout* layout = [[QSHorizontalCollectionViewLayout alloc] init];
    layout.itemWidth = 60.f;
    layout.itemHeight = 78.f;
    layout.horizontalSpace = 4.f;
    self.collectionView.collectionViewLayout = layout;
}

#pragma mark - QSMatcherItemSelectionViewProtocol
- (void)reloadData {
    [self.collectionView reloadData];
}
- (void)offsetToZero:(BOOL)fAnimate {
    [self.collectionView setContentOffset:CGPointZero animated:fAnimate];
}
- (void)showSelect:(BOOL)fAnimate {
    [self.collectionView setContentOffset:CGPointMake(64.f * self.selectIndex, 0) animated:fAnimate];
//    [self.collectionView scrollToItemAtIndexPath:[NSIndexPath indexPathForItem:self.selectIndex inSection:0] atScrollPosition:UICollectionViewScrollPositionLeft animated:fAnimate];
}


#pragma mark - UICollectionView DataSource
- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return [self.datasource numberOfItemInSelectionView:self];
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    
    NSDictionary* dict = [self.datasource selectionView:self itemDictAtIndex:indexPath.item];
    BOOL f = NO;
    if ([self.datasource respondsToSelector:@selector(selectionView:hasSelectItemId:)]) {
        NSString* itemId = [QSEntityUtil getIdOrEmptyStr:dict];
        f = [self.datasource selectionView:self hasSelectItemId:itemId];
    }
    QSMatcherItemScrollSelectionCollectionViewCell* cell = [collectionView dequeueReusableCellWithReuseIdentifier:QSMatcherItemScrollSelectionCollectionViewCellIdentifier forIndexPath:indexPath];
    [cell bindWithDict:dict];
    if (self.selectIndex == indexPath.item) {
        cell.checkmarkImgView.hidden = YES;
    } else {
        cell.checkmarkImgView.hidden = !f;
    }

    cell.hover = self.selectIndex == indexPath.item;
    return cell;
}

#pragma mark - UICollectionView Delegate
- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    NSIndexPath* oldIndexPath = [NSIndexPath indexPathForItem:self.selectIndex inSection:0];
    self.selectIndex = indexPath.item;
    [self.delegate selectionView:self didSelectItemAtIndex:indexPath.item];
    [UIView performWithoutAnimation:^{
        [collectionView reloadItemsAtIndexPaths:@[oldIndexPath]];
        [collectionView reloadItemsAtIndexPaths:@[indexPath]];
        
    }];

}

#pragma mark - UIScrollView Delegate
- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    
    if (scrollView.contentOffset.x + scrollView.bounds.size.width * 3 >= scrollView.contentSize.width) {
        [self.delegate selectionViewDidReachEnd:self];
    }
}

@end

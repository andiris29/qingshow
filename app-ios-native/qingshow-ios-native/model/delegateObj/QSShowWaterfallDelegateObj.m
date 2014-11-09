//
//  QSShowWaterfallDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/6/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSShowWaterfallDelegateObj.h"
#import "QSTimeCollectionViewCell.h"

@interface QSShowWaterfallDelegateObj ()
@property (strong, nonatomic) UICollectionView* collectionView;

@property (assign, nonatomic) int currentPage;

@end

@implementation QSShowWaterfallDelegateObj
#pragma mark - Config
- (id)init
{
    self = [super init];
    if (self) {
        self.resultArray = [@[] mutableCopy];
        self.currentPage = 1;
    }
    return self;
}

- (void)bindWithCollectionView:(UICollectionView *)collectionView
{
    self.collectionView = collectionView;
    self.collectionView.dataSource = self;
    self.collectionView.delegate = self;
    
    
    QSWaterFallCollectionViewLayout* layout = [[QSWaterFallCollectionViewLayout alloc] init];
    self.collectionView.collectionViewLayout = layout;
    self.collectionView.translatesAutoresizingMaskIntoConstraints = NO;
    
    self.collectionView.scrollEnabled=YES;
    self.collectionView.backgroundColor=[UIColor colorWithRed:240.f/255.f green:240.f/255.f blue:240.f/255.f alpha:1.f];
    [self.collectionView registerNib:[UINib nibWithNibName:@"QSWaterFallCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSWaterFallCollectionViewCell"];
    [self.collectionView registerNib:[UINib nibWithNibName:@"QSTimeCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSTimeCollectionViewCell"];
}
#pragma mark - Network
- (void)reloadData
{
    [self fetchDataOfPage:1];
}

- (void)fetchDataOfPage:(int)page
{
    MKNetworkOperation* op = self.networkBlock(^(NSArray *showArray, NSDictionary *metadata) {
        if (page == 1) {
            [self.resultArray removeAllObjects];
        }
        [self.resultArray addObjectsFromArray:showArray];
        [self.collectionView reloadData];
    }, ^(NSError *error) {
        NSLog(@"error");
    }, page);
}


#pragma mark - UICollecitonView Datasource And Delegate

- (NSDictionary*)getShowDictForIndexPath:(NSIndexPath*)indexPath
{
    return self.resultArray[indexPath.row - 1];
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        return CGSizeMake(145, 35);
    } else {
        NSDictionary* dict = [self getShowDictForIndexPath:indexPath];
        float height = [QSWaterFallCollectionViewCell getHeightWithData:dict];
        return CGSizeMake(145, height);
    }
    
}

//margin
-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    return UIEdgeInsetsMake(10, 10, 10, 10);
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
}


-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return self.resultArray.count + 1;
}


-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionViews cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        QSTimeCollectionViewCell* cell = (QSTimeCollectionViewCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSTimeCollectionViewCell" forIndexPath:indexPath];
        return cell;
    } else {
        QSWaterFallCollectionViewCell* cell = (QSWaterFallCollectionViewCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSWaterFallCollectionViewCell" forIndexPath:indexPath];
        cell.delegate = self;
        NSDictionary* dict = [self getShowDictForIndexPath:indexPath];
        [cell bindData:dict];
        
        return cell;
    }
}
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if ([self.delegate respondsToSelector:@selector(scrollViewDidScroll:)]) {
        [self.delegate scrollViewDidScroll:scrollView];
    }
}

@end

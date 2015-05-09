//
//  QSImageCollectionViewProvider.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/9/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSImageCollectionViewProvider.h"
#import "QSRecommendationDateCollectionViewCell.h"
#import "QSDateUtil.h"
#import "QSShowUtil.h"
#import "QSItemUtil.h"

@interface QSImageCollectionViewProvider ()
@property (strong, nonatomic) QSImageCollectionModel* clickedModel;

@end

@implementation QSImageCollectionViewProvider

@dynamic delegate;
- (instancetype)init{
    self = [super init];
    if (self) {
    }
    return self;
}
- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSImageItemCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSImageItemCollectionViewCell"];
    [self.view registerNib:[UINib nibWithNibName:@"QSRecommendationDateCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSRecommendationDateCollectionViewCell"];
//    [self.view registerNib:[UINib nibWithNibName:@"QSImageU01CollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSImageU01CollectionViewCell"];
}


#pragma mark - Collection View
-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionViews cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    UICollectionViewCell* cell = nil;
    QSImageCollectionModel* model = self.resultArray[indexPath.row];
    if (model.type == QSImageCollectionModelTypeDate) {
        QSRecommendationDateCollectionViewCell* dateCell = [collectionViews dequeueReusableCellWithReuseIdentifier:@"QSRecommendationDateCollectionViewCell" forIndexPath:indexPath];
        [dateCell bindWithDate:model.data];
        cell = dateCell;
    } else {
        QSImageCollectionViewCell* imgCell = [collectionViews dequeueReusableCellWithReuseIdentifier:@"QSImageItemCollectionViewCell" forIndexPath:indexPath];
        if (model.type == QSImageCollectionModelTypeShow) {
            [imgCell bindWithShow:model.data];
        } else {
            [imgCell bindWithItem:model.data];
        }
        cell = imgCell;
    }
    return cell;
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    [collectionView deselectItemAtIndexPath:indexPath animated:YES];
    QSImageCollectionModel* model = self.resultArray[indexPath.row];
    if (model.type != QSImageCollectionModelTypeDate) {
        self.clickedModel = model;
        if ([self.delegate respondsToSelector:@selector(didClickModel:provider:)]) {
            [self.delegate didClickModel:model provider:self];
        }
    }
}
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return CGSizeMake(150, 270);
}

- (void)refreshClickedData
{
    if (self.clickedModel) {
        NSUInteger row = [self.resultArray indexOfObject:self.clickedData];
        
        NSIndexPath* indexPath = [NSIndexPath indexPathForItem:row inSection:0];
        if (self.clickedModel.type == QSImageCollectionModelTypeDate) {
            QSRecommendationDateCollectionViewCell* cell = (QSRecommendationDateCollectionViewCell*)[self.view cellForItemAtIndexPath:indexPath];
            [cell bindWithDate:self.clickedModel.data];
        } else {
            QSImageCollectionViewCell* cell = (QSImageCollectionViewCell*)[self.view cellForItemAtIndexPath:indexPath];
            if (self.clickedModel.type == QSImageCollectionModelTypeShow) {
                [cell bindWithShow:self.clickedModel.data];
            } else {
                [cell bindWithItem:self.clickedModel.data];
            }
        }

        self.clickedModel = nil;
    }
}

@end

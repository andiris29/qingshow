//
//  QSS18Provider.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/24/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS18WaterfallProvider.h"
#import "QSTopShowOneDayCell.h"
#import "QSRecommendationDateCollectionViewCell.h"
@interface QSS18WaterfallProvider ()

@end

@implementation QSS18WaterfallProvider
@dynamic delegate;

- (instancetype)initWithDate:(NSDate*)date
{
    self = [super init];
    if (self) {
        self.date = date;
    }
    return self;
}

- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSTopShowOneDayCell" bundle:nil] forCellWithReuseIdentifier:kQSTopShowOneDayCellIdentifier];
    [self.view registerNib:[UINib nibWithNibName:@"QSRecommendationDateCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:kQSRecommendationDateCollectionViewCellIdentifier];
}

#pragma mark - Collection View
-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionViews cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    UICollectionViewCell* cell = nil;

    if (indexPath.row == 0) {
        QSRecommendationDateCollectionViewCell* dateCell = [collectionViews dequeueReusableCellWithReuseIdentifier:kQSRecommendationDateCollectionViewCellIdentifier forIndexPath:indexPath];
        [dateCell bindWithDate:self.date];
        dateCell.descLabel.text = @"Top List";
        dateCell.descLabel.textAlignment = NSTextAlignmentCenter;
        
        cell = dateCell;
    } else {
        NSDictionary* dict = self.resultArray[indexPath.row - 1];
        QSTopShowOneDayCell* showCell = [collectionViews dequeueReusableCellWithReuseIdentifier:kQSTopShowOneDayCellIdentifier forIndexPath:indexPath];
        [showCell bindWithShow:dict];
        cell = showCell;
        if ([UIScreen mainScreen].bounds.size.width == 414) {
            cell.contentView.transform  = CGAffineTransformMakeScale(1.3, 1.35);
        }
        else
        {
            cell.contentView.transform = CGAffineTransformMakeScale(1.1, 1.1);
        }
    }
    return cell;
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        return;
    } else {
        NSDictionary* dict = self.resultArray[indexPath.row - 1];
        self.clickedData = dict;
        if ([self.delegate respondsToSelector:@selector(didClickShow:ofProvider:)]) {
            [self.delegate didClickShow:dict ofProvider:self];
        }
    }

}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    float width = [UIScreen mainScreen].bounds.size.width / 2;
    float height = width / 9.f * 16.f;
    return CGSizeMake(width, height);
}

- (void)refreshClickedData
{
    if (self.clickedData) {
        NSUInteger row = [self.resultArray indexOfObject:self.clickedData] + 1;
        if (row < 1) {
            return;
        }
        NSIndexPath* indexPath = [NSIndexPath indexPathForItem:row inSection:0];
        QSTopShowOneDayCell* showCell = (QSTopShowOneDayCell*)[self.view cellForItemAtIndexPath:indexPath];
        [showCell bindWithShow:self.clickedData];
        self.clickedData = nil;
    }
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return self.resultArray.count + 1;
}

@end

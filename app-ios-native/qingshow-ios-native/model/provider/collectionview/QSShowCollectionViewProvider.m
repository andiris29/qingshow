//
//  QSShowWaterfallDelegateObj.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/6/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSShowCollectionViewProvider.h"
#import "QSShowUtil.h"

#define w ([UIScreen mainScreen].bounds.size.width)
#define h ([UIScreen mainScreen].bounds.size.height)
@interface QSShowCollectionViewProvider ()
@end

@implementation QSShowCollectionViewProvider
@dynamic delegate;
#pragma mark - Init
- (id)init
{
    self = [super init];
    if (self) {
    }
    return self;
}

#pragma mark - Cell
- (void)registerCell
{
    [self.view registerNib:[UINib nibWithNibName:@"QSMatchShowsCell"  bundle:nil] forCellWithReuseIdentifier:@"QSMatchShowsCellWithU01"];
}

#pragma mark - UICollecitonView Datasource And Delegate
- (void)refreshClickedData
{
    if (self.clickedData) {
        NSIndexPath* indexPath = [self getIndexPathOfShow:self.clickedData];
        QSMatchShowsCell* cell = (QSMatchShowsCell*)[self.view cellForItemAtIndexPath:indexPath];
        if (self.filterBlock) {
            if (!self.filterBlock(self.clickedData)) {
                //remove
                [self.resultArray removeObject:self.clickedData];
                [self.view reloadData];
                return;
            }
        }

        [cell bindWithDic:self.clickedData withIndex:(int)indexPath.item];
        self.clickedData = nil;
    }
}


- (NSDictionary*)getShowDictForIndexPath:(NSIndexPath*)indexPath
{
    NSInteger index = indexPath.row;
    if (index < 0) {
        return nil;
    } else {
        return self.resultArray[index];
    }
}
- (NSIndexPath*)getIndexPathOfShow:(NSDictionary*)showDict
{
    NSInteger index = [self.resultArray indexOfObject:showDict];
    
    return [NSIndexPath indexPathForRow:index inSection:0];
}


- (void)updateShow:(NSDictionary*)showDict
{
    NSIndexPath* i = [self getIndexPathOfShow:showDict];
    QSMatchShowsCell* cell = (QSMatchShowsCell*)[self.view cellForItemAtIndexPath:i];
    [cell bindWithDic:showDict withIndex:(int)i.item];
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    return CGSizeMake([UIScreen mainScreen].bounds.size.width/2-10, w);
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    NSDictionary* showDict = [self getShowDictForIndexPath:indexPath];
    self.clickedData = showDict;
    if (showDict && [self.delegate respondsToSelector:@selector(didClickShow:provider:)]) {
        [self.delegate didClickShow:showDict provider:self];
    }
}


-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.resultArray.count;
}


-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionViews cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    QSMatchShowsCell* cell = (QSMatchShowsCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSMatchShowsCellWithU01" forIndexPath:indexPath];
    cell.delegate = self;
    [cell bindWithDic:self.resultArray[indexPath.item] withIndex:(int)indexPath.item];
    if (w == 414) {
        cell.contentView.transform = CGAffineTransformMakeScale(w/(320-15), w/(320-12));
    }
    else
    {
        cell.contentView.transform = CGAffineTransformMakeScale(w/(320-15), w/(320-16));
    }
    
    cell.backgroundColor = [UIColor whiteColor];
    
    return (UICollectionViewCell *)cell;
}
#pragma mark - QSMatchShowsCellDelegate
- (void)headerImgViewPressed:(id)sender {
    if ([self.delegate respondsToSelector:@selector(didClickPeople:provider:)]) {
        [self.delegate didClickPeople:sender provider:self];
    }
}

- (void)matchImgViewPressed:(id)sender {
    self.clickedData = sender;
    if ([self.delegate respondsToSelector:@selector(didClickShow:provider:)]) {
        [self.delegate didClickShow:sender provider:self];
    }
}

@end

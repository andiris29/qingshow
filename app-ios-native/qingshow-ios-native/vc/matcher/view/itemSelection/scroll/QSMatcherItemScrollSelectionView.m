//
//  QSMatcherItemScrollSelectionView.m
//  qingshow-ios-native
//
//  Created by wxy325 on 7/10/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSMatcherItemScrollSelectionView.h"
#import "QSMatcherItemScrollSelectionCollectionViewCell.h"

@interface QSMatcherItemScrollSelectionView ()

@property (weak, nonatomic) IBOutlet UICollectionView* collectionView;

@end

@implementation QSMatcherItemScrollSelectionView

@synthesize selectIndex;
@synthesize datasource;
@synthesize delegate;

- (void)awakeFromNib {
    [self.collectionView registerNib:[UINib nibWithNibName:@"QSMatcherItemScrollSelectionCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:QSMatcherItemScrollSelectionCollectionViewCellIdentifier];
    self.collectionView.dataSource = self;
    self.collectionView.delegate = self;
}

#pragma mark - QSMatcherItemSelectionViewProtocol
- (void)reloadData {
    [self.collectionView reloadData];
}
- (void)offsetToZero:(BOOL)fAnimate {
    [self.collectionView setContentOffset:CGPointZero animated:fAnimate];
}

@end

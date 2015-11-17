//
//  QSS01MatchShowsViewController.h
//  qingshow-ios-native
//
//  Created by mhy on 15/6/18.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QSRootContentViewController.h"
#import "QSAbstractListViewProvider.h"

@interface QSS01MatchShowsViewController : QSRootContentViewController 

@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;
@property (weak, nonatomic) IBOutlet UIButton *backToTopbtn;

@end

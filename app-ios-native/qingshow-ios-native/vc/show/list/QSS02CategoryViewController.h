//
//  QSS02CategoryViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/15/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSShowCollectionViewProvider.h"
#import "QSFeedingCategory.h"


@interface QSS02CategoryViewController : UIViewController<QSShowProviderDelegate>

@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;

- (id)initWithCategory:(QSFeedingCategory)type;

@end

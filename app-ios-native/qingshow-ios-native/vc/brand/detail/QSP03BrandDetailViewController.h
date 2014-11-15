//
//  QSP03BrandDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/15/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSDetailBaseViewController.h"

@interface QSP03BrandDetailViewController : QSDetailBaseViewController

@property (strong, nonatomic) IBOutlet UICollectionView* showCollectionView;
@property (strong, nonatomic) IBOutlet UICollectionView* discountCollectionView;
@property (strong, nonatomic) IBOutlet UITableView* followerTableView;

- (id)initWithBrand:(NSDictionary*)brandDict;

@end

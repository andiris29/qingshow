//
//  QSBrandListViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/12/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSBigImageTableViewDelegateObj.h"
#import "QSBrandTableViewHeaderView.h"
@interface QSP03BrandListViewController : UIViewController  <QSBigImageTableViewDelegateObjDelegate, QSBrandTableViewHeaderViewDelegate>

//@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;
@property (weak, nonatomic) IBOutlet UITableView *tableView;

- (id)init;

@end

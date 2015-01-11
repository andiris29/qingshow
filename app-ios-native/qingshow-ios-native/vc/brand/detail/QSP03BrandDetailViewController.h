//
//  QSP03BrandDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/15/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSDetailBaseViewController.h"
#import "QSBigImageTableViewDelegateObj.h"

@interface QSP03BrandDetailViewController : QSDetailBaseViewController <QSBigImageTableViewDelegateObjDelegate>

@property (strong, nonatomic) IBOutlet UITableView* itemNewTableView;
@property (strong, nonatomic) IBOutlet UITableView* itemDiscountTableView;
@property (strong, nonatomic) IBOutlet UITableView* showTableView;
//@property (strong, nonatomic) IBOutlet UICollectionView* showCollectionView;

@property (strong, nonatomic) IBOutlet UITableView* followerTableView;

- (id)initWithBrand:(NSDictionary*)brandDict;
- (id)initWithBrand:(NSDictionary *)brandDict item:(NSDictionary*)itemDict;

@end

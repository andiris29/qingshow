//
//  QSP03BrandDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/15/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSDetailBaseViewController.h"
#import "QSBigImageTableViewProvider.h"
#import "QSItemImageListTableViewProvider.h"
@interface QSP04BrandDetailViewController : QSDetailBaseViewController <QSBigImageTableViewProviderDelegate, QSItemImageListTableViewProviderDelegate>

@property (weak, nonatomic) IBOutlet UITableView* itemNewTableView;
@property (weak, nonatomic) IBOutlet UITableView* itemDiscountTableView;
@property (weak, nonatomic) IBOutlet UITableView* showTableView;

@property (weak, nonatomic) IBOutlet UITableView* followerTableView;

- (id)initWithBrand:(NSDictionary*)brandDict;
- (id)initWithBrand:(NSDictionary *)brandDict item:(NSDictionary*)itemDict;

@end
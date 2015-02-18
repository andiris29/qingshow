//
//  QSU02UserDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/19/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSDetailBaseViewController.h"
#import "QSBigImageTableViewProvider.h"
@interface QSU01UserDetailViewController : QSDetailBaseViewController<QSBigImageTableViewProviderDelegate>

@property (strong, nonatomic) IBOutlet UICollectionView* likedCollectionView;
@property (strong, nonatomic) IBOutlet UICollectionView* recommendationCollectionView;
@property (strong, nonatomic) IBOutlet UITableView* followingTableView;
@property (weak, nonatomic) IBOutlet UITableView *likeBrandTableView;

- (id)initWithPeople:(NSDictionary*)peopleDict;
- (id)initWithCurrentUser;

@property (weak, nonatomic) IBOutlet UIButton *accountBtn;

@end

//
//  QSU02UserDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/19/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSDetailBaseViewController.h"
#import "QSImageCollectionViewProvider.h"

@protocol QSMenuProviderDelegate;

@interface QSU01UserDetailViewController : QSDetailBaseViewController <QSImageCollectionViewProviderDelegate>

@property (weak, nonatomic) IBOutlet UICollectionView *matcherCollectionView;
@property (strong, nonatomic) IBOutlet UICollectionView* recommendCollectionView;
@property (weak, nonatomic) IBOutlet UICollectionView *favorCollectionView;
@property (weak, nonatomic) IBOutlet UITableView *followingTableView;
@property (weak, nonatomic) IBOutlet UITableView *followerTableView;

@property (weak, nonatomic) NSObject<QSMenuProviderDelegate>* menuProvider;

- (id)initWithPeople:(NSDictionary*)peopleDict;
- (id)initWithCurrentUser;

@end

//
//  QSU02UserDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/19/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSDetailBaseViewController.h"
#import "QSPeopleListTableViewProvider.h"
#import "QSRootContentViewController.h"

@interface QSU01UserDetailViewController : QSDetailBaseViewController <QSPeoplelListTableViewProviderDelegate,
QSIRootContentViewController>

@property (weak, nonatomic) IBOutlet UICollectionView *matcherCollectionView;
@property (strong, nonatomic) IBOutlet UICollectionView* recommendCollectionView;
@property (weak, nonatomic) IBOutlet UICollectionView *favorCollectionView;
@property (weak, nonatomic) IBOutlet UITableView *followingTableView;
@property (weak, nonatomic) IBOutlet UITableView *followerTableView;

@property (strong, nonatomic) IBOutlet UIButton *menuBtn;
@property (weak, nonatomic) IBOutlet UIButton *settingBtn;

@property (weak, nonatomic) IBOutlet UIButton *backBtn;
@property (weak, nonatomic) IBOutlet UIButton *backToTopBtn;

- (IBAction)topToTopBtnPressed:(id)sender;


- (id)initWithPeople:(NSDictionary*)peopleDict;
- (id)initWithCurrentUser;

@end

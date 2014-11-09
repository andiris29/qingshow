//
//  QSModelDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSModelBadgeView.h"

#import "QSShowWaterfallDelegateObj.h"
#import "QSModelListTableViewDelegateObj.h"

@interface QSP02ModelDetailViewController : UIViewController <QSModelBadgeViewDelegate, QSShowWaterfallDelegateObjDelegate, QSModelListTableViewDelegateObjDelegate>

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *topConstrain;
@property (weak, nonatomic) IBOutlet UIView *badgeContainer;
@property (weak, nonatomic) IBOutlet UIView *contentContainer;

@property (strong, nonatomic) IBOutlet UICollectionView* showCollectionView;
@property (strong, nonatomic) IBOutlet UITableView* followingTableView;
@property (strong, nonatomic) IBOutlet UITableView* followerTableView;


- (id)initWithModel:(NSDictionary*)peopleDict;

@end

//
//  QSModelDetailViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSBadgeView.h"

#import "QSShowCollectionViewDelegateObj.h"
#import "QSModelListTableViewDelegateObj.h"

@interface QSDetailBaseViewController : UIViewController <QSBadgeViewDelegate, QSShowDelegateObjDelegate, QSModelListTableViewDelegateObjDelegate>

@property (strong, nonatomic) NSArray* viewArray;
@property (strong, nonatomic) QSBadgeView* badgeView;
@end

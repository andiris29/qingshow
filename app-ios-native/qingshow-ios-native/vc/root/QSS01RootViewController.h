//
//  QSViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSWaterFallCollectionViewCell.h"
#import "QSWaterFallCollectionViewLayout.h"
#import "QSRootMenuView.h"
#import "QSShowWaterfallDelegateObj.h"
@interface QSS01RootViewController : UIViewController< QSRootMenuViewDelegate, QSShowWaterfallDelegateObjDelegate>
@property (weak, nonatomic) IBOutlet UIView *menuContainer;

@property (strong, nonatomic) IBOutlet UICollectionView *collectionView;

@end

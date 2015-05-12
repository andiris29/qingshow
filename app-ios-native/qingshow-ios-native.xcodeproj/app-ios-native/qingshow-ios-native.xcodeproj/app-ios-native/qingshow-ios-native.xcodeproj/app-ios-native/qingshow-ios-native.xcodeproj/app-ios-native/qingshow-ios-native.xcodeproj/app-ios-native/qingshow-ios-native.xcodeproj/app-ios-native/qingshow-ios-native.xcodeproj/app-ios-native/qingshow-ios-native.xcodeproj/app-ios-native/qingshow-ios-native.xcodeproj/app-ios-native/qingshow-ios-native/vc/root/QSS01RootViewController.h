//
//  QSViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSShowCollectionViewCell.h"

#import "QSShowCollectionViewProvider.h"
#import "QSAbstractRootViewController.h"
@interface QSS01RootViewController : QSAbstractRootViewController< QSShowProviderDelegate, QSAbstractScrollProviderDelegate, UIAlertViewDelegate>

@property (strong, nonatomic) IBOutlet UICollectionView *collectionView;

@end
